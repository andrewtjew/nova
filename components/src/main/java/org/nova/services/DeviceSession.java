package org.nova.services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.nova.core.NameObject;
import org.nova.html.elements.FormElement;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.InputHidden;
import org.nova.html.tags.sub;
import org.nova.html.tags.time;
import org.nova.http.server.Context;
import org.nova.http.server.RemoteStateBinding;
import org.nova.json.ObjectMapper;
import org.nova.localization.CountryCode;
import org.nova.localization.CurrencyCode;
import org.nova.security.QuerySecurity;
import org.nova.security.SecurityUtils;
import org.nova.testing.Debugging;
import org.nova.tracing.Trace;


public abstract class DeviceSession<ROLE extends Enum> extends RoleSession<ROLE> implements RemoteStateBinding,QuerySecurity
{
    final static boolean DEBUG=false;
    protected HashMap<String,Object> pageStates;
    protected HashMap<String,Object> newPageStates;
    
    
    final protected ZoneId zoneId;
    final private long deviceSessionId;
    private Context context;
    
    
    public DeviceSession(long deviceSessionId,String token,ZoneId zoneId,Class<ROLE> roleType) throws Throwable
    {
        super(roleType,token, null);
        this.deviceSessionId=deviceSessionId;
        this.zoneId=zoneId;
        this.pageStates=new HashMap<String, Object>();
        this.newPageStates=null;
    }
    
    public long getDeviceSessionId()
    {
        return this.deviceSessionId;
    }
    
    public ZoneId getZoneId()
    {
        return this.zoneId;
    }
    
    @Deprecated
    protected void setContext(Context context)
    {
        this.context=context;
    }
    @Deprecated
    public Context getContext()
    {
        return this.context;
    }
    
    public void setPageState(TagElement<?> element) throws Throwable
    {
        if (element instanceof FormElement<?>)
        {
            element.returnAddInner(new InputHidden(STATE_KEY,element.id()));
        }
        setState(element.id(), element);
    }
    
    public void updateStates(boolean pageRequest)
    {
        if (pageRequest)
        {
            if (this.newPageStates!=null)
            {
                this.pageStates=this.newPageStates;
                this.newPageStates=null;
            }
        }
        else
        {
            this.newPageStates=null;
        }
    }

    public <T> T getPageState(String key)
    {
        if (Debugging.ENABLE && DEBUG)
        {
            if (this.pageStates.containsKey(key)==false)
            {
                System.err.println("No Page State for key="+key);
                for (Entry<String, Object> entry:this.pageStates.entrySet())
                {
                    System.err.println(entry.getKey()+":"+entry.getValue());
                }
            }
        }
        return (T)this.pageStates.get(key);
    }
    
    
    @Override
    public Object getState(Context context) throws Throwable
    {
        String id=context.getHttpServletRequest().getParameter(STATE_KEY);
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
            if (Debugging.ENABLE && DEBUG)
            {
                Debugging.log("UserSession","setPageState: key="+key+", page="+state.getClass().getCanonicalName());
            }            
        }
    }
    final static public String STATE_KEY="@";


    @Override
    public String getStateKey()
    {
        return STATE_KEY;
    }
    
//    private String continuationPage;
    private String continuation;
    
    public String setContinuation(Context context)
    {
        this.continuation=context.getPathAndQuery();
        return this.continuation;
    }
    public String setRefererContinuation(Context context)
    {
        HttpServletRequest request=context.getHttpServletRequest();
        this.continuation=request.getHeader("Referer");
        return this.continuation;
    }
    public void setContinuation(String pathAndQuery)
    {
        this.continuation=pathAndQuery;
    }
    public void clearContinuation()
    {
        this.continuation=null;
    }

//    public void setContinuationPage(Context context)
//    {
//        this.continuationPage=this.getPathAndQuery(context);
//    }

//    public String activateContination()
//    {
//        this.activeContinuation=this.continuation;
//        return this.continuationPage;
//    }
    
    public String useContinuation()
    {
        String continuation=this.continuation;
        if (continuation!=null)
        {
            this.continuation=null;
        }
        return continuation;
    }
    
     
    
}
