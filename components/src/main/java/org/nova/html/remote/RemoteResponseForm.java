package org.nova.html.remote;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.nova.html.elements.FormElement;
import org.nova.html.enums.method;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.JsObject;
import org.nova.http.server.Context;
import org.nova.http.server.FilterChain;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.StateParam;
import org.nova.tracing.Trace;


import org.nova.html.enums.enctype;

public abstract class RemoteResponseForm extends RemoteFormElement<RemoteResponseForm>
{
    public RemoteResponseForm()
    {
        super(true);
    }
    public abstract Method getRemoteResponseMethod();
    
    public static Method getRemoteResponseMethod(Class<? extends RemoteResponseForm> type)
    {
        for (Method method:type.getDeclaredMethods())
        {
            if (method.getReturnType()==RemoteResponse.class)
            {
                return method;
            }
        }
        return null;
    }
    
    public void invoke(Trace parent,Context context,RemoteResponse response) throws Throwable
    {
        Method method=getRemoteResponseMethod();
        if (method!=null)
        {
            Class<?>[] parameterTypes=method.getParameterTypes();
            Object[] parameters=new Object[parameterTypes.length];
            HttpServletRequest request=context.getHttpServletRequest();
            for (int i=0;i<parameters.length;i++)
            {
                Class<?> parameterType=parameterTypes[i];
                if (parameterType==Context.class)
                {
                    parameters[i]=context;
                }
                else if (parameterType==Trace.class)
                {
                    parameters[i]=parent;
                }
                else
                {
                    QueryParam queryParam=parameterType.getAnnotation(QueryParam.class);
                    if (queryParam!=null)
                    {
                        String name=queryParam.value();
                        String value=request.getParameter(name);
                        parameters[i]=FilterChain.parseParameter(parameterType, value);
                    }
                    else
                    {
                        StateParam stateParam=parameterType.getAnnotation(StateParam.class);
                        if (stateParam!=null)
                        {
                            parameters[i]=context.getStateParameter().get();
                        }
                    }
                }
            }
            method.invoke(this, parameters);
        }
        
    }
    
}