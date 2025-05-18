package org.nova.http.server;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.nova.tracing.Trace;

public class WebSocketContext
{
    final private Session session;
    final private Object state;
    public WebSocketContext(Session session,Object state)
    {
        this.session=session;
        this.state=state;
    }
    
    @SuppressWarnings("unchecked")
    public <STATE> STATE getState()
    {
        return (STATE)this.state;
    }
    
    public void sendText(Trace parent,String text) throws Throwable
    {
        this.session.getRemote().sendString(text);
    }
}
