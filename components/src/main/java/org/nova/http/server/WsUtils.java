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

import java.util.Enumeration;
import java.util.HashSet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class WsUtils
{
	public static String getRequestHeaders(HttpServletRequest request,RequestMethod requestMethod)
	{
		StringBuilder sb=new StringBuilder();
		Enumeration<String> names=request.getHeaderNames();
		while (names.hasMoreElements())
		{
			String header=names.nextElement();
            sb.append(header).append(":");
			Enumeration<String> values=request.getHeaders(header);
			
			boolean needSeperator=false;
			while (values.hasMoreElements())
			{
			    if (needSeperator)
			    {
			        sb.append(";");
			    }
			    else
			    {
			        needSeperator=true;
			    }
				String value=values.nextElement();
				sb.append(value);
			}
			sb.append("\r\n");
		}
		return sb.toString();
	}
    public static String getRequestParameters(HttpServletRequest request,RequestMethod requestMethod)
    {
        StringBuilder sb=new StringBuilder();
        Enumeration<String> names=request.getParameterNames();
        HashSet<String> hiddenParameters=requestMethod!=null?requestMethod.getHiddenParameters():null;
        while (names.hasMoreElements())
        {
            String name=names.nextElement();
            if (hiddenParameters!=null)
            {
                if (hiddenParameters.contains(name))
                {
                    sb.append(name).append("\r\n");
                    continue;
                }
            }
            sb.append(name).append(":");
            boolean needSeperator=false;
            for (String value:request.getParameterValues(name))
            {
                if (needSeperator)
                {
                    sb.append(";");
                }
                else
                {
                    needSeperator=true;
                }
                sb.append(value);
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }

	public static String getResponseHeaders(HttpServletResponse response)
	{
		StringBuilder sb=new StringBuilder();
		for (String header:response.getHeaderNames())
		{
            boolean needSeperator=false;
			sb.append(header).append(":");
			for (String value:response.getHeaders(header))
			{
                if (needSeperator)
                {
                    sb.append(";");
                }
                else
                {
                    needSeperator=true;
                }
				sb.append(value);
			}
			sb.append("\r\n");
		}
		return sb.toString();
	}
	
	public static String toAnySubMediaType(String mediaType)
	{
		int index=mediaType.indexOf('/');
		if (index<0)
		{
			return mediaType+"/*";
		}
		return mediaType.substring(0, index+1)+"*";
	}
	public static String removeParameterFromCoding(String coding)
	{
		int index=coding.indexOf(';');
		if (index<0)
		{
			return coding;
		}
		return coding.substring(0,index);
	}
	
	public static boolean isSubTypeAny(String mediaType)
	{
		if (mediaType.length()>1)
		{
			return "/*".equals(mediaType.substring(mediaType.length()-2));
		}
		return true;
	}
	public static boolean isURIDirectorySafe(String URI)
	{
	    if (URI.contains("/../"))
	    {
	        return false;
	    }
	    return true;
	}
		
}
