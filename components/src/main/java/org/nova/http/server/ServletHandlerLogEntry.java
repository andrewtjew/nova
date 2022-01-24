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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nova.tracing.Trace;

//public class ServletHandlerLogEntry
//{
//	final String requestHeaders;
//	final String URI;
//	final String method;
//	final String queryString;
//	final Trace trace;
//	final String remoteEndPoint;
//	final int statusCode;
//	final String responseHeaders;
//	public ServletHandlerLogEntry(Trace trace,HttpServletRequest request,HttpServletResponse response)
//	{
//		this.trace=trace;
//		this.requestHeaders=WsUtils.getRequestHeaders(request);
//		this.method=request.getMethod();
//		this.URI=request.getRequestURI();
//		this.queryString=request.getQueryString();
//		this.remoteEndPoint=request.getRemoteHost()+":"+request.getRemotePort();
//		this.responseHeaders=WsUtils.getResponseHeaders(response);
//		this.statusCode=response.getStatus();
//	}
//	public String getRequestHeaders()
//	{
//		return requestHeaders;
//	}
//	public String getURI()
//	{
//		return URI;
//	}
//	public String getMethod()
//	{
//		return method;
//	}
//	public String getQueryString()
//	{
//		return queryString;
//	}
//	public Trace getTrace()
//	{
//		return trace;
//	}
//	public String getRemoteEndPoint()
//	{
//		return remoteEndPoint;
//	}
//	public String getResponseHeaders()
//	{
//	    return this.responseHeaders;
//	}
//	public int getStatusCode()
//	{
//	    return this.getStatusCode();
//	}
//
//	
//}
