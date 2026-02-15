package org.nova.http.server;

import java.net.HttpCookie;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.websocket.api.Session;
import org.nova.json.ObjectMapper;
import org.nova.services.DeviceCookieState;
import org.nova.services.DeviceSession;
import org.nova.services.SimpleDeviceSessionFilter;
import org.nova.tracing.Trace;

public class DeviceSessionWebSocketInitializer<ROLE extends Enum<?>,DEVICESESSION extends DeviceSession<ROLE>,COOKIESTATE extends DeviceCookieState> extends WebSocketInitializer<DEVICESESSION>
{
    final private SimpleDeviceSessionFilter<ROLE,DEVICESESSION,COOKIESTATE> deviceSessionFilter;
    public DeviceSessionWebSocketInitializer(String webSocketPath,SimpleDeviceSessionFilter<ROLE,DEVICESESSION,COOKIESTATE> deviceSessionFilter,Class<? extends WebSocketHandling> handlerType,WebSocketHandlingInitialization<? extends WebSocketHandling> handlerInitialization) throws Throwable
    {
        super(webSocketPath,handlerType,handlerInitialization);
        this.deviceSessionFilter=deviceSessionFilter;
    }
    @Override
    public DEVICESESSION getState(Trace parent, Session session) throws Throwable
    {
        String cookieStateName=this.deviceSessionFilter.getCookieStateName();
        Class<COOKIESTATE> cookieStateType=this.deviceSessionFilter.getCookieStateType();
        for (HttpCookie cookie : session.getUpgradeRequest().getCookies())
        {
            if (cookieStateName.equals(cookie.getName()))
            {
                String value = cookie.getValue();
                value = URLDecoder.decode(value, StandardCharsets.UTF_8);
                var cookieState=ObjectMapper.readObject(value, cookieStateType);
                DEVICESESSION deviceSession=this.deviceSessionFilter.getSessionManager().getSessionByToken(cookieState.getToken());
                return deviceSession;
            }
        }
        return null;
    }

}
