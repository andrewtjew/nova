package org.nova.http.server;

import org.nova.tracing.Trace;

public interface WebSocketHandling
{
    default public void onWebSocketConnect(Trace parent,WebSocketContext context)
    {
    }
    default public void onWebSocketClose(Trace parent,int statusCode,String reason) throws Throwable
    {
    }
    default public void onWebSocketError(Trace parent,Throwable throwable) throws Throwable
    {
    }
    default public String onWebSocketText(Trace parent,String text) throws Throwable
    {
        return text;
    }
    
    default public void onWebSocketBinary(Trace parent,byte[] bytes, int offset, int length)
    {
    }
    
    
}
