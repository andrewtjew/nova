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

import org.nova.core.NameObject;
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
import org.nova.http.server.Context;
import org.nova.http.server.Filter;
import org.nova.http.server.RequestMethod;
import org.nova.http.server.Response;
import org.nova.localization.CountryCode;
import org.nova.security.QuerySecurity;
import org.nova.security.SecurityUtils;
import org.nova.services.RoleSession;
import org.nova.tracing.Trace;


public class DeviceSession<ROLE extends Enum<?>> extends RoleSession<ROLE> implements QuerySecurity
{
    static public record DeviceLocation(GeoLocation location,long created)
    {
        public DeviceLocation(GeoLocation location)
        {
            this(location,System.currentTimeMillis());
        }
    }
    
    final static boolean DEBUG=false;
    final static boolean DEBUG_SECURITY=false;
    final static String LOG_DEBUG_CATEGORY=DeviceSession.class.getSimpleName();
    
    
    final private long deviceSessionId;
    final protected SecretKey secretKey;
    final private String querySecurityPathPrefix;
    final private String language;
    private LatitudeLongitude position; 
    private CountryCode countryCode;
    private ZoneId zoneId;
    private long locationLastUpdated;
    static byte[] generateSecretKey(String token)
    {
        byte[] bytes = token.getBytes();
        byte[] secret = new byte[16]; 
        for (int i = 0; i < secret.length; i++)
        {
            int value = i;
            for (int j = 0; j < 8; j++)
            {
                value += bytes[(i * 3 + j) % bytes.length];
            }
            secret[i] = (byte) value;
        }
        return secret;
    }   
    
    public DeviceSession(long deviceSessionId,String token,Class<ROLE> roleType,String language,LatitudeLongitude position,CountryCode countryCode,ZoneId zoneId) throws Throwable
    {
        super(roleType,token, null);
        this.secretKey=new SecretKeySpec(generateSecretKey(token),"HmacSHA512");
        this.querySecurityPathPrefix="&"+this.getSecurityQueryKey()+"=";
        this.deviceSessionId=deviceSessionId;
        this.language=language;
        updateLocation(position, countryCode, zoneId);
    }
    
    public void updateLocation(LatitudeLongitude position,CountryCode countryCode,ZoneId zoneId)
    {
        this.position=position;
        this.countryCode=countryCode;
        this.zoneId=zoneId;
        this.locationLastUpdated=System.currentTimeMillis();
    }
    

    public String getLanguage()
    {
        return this.language;
    }
    
    public long getDeviceSessionId()
    {
        return this.deviceSessionId;
    }
    public LatitudeLongitude getPosition()
    {
        return this.position;
    }
    public ZoneId getZoneId()
    {
        return this.zoneId;
    }
    public CountryCode getCountryCode()
    {
        return this.countryCode;
    }
    public long getLocationLastUpdated()
    {
        return this.locationLastUpdated;
    }
    
    final static public String QUERY_KEY="@";

    @Override
    public String getSecurityQueryKey()
    {
        return QUERY_KEY;
    }

    @Override
    public AbnormalResult verifyRequest(Trace parent,Context context,Filter filter) throws Throwable
    {
        {
            AbnormalResult abnormalAccept=super.verifyRequest(parent, context,filter);
            if (abnormalAccept!=null)
            {
                return abnormalAccept;
            }
        }
        HttpServletRequest request=context.getHttpServletRequest();
        String queryString=request.getQueryString();
        if (queryString==null)
        {
            return null;
        }
        var requestMethod=context.getRequestMethod();
        if (requestMethod.isQueryVerificationRequired())
        {
//            if (getSecurityQueryKey()!=null)
//            {
//                //Require 
//                var map=context.getHttpServletRequest().getParameterMap();
//                int ignore=map.containsKey(getStateKey())?1:0;
//                if ((map.size()>ignore)&&(map.containsKey(getSecurityQueryKey())==false))
//                {
//                    if (DEBUG_SECURITY)
//                    {
//                        Debugging.log(LOG_DEBUG_CATEGORY,"No security key: expected key="+getSecurityQueryKey()+", method="+requestMethod.getMethod().getDeclaringClass().getName()+"."+requestMethod.getMethod().getName(),LogLevel.WARNING);
//                    }
//                    else
//                    {
//                        return new AbnormalResult<>();
//                    }
//                }
//            }
            if (getSecurityQueryKey()!=null)
            {
                var map=context.getHttpServletRequest().getParameterMap();
                if ((map.containsKey(getSecurityQueryKey())==false))
                {
                    if (DEBUG_SECURITY)
                    {
                        Debugging.log(LOG_DEBUG_CATEGORY,"No security key: expected key="+getSecurityQueryKey()+", method="+requestMethod.getMethod().getDeclaringClass().getName()+"."+requestMethod.getMethod().getName(),LogLevel.WARNING);
                    }
                    return new AbnormalResult();
                }
            }
        }
        
        String code=request.getParameter(getSecurityQueryKey());
        if (code!=null)
        {
            String query=request.getQueryString();
            int index=query.lastIndexOf(this.querySecurityPathPrefix);
            String text=query.substring(0,index);
            byte[] hmac=SecurityUtils.computeHashHMACSHA256(this.secretKey, text.getBytes());
            String computed=Base64.getUrlEncoder().encodeToString(hmac);
            return code.equals(computed)?null:new AbnormalResult();
        }
        if (Debug.ENABLE && DEBUG && DEBUG_SECURITY)
        {
            String query=request.getQueryString();
            Debugging.log(LOG_DEBUG_CATEGORY,"method="+requestMethod.getKey()+",query="+query);
        }
        return null;
    }
    
//    public void endRequest(Response<?> response)
//    {
//    }
    
    @Override
    public String signQuery(String query) throws Throwable
    {
        byte[] objectBytes=query.getBytes(StandardCharsets.UTF_8);
        byte[] hmac=SecurityUtils.computeHashHMACSHA256(this.secretKey, objectBytes);
        String code=Base64.getUrlEncoder().encodeToString(hmac);
        return query+this.querySecurityPathPrefix+code;
    }

    @Override
    public void onClose(Trace trace) throws Throwable
    {
    }

    @Override
    public NameObject[] getDisplayItems()
    {
        return null;
    }
    
    private PageStateRequestHandling state;
    
    @SuppressWarnings("unchecked")
    public <STATE extends PageStateRequestHandling> STATE getState()
    {
        return (STATE)this.state;
    }
    
    public void setState(PageStateRequestHandling state)
    {
        this.state=state;
    }
    public void clearState()
    {
        this.state=null;
    }
    
    
    
    
}
