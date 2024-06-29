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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.nova.html.ext.JsObject;
import org.nova.html.remote.RemoteStateContent;
import org.nova.json.ObjectMapper;

public class PathAndQuery
{
	private StringBuilder sb;
	private char separator; 
    
    public PathAndQuery(String path,char startSeparator) throws Throwable
    {
//        this.sb=path!=null?new StringBuilder(URLEncoder.encode(path,"UTF-8")):new StringBuilder();
        this.sb=path!=null?new StringBuilder(path):new StringBuilder();
        this.separator=startSeparator;
    }
    public PathAndQuery(RemoteStateContent<?> content,String path) throws Throwable
    {
        this(path,path.contains("?")?'&':'?');
        addQuery(content.getRemoteStateBinding().getStateKey(),content.id());
    }
    public PathAndQuery(String path) throws Throwable
    {
        this(path,path.contains("?")?'&':'?');
    }
    public PathAndQuery() throws Throwable
    {
        this(null);
    }
    public PathAndQuery addSegment(String segment) throws Exception
    {
        if (this.separator!='?')
        {
            throw new Exception();
        }
        this.sb.append(segment);
        return this;
    }

    public PathAndQuery addQuery(String key,Object value) throws Exception
	{
	    if (value==null)
	    {
	        return this;
	    }
        if (this.separator=='#')
        {
            throw new Exception();
        }
		this.sb.append(this.separator);
		this.separator='&';
		this.sb.append(key).append('=');
		this.sb.append(URLEncoder.encode(value.toString(), "UTF-8"));
		return this;
	}
    public PathAndQuery addJSONQuery(String key,Object value) throws Throwable
    {
        return addQuery(key,ObjectMapper.writeObjectToString(value));
    }
    public PathAndQuery addQuery(String key,String value) throws Exception
    {
        if (value==null)
        {
            return this;
        }
        if (this.separator=='#')
        {
            throw new Exception();
        }
        this.sb.append(this.separator);
        this.separator='&';
        this.sb.append(key).append('=').append(URLEncoder.encode(value, "UTF-8"));
        return this;
    }
    public PathAndQuery addQuery(String key,long value) throws Exception
    {
        if (this.separator=='#')
        {
            throw new Exception();
        }
        this.sb.append(this.separator);
        this.separator='&';
        this.sb.append(key).append('=').append(value);
        return this;
    }
	public PathAndQuery addQuery(String key,int value) throws Exception
	{
        if (this.separator=='#')
        {
            throw new Exception();
        }
		this.sb.append(this.separator);
		this.separator='&';
		this.sb.append(key).append('=').append(value);
		return this;
	}
	public PathAndQuery addQuery(String key,short value) throws Exception
	{
        if (this.separator=='#')
        {
            throw new Exception();
        }
		this.sb.append(this.separator);
		this.separator='&';
		this.sb.append(key).append('=').append(value);
		return this;
	}
    public PathAndQuery addFragment(String fragment) throws Exception
    {
        if (this.separator=='#')
        {
            throw new Exception();
        }
        this.separator='#';
        this.sb.append(this.separator).append(fragment);
        return this;
    }
    public boolean containQueries()
    {
        return this.separator=='&';
    }
	public String toString()
	{
		return this.sb.toString();
	}
	public char getSeparator()
	{
	    return this.separator;
	}
}
