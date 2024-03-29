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
import java.util.ArrayList;

import org.nova.core.NameString;
import org.nova.json.ObjectMapper;

public abstract class SecurePathAndQuery
{
    final protected String path;
    final protected ArrayList<NameString> parameters;
    
    public SecurePathAndQuery(String path) throws UnsupportedEncodingException
    {
        this.path=path;
        this.parameters=new ArrayList<NameString>();
    }
	public SecurePathAndQuery addQuery(String key,Object value) throws Exception
	{
	    if (value==null)
	    {
	        return this;
	    }
	    parameters.add(new NameString(key,URLEncoder.encode(value.toString(), "UTF-8")));
//        parameters.add(new NameString(key,value.toString()));
	    return this;
	}
    public SecurePathAndQuery addQuery(String key,long value) throws Exception
    {
        parameters.add(new NameString(key,Long.toString(value)));
        return this;
    }
    public SecurePathAndQuery addQuery(String key,int value) throws Exception
    {
        parameters.add(new NameString(key,Integer.toString(value)));
        return this;
    }
    public SecurePathAndQuery addQuery(String key,short value) throws Exception
    {
        parameters.add(new NameString(key,Short.toString(value)));
        return this;
    }
    public SecurePathAndQuery addJSONQuery(String key,Object value) throws Throwable
    {
        return addQuery(key,ObjectMapper.writeObjectToString(value));
    }
	abstract public String toString();
}
