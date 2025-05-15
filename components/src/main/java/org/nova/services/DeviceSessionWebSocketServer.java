package org.nova.services;

import java.net.HttpCookie;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.nova.http.server.WebSocketServer;
import org.nova.http.server.WebSocketHandler;
import org.nova.http.server.WebSocketHandlerState;
import org.nova.json.ObjectMapper;
import org.nova.logging.Logger;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;

import jakarta.servlet.http.Cookie;

abstract public class DeviceSessionWebSocketServer<ROLE extends Enum<?>,SESSION extends DeviceSession<ROLE>,COOKIESTATE extends DeviceCookieState> extends WebSocketServer<SESSION>
{
    final private String rootPath;
    final private DeviceSessionFilter<ROLE,SESSION,COOKIESTATE> deviceSessionFilter;
    public DeviceSessionWebSocketServer(TraceManager traceManager, Logger logger,DeviceSessionFilter<ROLE,SESSION,COOKIESTATE> deviceSessionFilter,String rootPath)
    {
        super(traceManager, logger);
        this.deviceSessionFilter=deviceSessionFilter;
        this.rootPath=rootPath;
    }

    @Override
    protected boolean handle(String text) throws Throwable
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected WebSocketHandlerState<SESSION> createWebSocketHandlerState(Trace parent, org.eclipse.jetty.websocket.api.Session session) throws Throwable
    {
        String cookieStateName=this.deviceSessionFilter.getCookieStateName();
        Class<COOKIESTATE> cookieStateType=this.deviceSessionFilter.getCookieStateType();
        for (HttpCookie cookie : session.getUpgradeRequest().getCookies())
        {
            if (cookieStateName.equals(cookie.getName()))
            {
                String value = cookie.getValue();
                value = URLDecoder.decode(value, StandardCharsets.UTF_8);
                COOKIESTATE cookieState=ObjectMapper.readObject(value, cookieStateType);
                SESSION sessionState=this.deviceSessionFilter.getSessionManager().getSessionByToken(cookieState.getToken());
                var objectState=new WebSocketHandlerState<SESSION>(createWebSocketHandler(parent,sessionState,session), sessionState);
                return objectState;
            }
        }
        return null;
    }
    
    public static class WebSocketObjectHandler
    {
        
    }
    
    public abstract WebSocketHandler<SESSION> createWebSocketHandler(Trace parent,SESSION state,Session session);

}
