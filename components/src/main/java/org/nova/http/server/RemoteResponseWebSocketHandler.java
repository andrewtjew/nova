package org.nova.http.server;

import org.nova.html.remote.RemoteResponse;
import org.nova.json.ObjectMapper;
import org.nova.services.Session;

public abstract class RemoteResponseWebSocketHandler<SESSION extends Session> extends WebSocketHandler<SESSION>
{

    protected RemoteResponseWebSocketHandler(SESSION state, org.eclipse.jetty.websocket.api.Session session)
    {
        super(state, session);
    }
    
    public void sendRemoteResponse(RemoteResponse response) throws Throwable
    {
        sendText(ObjectMapper.writeObjectToString(response.getInstructions()));
    }
    
}
