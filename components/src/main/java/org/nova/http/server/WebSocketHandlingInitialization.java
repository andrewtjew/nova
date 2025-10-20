package org.nova.http.server;

public interface WebSocketHandlingInitialization<HANDLER extends WebSocketHandling>
{
    public abstract HANDLER createWebSocketHandler();
}
