package org.nova.http.server;

public record WebSocketHandlerState<SESSION>(WebSocketHandler<SESSION> websocketHandler,SESSION session)
{
}
