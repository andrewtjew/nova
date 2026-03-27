package org.nova.service.deviceSession;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.nova.core.NameObject;
import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.geo.GeoLocation;
import org.nova.geo.LatitudeLongitude;
import org.nova.html.ext.HtmlUtils;
import org.nova.http.server.Context;
import org.nova.http.server.Filter;
import org.nova.localization.CountryCode;
import org.nova.security.PathAndQueryAuthentication;
import org.nova.security.SecurityUtils;
import org.nova.services.RoleSession;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;


public class DeviceSession<ROLE extends Enum<?>> extends RoleSession<ROLE> implements PathAndQueryAuthentication
{
    static public record DeviceLocation(GeoLocation location,long created)
    {
        public DeviceLocation(GeoLocation location)
        {
            this(location,System.currentTimeMillis());
        }
    }
    
    Throwable throwable;
    
    final boolean DEBUG=true;
    final boolean DEBUG_REQUEST_AUTHENTICATION=true;
    final static String LOG_DEBUG_CATEGORY=DeviceSession.class.getSimpleName();
    
    
    final private long deviceSessionId;
    final protected SecretKey secretKey;
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
    
    public DeviceSession(long deviceSessionId,String token,String language,LatitudeLongitude position,CountryCode countryCode,ZoneId zoneId,Class<ROLE> roleType) throws Throwable
    {
        super(roleType,token, null);
        this.secretKey=new SecretKeySpec(generateSecretKey(token),"HmacSHA512");
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
    
    @Override
    final public AbnormalResult verifyRequest(Trace parent,Context context,Filter filter) throws Throwable
    {
        {
            AbnormalResult abnormalAccept=super.verifyRequest(parent, context,filter);
            if (abnormalAccept!=null)
            {
                return abnormalAccept;
            }
        }
        if (this.isRequestAuthentic(context)==false)
        {
            return new AbnormalResult();
        }
        return null;
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
    
    private Object state;
    
    @SuppressWarnings("unchecked")
    public <STATE> STATE getState()
    {
        return (STATE)this.state;
    }
    
    public void setState(Object state)
    {
        this.state=state;
    }
    public void clearState()
    {
        this.state=null;
    }
    

    final static String QUERY_SECURITY_KEY="_";
    final static String QUERY_SECURITY_PREFIX=QUERY_SECURITY_KEY+"=";
    
    @Override
    public String signPathAndQuery(String pathAndQuery) throws Throwable
    {
        byte[] objectBytes=pathAndQuery.getBytes(StandardCharsets.UTF_8);
        byte[] hmac=SecurityUtils.computeHashHMACSHA256(this.secretKey, objectBytes);
        String code=Base64.getUrlEncoder().encodeToString(hmac);
        
        
        var signed=pathAndQuery.indexOf('?')>0?pathAndQuery+"&"+QUERY_SECURITY_PREFIX+code:pathAndQuery+"?"+QUERY_SECURITY_PREFIX+code;
        if (Debug.ENABLE && DEBUG && DEBUG_REQUEST_AUTHENTICATION)
        {
            Debugging.log(LOG_DEBUG_CATEGORY,"signed method="+signed+",text="+pathAndQuery);
        }
        return signed;
    }

    @Override
    public boolean isRequestAuthentic(Context context) throws Throwable
    {
        var requestMethod=context.getRequestMethod();
        if (requestMethod.isRequestAuthenticationRequired()==false)
        {
            return true;
        }
        String queryString=HtmlUtils.getRequestPathAndQuery(context);
        try 
        {
            int index=queryString.lastIndexOf(QUERY_SECURITY_PREFIX);
            String text=queryString.substring(0,index-1);
            String code=queryString.substring(index+QUERY_SECURITY_PREFIX.length());
            if (Debug.ENABLE && DEBUG && DEBUG_REQUEST_AUTHENTICATION)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"method="+requestMethod.getKey()+",pathAndQuery="+queryString+",text="+text+", code="+code);
            }
            byte[] hmac=SecurityUtils.computeHashHMACSHA256(this.secretKey, text.getBytes());
            String computed=Base64.getUrlEncoder().encodeToString(hmac);
            var result=code.equals(computed);
            if (Debug.ENABLE && DEBUG && DEBUG_REQUEST_AUTHENTICATION)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"method="+requestMethod.getKey()+",pathAndQuery="+queryString+",text="+text+", code="+code+", computed="+computed+", result="+result);
            }
            return result;
        }
        catch (Throwable t)
        {
            if (Debug.ENABLE && DEBUG && DEBUG_REQUEST_AUTHENTICATION)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"method="+requestMethod.getKey()+",pathAndQuery="+queryString);
                Debugging.log(LOG_DEBUG_CATEGORY,Utils.getStrackTraceAsString(t));
                
            }
            return false;
        }
    }
    
    
    
    
}
