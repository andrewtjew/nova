package org.nova.http.server;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.eclipse.jetty.websocket.api.Session;
import org.nova.html.remote.RemoteResponse;
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
        try (Trace trace=new Trace(parent, "WebSocketContext.sendText"))
        {
            try 
            {
                this.session.getRemote().sendString(text);
            }
            catch (Throwable t)
            {
                trace.close(t);
            }
        }
    }

    
    public void sendBinary(Trace parent,byte[] data,int offset,int length) throws Throwable
    {
        try (Trace trace=new Trace(parent, "WebSocketContext.sendBinary"))
        {
            try 
            {
                this.session.getRemote().sendBytes(ByteBuffer.wrap(data, offset,length));
            }
            catch (Throwable t)
            {
                trace.close(t);
            }
        }
    }
    public void sendBinary(Trace parent,byte[] data) throws Throwable
    {
        sendBinary(parent,data,0,data.length);
    }
}
