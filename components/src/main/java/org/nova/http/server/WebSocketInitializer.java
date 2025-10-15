package org.nova.http.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jetty.websocket.api.Session;
import org.nova.html.ext.HtmlUtils;
import org.nova.http.server.RequestMethodMap.DistanceContentWriter;
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
import org.nova.http.server.annotations.ParamName;
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

abstract public class WebSocketInitializer<STATE>
{
    final private String webSocketPath;
    final private WebSocketHandlingInitialization<?> handlingInitialization;
    final HashMap<String,WebSocketMethod> methods;
    
    public WebSocketInitializer(String webSocketPath,Class<? extends WebSocketHandling> handlerType,WebSocketHandlingInitialization<?> handlingInitialization) throws Throwable
    {
        this.handlingInitialization=handlingInitialization;
        this.webSocketPath=webSocketPath;
        this.methods=new HashMap<String, WebSocketMethod>();
        register(handlerType);
    }
    
    
    void register(Class<?> objectType) throws Throwable
    {
        WebSocketHandlerClassAnnotations classAnnotations = new WebSocketHandlerClassAnnotations();
        for (Class<?> classType=objectType;classType!=null;classType=classType.getSuperclass())
        {
            if (Modifier.isPublic(classType.getModifiers())==false)
            {
                throw new Exception("Class must be public. Site=" + classType.getCanonicalName());
            }
            for (Annotation annotation : classType.getAnnotations())
            {
                Class<?> type = annotation.annotationType();
                if (type == Log.class)
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
            registerMethod(method, new WebSocketHandlerClassAnnotations(classAnnotations));
        }
    }
    static String toSite(Class<?> objectType,Method method)
    {
        return objectType.getName()+"."+method.getName();
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
    
    private void registerMethod(Method method, WebSocketHandlerClassAnnotations handlerAnnotations) throws Throwable
    {
        Class<?> objectType=method.getDeclaringClass().getClass();
//        String httpMethod = null;
//        int verbs = 0;
        Path classPath=handlerAnnotations.path;
        handlerAnnotations.path=null;
        
        for (Annotation annotation : method.getAnnotations())
        {
            Class<?> type = annotation.annotationType();
            if (type == RequiredRoles.class)
            {
                handlerAnnotations.requiredRoles = (RequiredRoles) annotation;
            }
            else if (type == ForbiddenRoles.class)
            {
                handlerAnnotations.forbiddenRoles = (ForbiddenRoles) annotation;
            }
            else if (type == Log.class)
            {
                handlerAnnotations.log = (Log) annotation;
            }
            else if (type == Test.class)
            {
                handlerAnnotations.test= (Test) annotation;
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
        StringBuilder path=new StringBuilder();
        if (classPath!=null)
        {
            path.append(classPath.value());
        }
        if (handlerAnnotations.path!=null)
        {
            path.append(handlerAnnotations.path.value());
        }
        else
        {
            return;
        }
        
        String fullPath = path.toString();
        if (fullPath.length()==0)
        {
            throw new Exception("@Path annotation missing at method or class level or no root provided. Site=" + toSite(objectType,method));
        }

        ArrayList<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Parameter[] parameters=method.getParameters();
        Annotation[][] annotations = method.getParameterAnnotations();
        HashSet<String> hiddenParameters=new HashSet<String>();
        for (int parameterIndex = 0; parameterIndex < parameterTypes.length; parameterIndex++)
        {
            Class<?> parameterType = parameterTypes[parameterIndex];
            Parameter parameter=parameters[parameterIndex];
            Annotation[] parameterAnnotations = annotations[parameterIndex];

            DefaultValue defaultValue = null;
            Required required=null;
            ContentParam contentParam = null;
            PathParam pathParam = null;
            QueryParam queryParam = null;
            StateParam stateParam = null;
            
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
                    contentParam = (ContentParam) annotation;
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
            }

            ArrayList<Annotation> params = new ArrayList<Annotation>();
            if (contentParam != null)
            {
                params.add(contentParam);
            }
            if (pathParam != null)
            {
                params.add(pathParam);
            }
            if (queryParam != null)
            {
                params.add(queryParam);
            }
            if (stateParam != null)
            {
                params.add(stateParam);
            }

            // No multiple param annotations. Check each and add.
            
            if (parameterType == Trace.class)
            {
                rejectDefaultValueAndRequired(objectType, method, parameter, defaultValue, required);
                parameterInfos.add(new ParameterInfo(ParameterSource.TRACE, null,parameter.getName(), parameterIndex, parameterType, null,true));
                continue;
            }
            else if (parameterType == WebSocketContext.class)
            {
                rejectDefaultValueAndRequired(objectType, method, parameter, defaultValue, required);
                parameterInfos.add(new ParameterInfo(ParameterSource.CONTEXT, null,parameter.getName(), parameterIndex, parameterType, null,true));
                continue;
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
            else if (queryParam != null)
            {
                parameterInfos.add(new ParameterInfo(ParameterSource.QUERY, queryParam, queryParam.value(), parameterIndex, parameterType,
                        HtmlUtils.getDefaultValue(method, defaultValue, parameterType),required!=null));
            }
            else
            {
                parameterInfos.add(new ParameterInfo(ParameterSource.INTERNAL, null, parameter.getName(), parameterIndex, parameterType,
                        null,required!=null));
            }
        }

        Type returnType=method.getReturnType();


        boolean log=true;
        boolean logLastRequestsInMemory=true;
        boolean logRequestContent=true;
        boolean logResponseContent=true;
        boolean logRequestParameters=true;
        
        if (handlerAnnotations.log!=null)
        {
            log=handlerAnnotations.log.value();
            if (log==true)
            {
                logLastRequestsInMemory=handlerAnnotations.log.lastRequestsInMemory();
                logRequestParameters=handlerAnnotations.log.requestParameters();
                logRequestContent=handlerAnnotations.log.requestContent();
                logResponseContent=handlerAnnotations.log.responseContent();
            }
            else
            {
                logLastRequestsInMemory=false;
                logRequestContent=false;
                logResponseContent=false;
            }
        }
        if (fullPath.endsWith("/#"))
        {
            fullPath=fullPath.substring(0, fullPath.length()-1)+method.getName();
        }
        else if (fullPath.endsWith("/@"))
        {
            fullPath=fullPath.substring(0, fullPath.length()-1)+objectType.getSimpleName()+"/"+method.getName(); 
        }
        WebSocketMethod webSocketMethod = new WebSocketMethod(method, fullPath 
                ,parameterInfos.toArray(new ParameterInfo[parameterInfos.size()]), 
                log,logRequestParameters,logRequestContent,logResponseContent,logLastRequestsInMemory,
                10*1024,handlerAnnotations,hiddenParameters.size()==0?null:hiddenParameters);
        this.methods.put(webSocketMethod.getPath(), webSocketMethod);
    }
    
    Map<String,WebSocketMethod> getMethods()
    {
        return this.methods;
    }
    
    public String getWebSocketPath()
    {
        return this.webSocketPath;
    }
    public WebSocketHandling createWebSocketHandler()
    {
        return this.handlingInitialization.createWebSocketHandler();
    }
    public abstract STATE getState(Trace parent,Session session) throws Throwable;
    
}
