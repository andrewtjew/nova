package org.nova.services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;

import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.html.elements.FormElement;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.InputHidden;
import org.nova.html.remote.RemoteStateBinding;
import org.nova.http.server.Context;
import org.nova.http.server.RequestMethod;
import org.nova.security.QuerySecurity;
import org.nova.security.SecurityUtils;
import org.nova.tracing.Trace;


public abstract class DeviceSession<ROLE extends Enum<?>> extends RoleSession<ROLE> implements RemoteStateBinding,QuerySecurity
{
    final static String LOG_DEBUG_CATEGORY=DeviceSession.class.getSimpleName();
    final static boolean DEBUG=false;
    final static boolean DEBUG_PAGESTATE=false;
    final static boolean DEBUG_SECURITY=true;
    
    protected HashMap<String,Object> pageStates;
    protected HashMap<String,Object> newPageStates;
    protected Stack<String> continuations;
    protected Stack<String> newContinuations;
    
    final protected ZoneId zoneId;
    final private long deviceSessionId;
    final protected SecretKey secretKey;
    final private String querySecurityPathPrefix;

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
    
    public LocalDateTime toLocalDateTime(LocalDateTime utcDateTime)
    {
        return utcDateTime;
    }
    
    public LocalDateTime toUtcDateTime(LocalDateTime localDateTime)
    {
        return localDateTime;
    }
    
    public DeviceSession(long deviceSessionId,String token,ZoneId zoneId,Class<ROLE> roleType) throws Throwable
    {
        super(roleType,token, null);
        this.secretKey=new SecretKeySpec(generateSecretKey(token),"HmacSHA512");
        this.querySecurityPathPrefix="&"+this.getSecurityQueryKey()+"=";
        this.deviceSessionId=deviceSessionId;
        this.zoneId=zoneId;
        this.pageStates=new HashMap<String, Object>();
        this.newPageStates=null;
        this.continuations=new Stack<String>();
        this.newContinuations=null;
    }
    
    public long getDeviceSessionId()
    {
        return this.deviceSessionId;
    }
    
    public ZoneId getZoneId()
    {
        return this.zoneId;
    }
    
    public void setPageState(TagElement<?> element) throws Throwable
    {
        if (element instanceof FormElement<?>)
        {
            element.returnAddInner(new InputHidden(STATE_KEY,element.id()));
        }
        setState(element.id(), element);
    }
    
    public void clearLastPageStates()
    {
        if (this.newPageStates!=null)
        {
            this.pageStates=this.newPageStates;
            this.newPageStates=null;
        }
        if (this.newContinuations!=null)
        {
            this.continuations=this.newContinuations;
            this.newContinuations=null;
        }
    }

    @SuppressWarnings("unused")
    public <T> T getPageState(String key)
    {
        if (Debug.ENABLE && DEBUG && DEBUG_PAGESTATE)
        {
            if (this.pageStates.containsKey(key)==false)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"No page state: key="+key);
                for (Entry<String, Object> entry:this.pageStates.entrySet())
                {
                    Debugging.log(LOG_DEBUG_CATEGORY,"Object:key="+entry.getKey()+", value="+entry.getValue());
                }
            }
        }
        return (T)this.pageStates.get(key);
    }

    @Override
    public <T> T getRemoteCaller(Context context) throws Throwable
    {
        String id=context.getHttpServletRequest().getParameter(getStateKey());
        return getPageState(id);
    }
    @Override
    public void setState(String key,Object state) throws Throwable
    {
        if (state!=null)
        {
            if (this.newPageStates==null)
            {
                this.newPageStates=new HashMap<String, Object>();
            }
            this.newPageStates.put(key, state);
            this.pageStates.put(key, state);
            if (Debug.ENABLE && DEBUG)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"setPageState: key="+key+", page="+state.getClass().getCanonicalName());
            }            
        }
    }
    final static public String STATE_KEY="@";

    @Override
    public String getSecurityQueryKey()
    {
        return "_";
    }

    
    @Override
    public boolean verifyQuery(Context context) throws Throwable
    {
        HttpServletRequest request=context.getHttpServletRequest();
        RequestMethod requestMethod=context.getRequestMethod();
        if (requestMethod.isQueryVerificationRequired())
        {
            //Also require session to provide condition
            if (getSecurityQueryKey()!=null)
            {
                //Require 
                var map=context.getHttpServletRequest().getParameterMap();
                int ignore=map.containsKey(getStateKey())?1:0;
                if ((map.size()>ignore)&&(map.containsKey(getSecurityQueryKey())==false))
                {
                    if (DEBUG_SECURITY)
                    {
                        Debugging.log(LOG_DEBUG_CATEGORY,"No security key: expected key="+getSecurityQueryKey()+", method="+requestMethod.getMethod().getDeclaringClass().getName()+"."+requestMethod.getMethod().getName());
                    }
                    else
                    {
                        return false;
                    }
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
            return code.equals(computed);
        }
        else
        return true;
    }
    @Override
    public String signQuery(String query) throws Throwable
    {
        byte[] objectBytes=query.getBytes(StandardCharsets.UTF_8);
        byte[] hmac=SecurityUtils.computeHashHMACSHA256(this.secretKey, objectBytes);
        String code=Base64.getUrlEncoder().encodeToString(hmac);
        return query+this.querySecurityPathPrefix+code;
    }


    @Override
    public String getStateKey()
    {
        return STATE_KEY;
    }
    
    public String pushContinuation(String script)
    {
        if (this.newContinuations==null)
        {
            this.newContinuations=new Stack<String>();
        }
        this.newContinuations.push(script);
        this.continuations.push(script);
        return script;
    }
    public String pushContinuation(Context context)
    {
        return pushContinuation(HtmlUtils.getRequestPathAndQuery(context));
    }
    public String pushRefererContinuation(Context context)
    {
        HttpServletRequest request=context.getHttpServletRequest();
        return pushContinuation(request.getHeader("Referer"));
    }
    
    public void clearContinuations()
    {
        this.continuations.clear();
        this.newContinuations=null;
    }
    
    public String popContinuation()
    {
        if (this.continuations.size()==0)
        {
            return null;
        }
        return this.continuations.pop();
    }
    
     
    
}
