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

import java.util.ArrayList;

import jakarta.servlet.http.Cookie;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.http.Header;

//The class is made final to force http method handlers to specify the parametized content type which is needed to map to the proper content writer. 
public final class Response<CONTENT>
{
	final private CONTENT content;
	final private int statusCode;
    ArrayList<Header> headers;
    ArrayList<Cookie> cookies;

	public Response(int statusCode,CONTENT content)
	{
		this.statusCode=statusCode;
		this.content=content;
	}
	public Response()
	{
		this(HttpStatus.OK_200,null);
	}

	public Response(int statusCode)
	{
		this(statusCode,null);
	}
	public Response(CONTENT content)
	{
		this(HttpStatus.OK_200,content);
	}

	public CONTENT getContent()
	{
		return content;
	}

	public int getStatusCode()
	{
		return statusCode;
	}
    public void addHeader(String name,String value)
    {
        addHeader(new Header(name,value));
    }
    public void addHeader(Header header)
    {
        if (this.headers==null)
        {
            this.headers=new ArrayList<>();
        }
        this.headers.add(header);
    }
    public void addCookie(String name,String value)
    {
        if (this.cookies==null)
        {
            this.cookies=new ArrayList<>();
        }
        this.cookies.add(new Cookie(name,value));
    }
    public void addCookie(Cookie cookie)
    {
        if (this.cookies==null)
        {
            this.cookies=new ArrayList<>();
        }
        this.cookies.add(cookie);
    }
	
//    public static Response<Void> movedPermanently(String url)
//    {
//        Response<Void> response=new Response<Void>(HttpStatus.MOVED_PERMANENTLY_301);
//        response.addHeader("Location",url);
//        return response;
//    }
//    public static Response<Void> movedTemporarily(String url)
//    {
//        Response<Void> response=new Response<Void>(HttpStatus.MOVED_TEMPORARILY_302);
//        response.addHeader("Location",url);
//        return response;
//    }
//    public static Response<Element> seeOther(String url)
//    {
//        Response<Element> response=new Response<Element>(HttpStatus.SEE_OTHER_303);
//        response.addHeader("Location",url);
//        return response;
//    }

//    public static Response<Element> temporaryRedirect(String url)
//    {
//        Response<Element> response=new Response<Element>(HttpStatus.TEMPORARY_REDIRECT_307);
//        response.addHeader("Location",url);
//        return response;
//    }
//
//    public static Response<Element> permanentRedirect(String url)
//    {
//        Response<Element> response=new Response<Element>(HttpStatus.PERMANENT_REDIRECT_308);
//        response.addHeader("Location",url);
//        return response;
//    }


}
