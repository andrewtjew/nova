package org.nova.http.server;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.CookieStateParam;
import org.nova.http.server.annotations.ParamName;
import org.nova.json.ObjectMapper;
import org.nova.tracing.Trace;

public class RequestMethod
    {
        final private Parameters parameters;
        public RequestMethod(Method method,ContentReaders readers) throws Exception
        {
            this.parameters=new Parameters(method, readers);
        }
        public ParsedParameters decodeParameters(Trace trace,Context context,String[] pathParameters) throws Throwable
        {
//            String[] pathParameters=this.methodResult.parameters;
//            ParameterInfo[] parameterInfos=requestHandler.getParameterInfos();
            int contextParameterIndex=-1;
            int stateParameterIndex=-1;
            ParameterInfo[] parameterInfos=this.parameters.parameterInfos;
            Object[] parameters=new Object[parameterInfos.length];
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
                            parameters[i]=FilterChain.parseParameter(parameterInfo,cookie.getValue());
                        }
                        else
                        {
                            parameters[i]=FilterChain.parseParameter(parameterInfo,null);
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
                        CookieStateParam cookieStateParam=(CookieStateParam)parameterInfo.getAnnotation();
                        CookieState cookieState=new CookieState(cookieStateParam,parameterInfo,object);
                        parameters[i]=object;
                        context.setCookieState(cookieState);
                    }
                    catch (Throwable t)
                    {
                        throw new AbnormalException(Abnormal.BAD_COOKIE,t);
                    }
                    break;
                case HEADER:
                    try
                    {
                        parameters[i]=FilterChain.parseParameter(parameterInfo,request.getHeader(parameterInfo.getName()));
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
                            parameter=URLDecoder.decode(pathParameters[parameterInfo.getPathIndex()]);
                        }
                        catch (Throwable tt)
                        {
                            parameter=request.getParameter(parameterInfo.getName());
                        }
                    }
                    try
                    {
                        parameters[i]=FilterChain.parseParameter(parameterInfo,parameter);
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
                        parameters[i]=FilterChain.parseParameter(parameterInfo,parameter);
                    }
                    catch (Throwable t)
                    {
                        throw new AbnormalException(Abnormal.BAD_QUERY,t);
                    }
                    break;
                case SECURE_QUERY:
                    try
                    {
                        DecodingHttpServletRequest decodingHttpServletRequest =(DecodingHttpServletRequest)request;
                        String parameter=decodingHttpServletRequest.decodeParameter(parameterInfo.getName());
                        decodingHttpServletRequest.setParameter(parameterInfo.getName(),parameter);
                        parameters[i]=FilterChain.parseParameter(parameterInfo,parameter);
                        
                    }
                    catch (Throwable t)
                    {
                        throw new AbnormalException(Abnormal.BAD_QUERY,t);
                    }
                    break;
                case CONTEXT:
                    contextParameterIndex=i;
                    parameters[i]=context;
                    break;
                case STATE:
                    parameters[i]=context.getState();
                    stateParameterIndex=i;
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
                                parameters[i]=FilterChain.parseParameter(parameterInfo,value);
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
                    parameters[i]=FilterChain.parseParameter(parameterInfo,request.getParameter(parameterInfo.getName()));
                }
                default:
                    break;
                }
            }
            return new ParsedParameters(parameters, contextParameterIndex, stateParameterIndex);
        }
        public static Object parseParameter(Class<?> type,String value) throws Exception 
        {
            try
            {
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
                        return 0;
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
                        return 0L;
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
                        return (short)0;
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
                        return 0.0f;
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
                        return 0.0;
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
                        return false;
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
                throw new Exception("Error parsing parameter: type="+type.getCanonicalName()+", value="+value,t);
            }
        }
        
        
    }