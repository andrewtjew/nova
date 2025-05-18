package org.nova.http.server;

import java.io.InterruptedIOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.nova.logging.Logger;
import org.nova.services.SessionManager;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;

public class WebSocketResponder implements WebSocketListener
{
    final private TraceManager traceManager;
    final private Logger logger;
    final private WebSocketInitializer<?> initializer;
    final private WebSocketHandling handler;
    
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
                WebSocketContext context=new WebSocketContext(session,this.initializer.getState(trace, session));
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

    
    static class CommandMessage
    {
        public String path;
        public String query;
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
            }
            catch (Throwable t)
            {
                trace.close(t);
            }
        }
    }
}
