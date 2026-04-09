package org.nova.service.deviceSession;

import java.time.ZoneId;
import org.nova.geo.LatitudeLongitude;
import org.nova.http.server.Context;
import org.nova.localization.CountryCode;
import org.nova.security.PathAndQueryAuthentication;


public class Session implements PathAndQueryAuthentication
{
    final protected DeviceSession deviceSession;
    
    public Session(DeviceSession deviceSession) throws Throwable
    {
        this.deviceSession=deviceSession;
    }
    
    public DeviceSession getDeviceSession()
    {
        return this.deviceSession;
    }
    
    public long getDeviceSessionId()
    {
        return this.getDeviceSessionId();
    }
    public ZoneId getZoneId()
    {
        return this.deviceSession.getZoneId();
    }
    public CountryCode getCountryCode()
    {
        return this.deviceSession.getCountryCode();
    }

    public LatitudeLongitude getPosition()
    {
        return this.deviceSession.getPosition();
    }

    @Override
    public String signPathAndQuery(String pathAndQuery) throws Throwable
    {
        return this.deviceSession.signPathAndQuery(pathAndQuery);
    }
    
    @Override
    public boolean isRequestAuthentic(Context context) throws Throwable
    {
        return this.deviceSession.isRequestAuthentic(context);
    }
    
    
}
