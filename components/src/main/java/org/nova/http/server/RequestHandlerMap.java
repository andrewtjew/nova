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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;

import org.nova.http.client.PathAndQuery;
import org.nova.http.server.annotations.ParamName;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentParam;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.CookieParam;
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
import org.nova.http.server.annotations.StateParam;
import org.nova.http.server.annotations.TRACE;
import org.nova.http.server.annotations.Test;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

import com.jcraft.jsch.Buffer;

//TODO!!! Resolve Consumes and ContentReaders just like produces and contentwriters

class RequestHandlerMap
{
	static class Map extends HashMap<String, Node>
	{
		private static final long serialVersionUID = -4908433415114984382L;
	}

	static class Node
	{
		final Map map;
		RequestHandler requestHandler;

		Node()
		{
			map = new Map();
		}
	}

	final private Map patchMap;
	final private Map putMap;
	final private Map getMap;
	final private Map postMap;
	final private Map deleteMap;
	final private Map optionsMap;
	final private Map headMap;
	final private Map traceMap;
	final private HashMap<String, RequestHandler> requestHandlers;
	final private boolean test;
	final private int bufferSize;

	RequestHandlerMap(boolean test,int bufferSize)
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
		this.requestHandlers = new HashMap<>();
		this.bufferSize=bufferSize;
	}

	public RequestHandler[] getRequestHandlers()
	{
		return this.requestHandlers.values().toArray(new RequestHandler[this.requestHandlers.size()]);
	}

	public RequestHandler getRequestHandler(String key)
	{
		return this.requestHandlers.get(key);
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

	void registerObject(String root, Object object, Transformers transformers) throws Exception
	{
		Annotations classAnnotations = new Annotations();
		for (Class<?> classType=object.getClass();classType!=null;classType=classType.getSuperclass())
		{
		    if (Modifier.isPublic(classType.getModifiers())==false)
		    {
  //              throw new Exception("Class must be public. Site=" + classType.getCanonicalName());
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
    		}
		}
		for (Method method : object.getClass().getMethods())
		{
			registerMethod(root, object, method, new Annotations(classAnnotations), transformers);
		}
	}

	void registerObjectMethod(String root, Object object, Method method, Transformers transformers) throws Exception
	{
		registerMethod(root, object, method, new Annotations(), transformers);
	}

	private boolean isSimpleParameterType(Class<?> type)
	{
		return (type == int.class) || (type == Integer.class) || (type == long.class) || (type == Long.class) || (type == short.class) || (type == short.class)
				|| (type == float.class) || (type == Float.class) || (type == double.class) || (type == Double.class) || (type == boolean.class)
				|| (type == Boolean.class) || (type == String.class) || type.isEnum()||type==BigDecimal.class;
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private Object getDefaultValue(Method method, DefaultValue defaultValue, Class<?> type) throws Exception
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

	static class DistanceContentWriter
	{
	    int distance;
	    ContentWriter<?> contentWriter;
	    DistanceContentWriter(int distance,ContentWriter<?> contentWriter)
	    {
	        this.distance=distance;
	        this.contentWriter=contentWriter;
	    }
	}
	private void storeClosestDistanceWriter(HashMap<String,DistanceContentWriter> writers,String mediaType,int distance,ContentWriter<?> writer)
	{
	    DistanceContentWriter closest=writers.get(mediaType);
	    if ((closest==null)||(closest.distance>distance))
	    {
	        writers.put(mediaType,new DistanceContentWriter(distance, writer));
	    }
	}
	
	private void registerMethod(String root, Object object, Method method, Annotations handlerAnnotations, Transformers transformers) throws Exception
	{
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
			throw new Exception("Multiple Http verbs. Site=" + object.getClass().getCanonicalName() + "." + method.getName());
		}

		// filters
		ArrayList<Filter> handlerFilters = new ArrayList<Filter>();
		if (transformers.bottomFilters != null)
		{
			for (Filter filter : transformers.bottomFilters)
			{
				handlerFilters.add(filter);
			}
		}
		if (handlerAnnotations.filters != null)
		{
			for (Class<? extends Filter> type: handlerAnnotations.filters.value())
			{
				Filter filter = transformers.getFilter(type);
				if (filter == null)
				{
					throw new Exception("No instance of requested filter " + type.getName()+ ". Site=" + object.getClass().getCanonicalName() + "." + method.getName());
				}
				handlerFilters.add(filter);
			}
		}
		if (transformers.topFilters != null)
		{
			for (Filter filter : transformers.topFilters)
			{
				handlerFilters.add(filter);
			}
		}

		// parameters
		ArrayList<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Annotation[][] annotations = method.getParameterAnnotations();

		for (int parameterIndex = 0; parameterIndex < parameterTypes.length; parameterIndex++)
		{
			Class<?> parameterType = parameterTypes[parameterIndex];
			if (parameterType == Trace.class)
			{
				parameterInfos.add(new ParameterInfo(ParameterSource.TRACE, null,null, parameterIndex, parameterType, null));
				continue;
			}

			// Process parameter annotations
			Annotation[] parameterAnnotations = annotations[parameterIndex];

			DefaultValue defaultValue = null;
			ContentParam contentParam = null;
			CookieParam cookieParam = null;
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
				else if (type == ContentParam.class)
				{
					if (handlerAnnotations.contentReaders == null)
					{
						throw new Exception("Need @ContentReaders for @ContentParam." + object.getClass().getCanonicalName() + "." + method.getName());
					}
					contentParam = (ContentParam) annotation;
				}
				else if (type == CookieParam.class)
				{
					cookieParam = (CookieParam) annotation;
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
				throw new Exception("Only one param annotation allowed. Site=" + object.getClass().getCanonicalName() + "." + method.getName());
			}
//			else if (params.size() == 0)
//			{
//				if ((parameterType != Context.class)&&(parameterType!=Trace.class)&&(parameterType!=Queries.class))
//				{
//					throw new Exception("Annotation required for param. Site=" + object.getClass().getCanonicalName() + "." + method.getName());
//				}
//			}

			// No multiple param annotations. Check each and add.
			if (parameterType == Context.class)
			{
				if (defaultValue != null)
				{
					throw new Exception("@DefaultValue annotation not allowed for Context parameter. Site=" + object.getClass().getCanonicalName() + "."
							+ method.getName());
				}
				parameterInfos.add(new ParameterInfo(ParameterSource.CONTEXT, null, null, parameterIndex, parameterType, null));
			}
            else if (parameterType==Queries.class)
            {
                if (defaultValue != null)
                {
                    throw new Exception("@DefaultValue annotation not allowed with @QueryParams annotation. Site=" + object.getClass().getCanonicalName() + "."
                            + method.getName());
                }
                parameterInfos.add(new ParameterInfo(ParameterSource.QUERIES, null, null, parameterIndex, parameterType, null));
            }
			else if (contentParam != null)
			{
				if (defaultValue != null)
				{
					throw new Exception("@DefaultValue annotation not allowed with @ContentParam annnotation. Site=" + method.getName());
				}
				parameterInfos.add(new ParameterInfo(ParameterSource.CONTENT, contentParam, null, parameterIndex, parameterType,null));
			}
			else if (stateParam != null)
			{
				if (defaultValue != null)
				{
					throw new Exception("@DefaultValue annotation not allowed with @StateParam annotation. Site=" + method.getName());
				}
				parameterInfos.add(new ParameterInfo(ParameterSource.STATE, stateParam, null, parameterIndex, parameterType, null));
			}
			else if (cookieParam != null)
			{
				if (parameterType == Cookie.class)
				{
					if (defaultValue != null)
					{
						throw new Exception("@DefaultValue annotation not allowed with @CookieParam parameter with type Cookie. Site="
								+ object.getClass().getCanonicalName() + "." + method.getName());
					}
				}
				else if (isSimpleParameterType(parameterType) == false)
				{
					throw new Exception("Only simple types allowed for parameter. Site=" + method.getName());
				}
				parameterInfos.add(new ParameterInfo(ParameterSource.COOKIE, cookieParam, cookieParam.value(), parameterIndex, parameterType,
						getDefaultValue(method, defaultValue, parameterType)));
			}
			else if (pathParam != null)
			{
				if (isSimpleParameterType(parameterType) == false)
				{
					throw new Exception("Only simple types allowed for parameter. Site=" + object.getClass().getCanonicalName() + "." + method.getName());
				}
				parameterInfos.add(new ParameterInfo(ParameterSource.PATH, pathParam, pathParam.value(), parameterIndex, parameterType,
						getDefaultValue(method, defaultValue, parameterType)));
			}
			else if (queryParam != null)
			{
			    /*
				if (isSimpleParameterType(parameterType) == false)
				{
					throw new Exception("Only simple types allowed for parameter. Site=" + method.getName());
				}
				*/
				parameterInfos.add(new ParameterInfo(ParameterSource.QUERY, queryParam, queryParam.value(), parameterIndex, parameterType,
						getDefaultValue(method, defaultValue, parameterType)));
			}
			else if (headerParam != null)
			{
				if (isSimpleParameterType(parameterType) == false)
				{
					throw new Exception("Only simple types allowed for parameter. Site=" + object.getClass().getCanonicalName() + "." + method.getName());
				}
				parameterInfos.add(new ParameterInfo(ParameterSource.HEADER, headerParam, headerParam.value(), parameterIndex, parameterType,
						getDefaultValue(method, defaultValue, parameterType)));
			}
			else if (paramName!=null)
			{
                if (paramName.startsWith())
                {
                    parameterInfos.add(new ParameterInfo(ParameterSource.NAME, paramName, paramName.value(), parameterIndex, parameterType,
                            getDefaultValue(method, defaultValue, parameterType)));
                }
                else
                {
                    if (defaultValue != null)
                    {
                        throw new Exception("@DefaultValue annotation not allowed for ParamName with startsWith=false. Site=" + object.getClass().getCanonicalName() + "."
                                + method.getName());
                    }
    			    if (parameterType!=boolean.class)
    			    {
                        throw new Exception("Only the boolean type is allowed for ParamName when the startsWith field is false. Site=" + object.getClass().getCanonicalName() + "." + method.getName());
    			    }
                    parameterInfos.add(new ParameterInfo(ParameterSource.NAME, paramName, paramName.value(), parameterIndex, parameterType,
                            null));
                }
			}
			else
			{
                parameterInfos.add(new ParameterInfo(ParameterSource.INTERNAL, null, null, parameterIndex, parameterType,
                        null));
			}
		}

		// ContentReaders and writers
		HashMap<String, ContentWriter<?>> contentWriterMap = new HashMap<>();
		Type returnType=method.getReturnType();
		if (handlerAnnotations.contentWriters == null)
		{
			if (returnType != void.class)
			{
				throw new Exception("Method return type is not void. @ContentWriters annotation missing. Site="
						+ object.getClass().getCanonicalName() + "." + method.getName());
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
		        }
		        else
		        {
		            returnType=Object.class;
		        }
		    }
		    
		    if ((returnType!=void.class)&&(returnType!=Void.class))
		    {
		        HashMap<String,DistanceContentWriter> closestDistanceWriters=new HashMap<>();
    			for (Class<? extends ContentWriter<?>> type : handlerAnnotations.contentWriters.value())
    			{
    				ContentWriter<?> writer=transformers.getContentWriter(type);
    				if (writer==null)
    				{
                        throw new Exception("Need to register ContentWriter: "+type.getName()+", Site="+ object.getClass().getCanonicalName() + "." + method.getName());
    				}
				    ParameterizedType writerType=(ParameterizedType)writer.getClass().getGenericSuperclass();
				    Type writerContentType=writerType.getActualTypeArguments()[0];

				    try
				    {
    				    int distance=TypeUtils.getAncestorDistance((Class<?>)returnType, (Class<?>)writerContentType);
    				    if (distance>=0)
    				    {
        				    String mediaType=writer.getMediaType();
        				    storeClosestDistanceWriter(closestDistanceWriters, mediaType, distance, writer);
        					String anySubType = org.nova.http.server.WsUtils.toAnySubMediaType(mediaType);
                            storeClosestDistanceWriter(closestDistanceWriters, anySubType, distance, writer);
                            storeClosestDistanceWriter(closestDistanceWriters, "*/*", distance, writer);
    				    }
				    }
				    catch (Throwable t)
				    {
	                    throw new Exception("Cannot match return type with suitable content writer. Generic return type?. "
	                            + object.getClass().getCanonicalName() + "." + method.getName(),t);
				        
				    }
    			}
    			for (Entry<String, DistanceContentWriter> entry:closestDistanceWriters.entrySet())
    			{
    			    contentWriterMap.put(entry.getKey(), entry.getValue().contentWriter);
    			}
    			
    			if (contentWriterMap.size()==0)
    			{
                    throw new Exception("No suitable ContentWriter found. "
                            + object.getClass().getCanonicalName() + "." + method.getName());
    			}
		    }
		}

		HashMap<String, ContentReader<?>> contentReaderMap = new HashMap<>();
		if (handlerAnnotations.contentReaders!=null) 
		{
			for (Class<? extends ContentReader<?>> type : handlerAnnotations.contentReaders.value())
			{
				ContentReader<?> reader=transformers.getContentReader(type);
				if (reader!=null)
				{
					contentReaderMap.put(reader.getMediaType(), reader);
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
							"No ContentEncoder of type "+type.getName()+" is supplied. Site=" + object.getClass().getCanonicalName() + "." + method.getName());
					
				}
				contentDecoderMap.put(type.getName(),decoder);
			}
		}

		HashMap<String, ContentEncoder> contentEncoderMap = new HashMap<>();
		if (handlerAnnotations.contentEncoders!= null)
		{
			for (Class<? extends ContentEncoder> type: handlerAnnotations.contentEncoders.value())
			{
				ContentEncoder encoder=transformers.getContentEncoder(type);
				if (encoder==null)
				{
					throw new Exception(
							"No ContentDecoder of type "+type.getName()+" is supplied. Site=" + object.getClass().getCanonicalName() + "." + method.getName());
					
				}
                contentEncoderMap.put(type.getName(),encoder);
			}
		}

	    boolean log=true;
	    boolean logLastRequestsInMemory=true;
	    boolean logRequestHeaders=true;
	    boolean logRequestContent=true;
        boolean logResponseHeaders=true;
        boolean logResponseContent=true;
        boolean logRequestParameters=true;
        
        PathAndQuery path=new PathAndQuery();
        if (root!=null)
        {
            path.addSegment(root);
        }
        if (classPath!=null)
        {
            path.addSegment(classPath.value());
        }
        if (handlerAnnotations.path!=null)
        {
            path.addSegment(handlerAnnotations.path.value());
        }
        
        
        String fullPath = path.toString();
        if (fullPath.length()==0)
        {
            throw new Exception("@Path annotation missing at method or class level or no root provided. Site=" + object.getClass().getCanonicalName() + "." + method.getName());
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
            fullPath=fullPath.substring(0, fullPath.length()-1)+object.getClass().getSimpleName()+"/"+method.getName(); 
        }
	    Filter[] filters=handlerFilters.toArray(new Filter[handlerFilters.size()]);
	    
        RequestHandler requestHandler = new RequestHandler(object, method, httpMethod, fullPath, filters,
                parameterInfos.toArray(new ParameterInfo[parameterInfos.size()]), contentDecoderMap, contentEncoderMap, contentReaderMap, contentWriterMap,
                log,logRequestHeaders,logRequestParameters,logRequestContent,logResponseHeaders,logResponseContent,logLastRequestsInMemory,
                true,this.bufferSize);
        add(httpMethod, fullPath, requestHandler);
        
        for (Filter filter:filters)
        {
            filter.onRegister(requestHandler);
        }
        
	}
	

	void add(String method, String path, RequestHandler requestHandler) throws Exception
	{
		Map map = getMap(method);
		if (map == null)
		{
			throw new Exception("Invalid http method=" + method + ". Site=" + requestHandler.getMethod().getName());
		}
		this.requestHandlers.put(requestHandler.getKey(), requestHandler);
		String[] fragments = Utils.split(path, '/');
		if (fragments[0].length() != 0)
		{
			throw new Exception("Path must start with root / character. Site=" + requestHandler.getMethod().getName());
		}
		Node node = null;
		HashMap<String, Integer> indexMap = new HashMap<>();
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
					throw new Exception("Path parameter names must be unique. Path=" + path + ", site=" + requestHandler.getMethod().getName());
				}
				indexMap.put(name, index++);
				if (PathParam.AT_LEAST_ONE_SEGMENT.equals(name))
				{
					if (i != fragments.length - 1)
					{
						throw new Exception("Rest of path must be last path parameter. Path=" + path + ", site=" + requestHandler.getMethod().getName());
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
				node = new Node();
				map.put(key, node);
			}
			if (PathParam.AT_LEAST_ONE_SEGMENT.equals(key))
			{
				break;
			}
			map = node.map;
		}
		if (node.requestHandler != null)
		{
			throw new Exception(
					"Path conflict. Existing Path=" + node.requestHandler.getPath() + ", existing requestHandler=" + node.requestHandler.getObject().getClass().getName()+":"+node.requestHandler.getMethod().getName()
							 +", new Path=" + requestHandler.getPath() + ", new request handler=" + requestHandler.getObject().getClass().getName()+":"+requestHandler.getMethod().getName());
		}
		ParameterInfo[] parameterInfos = requestHandler.getParameterInfos();
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
		node.requestHandler = requestHandler;
	}

	RequestHandlerWithParameters resolve(String method, String path)
	{
		Map map = getMap(method);
		if (map == null)
		{
			return null;
		}
		String[] fragments = Utils.split(path, '/');
		Node node = null;
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
		if (node.requestHandler == null)
		{
			return null;
		}
		return new RequestHandlerWithParameters(node.requestHandler, parameters);
	}
}
