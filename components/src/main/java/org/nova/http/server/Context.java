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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;
import org.nova.utils.TypeUtils;

public class Context
{
	private RequestHandler requestHandler;
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	private ContentReader<?> contentReader;
	private ContentWriter<?> contentWriter;
	private Object state;
	private boolean captured=false;
	
	private String responseContentText;
	private String requestContentText;
	private DecoderContext decoderContext;
	private EncoderContext encoderContext;
	final private FilterChain filterChain;
	
	Context(FilterChain filterChain,DecoderContext decoderContext,EncoderContext encoderContext,RequestHandler requestHandler,HttpServletRequest servletRequest,HttpServletResponse servletResponse)
	{
	    this.filterChain=filterChain;
		this.requestHandler=requestHandler;
		this.httpServletRequest=servletRequest;
		this.httpServletResponse=servletResponse;
		this.decoderContext=decoderContext;
		this.encoderContext=encoderContext;
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
	public ContentReader<?> getContentReader()
	{
		return contentReader;
	}
	public void setContentReader(ContentReader<?> contentReader)
	{
		this.contentReader = contentReader;
	}
	public ContentWriter<?> getContentWriter()
	{
		return contentWriter;
	}
	public void setContentWriter(ContentWriter<?> contentWriter)
	{
		this.contentWriter = contentWriter;
	}
	public Object getState()
	{
		return state;
	}
	public void setState(Object state)
	{
		this.state = state;
	}
	public String getResponseContentText()
	{
		return responseContentText;
	}
	public void setResponseContentText(String responseContentText)
	{
		this.responseContentText=responseContentText;
	}
	private boolean requestContentTextValid=false;
	
	public void writeEncodedResponseContentText(String text) throws Throwable
	{
	    writeEncodedContentText(text,StandardCharsets.UTF_8);
	}
	public void writeEncodedContentText(String text,Charset charset) throws Throwable
	{
        OutputStream outputStream=this.encoderContext.getOutputStream();
        outputStream.write(text.getBytes(charset));
	    if (this.responseContentText==null)
	    {
	        this.responseContentText=text;
	    }
	    else
	    {
	        this.requestContentText+=text;
	    }
	}

    public void writeEncodedContent(byte[] content) throws Throwable
    {
        OutputStream outputStream=this.encoderContext.getOutputStream();
        outputStream.write(content);
    }
    public void writeEncodedContent(byte[] content,int offset,int length) throws Throwable
    {
        OutputStream outputStream=this.encoderContext.getOutputStream();
        outputStream.write(content,offset,length);
    }
	
    public String readDecodedRequestContentText() throws Throwable
    {
        return readDecodedContentText(StandardCharsets.UTF_8);
    }
    public String readDecodedContentText(Charset charset) throws Throwable
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
}
	
