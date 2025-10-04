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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.nova.http.server.annotations.ParamName;
import org.nova.html.ext.HtmlUtils;
import org.nova.http.server.annotations.Attributes;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentParam;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.CookieParam;
import org.nova.http.server.annotations.CookieStateParam;
import org.nova.http.server.annotations.DELETE;
import org.nova.http.server.annotations.DefaultValue;
import org.nova.http.server.annotations.Filters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.HEAD;
import org.nova.http.server.annotations.Log;
import org.nova.http.server.annotations.OPTIONS;
import org.nova.http.server.annotations.PATCH;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.PUT;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.PathParam;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.Required;
import org.nova.http.server.annotations.StateParam;
import org.nova.http.server.annotations.TRACE;
import org.nova.http.server.annotations.Test;
import org.nova.services.ForbiddenRoles;
import org.nova.services.RequiredRoles;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

//TODO!!! Resolve Consumes and ContentReaders just like produces and contentwriters

class RequestMethodMap
{
	static class Map extends HashMap<String, PathNode>
	{
		private static final long serialVersionUID = -4908433415114984382L;
	}

	final private Map patchMap;
	final private Map putMap;
	final private Map getMap;
	final private Map postMap;
	final private Map deleteMap;
	final private Map optionsMap;
	final private Map headMap;
	final private Map traceMap;
	final private HashMap<String, RequestMethod> requestMethods;
	final private boolean test;
	final private int bufferSize;

	RequestMethodMap(boolean test,int bufferSize)
	{
	    this.test=test;
		this.putMap = new Map();
		this.getMap = new Map();
		this.postMap = new Map();
		this.deleteMap = new Map();
		this.optionsMap = new Map();
		this.headMap = new Map();
		this.traceMap = new Map();
		this.patchMap = new Map();
		this.requestMethods = new HashMap<>();
		this.bufferSize=bufferSize;
	}

	public RequestMethod[] getRequestHandlers()
	{
		return this.requestMethods.values().toArray(new RequestMethod[this.requestMethods.size()]);
	}

	public RequestMethod getRequestHandler(String key)
	{
		return this.requestMethods.get(key);
	}

	private Map getMap(String method)
	{
		switch (method)
		{
		case "DELETE":
			return deleteMap;
		case "GET":
			return getMap;
		case "HEAD":
			return headMap;
		case "OPTIONS":
			return optionsMap;
		case "POST":
			return postMap;
		case "PUT":
			return putMap;
		case "TRACE":
			return traceMap;
		case "PATCH":
			return patchMap;
		}
		return null;
	}

	void registerObject(String root, Object object, Class<?> objectType,Transformers transformers) throws Throwable
	{
		ClassAnnotations classAnnotations = new ClassAnnotations();
		for (Class<?> classType=objectType;classType!=null;classType=classType.getSuperclass())
		{
		    if (Modifier.isPublic(classType.getModifiers())==false)
		    {
                throw new Exception("Class must be public. Site=" + classType.getCanonicalName());
		    }
    		for (Annotation annotation : classType.getAnnotations())
    		{
    			Class<?> type = annotation.annotationType();
    			if (type == ContentReaders.class)
    			{
    				classAnnotations.contentReaders = (ContentReaders) annotation;
    			}
    			else if (type == ContentWriters.class)
    			{
    				classAnnotations.contentWriters = (ContentWriters) annotation;
    			}
    			else if (type == ContentEncoders.class)
    			{
    				classAnnotations.contentEncoders = (ContentEncoders) annotation;
    			}
    			else if (type == ContentDecoders.class)
    			{
    				classAnnotations.contentDecoders = (ContentDecoders) annotation;
    			}
                else if (type == RequiredRoles.class)
                {
                    classAnnotations.requiredRoles = (RequiredRoles) annotation;
                }
                else if (type == ForbiddenRoles.class)
                {
                    classAnnotations.forbiddenRoles = (ForbiddenRoles) annotation;
                }
                else if (type == Filters.class)
                {
                    classAnnotations.filters = (Filters) annotation;
                }
                else if (type == Log.class)
                {
                    classAnnotations.log = (Log) annotation;
                }
                else if (type == Path.class)
                {
                    classAnnotations.path= (Path) annotation;
                }
                else if (type==Test.class)
                {
                    classAnnotations.test=(Test)annotation;
                }
                else if (type==Attributes.class)
                {
                    classAnnotations.attributes=(Attributes)annotation;
                }
    		}
		}
		for (Method method : objectType.getMethods())
		{
			registerMethod(root, object, method, new ClassAnnotations(classAnnotations), transformers);
		}
	}

	void registerObjectMethod(String root, Object object, Method method, Transformers transformers) throws Throwable
	{
		registerMethod(root, object, method, new ClassAnnotations(), transformers);
	}

	private boolean isSimpleParameterType(Class<?> type)
	{
		return (type == int.class) || (type == Integer.class) || (type == long.class) || (type == Long.class) || (type == short.class) || (type == short.class)
				|| (type == float.class) || (type == Float.class) || (type == double.class) || (type == Double.class) || (type == boolean.class)
				|| (type == Boolean.class) || (type == String.class) || type.isEnum()||type==BigDecimal.class;
	}

	static class DistanceContentWriter
	{
	    int distance;
	    ContentWriter contentWriter;
	    DistanceContentWriter(int distance,ContentWriter contentWriter)
	    {
	        this.distance=distance;
	        this.contentWriter=contentWriter;
	    }
	}
	private void storeClosestDistanceWriter(HashMap<String,DistanceContentWriter> writers,String mediaType,int distance,ContentWriter writer)
	{
	    DistanceContentWriter closest=writers.get(mediaType);
	    if ((closest==null)||(closest.distance>distance))
	    {
	        writers.put(mediaType,new DistanceContentWriter(distance, writer));
	    }
	}

	private void rejectDefaultValueAndRequired(Class<?> objectType,Method method,Parameter parameter,DefaultValue defaultValue,Required required) throws Exception
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
	
    static String toSite(Object object,Method method)
    {
        return object.getClass().getName()+"."+method.getName();
    }
    static String toSite(Method method)
    {
        return method.getDeclaringClass()+"."+method.getName();
    }
	
	private void registerMethod(String root, Object object, Method method, ClassAnnotations handlerAnnotations, Transformers transformers) throws Throwable
	{
	    Class<?> objectType=method.getDeclaringClass().getClass();
		String httpMethod = null;
		int verbs = 0;
		Path classPath=handlerAnnotations.path;
		handlerAnnotations.path=null;
		
		for (Annotation annotation : method.getAnnotations())
		{
			Class<?> type = annotation.annotationType();
			if (type == ContentReaders.class)
			{
				handlerAnnotations.contentReaders = (ContentReaders) annotation;
			}
			else if (type == ContentWriters.class)
			{
			    handlerAnnotations.contentWriters = (ContentWriters) annotation;
			}
			else if (type == ContentEncoders.class)
			{
			    handlerAnnotations.contentEncoders = (ContentEncoders) annotation;
			}
			else if (type == ContentDecoders.class)
			{
			    handlerAnnotations.contentDecoders = (ContentDecoders) annotation;
			}
            else if (type == RequiredRoles.class)
            {
                handlerAnnotations.requiredRoles = (RequiredRoles) annotation;
            }
            else if (type == ForbiddenRoles.class)
            {
                handlerAnnotations.forbiddenRoles = (ForbiddenRoles) annotation;
            }
			else if (type == Filters.class)
			{
			    handlerAnnotations.filters = (Filters) annotation;
			}
            else if (type == Log.class)
            {
                handlerAnnotations.log = (Log) annotation;
            }
            else if (type == Test.class)
            {
                handlerAnnotations.test= (Test) annotation;
            }
			else if (type == GET.class)
			{
				httpMethod = "GET";
				verbs++;
			}
			else if (type == PUT.class)
			{
				httpMethod = "PUT";
				verbs++;
			}
			else if (type == DELETE.class)
			{
				httpMethod = "DELETE";
				verbs++;
			}
			else if (type == POST.class)
			{
				httpMethod = "POST";
				verbs++;
			}
			else if (type == PATCH.class)
			{
				httpMethod = "PATCH";
				verbs++;
			}
			else if (type == HEAD.class)
			{
				httpMethod = "HEAD";
				verbs++;
			}
			else if (type == OPTIONS.class)
			{
				httpMethod = "OPTIONS";
				verbs++;
			}
			else if (type == TRACE.class)
			{
				httpMethod = "TRACE";
				verbs++;
			}
            else if (type == Path.class)
            {
                handlerAnnotations.path = (Path) annotation;
            }
            else if (type == Attributes.class)
            {
                handlerAnnotations.attributes = (Attributes) annotation;
            }
		}
		if (handlerAnnotations.test!=null)
		{
		    if (this.test==false)
		    {
		        return;
		    }
		}
		if (verbs == 0)
		{
			return;
		}
		if (verbs > 1)
		{
			throw new Exception("Multiple Http verbs. Site=" + toSite(object,method));
		}

		// filters
		ArrayList<Filter> bottomHandlerFilters = new ArrayList<Filter>();
		ArrayList<Filter> topHandlerFilters = new ArrayList<Filter>();
		if (handlerAnnotations.filters != null)
		{
			for (Class<? extends Filter> type: handlerAnnotations.filters.value())
			{
				Filter filter = transformers.getBottomFilter(type);
				if (filter != null)
				{
					bottomHandlerFilters.add(filter);
				}
				else
				{
					filter = transformers.getTopFilter(type);
					if (filter != null)
					{
						topHandlerFilters.add(filter);
					}
					else
					{
						throw new Exception("No instance of requested filter " + type.getName()+ ". Site=" + toSite(object,method));
					}
				}
			}
		}

		// parameters
		ContentReaders contentReaders=handlerAnnotations.contentReaders;
		ArrayList<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Parameter[] parameters=method.getParameters();
		Annotation[][] annotations = method.getParameterAnnotations();
		int cookieStateParamCount=0;
		HashSet<String> hiddenParameters=new HashSet<String>();
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
//			SecureQueryParam secureQueryParam=null;
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
					cookieStateParamCount++;
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
//                else if (type == SecureQueryParam.class)
//                {
//                    secureQueryParam = (SecureQueryParam) annotation;
//                    if (secureQueryParam.hideParameterValue())
//                    {
//                        hiddenParameters.add(secureQueryParam.value());
//                    }
//                }
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
//            if (secureQueryParam != null)
//            {
//                params.add(secureQueryParam);
//            }
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
				throw new Exception("Only one param annotation allowed. Site=" + toSite(object,method));
			}
//			else if (params.size() == 0)
//			{
//				if ((parameterType != Context.class)&&(parameterType!=Trace.class)&&(parameterType!=Queries.class))
//				{
//					throw new Exception("Annotation required for param. Site=" + object.getClass().getCanonicalName() + "." + method.getName());
//				}
//			}

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
						HtmlUtils.getDefaultValue(method, defaultValue, parameterType),required!=null));
			}
			else if (cookieStateParam != null)
			{
				cookieStateParamCount++;
				parameterInfos.add(new ParameterInfo(ParameterSource.COOKIE_STATE, cookieStateParam, cookieStateParam.value(), parameterIndex, parameterType,
				        HtmlUtils.getDefaultValue(method, defaultValue, parameterType),required!=null));
			}
			else if (pathParam != null)
			{
				if (isSimpleParameterType(parameterType) == false)
				{
					throw new Exception("Only simple types allowed for parameter. Site=" + toSite(object,method));
				}
				parameterInfos.add(new ParameterInfo(ParameterSource.PATH, pathParam, pathParam.value(), parameterIndex, parameterType,
				        HtmlUtils.getDefaultValue(method, defaultValue, parameterType),required!=null));
			}
			else if (queryParam != null)
			{
				parameterInfos.add(new ParameterInfo(ParameterSource.QUERY, queryParam, queryParam.value(), parameterIndex, parameterType,
				        HtmlUtils.getDefaultValue(method, defaultValue, parameterType),required!=null));
			}
			else if (headerParam != null)
			{
				if (isSimpleParameterType(parameterType) == false)
				{
					throw new Exception("Only simple types allowed for parameter. Site=" + toSite(object,method));
				}
				parameterInfos.add(new ParameterInfo(ParameterSource.HEADER, headerParam, headerParam.value(), parameterIndex, parameterType,
				        HtmlUtils.getDefaultValue(method, defaultValue, parameterType),required!=null));
			}
			else if (paramName!=null)
			{
                if (paramName.startsWith())
                {
                    parameterInfos.add(new ParameterInfo(ParameterSource.NAME, paramName, paramName.value(), parameterIndex, parameterType,
                            HtmlUtils.getDefaultValue(method, defaultValue, parameterType),required!=null));
                }
                else
                {
                    rejectDefaultValueAndRequired(objectType, method, parameter, defaultValue, required);
    			    if (parameterType!=boolean.class)
    			    {
    		            throw new Exception("When the startsWith field is false for Param name only the boolean type is allowed for parameter "+parameter.getName()+". Site="+toSite(object,method));
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

		// ContentReaders and writers
		HashMap<String, ContentWriter> contentWriterMap = new HashMap<>();
		Type returnType=method.getReturnType();
		if (handlerAnnotations.contentWriters == null)
		{
			if (returnType != void.class)
			{
				throw new Exception("Method return type is not void. @ContentWriters annotation missing. Site="+toSite(object,method));
			}
		}
		else 
		{
		    if (returnType==Response.class)
		    {
		        Type genericReturnType=method.getGenericReturnType();
		        if (genericReturnType instanceof ParameterizedType)
		        {
		            returnType=((ParameterizedType)genericReturnType).getActualTypeArguments()[0];
		            if (returnType instanceof WildcardType)
		            {
                        throw new Exception("Need to specify response type: "+", Site="+ toSite(object,method));
//                        throw new Exception("Need to specify response type: "+", Site="+ objectType.getCanonicalName() + "." + method.getName());
		            }
		        }
		        else
		        {
		            returnType=void.class;
		        }
		    }
		    
		    if ((returnType!=void.class)&&(returnType!=Void.class))
		    {
		        HashMap<String,DistanceContentWriter> closestDistanceWriters=new HashMap<>();
    			for (Class<? extends ContentWriter> type : handlerAnnotations.contentWriters.value())
    			{
    				ContentWriter writer=transformers.getContentWriter(type);
    				if (writer==null)
    				{
                        throw new Exception("Need to register ContentWriter: "+type.getName()+", Site="+ toSite(object,method));
    				}
				    try
				    {
    				    int distance=TypeUtils.getAncestorDistance((Class<?>)returnType, writer.getContentType());
    				    if (distance>=0)
    				    {
        				    String mediaType=writer.getMediaType().toLowerCase();
        				    storeClosestDistanceWriter(closestDistanceWriters, mediaType, distance, writer);
        					String anySubType = org.nova.http.server.WsUtils.toAnySubMediaType(mediaType);
                            storeClosestDistanceWriter(closestDistanceWriters, anySubType, distance, writer);
                            storeClosestDistanceWriter(closestDistanceWriters, "*/*", distance, writer);
    				    }
				    }
				    catch (Throwable t)
				    {
	                    throw new Exception("Cannot match return type with suitable content writer. Generic return type?. Site="
	                            + toSite(object,method),t);
				        
				    }
    			}
    			for (Entry<String, DistanceContentWriter> entry:closestDistanceWriters.entrySet())
    			{
    			    contentWriterMap.put(entry.getKey(), entry.getValue().contentWriter);
    			}
    			
    			if (contentWriterMap.size()==0)
    			{
                    throw new Exception("No suitable ContentWriter found. Site="
                            + toSite(method));
    			}
		    }
		}

		HashMap<String, ContentReader> contentReaderMap = new HashMap<>();
		if (handlerAnnotations.contentReaders!=null) 
		{
			for (Class<? extends ContentReader> type : handlerAnnotations.contentReaders.value())
			{
				ContentReader reader=transformers.getContentReader(type);
				if (reader!=null)
				{
					contentReaderMap.put(reader.getMediaType().toLowerCase(), reader);
				}
			}
		}
		
		HashMap<String, ContentDecoder> contentDecoderMap = new HashMap<>();
		if (handlerAnnotations.contentDecoders != null)
		{
			for (Class<? extends ContentDecoder> type: handlerAnnotations.contentDecoders.value())
			{
				ContentDecoder decoder=transformers.getContentDecoder(type);
				if (decoder==null)
				{
					throw new Exception(
							"No ContentEncoder of type "+type.getName()+" is supplied. Site=" + toSite(object,method));
					
				}
				contentDecoderMap.put(decoder.getCoding(),decoder);
			}
		}

		ArrayList<ContentEncoder> contentEncoderList = new ArrayList<>();
		if (handlerAnnotations.contentEncoders!= null)
		{
			for (Class<? extends ContentEncoder> type: handlerAnnotations.contentEncoders.value())
			{
				ContentEncoder encoder=transformers.getContentEncoder(type);
				if (encoder==null)
				{
					throw new Exception(
							"No ContentDecoder of type "+type.getName()+" is supplied. Site=" + toSite(object,method));
					
				}
                contentEncoderList.add(encoder);
			}
		}

	    boolean log=true;
	    boolean logLastRequestsInMemory=true;
	    boolean logRequestHeaders=true;
	    boolean logRequestContent=true;
        boolean logResponseHeaders=true;
        boolean logResponseContent=true;
        boolean logRequestParameters=true;
        
        StringBuilder path=new StringBuilder();
        if (root!=null)
        {
            path.append(root);
        }
        if (classPath!=null)
        {
            path.append(classPath.value());
        }
        if (handlerAnnotations.path!=null)
        {
            path.append(handlerAnnotations.path.value());
        }
        
        
        String fullPath = path.toString();
        if (fullPath.length()==0)
        {
            throw new Exception("@Path annotation missing at method or class level or no root provided. Site=" + toSite(object,method));
        }
	    if (handlerAnnotations.log!=null)
	    {
	        log=handlerAnnotations.log.value();
	        if (log==true)
	        {
    	        logLastRequestsInMemory=handlerAnnotations.log.lastRequestsInMemory();
    	        logRequestHeaders=handlerAnnotations.log.requestHeaders();
    	        logRequestParameters=handlerAnnotations.log.requestParameters();
    	        logRequestContent=handlerAnnotations.log.requestContent();
    	        logResponseHeaders=handlerAnnotations.log.responseHeaders();
    	        logResponseContent=handlerAnnotations.log.responseContent();
	        }
	        else
	        {
                logLastRequestsInMemory=false;
                logRequestHeaders=false;
                logRequestContent=false;
                logResponseHeaders=false;
                logResponseContent=false;
	        }
	    }
        if (fullPath.endsWith("/#"))
        {
            fullPath=fullPath.substring(0, fullPath.length()-1)+method.getName();
     //       System.out.println("METHOD:"+fullPath);
        }
        else if (fullPath.endsWith("/@"))
        {
            fullPath=fullPath.substring(0, fullPath.length()-1)+objectType.getSimpleName()+"/"+method.getName(); 
        }
	    Filter[] bottomFilters=bottomHandlerFilters.toArray(new Filter[bottomHandlerFilters.size()]);
	    Filter[] topFilters=topHandlerFilters.toArray(new Filter[topHandlerFilters.size()]);
        RequestMethod requestMethod = new RequestMethod(object, method, httpMethod, fullPath, bottomFilters,topFilters
                ,parameterInfos.toArray(new ParameterInfo[parameterInfos.size()]), contentDecoderMap
                ,contentEncoderList.toArray(new ContentEncoder[contentEncoderList.size()]), contentReaderMap, contentWriterMap,
                log,logRequestHeaders,logRequestParameters,logRequestContent,logResponseHeaders,logResponseContent,logLastRequestsInMemory,
                this.bufferSize,cookieStateParamCount,handlerAnnotations,hiddenParameters.size()==0?null:hiddenParameters);
        add(httpMethod, fullPath, requestMethod);
        
        for (Filter filter:bottomFilters)
        {
            filter.onRegister(requestMethod);
        }
        
	}
	
	static public class FragmentIndexMap extends HashMap<String,Integer>
	{
        private static final long serialVersionUID = -3883360549423832556L;
	}

	void add(String method, String path, RequestMethod requestMethod) throws Exception
	{
		Map map = getMap(method);
		if (map == null)
		{
			throw new Exception("Invalid http method=" + method + ". Site=" + requestMethod.getMethod().getName());
		}
		String[] fragments = Utils.split(path, '/');
		if (fragments[0].length() != 0)
		{
			throw new Exception("Path must start with root / character. Site=" + requestMethod.getMethod().getName()+", path="+path);
		}
		PathNode node = null;
		FragmentIndexMap indexMap = requestMethod.getFragmentIndexMap();
		int index = 0;
		for (int i = 1; i < fragments.length; i++)
		{
			String fragment = fragments[i];
			String key = fragment;
			if ((fragment.length() > 0) && (fragment.charAt(0) == '{') && (fragment.charAt(fragment.length() - 1) == '}'))
			{
				String name = fragment.substring(1, fragment.length() - 1);
				if (indexMap.containsKey(name))
				{
					throw new Exception("Path parameter names must be unique. Path=" + path + ", site=" + requestMethod.getMethod().getName());
				}
				indexMap.put(name, index++);
				if (PathParam.AT_LEAST_ONE_SEGMENT.equals(name))
				{
					if (i != fragments.length - 1)
					{
						throw new Exception("Rest of path must be last path parameter. Path=" + path + ", site=" + requestMethod.getMethod().getName());
					}
					key = PathParam.AT_LEAST_ONE_SEGMENT;
				}
				else
				{
					key = "/";
				}
			}
			node = map.get(key);
			if (node == null)
			{
				node = new PathNode();
				map.put(key, node);
			}
			if (PathParam.AT_LEAST_ONE_SEGMENT.equals(key))
			{
				break;
			}
			map = node.map;
		}
		RequestMethod nodeRequestHandler=node.getRequestMethod();
		if (nodeRequestHandler != null)
		{

            if (requestMethod.getObject()!=null)
		    {
    			throw new Exception(
    			        "Path conflict with incompatible types. Existing Path=" + nodeRequestHandler.getPath() + ", existing requestMethod=" + nodeRequestHandler.getMethod().getDeclaringClass().getName()+":"+nodeRequestHandler.getMethod().getName()
    							 +", new Path=" + requestMethod.getPath() + ", new request handler=" + requestMethod.getMethod().getDeclaringClass().getName()+":"+requestMethod.getMethod().getName());
		    }
//            if (TypeUtils.isDerivedFrom(node.requestMethod.getMethod().getDeclaringClass(), requestMethod.getMethod().getDeclaringClass())==false)
//            {
//                throw new Exception(
//                        "Path conflict with incompatible state types. Existing Path=" + node.requestMethod.getPath() + ", existing requestMethod=" + node.requestMethod.getMethod().getDeclaringClass().getName()+":"+node.requestMethod.getMethod().getName()
//                                 +", new Path=" + requestMethod.getPath() + ", new request handler=" + requestMethod.getMethod().getDeclaringClass().getName()+":"+requestMethod.getMethod().getName());
//            }
            if (nodeRequestHandler.getMethod().equals(requestMethod.getMethod())==false)
            {
                throw new Exception(
                        "Path conflict types. Existing Path=" + nodeRequestHandler.getPath() + ", existing requestMethod=" + nodeRequestHandler.getMethod().getDeclaringClass().getName()+":"+nodeRequestHandler.getMethod().getName()
                                 +", new Path=" + requestMethod.getPath() + ", new request handler=" + requestMethod.getMethod().getDeclaringClass().getName()+":"+requestMethod.getMethod().getName());
            }
            
            return;
		}
		ParameterInfo[] parameterInfos = requestMethod.getParameterInfos();
		for (int i = 0; i < parameterInfos.length; i++)
		{
			ParameterInfo parameterInfo = parameterInfos[i];
			if (parameterInfo.getSource() == ParameterSource.PATH)
			{
				Integer pathIndex = indexMap.get(parameterInfo.getName());
				if (pathIndex == null)
				{
					throw new Exception("PathParam name not found in Path. Path=" + path + ", PathParam name=" + parameterInfo.getName());
				}
				parameterInfos[i] = new ParameterInfo(parameterInfo, pathIndex);
			}
		}
		node.requestMethod = requestMethod;
        this.requestMethods.put(requestMethod.getKey(), requestMethod);
	}

	RequestMethodWithParameters resolve(String method, String path)
	{
		Map map = getMap(method);
		if (map == null)
		{
			return null;
		}
		String[] fragments = Utils.split(path, '/');
		PathNode node = null;
		String[] parameters = new String[fragments.length];
		int index = 0;
		for (int i = 1; i < fragments.length; i++)
		{
			String fragment = fragments[i];
			node = map.get(fragment);
			if (node == null)
			{
				node = map.get(PathParam.AT_LEAST_ONE_SEGMENT);
				if (node != null)
				{
					StringBuilder sb = new StringBuilder();
					sb.append(fragments[i]);
					for (int j = i + 1; j < fragments.length; j++)
					{
						sb.append("/" + fragments[j]);
					}
					parameters[index++] = sb.toString();
					break;
				}
				node = map.get("/");
				if (node != null)
				{
					parameters[index++] = fragment;
					map = node.map;
					continue;
				}
				return null;
			}
			map = node.map;
		}
		RequestMethod nodeRequestMethod=node.getRequestMethod();
		if (nodeRequestMethod == null)
		{
			return null;
		}
		return new RequestMethodWithParameters(nodeRequestMethod, parameters);
	}
}
