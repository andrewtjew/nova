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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.http.server.annotations.ParamName;
import org.nova.json.ObjectMapper;
import org.nova.tracing.Trace;

public class FilterChain
{
	private int filterIndex=0;
	private final RequestHandlerWithParameters methodResult;
	private final Filter[] filters;
	private final DecoderContext decoderContext;
	FilterChain(RequestHandlerWithParameters methodResult,DecoderContext decoderContext)
	{
		this.methodResult=methodResult;
		this.filters=methodResult.requestHandler.getFilters();
		this.decoderContext=decoderContext;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object buildParameter(ParameterInfo parameterInfo,String value) throws Exception 
	{
	    try
	    {
    		if (value==null)
    		{
    			if (parameterInfo.getDefaultValue()!=null)
    			{
    				return parameterInfo.getDefaultValue();
    			}
    		}
    		Class<?> type=parameterInfo.getType();
    		if (type==String.class)
    		{
    			return value;
    		}
    		if (type==int.class)
    		{
    		    if (value==null)
    		    {
    		        return 0;
    		    }
                value=value.trim();
                if (value.length()==0)
                {
                    if (parameterInfo.getDefaultValue()!=null)
                    {
                        return parameterInfo.getDefaultValue();
                    }
                }
    			return Integer.parseInt(value);
    		}
            if (type==Integer.class)
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    return null;
                }
                return Integer.parseInt(value);
            }
    		if (type==long.class)
    		{
                if (value==null)
                {
                    return 0L;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    if (parameterInfo.getDefaultValue()!=null)
                    {
                        return parameterInfo.getDefaultValue();
                    }
                }
    			return Long.parseLong(value);
    		}
            if (type==Long.class)
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    return null;
                }
                return Long.parseLong(value);
            }
    		if (type==short.class)
    		{
                if (value==null)
                {
                    return (short)0;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    if (parameterInfo.getDefaultValue()!=null)
                    {
                        return parameterInfo.getDefaultValue();
                    }
                }
    			return Short.parseShort(value);
    		}
            if (type==Short.class)
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    return null;
                }
                return Short.parseShort(value);
            }
    		if (type==float.class)
    		{
                if (value==null)
                {
                    return 0.0f;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    if (parameterInfo.getDefaultValue()!=null)
                    {
                        return parameterInfo.getDefaultValue();
                    }
                }
    			return Float.parseFloat(value);
    		}
            if (type==Float.class)
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    return null;
                }
                return Float.parseFloat(value);
            }
    		if (type==double.class)
    		{
                if (value==null)
                {
                    return 0.0;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    if (parameterInfo.getDefaultValue()!=null)
                    {
                        return parameterInfo.getDefaultValue();
                    }
                }
    			return Double.parseDouble(value);
    		}
            if (type==Double.class)
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    return null;
                }
                return Double.parseDouble(value);
            }
    		if (type==boolean.class)
    		{
                if (value==null)
                {
                    return false;
                }
                value=value.trim().toLowerCase();
                if (value.length()==0)
                {
                    if (parameterInfo.getDefaultValue()!=null)
                    {
                        return parameterInfo.getDefaultValue();
                    }
                }
                if ("on".equals(value))
                {
                    return true;
                }
    		    return "true".equals(value);
    		}
            if (type==Boolean.class)
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim().toLowerCase();
                if (value.length()==0)
                {
                    return null;
                }
                if ("on".equals(value))
                {
                    return true;
                }
                return !("false".equals(value));
            }
            if (type.isEnum())
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    return null;
                }
                return Enum.valueOf((Class<Enum>)type, value);
            }
            if (type==BigDecimal.class)
            {
                if (value==null)
                {
                    return null;
                }
                value=value.trim();
                if (value.length()==0)
                {
                    return null;
                }
                return new BigDecimal(value);
            }
            return ObjectMapper.readObject(value, type);
	    }
	    catch (Throwable t)
	    {
            throw new Exception("Error parsing parameter "+parameterInfo.getName()+", value="+value,t);
	    }
//        throw new Exception("Unable to parse parameter "+parameterInfo.getName()+", value="+value);
	}

    RequestHandler requestHandler;
    Object[] parameters;

    public void decodeParameters(Trace trace,Context context) throws Throwable
    {
        if (this.requestHandler!=null)
        {
            return; 
        }
        this.requestHandler=this.methodResult.requestHandler;
        String[] pathParameters=this.methodResult.parameters;
        ParameterInfo[] parameterInfos=requestHandler.getParameterInfos();
        this.parameters=new Object[parameterInfos.length];
        HttpServletRequest request=context.getHttpServletRequest();
        
        ContentReader<?> reader=null;
        Object content=null;
        
        for (int i=0;i<parameterInfos.length;i++)
        {
            ParameterInfo parameterInfo=parameterInfos[i];
            switch (parameterInfo.getSource())
            {
            case CONTENT:
                if (reader==null)
                {
                    reader=context.getContentReader();
                    if (reader!=null)
                    {
                        content=reader.read(context,this.decoderContext.getContentLength(),this.decoderContext.getInputStream(),parameterInfo.getType());
                    }
                    else
                    {
                        int value=this.decoderContext.getInputStream().read();
                        if (value==-1)
                        {
                            content=null;
                        }
                        else
                        {
                            throw new AbnormalException(Abnormal.NO_READER);
                        }
                    }
                }
                parameters[i]=content;
                break;
            case COOKIE:
                try
                {
                    Cookie cookie=null;
                    for (Cookie c:request.getCookies())
                    {
                        if (parameterInfo.getName().equals(c.getName()))
                        {
                            cookie=c;
                            break;
                        }
                    }
                    if (parameterInfo.getType()==Cookie.class)
                    {
                        parameters[i]=cookie;
                    }
                    else if (cookie!=null)
                    {
                        parameters[i]=buildParameter(parameterInfo,cookie.getValue());
                    }
                    else
                    {
                        parameters[i]=buildParameter(parameterInfo,null);
                    }
                }
                catch (Throwable t)
                {
                    throw new AbnormalException(Abnormal.BAD_COOKIE,t);
                }
                break;
            case HEADER:
                try
                {
                    parameters[i]=buildParameter(parameterInfo,request.getHeader(parameterInfo.getName()));
                }
                catch (Throwable t)
                {
                    throw new AbnormalException(Abnormal.BAD_HEADER,t);
                }
                break;
            case PATH:
            {
                String name=null;
                try
                {
                    name=URLDecoder.decode(pathParameters[parameterInfo.getPathIndex()],StandardCharsets.UTF_8);
                }
                catch (Throwable t)
                {
                    try
                    {
                        name=URLDecoder.decode(pathParameters[parameterInfo.getPathIndex()]);
                    }
                    catch (Throwable tt)
                    {
                        name=request.getParameter(parameterInfo.getName());
                    }
                }
                try
                {
                    parameters[i]=buildParameter(parameterInfo,name);
                }
                catch (Throwable t)
                {
                    throw new AbnormalException(Abnormal.BAD_PATH,t);
                }
                break;
            }
            case QUERY:
                try
                {
                    parameters[i]=buildParameter(parameterInfo,request.getParameter(parameterInfo.getName()));
                }
                catch (Throwable t)
                {
                    throw new AbnormalException(Abnormal.BAD_QUERY,t);
                }
                break;
            case CONTEXT:
                parameters[i]=context;
                break;
            case STATE:
                parameters[i]=context.getState();
                break;
            case TRACE:
                parameters[i]=trace;
                break;
            case QUERIES:
                parameters[i]=new Queries(request);
                break;
            case NAME:
            {
                ParamName paramName=(ParamName)parameterInfo.getAnnotation();
                if (paramName.startsWith())
                {
                    Enumeration<String> enumeration=request.getParameterNames();
                    while (enumeration.hasMoreElements())
                    {
                        String parameterName=enumeration.nextElement();
                        if (parameterName.startsWith(paramName.value()))
                        {
                            String value=parameterName.substring(paramName.value().length());
                            parameters[i]=buildParameter(parameterInfo,value);
                        }
                    }
                    
                }
                else
                {
                    parameters[i]=request.getParameter(parameterInfo.getName())!=null;
                }
                break;
            }
            default:
                break;
            }
        }
    }
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Response<?> next(Trace trace,Context context) throws Throwable
	{
		if (this.filterIndex<this.filters.length)
		{
			return filters[this.filterIndex++].executeNext(trace,context,this);
		}

		decodeParameters(trace, context);
        ParameterInfo[] parameterInfos=this.requestHandler.getParameterInfos();
		try
		{
			Object result=requestHandler.getMethod().invoke(requestHandler.getObject(), parameters);
			if (context.isCaptured()==false)
			{
				if (result==null)
				{
					return new Response(context.getHttpServletResponse().getStatus());
				}
				else if (result.getClass()==Response.class)
				{
					return (Response<?>)result;
				}
				return new Response(HttpStatus.OK_200,result);
			}
		}
        catch (IllegalArgumentException e)
        {
            StringBuilder sb=new StringBuilder("IllegalArguments for "+context.getRequestHandler().getKey());
            for (int i=0;i<parameterInfos.length;i++)
            {
                ParameterInfo parameterInfo=parameterInfos[i];
                if (parameterInfo.getParameterType().isPrimitive())
                { 
                    if (parameters[i]==null)
                    {
                        sb.append(", missing "+parameterInfo.getName());
                    }
                }
            }
            Exception exception=new Exception(sb.toString(),e);
            exception.printStackTrace();
            throw exception;
        } 
		catch (IllegalAccessException e)
		{
			throw e;
		}
		catch (InvocationTargetException e)
		{
			throw e.getTargetException();
		}
        catch (Throwable e)
        {
            throw e;
        }
		return null;
	}
}
