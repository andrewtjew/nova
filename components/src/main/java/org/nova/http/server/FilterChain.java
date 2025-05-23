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
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.core.ObjectBox;
import org.nova.html.remote.RemoteStateBinding;
import org.nova.http.server.annotations.CookieStateParam;
import org.nova.http.server.annotations.ParamName;
import org.nova.json.ObjectMapper;
import org.nova.tracing.Trace;

public class FilterChain
{
    private int filterIndex=0;
    private final RequestHandlerWithParameters requestHandlerWithParameters;
    private final Filter[] bottomFilters;
    private final Filter[] topFilters;
    RequestHandler requestHandler;
    Object[] parameters;
    int stateParameterIndex=-1;
    int contentParameterIndex=-1;
    static HashMap<String,Integer> cookieMaxAgeMap=new HashMap<String, Integer>();
    
    FilterChain(RequestHandlerWithParameters requestHandlerWithParameters)
	{
		this.requestHandlerWithParameters=requestHandlerWithParameters;
		this.bottomFilters=requestHandlerWithParameters.requestHandler.getBottomFilters();
		this.topFilters=this.requestHandlerWithParameters.requestHandler.getTopFilters();
		this.parameters=null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static Object parseParameter(ParameterInfo parameterInfo,String value) throws Exception 
	{
        if (value==null)
        {
            if (parameterInfo.getDefaultValue()!=null)
            {
                return parameterInfo.getDefaultValue();
            }
            if (parameterInfo.isRequired())
            {
                throw new Exception("Request does not provide required value for parameter "+parameterInfo.getName());
            }
        }
	    try
	    {
    		Class<?> type=parameterInfo.getType();
    		if (type==String.class)
    		{
    			return value;
    		}
    		if (type==int.class)
    		{
    		    if (value==null)
    		    {
    		        return 0;//
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
	
    void setStateParameter(Object state)
    {
        if (this.stateParameterIndex>=0)
        {
            this.parameters[this.stateParameterIndex]=state;
        }
    }
    void setContentParameter(Object state)
    {
        if (this.contentParameterIndex>=0)
        {
            this.parameters[this.contentParameterIndex]=state;
        }
    }
    
    Object getContentParameter()
    {
        if (this.contentParameterIndex>=0)
        {
            return this.parameters[this.contentParameterIndex];
        }
        return null;
    }

    ObjectBox getStateParameter()
    {
        if (this.stateParameterIndex>=0)
        {
            return new ObjectBox(this.parameters[this.stateParameterIndex]);
        }
        return null;
    }
	
    public void decodeParameters(Trace trace,Context context) throws Throwable
    {
        this.requestHandler=this.requestHandlerWithParameters.requestHandler;
        String[] pathParameters=this.requestHandlerWithParameters.parameters;
        ParameterInfo[] parameterInfos=requestHandler.getParameterInfos();
        this.parameters=new Object[parameterInfos.length];
        HttpServletRequest request=context.getHttpServletRequest();
        ContentReader reader=null;
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
                        content=reader.read(context,parameterInfo.getType());
                    }
                    else
                    {
                        DecoderContext decoderContext=context.getDecoderContext();
                        int value=decoderContext.getInputStream().read();
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
                    Cookie[] cookies=request.getCookies();
                    if (cookies!=null)
                    {
                        for (Cookie c:cookies)
                        {
                            if (parameterInfo.getName().equals(c.getName()))
                            {
                                cookie=c;
                                break;
                            }
                        }
                    }

                    if (parameterInfo.getType()==Cookie.class)
                    {
                        parameters[i]=cookie;
                    }
                    else if (cookie!=null)
                    {
                        parameters[i]=parseParameter(parameterInfo,cookie.getValue());
                    }
                    else
                    {
                        parameters[i]=parseParameter(parameterInfo,null);
                    }
                }
                catch (Throwable t)
                {
                    throw new AbnormalException(Abnormal.BAD_COOKIE,t);
                }
                break;
            case COOKIE_STATE:
                try
                {
                    Cookie cookie=null;
                    Cookie[] cookies=request.getCookies();
                    if (cookies!=null)
                    {
                        for (Cookie c:cookies)
                        {
                            if (parameterInfo.getName().equals(c.getName()))
                            {
                                cookie=c;
                                break;
                            }
                        }
                    }

                    String value=null;
                    if (cookie!=null)
                    {
                        value=cookie.getValue();
                        value=URLDecoder.decode(value,StandardCharsets.UTF_8);
                    }
                    else
                    {
                    	Object defaultValue=parameterInfo.getDefaultValue();
                    	if (defaultValue!=null)
                    	{
                    		value=defaultValue.toString();
                    	}
                    }
                    Object object=null;
                    try
                    {
                    	object=ObjectMapper.readObject(value, parameterInfo.getType());
                    }
                    catch (Throwable t)
                    {
                    }
                    if (object==null)
                    {
                        try
                        {
                        	object=ObjectMapper.readObject("{}", parameterInfo.getType());
                        }
                        catch (Throwable t)
                        {
                        }
                    }
                    this.parameters[i]=object;
                }
                catch (Throwable t)
                {
                    throw new AbnormalException(Abnormal.BAD_COOKIE,t);
                }
                break;
            case HEADER:
                try
                {
                    parameters[i]=parseParameter(parameterInfo,request.getHeader(parameterInfo.getName()));
                }
                catch (Throwable t)
                {
                    throw new AbnormalException(Abnormal.BAD_HEADER,t);
                }
                break;
            case PATH:
            {
                String parameter=null;
                try
                {
                    parameter=URLDecoder.decode(pathParameters[parameterInfo.getPathIndex()],StandardCharsets.UTF_8);
                }
                catch (Throwable t)
                {
                    try
                    {
                        parameter=URLDecoder.decode(pathParameters[parameterInfo.getPathIndex()],StandardCharsets.UTF_8);
                    }
                    catch (Throwable tt)
                    {
                        parameter=request.getParameter(parameterInfo.getName());
                    }
                }
                try
                {
                    parameters[i]=parseParameter(parameterInfo,parameter);
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
                    String parameter=request.getParameter(parameterInfo.getName());
                    parameters[i]=parseParameter(parameterInfo,parameter);
                }
                catch (Throwable t)
                {
                    throw new AbnormalException(Abnormal.BAD_QUERY,t);
                }
                break;
            case CONTEXT:
                this.contentParameterIndex=i;
                parameters[i]=context;
                break;
            case STATE:
                parameters[i]=context.getState();
                this.stateParameterIndex=i;
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
                            parameters[i]=parseParameter(parameterInfo,value);
                        }
                    }
                    
                }
                else
                {
                    parameters[i]=request.getParameter(parameterInfo.getName())!=null;
                }
            }
            break;
            case INTERNAL:
            {
                parameters[i]=parseParameter(parameterInfo,request.getParameter(parameterInfo.getName()));
            }
            default:
                break;
            }
        }
    }
	
    /* Stack 
     * 
     * invoke
     * TopFilterA
     * TopFilterB
     * decode params + set state cookies
     * BottomFilterC
     * BottomFilterD
     */
    
    
	public Response<?> next(Trace trace,Context context) throws Throwable
	{
		int index=this.filterIndex++;
		if (index<this.bottomFilters.length)
		{
			return this.bottomFilters[index].executeNext(trace,context);
		}
		if (index==this.bottomFilters.length)
		{
	        if (this.requestHandler==null)
	        {
	            decodeParameters(trace, context);
	        }
		}
		Response<?> response=null;
		if (index-this.bottomFilters.length<this.topFilters.length)
		{
			response=this.topFilters[index-this.bottomFilters.length].executeNext(trace,context);
		}
		else if (index==this.bottomFilters.length+this.topFilters.length)
		{
			response=invoke(context);
		}
		if (index==this.bottomFilters.length)
		{
			if (context.isCaptured()==false)
			{
				HttpServletResponse servletResponse=context.getHttpServletResponse();
		        ParameterInfo[] parameterInfos=this.requestHandler.getParameterInfos();
				if (requestHandler.cookieParamCount>0)
				{
					for (int i=0;i<parameterInfos.length;i++)
					{
						ParameterInfo info=parameterInfos[i];
						if (info.getAnnotation() instanceof CookieStateParam)
						{   
						    CookieStateParam cookieStateParam=(CookieStateParam)info.getAnnotation();
						    if (cookieStateParam.add())
						    {
    	                        String value=ObjectMapper.writeObjectToString(parameters[i]);
    	                        value=URLEncoder.encode(value,"UTF-8");
    	                        String name=info.getName();
    	                        Cookie cookie=new Cookie(name, value);

//    	                        Integer maxAge=cookieMaxAgeMap.get(info.getName());
//    	                        if (maxAge==null)
//    	                        {
//    	                            int cookieMaxAge=cookieStateParam.maxAge();
//    	                            if (cookieMaxAge>-1)
//    	                            {
//    	                                maxAge=cookieMaxAge;
//    	                                cookieMaxAgeMap.put(name, maxAge);
//    	                            }
//    	                        }
//    	                        if (maxAge!=null)
//                                {
//                                    cookie.setMaxAge(maxAge);
//                                }

    	                        Integer maxAge=cookieStateParam.maxAge();
    	                        if (maxAge<0)
    	                        {
                                    maxAge=cookieMaxAgeMap.get(info.getName());
    	                        }
                                if (maxAge!=null)
                                {
                                    cookie.setMaxAge(maxAge);
                                }

    	                        cookie.setPath(cookieStateParam.path());
    	                        servletResponse.addCookie(cookie);
						    }
						}
					}
				}
			}
		}
		return response;
	}
	
	Response<?> invoke(Context context) throws Throwable
	{
        ParameterInfo[] parameterInfos=this.requestHandler.getParameterInfos();
		try
		{
		    Object object=requestHandler.getObject();
		    if (object==null)
		    {
		        ObjectBox box=context.getStateParameter();
		        if (box!=null)
		        {
    		        Object binding=box.get();
    		        if (binding!=null)
    		        {
    		            if (binding instanceof RemoteStateBinding)
    		            {
    		                object=((RemoteStateBinding)binding).getRemoteCaller(context);
    		            }
    		        }
    		        if (object==null)
    		        {
    		            RequestHandler handler=context.getRequestHandler();
    		            throw new Exception("No state "+handler.getMethod().getDeclaringClass().getCanonicalName()+"."+handler.getMethod().getName()+". Filter may be missing. Remote.js_postStatic may have been called instead of stateObject.js_postStatic."+", URL="+handler.getPath());
    		        }
		        }
		    }
			Object result=requestHandler.getMethod().invoke(object, parameters);
			if (context.isCaptured()==false)
			{
				if (result==null)
				{
					return new Response<>(context.getHttpServletResponse().getStatus());
				}
				else if (result.getClass()==Response.class)
				{
					return (Response<?>)result;
				}
				return new Response<>(HttpStatus.OK_200,result);
			}
			return null;
		}
        catch (IllegalArgumentException e)
        {
            StringBuilder sb=new StringBuilder("IllegalArguments for "+context.getRequestHandler().getKey());
            for (int i=0;i<parameterInfos.length;i++)
            {
                ParameterInfo parameterInfo=parameterInfos[i];
                if (parameterInfo.getType().isPrimitive())
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
	}
}
