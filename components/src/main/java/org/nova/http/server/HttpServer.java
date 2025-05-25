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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.annotations.Description;
import org.nova.collections.RingBuffer;
import org.nova.debug.Debugging;
import org.nova.http.Header;
import org.nova.json.ObjectMapper;
import org.nova.logging.Item;
import org.nova.logging.Logger;
import org.nova.metrics.RateMeter;
import org.nova.operations.OperatorVariable;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

public class HttpServer 
{
    static AtomicLong RUNTIME_KEY_GENERATOR=new AtomicLong();
    
	final private RequestMethodMap requestMethodMap;
	final private TraceManager traceManager;
	final private IdentityContentDecoder identityContentDecoder;
	final private IdentityContentEncoder identityContentEncoder;
	@Description("Overall request rate.")
	final private RateMeter requestRateMeter;
	final private String categoryPrefix;
	final private RingBuffer<RequestLogEntry> lastRequestsLogEntries;
	final private RingBuffer<RequestLogEntry> lastExceptionRequestsLogEntries;
	final private RingBuffer<RequestHandlerNotFoundLogEntry> lastRequestHandlerNotFoundLogEntries;
	private Transformers transformers;
	final private Logger logger;
    private ArrayList<ServletHandler> frontServletHandlers;
    private ArrayList<ServletHandler> backServletHandlers;
//  final private Server[] servers;
//  final private int [] ports;
	
    final private boolean test;
    
    @OperatorVariable()
    private boolean logRequestHandlersOnly=true;
    
	
	public HttpServer(TraceManager traceManager, Logger logger,boolean test,HttpServerConfiguration configuration) throws Exception
	{
	    this.logger=logger;
		this.categoryPrefix=configuration.categoryPrefix+"@";
		this.requestMethodMap = new RequestMethodMap(test,configuration.requestLastRequestLogEntryBufferSize);
		this.traceManager = traceManager;
		this.requestRateMeter = new RateMeter();
		this.identityContentDecoder = new IdentityContentDecoder();
		this.identityContentEncoder = new IdentityContentEncoder();
		this.test=test;
		this.lastRequestsLogEntries=new RingBuffer<>(new RequestLogEntry[configuration.lastRequestLogEntryBufferSize]);
		this.lastExceptionRequestsLogEntries=new RingBuffer<>(new RequestLogEntry[configuration.lastExceptionRequestLogEntryBufferSize]);
		this.lastRequestHandlerNotFoundLogEntries=new RingBuffer<>(new RequestHandlerNotFoundLogEntry[configuration.lastNotFoundLogEntryBufferSize]);
				
		this.transformers=new Transformers();
        this.frontServletHandlers=new ArrayList<>();
        this.backServletHandlers=new ArrayList<>();
	}

//	public HttpServer(TraceManager traceManager, Logger logger,boolean test,Server server) throws Exception
//	{
//		this(traceManager, logger ,test,new HttpServerConfiguration(),  new Server[]{server});
//	}
	
//	public Server[] getServers()
//	{
//	    return this.servers;
//	}

	protected Logger getLogger()
	{
	    return this.logger;
	}
	
	protected TraceManager getTraceManager()
	{
	    return this.traceManager;
	}
	
//    abstract public void start() throws Throwable;
//    abstract public void stop() throws Throwable;
	
	public void setTransformers(Transformers transformers)
	{
	    this.transformers=transformers;
	}

	public void addContentReaders(ContentReader...contentReaders)
    {
	    this.transformers.add(contentReaders);
    }
    public void addContentWriters(ContentWriter...contentWriters)
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
    public void addBottomFilters(Filter...filters)
    {
        this.transformers.addBottomFilters(filters);
    }
    
    @Deprecated
    //Use addBottomFilters instead
    public void addFilters(Filter...filters)
    {
        this.transformers.addBottomFilters(filters);
    }
    public void addTopFilters(Filter...filters)
    {
        this.transformers.addTopFilters(filters);
    }
    public void registerBackServletHandlers(ServletHandler...servletHandlers)
    {
        for (ServletHandler handler:servletHandlers)
        {
            this.backServletHandlers.add(handler);
        }
    }
    public void registerFrontServletHandlers(ServletHandler...servletHandlers)
    {
        for (ServletHandler handler:servletHandlers)
        {
            this.frontServletHandlers.add(0,handler);
        }
    }

	public Transformers getTransformers()
	{
	    return this.transformers;
	}
	
	public void registerHandlers(Object object) throws Throwable
	{
		registerHandlers(null, object);
	}

	public void registerHandlers(String root, Object object) throws Throwable
	{
		this.requestMethodMap.registerObject(root, object, object.getClass(), this.transformers);
	}

	public void registerHandlers(Class<?> objectType) throws Throwable
    {
        this.requestMethodMap.registerObject(null, null, objectType, this.transformers);
    }
    public void registerHandlers(String root,Class<?> objectType) throws Throwable
    {
        this.requestMethodMap.registerObject(root, null, objectType, this.transformers);
    }

	public void registerHandler(String root, Object object, Method method) throws Throwable
	{
		this.requestMethodMap.registerObjectMethod(root, object, method, this.transformers);
	}
	
	HashMap<String,WebSocketInitializer<?>> webSocketInitializers=new HashMap<String, WebSocketInitializer<?>>();
	
	public void registerWebSocket(WebSocketInitializer<?> initializer)
	{
	    this.webSocketInitializers.put(initializer.getWebSocketPath(), initializer);
	}
	
	Map<String,WebSocketInitializer<?>> getWebSocketInitializers()
	{
	    return this.webSocketInitializers;
	}
	public RequestMethod[] getRequestHandlers()
	{
		return this.requestMethodMap.getRequestHandlers();
	}
	public RequestMethod getRequestHandler(String key)
	{
		return this.requestMethodMap.getRequestHandler(key);
	}

	
	private ContentReader findContentReader(String contentType, RequestMethod handler)
	{
		if ((contentType == null) || (contentType.length() == 0))
		{
			return null;
		}
		contentType=contentType.toLowerCase();
		contentType = org.nova.utils.Utils.split(contentType, ';')[0];

		ContentReader reader = handler.getContentReaders().get(contentType);
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

	private ContentWriter findContentWriter(String accept, RequestMethod handler)
	{
		Map<String, ContentWriter> map = handler.getContentWriters();
		if (accept != null)
		{
		    accept=accept.toLowerCase();
			List<ValueQ> list = ValueQ.sortDescending(accept);
			for (ValueQ item : list)
			{
				ContentWriter writer = map.get(item.value);
				if (writer != null)
				{
					return writer;
				}
			}
			for (ValueQ item : list)
			{
				String anySubMediaType=WsUtils.toAnySubMediaType(item.value);
				ContentWriter writer = map.get(anySubMediaType);
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

	private ContentDecoder getContentDecoder(String contentEncoding, RequestMethod handler) throws AbnormalException
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

	private ContentEncoder getContentEncoder(String acceptEncoding, RequestMethod handler) throws AbnormalException
	{
		if (acceptEncoding == null)
		{
			return this.identityContentEncoder;
		}
		List<ValueQ> list = ValueQ.sortDescending(acceptEncoding);
        ContentEncoder[] encoders = handler.getContentEncoders();
        ContentEncoder bestEncoder=null;
        double bestQ=Double.MIN_VALUE;
        for (ContentEncoder encoder:encoders)
        {
            for (ValueQ value:list)
            {
                if ((bestEncoder!=null)&&(bestQ>=value.q))
                {
                    break;
                }
                if (encoder.getCoding().equalsIgnoreCase(value.value))
                {
                    bestEncoder=encoder;
                    bestQ=value.q;
                    break;
                }
            }
        }
        if (bestEncoder==null)
        {
            bestEncoder=this.identityContentEncoder;
        }
		return bestEncoder;
	}

    final static boolean TESTING=false;
    
	public boolean handle(String URI,HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Throwable
	{
		try (Trace trace = new Trace(this.traceManager, "HttpServer.handle"))
		{
		    this.requestRateMeter.increment();
			String method = servletRequest.getMethod();
            boolean before=false;
            for (ServletHandler handler:this.frontServletHandlers)
            {
                before=handle(trace,handler,method, URI,servletRequest,servletResponse);
                if (before)
                {
                    return true;
                }
            }
			RequestMethodWithParameters requestMethodWithParameters = this.requestMethodMap.resolve(method, URI);
			if (requestMethodWithParameters != null)
			{
	            handle(trace, servletRequest, servletResponse, requestMethodWithParameters);
	            return true;
			}
			boolean after=false;
            for (ServletHandler handler:this.backServletHandlers)
			{
				after=handle(trace,handler,method, URI,servletRequest,servletResponse);
				if (after)
				{
				    return true;
				}
			}
			if (TESTING)
			{
				Debugging.log(method+" "+URI+": No Handler");
			}
			synchronized (this.lastRequestHandlerNotFoundLogEntries)
			{
				this.lastRequestHandlerNotFoundLogEntries.add(new RequestHandlerNotFoundLogEntry(trace,servletRequest));
			}
			return false;
		}
	}
    private boolean handle(Trace parent, ServletHandler handler,String method, String URI, HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        String key=method+" "+URI;
        Trace trace = new Trace(traceManager,parent,this.categoryPrefix+key);
        try
        {
            if (handler.handle(trace, request, response)==false)
            {
                return false;
            }
        }
        catch (Throwable e)
        {
            trace.close(e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return true;
        }
        finally
        {
            trace.close();
        }
            /*
            int status=response.getStatus();
            if ((status>=400)&&(status<500))
            {
                synchronized (this.lastRequestHandlerNotFoundLogEntries)
                {
                    this.lastRequestHandlerNotFoundLogEntries.add(new RequestHandlerNotFoundLogEntry(trace,request));
                }
            }
            */
        RequestLogEntry entry=new RequestLogEntry(trace,null,null,null,request,response);
        if (this.logRequestHandlersOnly==false)
        {
            if (handler.isLogLastRequestsInMemory())
            {
                synchronized (this.lastRequestsLogEntries)
                {
                    this.lastRequestsLogEntries.add(entry);
                }
                if (trace.getThrowable()!=null)
                {
                    synchronized (this.lastExceptionRequestsLogEntries)
                    {
                        this.lastExceptionRequestsLogEntries.add(entry);
                    }
                }
            }
        }
        ArrayList<Item> items=new ArrayList<>();
        if (handler.isLog())
        {
            items.add(new Item("remoteEndPoint",entry.getRemoteEndPoint()));
            items.add(new Item("queryString",entry.getQueryString()));
            items.add(new Item("statusCode",entry.statusCode));
            items.add(new Item("contentType",entry.getContentType()));
        }
        if ((handler.isLogRequestHeaders()&&entry.requestHeaders!=null))
        {
            if (entry.requestHeaders!=null)
            {
                items.add(new Item("requestHeaders",entry.getRequestHeaders()));
            }
        }
        if (handler.isLogResponseHeaders())
        {
            if (entry.responseHeaders!=null)
            {
                items.add(new Item("responseHeaders",entry.getResponseHeaders()));
            }
        }
        this.logger.log(parent,key,Logger.toArray(items));
        return true;
    }

    DecoderContext openDecoderContext(HttpServletRequest servletRequest,HttpServletResponse servletResponse,RequestMethod handler) throws AbnormalException, Throwable
    {
        if ("application/x-www-form-urlencoded".equalsIgnoreCase(servletRequest.getParameter("Content-Type"))==false)
        {
            return getContentDecoder(servletRequest.getHeader("Content-Encoding"), handler).open(servletRequest, servletResponse);
        }
        else 
        {
            return this.identityContentDecoder.open(servletRequest, servletResponse);
        }
    }
    
	private void handle(Trace parent, HttpServletRequest servletRequest, HttpServletResponse servletResponse, RequestMethodWithParameters requestMethodWithParameters) throws Throwable
	{
		long responseUncompressedContentSize=0;
		long requestUncompressedContentSize=0;
		long responseCompressedContentSize=0;
		long requestCompressedContentSize=0;
		String requestContentText=null;
		String responseContentText=null;
		RequestMethod requestMethod = requestMethodWithParameters.requestMethod();
		Trace trace = new Trace(traceManager,parent,this.categoryPrefix+ requestMethod.getKey());
		try
		{
		    if (requestMethod.isTest()&&(this.test==false))
		    {
		        servletResponse.setStatus(HttpStatus.FORBIDDEN_403);
		    }
		    else
		    {
                ContentEncoder contentEncoder = getContentEncoder(servletRequest.getHeader("Accept-Encoding"), requestMethod);
                try (EncoderContext encoderContext = contentEncoder.open(servletRequest, servletResponse))
                {
    		        try (DecoderContext decoderContext = openDecoderContext(servletRequest, servletResponse, requestMethod))
    		        {
                        FilterChain chain = new FilterChain(requestMethodWithParameters);
                        Context context = new Context(chain,decoderContext, encoderContext,requestMethodWithParameters, servletRequest, servletResponse);
                        try 
            			{
            				context.setContentReader(findContentReader(servletRequest.getContentType(), requestMethod));
            				context.setContentWriter(findContentWriter(servletRequest.getHeader("Accept"), requestMethod));
            
            				Response<?> response = chain.next(trace, context);
                            servletResponse=context.getHttpServletResponse();
                            
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
            						        jakarta.servlet.http.Cookie httpCookie=new jakarta.servlet.http.Cookie(cookie.getName(),cookie.getValue());
            						        httpCookie.setPath("/");
            						        servletResponse.addCookie(httpCookie);
            						    }
            						}
            						servletResponse.setStatus(response.getStatusCode());
            						ContentWriter writer = context.getContentWriter();
            						if (writer != null)
            						{ 
                                        if (servletResponse.getContentType()==null)
                                        {
                                            servletResponse.setContentType(writer.getMediaType());
                                        }
                                        Object content = response.getContent();
                                        writer.write(context, content);
            						}
            						else if (response.getContent() != null)
            						{
            							Class<?> returnType=requestMethod.getMethod().getReturnType();
            							if (returnType!=void.class)
            							{
            							    throw new AbnormalException(Abnormal.NO_WRITER);
            							}
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
                                    servletResponse.addCookie(new jakarta.servlet.http.Cookie(cookie.getName(),cookie.getValue()));
                                }
                            }
                            servletResponse.setStatus(e.statusCode);
            			}
                        finally
                        {
                            if (context.isCaptured()==false)
                            {
                                requestUncompressedContentSize=decoderContext.getUncompressedContentSize();
                                requestCompressedContentSize=decoderContext.getCompressedContentSize();
                                requestContentText=context.getRequestContentText();
                                responseContentText=context.getResponseContentText();
                            }
                            servletRequest=context.getHttpServletRequest();
                            servletResponse=context.getHttpServletResponse();
                        }
                    }
    		        catch (Throwable e)
    		        {
    		            if (this.test)
    		            {
    		                servletResponse.setHeader("Content-Type","text/html");
    		                byte[] content=Utils.toString(e).replace("\n", "<br>").getBytes();
    		                encoderContext.encode(servletResponse, content, 0, content.length);
    		            }
    		            servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
                        String key=requestMethod.getKey();
                        trace.close(new Exception(key,e));
    		        }
                    finally
                    {
                        encoderContext.close();
                        responseUncompressedContentSize=encoderContext.getUncompressedContentSize();
                        responseCompressedContentSize=encoderContext.getCompressedContentSize();
                    }
                }
		    }
		}
		catch (Throwable e)
		{
            trace.close(e);
            if (this.test)
            {
                servletResponse.setHeader("Content-Type","text/html");
                servletResponse.getOutputStream().print(Utils.toString(e));
                e.printStackTrace();
            }
            servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
		finally
		{
			trace.close();
			requestMethod.update(servletResponse.getStatus(), trace.getDurationNs(),requestUncompressedContentSize,responseUncompressedContentSize,requestCompressedContentSize,responseCompressedContentSize);
			RequestLogEntry entry=new RequestLogEntry(trace,requestMethod,requestContentText,responseContentText,servletRequest,servletResponse);
			if (requestMethod.isLogLastRequestsInMemory())
			{
			    requestMethod.log(entry);
                synchronized (this.lastRequestsLogEntries)
                {
                    this.lastRequestsLogEntries.add(entry);
                }
    			if (trace.getThrowable()!=null)
    			{
    				synchronized (this.lastExceptionRequestsLogEntries)
    				{
    					this.lastExceptionRequestsLogEntries.add(entry);
    				}
    			}
			}
            ArrayList<Item> items=new ArrayList<>();
            if (requestMethod.isLog())
            {
                items.add(new Item("remoteEndPoint",entry.remoteEndPoint));
                items.add(new Item("request",entry.request));
                items.add(new Item("statusCode",entry.statusCode));
                items.add(new Item("queryString",entry.getQueryString()));
                items.add(new Item("contentType",entry.getContentType()));
            }
            if ((requestMethod.isLogRequestHeaders()&&entry.requestHeaders!=null))
            {
                if (entry.requestHeaders!=null)
                {
                    items.add(new Item("requestHeaders",entry.requestHeaders));
                }
            }
            if (requestMethod.isLogRequestContent())
            {                
                if (entry.requestContentText!=null)
                {
                    items.add(new Item("requestContent",entry.requestContentText));
                }
            }
            if (requestMethod.isLogResponseHeaders())
            {
                if (entry.responseHeaders!=null)
                {
                    items.add(new Item("responseHeaders",entry.responseHeaders));
                }
            }
            if (requestMethod.isLogResponseContent())
            {
                if (entry.responseContentText!=null)
                {
                    items.add(new Item("responseContent",entry.responseContentText));
                }
            }
            this.logger.log(trace,requestMethod.getKey(),Logger.toArray(items));
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

	public void clearLastRequestLogEntries()
    {
        synchronized (this.lastRequestsLogEntries)
        {
            this.lastRequestsLogEntries.clear();
        }
    }
	
	public RequestHandlerNotFoundLogEntry[] getRequestHandlerNotFoundLogEntries()
	{
		synchronized (this.lastRequestHandlerNotFoundLogEntries)
		{
			List<RequestHandlerNotFoundLogEntry> list=this.lastRequestHandlerNotFoundLogEntries.getSnapshot();
            return list.toArray(new RequestHandlerNotFoundLogEntry[list.size()]);
		}
	}
	
    public void clearRequestHandlerNotFoundLogEntries()
    {
        synchronized (this.lastRequestHandlerNotFoundLogEntries)
        {
            this.lastRequestHandlerNotFoundLogEntries.clear();
        }
    }
    
	public RequestLogEntry[] getLastExceptionRequestLogEntries()
	{
		synchronized (this.lastExceptionRequestsLogEntries)
		{
            List<RequestLogEntry> list=this.lastExceptionRequestsLogEntries.getSnapshot();
            return list.toArray(new RequestLogEntry[list.size()]);
		}
	}

	public void clearLastExceptionRequestLogEntries()
    {
        synchronized (this.lastExceptionRequestsLogEntries)
        {
            this.lastExceptionRequestsLogEntries.clear();
        }
    }
	
	public RateMeter getRequestRateMeter()
	{
		return this.requestRateMeter;
	}
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
    static Object parseParameter(ParameterInfo parameterInfo,String value) throws Exception 
    {
        if (value==null)
        {
            if (parameterInfo.getDefaultValue()!=null)
            {
                return parameterInfo.getDefaultValue();
            }
            if (parameterInfo.isRequired())
            {
                throw new Exception("Request does not provide required value for parameter "+parameterInfo.getName());
            }
        }
        try
        {
            Class<?> type=parameterInfo.getType();
            if (type==String.class)
            {
                return value;
            }
            if (type==int.class)
            {
                if (value==null)
                {
                    return 0;//
                }
                value=value.trim();
                if (value.length()==0)
                {
                    if (parameterInfo.getDefaultValue()!=null)
                    {
                        return parameterInfo.getDefaultValue();
                    }
                }
                return Integer.parseInt(value);
            }
            if (type==Integer.class)
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    return null;
                }
                return Integer.parseInt(value);
            }
            if (type==long.class)
            {
                if (value==null)
                {
                    return 0L;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    if (parameterInfo.getDefaultValue()!=null)
                    {
                        return parameterInfo.getDefaultValue();
                    }
                }
                return Long.parseLong(value);
            }
            if (type==Long.class)
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    return null;
                }
                return Long.parseLong(value);
            }
            if (type==short.class)
            {
                if (value==null)
                {
                    return (short)0;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    if (parameterInfo.getDefaultValue()!=null)
                    {
                        return parameterInfo.getDefaultValue();
                    }
                }
                return Short.parseShort(value);
            }
            if (type==Short.class)
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    return null;
                }
                return Short.parseShort(value);
            }
            if (type==float.class)
            {
                if (value==null)
                {
                    return 0.0f;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    if (parameterInfo.getDefaultValue()!=null)
                    {
                        return parameterInfo.getDefaultValue();
                    }
                }
                return Float.parseFloat(value);
            }
            if (type==Float.class)
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    return null;
                }
                return Float.parseFloat(value);
            }
            if (type==double.class)
            {
                if (value==null)
                {
                    return 0.0;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    if (parameterInfo.getDefaultValue()!=null)
                    {
                        return parameterInfo.getDefaultValue();
                    }
                }
                return Double.parseDouble(value);
            }
            if (type==Double.class)
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    return null;
                }
                return Double.parseDouble(value);
            }
            if (type==boolean.class)
            {
                if (value==null)
                {
                    return false;
                }
                value=value.trim().toLowerCase();
                if (value.length()==0)
                {
                    if (parameterInfo.getDefaultValue()!=null)
                    {
                        return parameterInfo.getDefaultValue();
                    }
                }
                if ("on".equals(value))
                {
                    return true;
                }
                return "true".equals(value);
            }
            if (type==Boolean.class)
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim().toLowerCase();
                if (value.length()==0)
                {
                    return null;
                }
                if ("on".equals(value))
                {
                    return true;
                }
                return !("false".equals(value));
            }
            if (type.isEnum())
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    return null;
                }
                return Enum.valueOf((Class<Enum>)type, value);
            }
            if (type==BigDecimal.class)
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    return null;
                }
                return new BigDecimal(value);
            }
            return ObjectMapper.readObject(value, type);
        }
        catch (Throwable t)
        {
            throw new Exception("Error parsing parameter "+parameterInfo.getName()+", value="+value,t);
        }
//        throw new Exception("Unable to parse parameter "+parameterInfo.getName()+", value="+value);
    }
	
	
}
