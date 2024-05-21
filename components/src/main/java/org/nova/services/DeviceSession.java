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
import org.nova.security.SecurityUtils;
import org.nova.testing.Debugging;
import org.nova.tracing.Trace;


public abstract class DeviceSession<ROLE extends Enum> extends RoleSession<ROLE> implements RemoteStateBinding
{
    final static boolean DEBUG=true;
    final protected HashMap<String,Object> pageStates;
    final protected ZoneId zoneId;
    final private long deviceSessionId;
    private Context context;
    
    private boolean isPage=false;
    
    public DeviceSession(long deviceSessionId,String token,ZoneId zoneId,Class<ROLE> roleType) throws Throwable
    {
        super(roleType,token, null);
        this.deviceSessionId=deviceSessionId;
        this.zoneId=zoneId;
        this.pageStates=new HashMap<String, Object>();
    }
    
    public long getDeviceSessionId()
    {
        return this.deviceSessionId;
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
    
    public boolean isPage()
    {
        if (this.isPage)
        {
            this.isPage=false;
            return true;
        }
        return false;
    }

    public void setupPage()
    {
        this.isPage=true;
        this.pageStates.clear();
    }


    

    //------------------
    public ZoneId getZoneId()
    {
        return this.zoneId;
    }
    
    protected void setContext(Context context)
    {
        this.context=context;
    }
    public Context getContext()
    {
        return this.context;
    }
    
    public void setPageState(TagElement<?> element)
    {
        setPageState(element.id(), element);
    }
    public void setPageState(String key,Object state)
    {
        if (state!=null)
        {
            this.pageStates.put(key, state);
//            if (Debugging.ENABLE && DEBUG)
//            {
//                Debugging.log("UserSession","setPageState: key="+key+", page="+state.getClass().getCanonicalName());
//            }            
        }
    }
    @Override
    public TagElement<?> getState(Context context) throws Throwable
    {
        String id=context.getHttpServletRequest().getParameter(STATE_KEY);
        return getPageState(id);
    }
    @Override
    public void setState(TagElement<?> element) throws Throwable
    {
        if (element instanceof FormElement<?>)
        {
            element.returnAddInner(new InputHidden(STATE_KEY,element.id()));
        }
        setPageState(element);
    }
    final static public String STATE_KEY="@";


    @Override
    public String getKey()
    {
        return STATE_KEY;
    }
}
