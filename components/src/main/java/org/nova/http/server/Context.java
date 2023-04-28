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

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.core.ObjectBox;
import org.nova.http.server.annotations.CookieStateParam;
import org.nova.http.server.annotations.PathParam;
import org.nova.http.server.annotations.QueryParam;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;

public class Context
{
	private RequestHandler requestHandler;
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	private ContentReader contentReader;
	private ContentWriter contentWriter;
	private Object state;
	private boolean captured=false;
	private String responseContentText;
	private String requestContentText;
	private DecoderContext decoderContext;
	private EncoderContext encoderContext;
	final private FilterChain filterChain;
	private HashMap<String,CookieState> cookieStates;
	
	Context(FilterChain filterChain,DecoderContext decoderContext,EncoderContext encoderContext,RequestHandler requestHandler,HttpServletRequest servletRequest,HttpServletResponse servletResponse)
	{
	    this.filterChain=filterChain;
		this.requestHandler=requestHandler;
		this.httpServletRequest=servletRequest;
		this.httpServletResponse=servletResponse;
		this.decoderContext=decoderContext;
		this.encoderContext=encoderContext;
		this.cookieStates=new HashMap<String, CookieState>();
	}

	public Response<?> next(Trace parent) throws Throwable
	{
	    return this.filterChain.next(parent, this);
	}
	public FilterChain getFilterChain()
	{
	    return this.filterChain;
	}
	public DecoderContext getDecoderContext()
	{
	    return this.decoderContext;
	}
	public void setDecoderContext(DecoderContext decoderContext)
	{
	    this.decoderContext=decoderContext;
	}
	public RequestHandler getRequestHandler()
	{
		return requestHandler;
	}
	public void setRequestHandler(RequestHandler requestHandler)
	{
		this.requestHandler = requestHandler;
	}
	public HttpServletRequest getHttpServletRequest()
	{
		return httpServletRequest;
	}
	public void setHttpServletRequest(HttpServletRequest httpServletRequest)
	{
		this.httpServletRequest = httpServletRequest;
	}
	public HttpServletResponse getHttpServletResponse()
	{
		return httpServletResponse;
	}
	public void setHttpServletResponse(HttpServletResponse httpServletResponse)
	{
		this.httpServletResponse = httpServletResponse;
	}
	public ContentReader getContentReader()
	{
		return contentReader;
	}
	public void setContentReader(ContentReader contentReader)
	{
		this.contentReader = contentReader;
	}
	public ContentWriter getContentWriter()
	{
		return contentWriter;
	}
	public void setContentWriter(ContentWriter contentWriter)
	{
		this.contentWriter = contentWriter;
	}
	public void setCookieState(CookieState cookieState)
	{
		this.cookieStates.put(cookieState.cookieStateParam.value(), cookieState);
	}
//	public Map<String,CookieState> getCookieStates()
//	{
//		return this.cookieStates;
//	}
	public ObjectBox getCookieState(String name)
	{
		ParameterInfo[] infos=this.requestHandler.getParameterInfos();
		for (int i=0;i<infos.length;i++)
		{
			ParameterInfo info=infos[i];
			if ((info.getAnnotation() instanceof CookieStateParam)&&(info.getName().equals(name)))
			{
				return new ObjectBox(this.filterChain.parameters[i]);
			}
		}
		return null;
	}
	
	public ObjectBox getQueryParameter(String name)
	{
		ParameterInfo[] infos=this.requestHandler.getParameterInfos();
		for (int i=0;i<infos.length;i++)
		{
			ParameterInfo info=infos[i];
			if ((info.getAnnotation() instanceof QueryParam)&&(info.getName().equals(name)))
			{
				return new ObjectBox(this.filterChain.parameters[i]);
			}
		}
		return null;
	}
	public ObjectBox getPathParameter(String name)
	{
		ParameterInfo[] infos=this.requestHandler.getParameterInfos();
		for (int i=0;i<infos.length;i++)
		{
			ParameterInfo info=infos[i];
			if ((info.getAnnotation() instanceof PathParam)&&(info.getName().equals(name)))
			{
				return new ObjectBox(this.filterChain.parameters[i]);
			}
		}
		return null;
	}
	
	public Object getState()
	{
		return state;
	}
    public void setState(Object state)
    {
        this.state = state;
    }

    public void setStateParameter(Object state)
    {
        this.filterChain.setStateParameter(state);
    }
    public void setContentParameter(Object content)
    {
        this.filterChain.setContentParameter(content);
    }
    
    public ObjectBox getStateParameter()
    {
        return this.filterChain.getStateParameter();
    }
    public Object getContentParameter()
    {
        return this.filterChain.getContentParameter();
    }
	public String getResponseContentText()
	{
		return responseContentText;
	}
//	public void setResponseContentText(String responseContentText)
//	{
//		this.responseContentText=responseContentText;
//	}
	private boolean requestContentTextValid=false;
	
	public void writeContent(String text) throws Throwable
	{
	    writeContent(text,StandardCharsets.UTF_8);
	}
	public void writeContent(String text,Charset charset) throws Throwable
	{
	    if (this.responseContentText==null)
	    {
	        this.responseContentText=text;
	    }
	    else
	    {
	        this.responseContentText+=text;
	    }
        writeContent(text.getBytes(charset));
	}
    public OutputStream getOutputStream() throws Throwable
    {
        return this.encoderContext.getOutputStream(this.httpServletResponse);
    }

    public void writeContent(byte[] content) throws Throwable
    {
        if (content!=null)
        {
            writeContent(content,0,content.length);
        }
    }
    public void writeContent(byte[] content,int offset,int length) throws Throwable
    {
        this.encoderContext.encode(this.httpServletResponse, content,offset,length);
    }
	
//    public String readDecodedRequestContentText() throws Throwable
//    {
//        return readDecodedContentText(StandardCharsets.UTF_8);
//    }
    public String readDecodedRequestContentText() throws Throwable
    {
    	return readDecodedRequestContentText(StandardCharsets.UTF_8);
    }
    public String readDecodedRequestContentText(Charset charset) throws Throwable
    {
        if (this.requestContentTextValid==false)
        {
            InputStream inputStream=this.decoderContext.getInputStream();
            this.requestContentText=FileUtils.readString(inputStream, charset);
            this.requestContentTextValid=true;
        }
        return this.requestContentText;
    }
    public byte[] readDecodedContent() throws Throwable
    {
        InputStream inputStream=this.decoderContext.getInputStream();
        int bufferSize=this.httpServletRequest.getContentLength();
        if (bufferSize<=0)
        {
            bufferSize=4096;
        }
        return FileUtils.readBytes(inputStream, bufferSize);
    }
    String getRequestContentText() throws Throwable
    {
        return this.requestContentText;
    }
	public void setRequestContentText(String requestContentText)
	{
		this.requestContentText=requestContentText;
        this.requestContentTextValid=true;
	}
	public boolean isCaptured()
	{
		return captured;
	}
    public void setCaptured(boolean captured)
    {
        this.captured = captured;
    }
	public void capture()
	{
		this.captured = true;
	}
    public void seeOther(String url)
    {
        this.httpServletResponse.setStatus(HttpStatus.SEE_OTHER_303);
        this.httpServletResponse.setHeader("Location",url);
    }
    public void movedPermanently(String url)
    {
        this.httpServletResponse.setStatus(HttpStatus.MOVED_PERMANENTLY_301);
        this.httpServletResponse.setHeader("Location",url);
    }
    public void temporaryRedirect(String url)
    {
        this.httpServletResponse.setStatus(HttpStatus.TEMPORARY_REDIRECT_307);
        this.httpServletResponse.setHeader("Location",url);
    }
}
	
