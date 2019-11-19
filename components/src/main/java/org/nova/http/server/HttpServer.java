/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.nova.annotations.Description;
import org.nova.collections.RingBuffer;
import org.nova.http.Header;
import org.nova.logging.Item;
import org.nova.logging.Logger;
import org.nova.metrics.RateMeter;
import org.nova.operations.OperatorVariable;
import org.nova.test.Testing;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;
import org.nova.utils.Utils;

import com.google.common.base.Strings;

public class HttpServer 
{
	final private RequestHandlerMap requestHandlerMap;
	final private TraceManager traceManager;
	final private IdentityContentDecoder identityContentDecoder;
	final private IdentityContentEncoder identityContentEncoder;
	@Description("Overall request rate.")
	final private RateMeter requestRateMeter;
	final private String categoryPrefix;
	final private Server[] servers;
	final private RingBuffer<RequestLogEntry> lastRequestsLogEntries;
	final private RingBuffer<RequestLogEntry> lastExceptionRequestsLogEntries;
	final private RingBuffer<RequestHandlerNotFoundLogEntry> lastRequestHandlerNotFoundLogEntries;
	final private int [] ports;
	private Transformers transformers;
	final private Logger logger;
	private boolean started;
	
	@OperatorVariable()
	private boolean test;
	
	public HttpServer(TraceManager traceManager, Logger logger,boolean test,HttpServerConfiguration configuration,Server[] servers) throws Exception
	{
	    this.logger=logger;
	    this.ports=new int[servers.length];
	    for (int i=0;i<servers.length;i++)
	    {
	        this.ports[i]=((ServerConnector)((servers[i].getConnectors())[0])).getPort();
	    }
		this.categoryPrefix=configuration.categoryPrefix+"@";
		this.requestHandlerMap = new RequestHandlerMap(test,configuration.requestLastRequestLogEntryBufferSize);
		this.traceManager = traceManager;
		this.servers = servers;
		this.requestRateMeter = new RateMeter();
		this.identityContentDecoder = new IdentityContentDecoder();
		this.identityContentEncoder = new IdentityContentEncoder();
		this.test=test;
		this.lastRequestsLogEntries=new RingBuffer<>(new RequestLogEntry[configuration.lastRequestLogEntryBufferSize]);
		this.lastExceptionRequestsLogEntries=new RingBuffer<>(new RequestLogEntry[configuration.lastExceptionRequestLogEntryBufferSize]);
		this.lastRequestHandlerNotFoundLogEntries=new RingBuffer<>(new RequestHandlerNotFoundLogEntry[configuration.lastNotFoundBufferSize]);
		this.transformers=new Transformers();
	}

	public HttpServer(TraceManager traceManager, Logger logger,boolean test,Server server) throws Exception
	{
		this(traceManager, logger ,test,new HttpServerConfiguration(),  new Server[]{server});
	}
	

	public void start() throws Exception
	{
	    if (started)
	    {
	        return;
	    }
		for (Server server:this.servers)
		{
	        AbstractHandler handler=new AbstractHandler()
	        {
	            @Override
	            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	            {
	                handleRequest(target, baseRequest, request, response);
	            }
	        };
			server.setHandler(handler);
			server.start();
		}
        this.started=true;
	}
	
	public void setTransformers(Transformers transformers)
	{
	    this.transformers=transformers;
	}

	public void addContentReaders(ContentReader<?>...contentReaders)
    {
	    this.transformers.add(contentReaders);
    }
    public void addContentWriters(ContentWriter<?>...contentWriters)
    {
        this.transformers.add(contentWriters);
    }
    public void addContentEncoders(ContentEncoder...contentEncoders)
    {
        this.transformers.add(contentEncoders);
    }
    public void addContentDecoders(ContentDecoder...contentDecoders)
    {
        this.transformers.add(contentDecoders);
    }
    public void addFilters(Filter...filters)
    {
        this.transformers.add(filters);
    }

    public int[] getPorts()
    {
        return this.ports;
    }
	public Transformers getTransformers()
	{
	    return this.transformers;
	}
	
	public void registerHandlers(Object object) throws Exception
	{
		registerHandlers(null, object);
	}

	public void registerHandlers(String root, Object object) throws Exception
	{
		this.requestHandlerMap.registerObject(root, object, this.transformers);
	}

	public void registerHandler(String root, Object object, Method method) throws Exception
	{
		this.requestHandlerMap.registerObjectMethod(root, object, method, this.transformers);
	}

	public RequestHandler[] getRequestHandlers()
	{
		return this.requestHandlerMap.getRequestHandlers();
	}
	public RequestHandler getRequestHandler(String key)
	{
		return this.requestHandlerMap.getRequestHandler(key);
	}
	public void handleRequest(String string, Request request, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
			throws IOException, ServletException
	{
		try
		{
			this.requestRateMeter.increment();
			decode(servletRequest, servletResponse);
		}
		catch (Throwable t)
		{
		    this.logger.log(t);
		}
		finally
		{
            request.setHandled(true);
		}
	}

	private ContentReader<?> findContentReader(String contentType, RequestHandler handler)
	{
		if ((contentType == null) || (contentType.length() == 0))
		{
			return null;
		}
		contentType = org.nova.utils.Utils.split(contentType, ';')[0];

		ContentReader<?> reader = handler.getContentReaders().get(contentType);
		if (reader != null)
		{
			return reader;
		}
		reader = handler.getContentReaders().get(WsUtils.toAnySubMediaType(contentType));
		if (reader != null)
		{
			return reader;
		}
		return handler.getContentReaders().get("*/*");
	}

	private ContentWriter<?> findContentWriter(String accept, RequestHandler handler)
	{
		Map<String, ContentWriter<?>> map = handler.getContentWriters();
		if (accept != null)
		{
			List<ValueQ> list = ValueQ.sort(accept);
			for (ValueQ item : list)
			{
				ContentWriter<?> writer = map.get(item.value);
				if (writer != null)
				{
					return writer;
				}
			}
			for (ValueQ item : list)
			{
				String anySubMediaType=WsUtils.toAnySubMediaType(item.value);
				ContentWriter<?> writer = map.get(anySubMediaType);
				if (writer != null)
				{
					return writer;
				}
			}
			return null;
		}
		else
		{
			return map.get("*/*"); 
		}
	}

	private ContentDecoder getContentDecoder(String contentEncoding, RequestHandler handler) throws AbnormalException
	{
		if (contentEncoding == null)
		{
			return this.identityContentDecoder;
		}
		ContentDecoder decoder = handler.getContentDecoders().get(contentEncoding);
		if (decoder != null)
		{
			return decoder;
		}
		throw new AbnormalException(Abnormal.NO_DECODER);
	}

	private ContentEncoder getContentEncoder(String acceptEncoding, RequestHandler handler) throws AbnormalException
	{
		if (acceptEncoding == null)
		{
			return this.identityContentEncoder;
		}
		List<ValueQ> list = ValueQ.sort(acceptEncoding);
		for (ValueQ item : list)
		{
			ContentEncoder encoder = handler.getContentEncoders().get(item.value);
			if (encoder != null)
			{
				return encoder;
			}
		}
		return this.identityContentEncoder;
	}

	private void decode(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Throwable
	{
		try (Trace trace = new Trace(this.traceManager, "HttpServer.handle"))
		{
			String URI = servletRequest.getRequestURI();
			String method = servletRequest.getMethod();
			RequestHandlerWithParameters requestHandlerWithParameters = this.requestHandlerMap.resolve(method, URI);
			if (requestHandlerWithParameters == null)
			{
				if (Testing.ENABLED)
				{
					System.out.println(method+" "+URI+": No Handler");
					servletResponse.getWriter().println("404 - NOT FOUND: "+URI);
				}	
				servletResponse.setStatus(HttpStatus.NOT_FOUND_404);
				synchronized (this.lastRequestHandlerNotFoundLogEntries)
				{
					this.lastRequestHandlerNotFoundLogEntries.add(new RequestHandlerNotFoundLogEntry(trace,servletRequest));
				}
			}
			if (requestHandlerWithParameters.requestHandler.isPublic()==true)
			{
				//TODO: prevent access
			}
			handle(trace, servletRequest, servletResponse, requestHandlerWithParameters);
		}
	}

	private void handle(Trace parent, HttpServletRequest servletRequest, HttpServletResponse servletResponse, RequestHandlerWithParameters requestHandlerWithParameters) throws Throwable
	{
		long responseUncompressedContentSize=0;
		long requestUncompressedContentSize=0;
		long responseCompressedContentSize=0;
		long requestCompressedContentSize=0;
		Context context = new Context(requestHandlerWithParameters.requestHandler, servletRequest, servletResponse);
		RequestHandler handler = requestHandlerWithParameters.requestHandler;
		Trace trace = new Trace(traceManager,parent,this.categoryPrefix+ handler.getKey());
		try
		{
		    
            DecoderContext decoderContext = null;
            if ("application/x-www-form-urlencoded".equals(servletRequest.getParameter("Content-Type"))==false)
            {
                decoderContext = getContentDecoder(servletRequest.getHeader("Content-Encoding"), handler).open(servletRequest, servletResponse);
            }
            //for "application/x-www-form-urlencoded" we cannot read the content, otherwise the request parameters will not be created.
			
			try 
			{
				FilterChain chain = new FilterChain(requestHandlerWithParameters, decoderContext);
				context.setContentReader(findContentReader(servletRequest.getContentType(), handler));
				context.setContentWriter(findContentWriter(servletRequest.getHeader("Accept"), handler));

				Response<?> response = chain.next(trace, context);
                if (context.isCaptured()==false)
                {
					if (response != null)
					{
						if (response.headers != null)
						{
							for (Header header : response.headers)
							{
								servletResponse.setHeader(header.getName(), header.getValue());
							}
						}
						if (response.cookies!=null)
						{
						    for (Cookie cookie:response.cookies)
						    {
						        servletResponse.addCookie(new javax.servlet.http.Cookie(cookie.getName(),cookie.getValue()));
						    }
						}
						servletResponse.setStatus(response.getStatusCode());
						ContentWriter<?> writer = context.getContentWriter();
						if (writer != null)
						{
                            if (servletResponse.getContentType()==null)
                            {
                                servletResponse.setContentType(writer.getMediaType());
                            }
							ContentEncoder contentEncoder = getContentEncoder(servletRequest.getHeader("Accept-Encoding"), handler);
							if (Strings.isNullOrEmpty(contentEncoder.getCoding())==false)
							{
							    servletResponse.setHeader("Content-Encoding", contentEncoder.getCoding());
							}
							EncoderContext encoderContext = contentEncoder.open(servletRequest, servletResponse);
							try 
							{
								Method write = writer.getClass().getMethod("write", Context.class, OutputStream.class, Object.class);
								Object content = response.getContent();
								write.invoke(writer, context, encoderContext.getOutputStream(), content);
	    
							}
							finally
							{
								encoderContext.close();
								responseUncompressedContentSize=encoderContext.getUncompressedContentSize();
								responseCompressedContentSize=encoderContext.getCompressedContentSize();
							}
							
						}
						else if (response.getContent() != null)
						{
							if (Testing.ENABLED)
							{
								System.out.println("NO_WRITER: Accept="+servletRequest.getHeader("Accept"));
							}
							throw new AbnormalException(Abnormal.NO_WRITER);
						}
					}
				}
			}
			catch (StatusException e)
			{
                if (e.headers != null)
                {
                    for (Header header : e.headers)
                    {
                        servletResponse.setHeader(header.getName(), header.getValue());
                    }
                }
                if (e.cookies!=null)
                {
                    for (Cookie cookie:e.cookies)
                    {
                        servletResponse.addCookie(new javax.servlet.http.Cookie(cookie.getName(),cookie.getValue()));
                    }
                }
                servletResponse.setStatus(e.statusCode);
			}
            catch (Throwable e)
            {
                throw new Exception(requestHandlerWithParameters.requestHandler.getKey(),e);
            }
			finally
			{
				if (decoderContext!=null)
				{
					requestUncompressedContentSize=decoderContext.getUncompressedContentSize();
					requestCompressedContentSize=decoderContext.getCompressedContentSize();
					decoderContext.close();
				}
			}
		}
		catch (Throwable e)
		{
			trace.close(e);
            servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
            if (this.test)
            {
                servletResponse.getOutputStream().print(Utils.toString(e));
            }
		}
		finally
		{
			trace.close();
			handler.update(context,servletResponse.getStatus(), trace.getDurationNs(),requestUncompressedContentSize,responseUncompressedContentSize,requestCompressedContentSize,responseCompressedContentSize);
			RequestLogEntry entry=new RequestLogEntry(trace,context,handler,servletRequest,servletResponse);
			
			if (handler.isLogLastRequestsInMemory())
			{
			    handler.log(entry);
                this.lastRequestsLogEntries.add(entry);
    			if (trace.getThrowable()!=null)
    			{
    				synchronized (this.lastExceptionRequestsLogEntries)
    				{
    					this.lastExceptionRequestsLogEntries.add(entry);
    				}
    			}
			}
            ArrayList<Item> items=new ArrayList<>();
            if (handler.isLog())
            {
                items.add(new Item("remoteEndPoint",entry.remoteEndPoint));
                items.add(new Item("request",entry.request));
                items.add(new Item("statusCode",entry.statusCode));
                items.add(new Item("queryString",entry.getQueryString()));
            }
            if ((handler.isLogRequestHeaders()&&entry.requestHeaders!=null))
            {
                if (entry.requestHeaders!=null)
                {
                    items.add(new Item("requestHeaders",entry.requestHeaders));
                }
            }
            if (handler.isLogRequestContent())
            {                
                if (entry.requestContentText!=null)
                {
                    items.add(new Item("requestContent",entry.requestContentText));
                }
            }
            if (handler.isLogResponseHeaders())
            {
                if (entry.responseHeaders!=null)
                {
                    items.add(new Item("responseHeaders",entry.responseHeaders));
                }
            }
            if (handler.isLogResponseContent())
            {
                if (entry.responseContentText!=null)
                {
                    items.add(new Item("responseContent",entry.responseContentText));
                }
            }
            this.logger.log(trace,handler.getKey(),Logger.toArray(items));
		}
	}

	public RequestLogEntry[] getLastRequestLogEntries()
	{
		synchronized (this.lastRequestsLogEntries)
		{
			List<RequestLogEntry> list=this.lastRequestsLogEntries.getSnapshot();
            return list.toArray(new RequestLogEntry[list.size()]);
		}
	}
	
	public RequestHandlerNotFoundLogEntry[] getRequestHandlerNotFoundLogEntries()
	{
		synchronized (this.lastRequestsLogEntries)
		{
			List<RequestHandlerNotFoundLogEntry> list=this.lastRequestHandlerNotFoundLogEntries.getSnapshot();
            return list.toArray(new RequestHandlerNotFoundLogEntry[list.size()]);
		}
	}
	
	public RequestLogEntry[] getLastExceptionRequestLogEntries()
	{
		synchronized (this.lastRequestsLogEntries)
		{
            List<RequestLogEntry> list=this.lastExceptionRequestsLogEntries.getSnapshot();
            return list.toArray(new RequestLogEntry[list.size()]);
		}
	}
	public RateMeter getRequestRateMeter()
	{
		return this.requestRateMeter;
	}
	
}
