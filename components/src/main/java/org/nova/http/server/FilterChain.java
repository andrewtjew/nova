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
    private final RequestMethodWithParameters requestMethodWithParameters;
    Object[] parameters;
    int stateParameterIndex=-1;
    int contentParameterIndex=-1;
    static HashMap<String,Integer> cookieMaxAgeMap=new HashMap<String, Integer>();
    
    FilterChain(RequestMethodWithParameters requestMethodWithParameters)
	{
		this.requestMethodWithParameters=requestMethodWithParameters;
		this.parameters=null;
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
        RequestMethod requestMethod=this.requestMethodWithParameters.requestMethod();
        String[] pathParameters=this.requestMethodWithParameters.parameters();
        ParameterInfo[] parameterInfos=requestMethod.getParameterInfos();
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
                        parameters[i]=HttpServer.parseParameter(parameterInfo,cookie.getValue());
                    }
                    else
                    {
                        parameters[i]=HttpServer.parseParameter(parameterInfo,null);
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
                    parameters[i]=HttpServer.parseParameter(parameterInfo,request.getHeader(parameterInfo.getName()));
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
                    parameters[i]=HttpServer.parseParameter(parameterInfo,parameter);
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
                    parameters[i]=HttpServer.parseParameter(parameterInfo,parameter);
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
                            parameters[i]=HttpServer.parseParameter(parameterInfo,value);
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
                parameters[i]=HttpServer.parseParameter(parameterInfo,request.getParameter(parameterInfo.getName()));
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
    
    int filterIndex;
    
	public Response<?> next(Trace trace,Context context) throws Throwable
	{
		int index=this.filterIndex++;
		var bottomFilters=this.requestMethodWithParameters.requestMethod().getBottomFilters();
        var topFilters=this.requestMethodWithParameters.requestMethod().getTopFilters();
		if (index<bottomFilters.length)
		{
			return bottomFilters[index].executeNext(trace,context);
		}
		if (index==bottomFilters.length)
		{
	        decodeParameters(trace, context);
		}
		Response<?> response=null;
		if (index-bottomFilters.length<topFilters.length)
		{
			response=topFilters[index-bottomFilters.length].executeNext(trace,context);
		}
		else if (index==bottomFilters.length+topFilters.length)
		{
			response=invoke(context);
		}
		if (index==bottomFilters.length)
		{
			if (context.isCaptured()==false)
			{
				HttpServletResponse servletResponse=context.getHttpServletResponse();
				RequestMethod requestMethod=this.requestMethodWithParameters.requestMethod();
		        ParameterInfo[] parameterInfos=requestMethod.getParameterInfos();
				if (requestMethod.cookieParamCount>0)
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
	    RequestMethod requestMethod=this.requestMethodWithParameters.requestMethod();
        ParameterInfo[] parameterInfos=requestMethod.getParameterInfos();
		try
		{
		    Object object=requestMethod.getObject();
		    if (object==null)
		    {
		        ObjectBox box=context.getStateParameter();
		        if (box!=null)
		        {
    		        Object binding=box.get();
    		        if (binding!=null)
    		        {
    		            try
    		            {
        		            if (binding instanceof RemoteStateBinding)
        		            {
        		                object=((RemoteStateBinding)binding).getPageState(context);
        		            }
    		            }
    		            catch (Throwable t)
    		            {
                            throw new Exception("Unable to bind: handler="+requestMethod.getMethod().getDeclaringClass().getCanonicalName()+"."+requestMethod.getMethod().getName()+", URL="+requestMethod.getPath());
    		            }
    		        }
    		        if (object==null)
    		        {
    		            RequestMethod handler=context.getRequestMethod();
    		            throw new Exception("No state: handler="+handler.getMethod().getDeclaringClass().getCanonicalName()+"."+handler.getMethod().getName()+". Filter may be missing. Remote.js_postStatic may have been called instead of stateObject.js_postStatic."+", URL="+handler.getPath());
    		        }
		        }
		    }
			Object result=requestMethod.getMethod().invoke(object, parameters);
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
            StringBuilder sb=new StringBuilder("IllegalArguments for "+context.getRequestMethod().getKey());
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
