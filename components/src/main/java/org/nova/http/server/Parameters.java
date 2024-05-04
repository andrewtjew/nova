package org.nova.http.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;

import org.nova.http.server.annotations.ContentParam;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.CookieParam;
import org.nova.http.server.annotations.CookieStateParam;
import org.nova.http.server.annotations.DefaultValue;
import org.nova.http.server.annotations.ParamName;
import org.nova.http.server.annotations.PathParam;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.Required;
import org.nova.http.server.annotations.StateParam;
import org.nova.tracing.Trace;

public class Parameters
    {
        final public ParameterInfo[] parameterInfos;
        final public HashSet<String> hiddenParameters;
        public Parameters(Method method,ContentReaders contentReaders) throws Exception
        {
            Class<?> objectType=method.getDeclaringClass();
            // parameters
            ArrayList<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Parameter[] parameters=method.getParameters();
            Annotation[][] annotations = method.getParameterAnnotations();
            this.hiddenParameters=new HashSet<String>();
            for (int parameterIndex = 0; parameterIndex < parameterTypes.length; parameterIndex++)
            {
                Class<?> parameterType = parameterTypes[parameterIndex];
                Parameter parameter=parameters[parameterIndex];
                // Process parameter annotations
                Annotation[] parameterAnnotations = annotations[parameterIndex];

                DefaultValue defaultValue = null;
                Required required=null;
                ContentParam contentParam = null;
                CookieParam cookieParam = null;
                CookieStateParam cookieStateParam = null;
                HeaderParam headerParam = null;
                PathParam pathParam = null;
                QueryParam queryParam = null;
                StateParam stateParam = null;
                ParamName paramName=null;
                
                for (Annotation annotation : parameterAnnotations)
                {
                    Class<?> type = annotation.annotationType();
                    if (type == DefaultValue.class)
                    {
                        defaultValue = (DefaultValue) annotation;
                    }
                    else if (type == Required.class)
                    {
                        required = (Required) annotation;
                    }
                    else if (type == ContentParam.class)
                    {
                        if (contentReaders == null)
                        {
                            throw new Exception("Need @ContentReaders for @ContentParam "+parameter.getName()+" for method " + objectType.getCanonicalName() + "." + method.getName());
                        }
                        contentParam = (ContentParam) annotation;
                    }
                    else if (type == CookieParam.class)
                    {
                        cookieParam=(CookieParam)annotation;
                    }
                    else if (type == CookieStateParam.class)
                    {
                        cookieStateParam=(CookieStateParam)annotation;
                    }
                    else if (type == HeaderParam.class)
                    {
                        headerParam = (HeaderParam) annotation;
                    }
                    else if (type == PathParam.class)
                    {
                        pathParam = (PathParam) annotation;
                    }
                    else if (type == QueryParam.class)
                    {
                        queryParam = (QueryParam) annotation;
                        if (queryParam.hidden())
                        {
                            hiddenParameters.add(queryParam.value());
                        }
                    }
                    else if (type == StateParam.class)
                    {
                        stateParam = (StateParam) annotation;
                    }
                    else if (type==ParamName.class)
                    {
                        paramName=(ParamName)annotation;
                    }
                }

                // Check if there are multiple param annotations
                ArrayList<Annotation> params = new ArrayList<Annotation>();
                if (contentParam != null)
                {
                    params.add(contentParam);
                }
                if (cookieParam != null)
                {
                    params.add(cookieParam);
                }
                if (cookieStateParam != null)
                {
                    params.add(cookieStateParam);
                }
                if (pathParam != null)
                {
                    params.add(pathParam);
                }
                if (headerParam != null)
                {
                    params.add(headerParam);
                }
                if (queryParam != null)
                {
                    params.add(queryParam);
                }
                if (stateParam != null)
                {
                    params.add(stateParam);
                }
                if (paramName != null)
                {
                    params.add(paramName);
                }
                if (params.size() > 1)
                {
                    throw new Exception("Only one param annotation allowed. Site=" + objectType.getCanonicalName() + "." + method.getName());
                }
//              else if (params.size() == 0)
//              {
//                  if ((parameterType != Context.class)&&(parameterType!=Trace.class)&&(parameterType!=Queries.class))
//                  {
//                      throw new Exception("Annotation required for param. Site=" + object.getClass().getCanonicalName() + "." + method.getName());
//                  }
//              }

                // No multiple param annotations. Check each and add.
                
                if (parameterType == Trace.class)
                {
                    rejectDefaultValueAndRequired(objectType, method, parameter, defaultValue, required);
                    parameterInfos.add(new ParameterInfo(ParameterSource.TRACE, null,parameter.getName(), parameterIndex, parameterType, null,true));
                    continue;
                }

                if (parameterType == Context.class)
                {
                    rejectDefaultValueAndRequired(objectType, method, parameter, defaultValue, required);
                    parameterInfos.add(new ParameterInfo(ParameterSource.CONTEXT, null, parameter.getName(), parameterIndex, parameterType, null,true));
                }
                else if (parameterType==Queries.class)
                {
                    rejectDefaultValueAndRequired(objectType, method, parameter, defaultValue, required);
                    parameterInfos.add(new ParameterInfo(ParameterSource.QUERIES, null, parameter.getName(), parameterIndex, parameterType, null, true));
                }
                else if (contentParam != null)
                {
                    rejectDefaultValueAndRequired(objectType, method, parameter, defaultValue, required);
                    parameterInfos.add(new ParameterInfo(ParameterSource.CONTENT, contentParam, parameter.getName(), parameterIndex, parameterType,null,true));
                }
                else if (stateParam != null)
                {
                    rejectDefaultValueAndRequired(objectType, method, parameter, defaultValue, required);
                    parameterInfos.add(new ParameterInfo(ParameterSource.STATE, stateParam, parameter.getName(), parameterIndex, parameterType, null,true));
                }
                else if (cookieParam != null)
                {
                    parameterInfos.add(new ParameterInfo(ParameterSource.COOKIE, cookieParam, cookieParam.value(), parameterIndex, parameterType,
                            getDefaultValue(method, defaultValue, parameterType),required!=null));
                }
                else if (cookieStateParam != null)
                {
                    parameterInfos.add(new ParameterInfo(ParameterSource.COOKIE_STATE, cookieStateParam, cookieStateParam.value(), parameterIndex, parameterType,
                            getDefaultValue(method, defaultValue, parameterType),required!=null));
                }
                else if (pathParam != null)
                {
                    if (isSimpleParameterType(parameterType) == false)
                    {
                        throw new Exception("Only simple types allowed for parameter. Site=" + objectType.getCanonicalName() + "." + method.getName());
                    }
                    parameterInfos.add(new ParameterInfo(ParameterSource.PATH, pathParam, pathParam.value(), parameterIndex, parameterType,
                            getDefaultValue(method, defaultValue, parameterType),required!=null));
                }
                else if (queryParam != null)
                {
                    parameterInfos.add(new ParameterInfo(ParameterSource.QUERY, queryParam, queryParam.value(), parameterIndex, parameterType,
                            getDefaultValue(method, defaultValue, parameterType),required!=null));
                }
                else if (headerParam != null)
                {
                    if (isSimpleParameterType(parameterType) == false)
                    {
                        throw new Exception("Only simple types allowed for parameter. Site=" + objectType.getCanonicalName() + "." + method.getName());
                    }
                    parameterInfos.add(new ParameterInfo(ParameterSource.HEADER, headerParam, headerParam.value(), parameterIndex, parameterType,
                            getDefaultValue(method, defaultValue, parameterType),required!=null));
                }
                else if (paramName!=null)
                {
                    if (paramName.startsWith())
                    {
                        parameterInfos.add(new ParameterInfo(ParameterSource.NAME, paramName, paramName.value(), parameterIndex, parameterType,
                                getDefaultValue(method, defaultValue, parameterType),required!=null));
                    }
                    else
                    {
                        rejectDefaultValueAndRequired(objectType, method, parameter, defaultValue, required);
                        if (parameterType!=boolean.class)
                        {
                            throw new Exception("When the startsWith field is false for Param name only the boolean type is allowed for parameter "+parameter.getName()+" in "+objectType.getCanonicalName() + "."
                                    + method.getName());
                        }
                        parameterInfos.add(new ParameterInfo(ParameterSource.NAME, paramName, paramName.value(), parameterIndex, parameterType,
                                null,true));
                    }
                }
                else
                {
                    parameterInfos.add(new ParameterInfo(ParameterSource.INTERNAL, null, parameter.getName(), parameterIndex, parameterType,
                            null,required!=null));
                }
            }
            this.parameterInfos=parameterInfos.toArray(new ParameterInfo[parameterInfos.size()]);       
        }
        public void rejectDefaultValueAndRequired(Class<?> objectType,Method method,Parameter parameter,DefaultValue defaultValue,Required required) throws Exception
        {
            if (defaultValue != null)
            {
                throw new Exception("@DefaultValue not allowed for parameter "+parameter.getName()+" in "+objectType.getCanonicalName() + "."
                        + method.getName());
            }
            if (required != null)
            {
                throw new Exception("@Required not allowed for parameter "+parameter.getName()+" in "+objectType.getCanonicalName() + "."
                        + method.getName());
            }
        }
        public Object getDefaultValue(Method method, DefaultValue defaultValue, Class<?> type) throws Exception
        {
            if (defaultValue == null)
            {
                return null;
            }
            try
            {
                if (type == int.class)
                {
                    return Integer.parseInt(defaultValue.value());
                }
                else if (type == Integer.class)
                {
                    if (defaultValue.value().length()==0)
                    {
                        return null;
                    }
                    return Integer.parseInt(defaultValue.value());
                }
                else if (type == long.class)
                {
                    return Long.parseLong(defaultValue.value());
                }
                else if (type == Long.class)
                {
                    if (defaultValue.value().length()==0)
                    {
                        return null;
                    }
                    return Long.parseLong(defaultValue.value());
                }
                else if (type == short.class)
                {
                    return Short.parseShort(defaultValue.value());
                }
                else if (type == Short.class)
                {
                    if (defaultValue.value().length()==0)
                    {
                        return null;
                    }
                    return Short.parseShort(defaultValue.value());
                }
                else if (type == float.class)
                {
                    return Float.parseFloat(defaultValue.value());
                }
                else if (type == Float.class)
                {
                    if (defaultValue.value().length()==0)
                    {
                        return null;
                    }
                    return Float.parseFloat(defaultValue.value());
                }
                else if (type == double.class)
                {
                    return Double.parseDouble(defaultValue.value());
                }
                else if (type == Double.class)
                {
                    if (defaultValue.value().length()==0)
                    {
                        return null;
                    }
                    return Double.parseDouble(defaultValue.value());
                }
                else if (type == boolean.class)
                {
                    String value = defaultValue.value().toLowerCase();
                    if (value.equals("true"))
                    {
                        return true;
                    }
                    if (value.equals("false"))
                    {
                        return false;
                    }
                }
                else if (type == Boolean.class)
                {
                    if (defaultValue.value().length()==0)
                    {
                        return null;
                    }
                    String value = defaultValue.value().toLowerCase();
                    if (value.equals("true"))
                    {
                        return true;
                    }
                    if (value.equals("false"))
                    {
                        return false;
                    }
                }
                else if (type == String.class)
                {
                    return defaultValue.value();
                }
                else if (type == BigDecimal.class)
                {
                    if (defaultValue.value().length()==0)
                    {
                        return null;
                    }
                    return new BigDecimal(Long.parseLong(defaultValue.value()));
                }
                else if (type.isEnum())
                {
                    return Enum.valueOf((Class<Enum>) type, defaultValue.value());
                }
            }
            catch (Throwable t)
            {
            }
            throw new Exception("Unable to parse @DefaultValue value. Value=" + defaultValue.value() + ". Site=" + method.getName());
        }
        
        public boolean isSimpleParameterType(Class<?> type)
        {
            return (type == int.class) || (type == Integer.class) || (type == long.class) || (type == Long.class) || (type == short.class) || (type == short.class)
                    || (type == float.class) || (type == Float.class) || (type == double.class) || (type == Double.class) || (type == boolean.class)
                    || (type == Boolean.class) || (type == String.class) || type.isEnum()||type==BigDecimal.class;
        }
    }