package org.nova.http.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.nova.logging.Logger;
import org.nova.services.SessionManager;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;

public abstract class WebSocketServer<STATE> implements WebSocketListener
{
    final private TraceManager traceManager;
    final private Logger logger;
    private WebSocketHandlerState<STATE> handlerState;
    protected STATE session;
    

    abstract protected WebSocketHandlerState<STATE> createWebSocketHandlerState(Trace parent,Session session) throws Throwable;
    abstract protected boolean handle(String text) throws Throwable;
    
    public WebSocketServer(TraceManager traceManager,Logger logger)
    {
        this.traceManager=traceManager;
        this.logger=logger;
    }
    
    @Override
    public void onWebSocketConnect(Session session)
    {
        try (Trace trace=new Trace(this.traceManager,this.getClass().getSimpleName()+".onWebSocketConnect"))
        {
            try
            {
                this.handlerState=createWebSocketHandlerState(trace,session);
                this.handlerState.websocketHandler().onWebSocketConnect(trace);
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
                this.handlerState.websocketHandler().onWebSocketClose(trace,statusCode,reason);
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
                this.handlerState.websocketHandler().onWebSocketError(trace,throwable);
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
                this.handlerState.websocketHandler().onWebSocketBinary(trace,bytes,offset,length);
            }
            catch (Throwable t)
            {
                trace.close(t);
            }
        }
    }

    @Override
    public void onWebSocketText(String text)
    {
        try (Trace trace=new Trace(this.traceManager,this.getClass().getSimpleName()+".onWebSocketText"))
        {
            try
            {
                handle(this.handlerState.websocketHandler().onWebSocketText(trace, text));
            }
            catch (Throwable t)
            {
                trace.close(t);
            }
        }
    }
}
