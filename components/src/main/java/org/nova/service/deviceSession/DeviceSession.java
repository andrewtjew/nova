package org.nova.service.deviceSession;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.HashMap;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.geo.GeoLocation;
import org.nova.geo.LatitudeLongitude;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.operator.NameValueList;
import org.nova.http.server.Context;
import org.nova.http.server.Filter;
import org.nova.localization.CountryCode;
import org.nova.metrics.RateMeter;
import org.nova.security.PathAndQuerySecurity;
import org.nova.security.SecurityUtils;
import org.nova.services.AbnormalResult;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;


public class DeviceSession implements PathAndQuerySecurity
{
    static public record DeviceLocation(GeoLocation location,long created)
    {
        public DeviceLocation(GeoLocation location)
        {
            this(location,System.currentTimeMillis());
        }
    }
    
    Throwable throwable;
    
    final static boolean DEBUG=true;
    final boolean DEBUG_REQUEST_AUTHENTICATION=true;
    final static String LOG_DEBUG_CATEGORY=DeviceSession.class.getSimpleName();
    
    final private String querySecurityKey;
    final private long deviceSessionId;
    final protected SecretKey secretKey;
    final private String language;
    private LatitudeLongitude position; 
    private CountryCode countryCode;
    private ZoneId zoneId;
    private long positionLastUpdated;
    
    final String token;
    final long created;
    private long lastAccess;
    private RateMeter accessRateMeter;
    private Context context;
    
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

    public DeviceSession(String querySecurityKey,long deviceSessionId,String token,String language,LatitudeLongitude position,CountryCode countryCode,ZoneId zoneId) throws Throwable
    {
        this.querySecurityKey=querySecurityKey;
        this.token=token;
        this.lastAccess=this.created=System.currentTimeMillis();
        this.accessRateMeter=new RateMeter();

        this.secretKey=new SecretKeySpec(generateSecretKey(token+System.currentTimeMillis()),"HmacSHA512");
        this.deviceSessionId=deviceSessionId;
        this.language=language;
        updateLocation(position, countryCode, zoneId);
        this.states=new HashMap<Integer, Object>();
    }
    
    public String getQuerySecurityKey()
    {
        return this.querySecurityKey;
    }
    
    public long getLastAccess()
    {
        return lastAccess;
    }
    public long getCreated()
    {
        return created;
    }

    public RateMeter getAccessRateMeter()
    {
        return this.accessRateMeter;
    }    
    public void updateLocation(LatitudeLongitude position,CountryCode countryCode,ZoneId zoneId)
    {
        this.position=position;
        this.countryCode=countryCode;
        this.zoneId=zoneId;
        this.positionLastUpdated=System.currentTimeMillis();
    }
    
    void setContext(Context context)
    {
        this.context=context;
    }
    
    public Context getContext()
    {
        return this.context;
    }

    public String getLanguage()
    {
        return this.language;
    }
    public long getSessionId()
    {
        return this.deviceSessionId;
    }
    public String getSessionToken()
    {
        return token;
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
    public long getPositionLastUpdated()
    {
        return this.positionLastUpdated;
    }
    
    final public AbnormalResult verifyRequest(Trace parent,Context context,Filter filter) throws Throwable
    {
        if (this.isRequestSecure(context)==false)
        {
            return new AbnormalResult();
        }
        return null;
    }

    public void close(Trace parent) throws Throwable
    {
        for (var state:this.states.values())
        {
            if (state!=null)
            {
                if (state instanceof SessionRequestHandling)
                {
                    ((SessionRequestHandling)state).onClose(parent);
                }
            }
        }
        this.states.clear();
    }

    final private HashMap<Integer,Object> states;
    
    public <STATE> STATE getState(Class<?> type)
    {
        int id=System.identityHashCode(type);
        return (STATE)this.states.get(id);
    }
    public void setState(Object state)
    {
        int id=System.identityHashCode(state.getClass());
        this.states.put(id, state);
    }
    public void removeState(Class<?> type)
    {
        int id=System.identityHashCode(type);
        this.states.remove(id);
    }
    public void clearStates()
    {
        this.states.clear();
    }

    
//    final public static String QUERY_SECURITY_KEY="_";
//    final public static String QUERY_SECURITY_PREFIX=QUERY_SECURITY_KEY+"=";
    
    @Override
    public String securePathAndQuery(String pathAndQuery) throws Throwable
    {
        byte[] objectBytes=pathAndQuery.getBytes(StandardCharsets.UTF_8);
        byte[] hmac=SecurityUtils.computeHashHMACSHA256(this.secretKey, objectBytes);
        String code=Base64.getUrlEncoder().encodeToString(hmac);
        
        
        var signed=pathAndQuery.indexOf('?')>0?pathAndQuery+"&"+this.querySecurityKey+"="+code:pathAndQuery+"?"+this.querySecurityKey+"="+code;
        if (Debug.ENABLE && DEBUG && DEBUG_REQUEST_AUTHENTICATION)
        {
            Debugging.log(LOG_DEBUG_CATEGORY,"secured pathAndQuery="+signed+",original pathAndQuery="+pathAndQuery);
        }
        return signed;
    }

    @Override
    public boolean isRequestSecure(Context context) throws Throwable
    {
        var requestMethod=context.getRequestMethod();
        if (requestMethod.isPathAndQueryAuthenticationRequired()==false)
        {
            return true;
        }
        String queryString=HtmlUtils.getRequestPathAndQuery(context);
        try 
        {
            int index=queryString.lastIndexOf(this.querySecurityKey+"=");
            if (index<0)
            {
                if (Debug.ENABLE && DEBUG && DEBUG_REQUEST_AUTHENTICATION)
                {
                    Debugging.log(LOG_DEBUG_CATEGORY,"Authentication required: method="+requestMethod.getKey()+",pathAndQuery="+queryString);
                }
                return false;
            }
            String text=queryString.substring(0,index-1);
            String code=queryString.substring(index+this.querySecurityKey.length()+1);
            if (Debug.ENABLE && DEBUG && DEBUG_REQUEST_AUTHENTICATION)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"method="+requestMethod.getKey()+",pathAndQuery="+queryString+",text="+text+", code="+code);
            }
            byte[] hmac=SecurityUtils.computeHashHMACSHA256(this.secretKey, text.getBytes());
            String computed=Base64.getUrlEncoder().encodeToString(hmac);
            var result=code.equals(computed);
            if (Debug.ENABLE && DEBUG && DEBUG_REQUEST_AUTHENTICATION)
            {
                if (result==false)
                {
                    Debugging.log(LOG_DEBUG_CATEGORY,"Authentication failed: method="+requestMethod.getKey()+",pathAndQuery="+queryString+",text="+text+", code="+code+", computed="+computed+", result="+result);
                }
            }
            return result;
        }
        catch (Throwable t)
        {
            if (Debug.ENABLE && DEBUG && DEBUG_REQUEST_AUTHENTICATION)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"Authentication exception: method="+requestMethod.getKey()+",pathAndQuery="+queryString);
                Debugging.log(LOG_DEBUG_CATEGORY,Utils.getStrackTraceAsString(t));
            }
            return false;
        }
    }
    
    public static LocalDateTime toUTC(long epoch_ms)
    {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch_ms), ZoneId.of("UTC"));
    }
       
    void fill(NameValueList list)
    {
        
        list.add("Token",token);
        list.add("DeviceSessionID",this.deviceSessionId);
        list.add("Created",toUTC(this.created));
        list.add("LastAccess",toUTC(this.lastAccess));
        var sample=this.accessRateMeter.sample();
        list.add("AccessRate","instantenous rate="+String.format("%.2f", sample.getRate())+", weighted rate="+String.format("%.2f", sample.getWeightedRate())+", total="+sample.getTotalCount());
        list.add("ZoneId",this.zoneId);
        list.add("Language",this.language);
        list.add("CountryCode",this.countryCode);
        if (this.position!=null)
        {
            list.add("Position","lattitude="+this.position.latitude+", longitude="+this.position.longitude);
            list.add("PositionLastUpdated",toUTC(this.positionLastUpdated));
        }
        for (var state:this.states.values())
        {
            if (state instanceof AdditionalSessionInformation)
            {
                ((AdditionalSessionInformation)state).fill(list);
            }
        }

        
    }
}
