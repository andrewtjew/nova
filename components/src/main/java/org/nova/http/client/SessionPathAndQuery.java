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
import org.nova.html.ext.JsObject;
import org.nova.json.ObjectMapper;
import org.nova.security.QuerySecurity;

//public class SessionPathAndQuery
//{
//    final protected String path;
//    final protected ArrayList<NameString> parameters;
//    final private QuerySecurity security;    
//    public SessionPathAndQuery(QuerySecurity security,String path) throws UnsupportedEncodingException
//    {
//        this.security=security;
//        this.path=path;
//        this.parameters=new ArrayList<NameString>();
//    }
//	public SessionPathAndQuery addQuery(String key,Object value) throws Exception
//	{
//	    if (value==null)
//	    {
//	        return this;
//	    }
//	    parameters.add(new NameString(key,URLEncoder.encode(value.toString(), "UTF-8")));
////        parameters.add(new NameString(key,value.toString()));
//	    return this;
//	}
//    public SessionPathAndQuery addQuery(String key,long value) throws Exception
//    {
//        parameters.add(new NameString(key,Long.toString(value)));
//        return this;
//    }
//    public SessionPathAndQuery addQuery(String key,int value) throws Exception
//    {
//        parameters.add(new NameString(key,Integer.toString(value)));
//        return this;
//    }
//    public SessionPathAndQuery addQuery(String key,short value) throws Exception
//    {
//        parameters.add(new NameString(key,Short.toString(value)));
//        return this;
//    }
//    public SessionPathAndQuery addJSONQuery(String key,Object value) throws Throwable
//    {
//        return addQuery(key,ObjectMapper.writeObjectToString(value));
//    }
//    @Override
//    public String toString()
//    {
//        try
//        {
//            return path+security.encodeParameters(this.parameters);
//        }
//        catch (Throwable t)
//        {
//            return this.path;
//        }
//    }
//}

public class SessionPathAndQuery extends PathAndQuery
{
    final private QuerySecurity security;

    public SessionPathAndQuery(QuerySecurity security, String path) throws Throwable 
    {
        super(path);
        this.security = security;
    }

    @Override
    public String toString()
    {
        String pathAndQuery=super.toString();
        try
        {
            int index=pathAndQuery.indexOf('?');
            if (index<0)
            {
                return pathAndQuery;
            }
            String path=pathAndQuery.substring(0,index+1);
            String query=pathAndQuery.substring(index+1);
            return path+security.sign(query);
        }
        catch (Throwable t)
        {
            return pathAndQuery;
        }
    }
}