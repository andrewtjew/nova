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
package org.nova.http.client;

import org.apache.http.HttpResponse;

public class JSONResponse<TYPE>
{
	private final int statusCode;
    private final String text;
    private final TYPE content;
	private final HttpResponse response;
	private final Throwable exception;
	public JSONResponse(int statusCode,String text,TYPE content,Throwable exception,HttpResponse response) 
	{
		this.statusCode=statusCode;
		this.content=content;
		this.response=response;
		this.exception=exception;
		this.text=text;
	}
//    public JSONResponse(String text,TYPE content,HttpResponse response) 
//    {
//        this(200,text,content,response);
//    }
	public TYPE get() throws Exception
	{
		if (this.statusCode>=300)
		{
			throw new Exception("Invalid content. StatusCode="+statusCode);
		}
		return content;
	}
	public int getStatusCode()
	{
		return this.statusCode;
	}
	public HttpResponse getHttpResponse()
	{
	    return this.response;
	}
	public String getText()
	{
	    return text;
	}
}
