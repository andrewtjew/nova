package org.nova.service.deviceSession;

import java.time.ZoneId;
import org.nova.geo.LatitudeLongitude;
import org.nova.http.server.Context;
import org.nova.localization.CountryCode;
import org.nova.security.PathAndQuerySecurity;


public class Session implements PathAndQuerySecurity
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
    
    public String getSessionToken()
    {
        return this.deviceSession.getSessionToken();
    }
    public long getSessionId()
    {
        return this.deviceSession.getSessionId();
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
    public String securePathAndQuery(String pathAndQuery) throws Throwable
    {
        return this.deviceSession.securePathAndQuery(pathAndQuery);
    }
    
    @Override
    public boolean isRequestSecure(Context context) throws Throwable
    {
        return this.deviceSession.isRequestSecure(context);
    }
    
    
}
