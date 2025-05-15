package org.nova.http.server;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.nova.html.remote.RemoteResponse;
import org.nova.json.ObjectMapper;
import org.nova.tracing.Trace;

public abstract class WebSocketHandler<STATE>
{
    final private Session session;
    final protected STATE state;
    protected WebSocketHandler(STATE state,Session session)
    {
        this.session=session;
        this.state=state;
    }
    public abstract void onWebSocketConnect(Trace parent) throws Throwable;
    public abstract void onWebSocketClose(Trace parent,int statusCode,String reason) throws Throwable;
    public abstract void onWebSocketError(Trace parent,Throwable throwable) throws Throwable;
    public abstract String onWebSocketText(Trace parent,String text) throws Throwable;
    public abstract void onWebSocketBinary(Trace parent,byte[] bytes, int offset, int length) throws Throwable;
    
    public void sendText(String text) throws Throwable
    {
        this.session.getRemote().sendString(text,null);
    }
    
    
}
