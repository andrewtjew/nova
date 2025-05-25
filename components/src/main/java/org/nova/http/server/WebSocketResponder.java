package org.nova.http.server;

import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.nova.core.ObjectBox;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.remote.RemoteStateBinding;
import org.nova.http.server.annotations.ParamName;
import org.nova.json.ObjectMapper;
import org.nova.logging.Logger;
import org.nova.services.SessionManager;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class WebSocketResponder implements WebSocketListener
{
    final private TraceManager traceManager;
    final private Logger logger;
    final private WebSocketInitializer<?> initializer;
    final private WebSocketHandling handler;
    private WebSocketContext context;
    
    public WebSocketResponder(TraceManager traceManager,Logger logger,WebSocketInitializer<?> initializer)
    {
        this.traceManager=traceManager;
        this.logger=logger;
        this.initializer=initializer;
        this.handler=initializer.createWebSocketHandler();
    }
    
    @Override
    public void onWebSocketConnect(Session session)
    {
        try (Trace trace=new Trace(this.traceManager,this.getClass().getSimpleName()+".onWebSocketConnect"))
        {
            try
            {
                this.context=new WebSocketContext(session,this.initializer.getState(trace, session));
                this.handler.onWebSocketConnect(trace,context);
            }
            catch (Throwable t)
            {
                trace.close(t);
            }
        }
    }
    
    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        try (Trace trace=new Trace(this.traceManager,this.getClass().getSimpleName()+".onWebSocketClose"))
        {
            try
            {
                this.handler.onWebSocketClose(trace,statusCode,reason);
            }
            catch (Throwable t)
            {
                trace.close(t);
            }
        }
    }

    @Override
    public void onWebSocketError(Throwable throwable)
    {
        try (Trace trace=new Trace(this.traceManager,this.getClass().getSimpleName()+".onWebSocketClose"))
        {
            try
            {
                this.handler.onWebSocketError(trace,throwable);
            }
            catch (Throwable t)
            {
                trace.close(t);
            }
        }
    }

    @Override
    public void onWebSocketBinary(byte[] bytes, int offset, int length)
    {
        try (Trace trace=new Trace(this.traceManager,this.getClass().getSimpleName()+".onWebSocketClose"))
        {
            try
            {
                this.handler.onWebSocketBinary(trace,bytes,offset,length);
            }
            catch (Throwable t)
            {
                trace.close(t);
            }
        }
    }
    public Object[] decodeParameters(Trace trace,WebSocketMethod method,String query,String content) throws Throwable
    {
        Object[] parameters;        
   //     String[] pathParameters=method.parameters;
        ParameterInfo[] parameterInfos=method.getParameterInfos();
        parameters=new Object[parameterInfos.length];
//        Object content=null;
        HashMap<String,String> queryMap=new HashMap<String, String>();
        if (query!=null)
        {
            String[] keyValues=query.split("&");
            for (String keyValue:keyValues)
            {
                String[] parts=keyValue.split("=");
                if (parts.length==2)
                {
                    queryMap.put(parts[0], URLDecoder.decode(parts[1], StandardCharsets.UTF_8));
                }
            }
        }
        
        
        for (int i=0;i<parameterInfos.length;i++)
        {
            ParameterInfo parameterInfo=parameterInfos[i];
            switch (parameterInfo.getSource())
            {
            case CONTENT:
                parameters[i]=ObjectMapper.readObject(content, parameterInfo.getType());
                break;
//            case PATH:
//            {
//                String parameter=null;
//                try
//                {
//                    parameter=URLDecoder.decode(pathParameters[parameterInfo.getPathIndex()],StandardCharsets.UTF_8);
//                }
//                catch (Throwable t)
//                {
//                    try
//                    {
//                        parameter=URLDecoder.decode(pathParameters[parameterInfo.getPathIndex()],StandardCharsets.UTF_8);
//                    }
//                    catch (Throwable tt)
//                    {
//                        parameter=request.getParameter(parameterInfo.getName());
//                    }
//                }
//                try
//                {
//                    parameters[i]=parseParameter(parameterInfo,parameter);
//                }
//                catch (Throwable t)
//                {
//                    throw new AbnormalException(Abnormal.BAD_PATH,t);
//                }
//                break;
//            }
            case QUERY:
                try
                {
                    String value=queryMap.get(parameterInfo.getName());
                    parameters[i]=HttpServer.parseParameter(parameterInfo,value);
                }
                catch (Throwable t)
                {
                    throw new AbnormalException(Abnormal.BAD_QUERY,t);
                }
                break;
            case CONTEXT:
                parameters[i]=this.context;
                break;
            case STATE:
                parameters[i]=context.getState();
                break;
            case TRACE:
                parameters[i]=trace;
                break;
            }
        }
        return parameters;
    }

    Object invoke(WebSocketMethod method,Object[] parameters) throws Throwable
    {
        ParameterInfo[] parameterInfos=method.getParameterInfos();
        try
        {
            return method.getMethod().invoke(this.handler, parameters);
        }
        catch (IllegalArgumentException e)
        {
            StringBuilder sb=new StringBuilder("IllegalArguments for "+method.getPath());
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
    
    static class PathCommand
    {
        public String pathAndQuery;
        public String content;
    }
    
    @Override
    public void onWebSocketText(String text)
    {
        try (Trace trace=new Trace(this.traceManager,this.getClass().getSimpleName()+".onWebSocketText"))
        {
            try
            {
                String commandMessage=this.handler.onWebSocketText(trace, text);
                System.out.println("commandMessage:"+commandMessage);
                if (commandMessage!=null)
                {
                    PathCommand command=ObjectMapper.readObject(commandMessage, PathCommand.class);
                    if (command!=null)
                    {
                        String path;
                        String query;
                        int index=command.pathAndQuery.indexOf("?");
                        if (index>=0)
                        {
                            path=command.pathAndQuery.substring(0,index);
                            query=command.pathAndQuery.substring(index+1);
                        }
                        else
                        {
                            path=command.pathAndQuery;
                            query=null;
                        }
                        
                        WebSocketMethod method=this.initializer.methods.get(path);
                        if (method!=null)
                        {
                            Object[] parameters=decodeParameters(trace, method, query,command.content);
                            Object response=invoke(method, parameters);
                            if (response!=null)
                            {
                                if (response instanceof RemoteResponse)
                                {
                                    String responseText=ObjectMapper.writeObjectToString(((RemoteResponse)response).getInstructions());
                                    this.context.sendText(trace, responseText);
                                    
                                }
                                else
                                {
                                    String responseText=ObjectMapper.writeObjectToString(response);
                                    this.context.sendText(trace, responseText);
                                }
                            }
                        }
                        else
                        {
                            //log
                        }
                    }
                    
                }
            }
            catch (Throwable t)
            {
                trace.close(t);
            }
        }
    }
}
