package com.evolve.legacy.services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;

import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.debug.LogLevel;
import org.nova.html.elements.Element;
import org.nova.html.elements.FormElement;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.InputHidden;
import org.nova.html.remote.RemoteStateBinding;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.Context;
import org.nova.http.server.Filter;
import org.nova.http.server.RequestMethod;
import org.nova.security.PathAndQueryAuthentication;
import org.nova.security.SecurityUtils;
import org.nova.services.AbnormalResult;
import org.nova.tracing.Trace;


public abstract class DeviceSession2<ROLE extends Enum<?>> extends RoleSession<ROLE> implements RemoteStateBinding,PathAndQueryAuthentication
{
    final static boolean DEBUG=false;
    final static boolean DEBUG_PAGESTATE=false;
    final static boolean DEBUG_SECURITY=false;
    final static String LOG_DEBUG_CATEGORY=DeviceSession2.class.getSimpleName();
    
    protected HashMap<String,Object> states;
    protected HashMap<String,Object> newPageStates;
    
    final private long deviceSessionId;
    final protected SecretKey secretKey;

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
    
    
    public DeviceSession2(long deviceSessionId,String token,Class<ROLE> roleType) throws Throwable
    {
        super(roleType,token, null);
        this.secretKey=new SecretKeySpec(generateSecretKey(token),"HmacSHA512");
        this.deviceSessionId=deviceSessionId;
        this.states=new HashMap<String, Object>();
        this.newPageStates=null;
    }
    public long getDeviceSessionId()
    {
        return this.deviceSessionId;
    }
    
    public void setPageState(TagElement<?> element) throws Throwable
    {
        if (element instanceof FormElement<?>)
        {
            element.returnAddInner(new InputHidden(STATE_KEY,element.id()));
        }
        setState(element.id(), element);
    }

    public void setPageState(long key,Object state) throws Throwable
    {
        setState(Long.toString(key),state);
    }

    public void setState(String key,Object state) throws Throwable
    {
        if (Debug.ENABLE && DEBUG)
        {
            if (key==null)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"setPageState: key=null");
            }
        }            
        if (state!=null)
        {
            if (this.newPageStates==null)
            {
                this.newPageStates=new HashMap<String, Object>();
            }
            this.newPageStates.put(key, state);
            this.states.put(key, state);
            if (Debug.ENABLE && DEBUG)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"setPageState: key="+key+", page="+state);
            }            
        }
    }
    
    public void clearLastStates()
    {
        if (this.newPageStates!=null)
        {
            this.states=this.newPageStates;
            this.newPageStates=null;
        }
    }

    @SuppressWarnings("unused")
    public <T> T getPageState(String key)
    {
        if (Debug.ENABLE && DEBUG && DEBUG_PAGESTATE)
        {
            if (this.states.containsKey(key)==false)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"No page state: key="+key);
                for (Entry<String, Object> entry:this.states.entrySet())
                {
                    Debugging.log(LOG_DEBUG_CATEGORY,"Object:key="+entry.getKey()+", value="+entry.getValue());
                }
            }
        }
        @SuppressWarnings("unchecked")
        T state=(T)this.states.get(key);
        if (state!=null)
        {
            //As long as the handler refers to the state, we keep it alive.
            if (this.newPageStates==null)
            {
                this.newPageStates=new HashMap<String, Object>();
            }
            this.newPageStates.put(key, state);
        }
        return state;
    }

    @SuppressWarnings("unused")
    public <T> T getPageState(long key)
    {
        return getPageState(Long.toString(key));
    }
    
    @SuppressWarnings({"unused", "unchecked"})
    public <T> T removePageState(long key)
    {
        return removePageState(Long.toString(key));
    }    
    
    @SuppressWarnings({"unused", "unchecked"})
    public <T> T removePageState(String key)
    {
        if (Debug.ENABLE && DEBUG && DEBUG_PAGESTATE)
        {
            if (this.states.containsKey(key)==false)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"No page state: key="+key);
                for (Entry<String, Object> entry:this.states.entrySet())
                {
                    var object=entry.getValue();
                    if (object instanceof Element)
                    {
                        Debugging.log(LOG_DEBUG_CATEGORY,"Object:key="+entry.getKey()+", class="+object.getClass().getName());
                    }
                    else
                    {
                        Debugging.log(LOG_DEBUG_CATEGORY,"Object:key="+entry.getKey()+", value="+entry.getValue());
                    }
                }
            }
        }
        if (this.newPageStates!=null)
        {
            this.newPageStates.remove(key);
        }
        return (T)this.states.remove(key);
        
    }

    @Override
    public <T> T getState(Context context) throws Throwable
    {
        String id=context.getHttpServletRequest().getParameter(STATE_KEY);
        return getPageState(id);
    }
    
    @Override
    final public <PATHANDQUERY extends PathAndQuery> PATHANDQUERY bind(String id,Object state,PATHANDQUERY pathAndQuery) throws Throwable
    {
        setState(id,state);
        pathAndQuery.addQuery(STATE_KEY, id);
        return pathAndQuery;
    }
  
    @Override
    final public void bind(FormElement<?> element) throws Throwable
    {
        setState(element.id(),element);
        element.addInner(new InputHidden(STATE_KEY,element.id()));
    }
    
    final static public String STATE_KEY="@";
    final static public String QUERY_SECURITY_KEY="_";
    final static public String QUERY_SECURITY_PREFIX="&"+QUERY_SECURITY_KEY+"=";

    public RequestMethod getCurrentRequestMethod()
    {
        return this.requestMethod;
    }
    
    private RequestMethod requestMethod;    
    @Override
    public AbnormalResult verifyRequest(Trace parent,Context context,Filter filter) throws Throwable
    {
        var abnormalResult=super.verifyRequest(parent, context,filter);
        if (abnormalResult!=null)
        {
            return abnormalResult;
        }
        if (this.isRequestAuthentic(context)==false)
        {
            return new AbnormalResult();
        }
        return null;
    }
    @Override
    public String signPathAndQuery(String query) throws Throwable
    {
        byte[] objectBytes=query.getBytes(StandardCharsets.UTF_8);
        byte[] hmac=SecurityUtils.computeHashHMACSHA256(this.secretKey, objectBytes);
        String code=Base64.getUrlEncoder().encodeToString(hmac);
        return query+QUERY_SECURITY_PREFIX+code;
    }


//    @Override
//    public String getStateKey()
//    {
//        return STATE_KEY;
//    }
    @Override
    public String getQueryBinding(TagElement<?> element)
    {
        return STATE_KEY+"="+element.id();
    }
    public boolean isRequestAuthentic(Context context) throws Throwable
    {
        this.requestMethod=context.getRequestMethod();
        HttpServletRequest request=context.getHttpServletRequest();
        String queryString=request.getQueryString();
        if (queryString==null)
        {
            return true;
        }
        
        if ((requestMethod.getRequiredRoles()==null)||(requestMethod.getRequiredRoles().value().length==0))
        {
            var map=context.getHttpServletRequest().getParameterMap();
            int ignore=map.containsKey(STATE_KEY)?1:0;
            if ((map.size()>ignore)&&(map.containsKey(QUERY_SECURITY_KEY)==false))
            {
                if (DEBUG_SECURITY)
                {
                    Debugging.log(LOG_DEBUG_CATEGORY,"No security key: method="+requestMethod.getMethod().getDeclaringClass().getName()+"."+requestMethod.getMethod().getName(),LogLevel.WARNING);
                }
                else
                {
                    return false;
                }
            }
        }
        //!! This makes QuerySecurity a choice thing.
        String code=request.getParameter(QUERY_SECURITY_KEY);
        if (code!=null)
        {
            String query=request.getQueryString();
            int index=query.lastIndexOf(QUERY_SECURITY_PREFIX);
            String text=query.substring(0,index);
            byte[] hmac=SecurityUtils.computeHashHMACSHA256(this.secretKey, text.getBytes());
            String computed=Base64.getUrlEncoder().encodeToString(hmac);
            return code.equals(computed);
        }
        if (Debug.ENABLE && DEBUG && DEBUG_SECURITY)
        {
            String query=request.getQueryString();
            Debugging.log(LOG_DEBUG_CATEGORY,"method="+requestMethod.getKey()+",query="+query);
        }
        return true;
    }
}
