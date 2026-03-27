package org.nova.service.deviceSession;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;

import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.debug.LogLevel;
import org.nova.geo.GeoLocation;
import org.nova.geo.LatitudeLongitude;
import org.nova.html.elements.Element;
import org.nova.html.elements.FormElement;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.InputHidden;
import org.nova.html.remote.RemoteStateBinding;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.Context;
import org.nova.http.server.Filter;
import org.nova.http.server.RequestMethod;
import org.nova.http.server.Response;
import org.nova.localization.CountryCode;
import org.nova.security.PathAndQueryAuthentication;
import org.nova.security.SecurityUtils;
import org.nova.service.deviceSession.DeviceSession.DeviceLocation;
import org.nova.tracing.Trace;


public class Session
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
    
}
