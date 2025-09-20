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

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;
import org.nova.utils.TypeUtils;

public class RequestHandlerNotFoundLogEntry
{
	final private String requestHeaders;
	final private String URI;
	final private String method;
	final private String queryString;
	final private Trace trace;
	final private String remoteEndPoint;
	final private String content;
	public RequestHandlerNotFoundLogEntry(Trace trace,HttpServletRequest request) throws Throwable
	{
		this.trace=trace;
		this.requestHeaders=WsUtils.getRequestHeaders(request,null);
		this.method=request.getMethod();
		this.URI=request.getRequestURI();
		this.queryString=request.getQueryString();
		this.remoteEndPoint=request.getRemoteHost()+":"+request.getRemotePort();
		String contentType=request.getContentType();
		if (TypeUtils.isNullOrSpace(contentType)==false)
		{
		    contentType=contentType.toLowerCase();
		    if (contentType.startsWith("text/")||contentType.endsWith("/json"))
		    {
		        this.content=FileUtils.readString(request.getInputStream());
		    }
		    else
		    {
		        this.content=null;
		    }
		}
		else 
		{
		    this.content=null;
        }
	}
	public String getRequestHeaders()
	{
		return requestHeaders;
	}
	public String getURI()
	{
		return URI;
	}
	public String getMethod()
	{
		return method;
	}
	public String getQueryString()
	{
		return queryString;
	}
	public Trace getTrace()
	{
		return trace;
	}
	public String getRemoteEndPoint()
	{
		return remoteEndPoint;
	}
	public String getContent()
	{
	    return this.content;
	}
}
