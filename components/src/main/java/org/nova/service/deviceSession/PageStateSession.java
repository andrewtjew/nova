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


public abstract class PageStateSession<ROLE extends Enum<?>> extends RoleSession<ROLE> implements RemoteStateBinding,SessionRequestHandling
{
    final static boolean DEBUG=false;
    final static boolean DEBUG_PAGESTATE=false;
    final static String LOG_DEBUG_CATEGORY=PageStateSession.class.getSimpleName();

    static class PageStateSet
    {
        public HashMap<String,Object> states;
        public HashMap<String,Object> newStates;

        public Object getState(String key)
        {
            Object state=null;
            if (this.states!=null)
            {
                state=this.states.get(key);
            }
            if (state!=null)
            {
                //As long as the handler refers to the state, we keep it alive.
                if (this.newStates==null)
                {
                    this.newStates=new HashMap<String, Object>();
                }
                this.newStates.put(key, state);
            }
            else
            {
                state=this.newStates.get(key);
            }
            return state;
        }
        public void setState(String key,Object state) throws Throwable
        {
            if (state!=null)
            {
                if (this.newStates==null)
                {
                    this.newStates=new HashMap<>();
                }
                this.newStates.put(key, state);
            }
        }
        public Object removeState(String key)
        {
            Object removed=null;
            if (this.newStates!=null)
            {
                removed=this.newStates.remove(key);
            }
            if (this.states!=null)
            {
                if (removed!=null)
                {
                    this.states.remove(key);
                }
                else
                {
                    removed=this.states.remove(key);
                }
            }
            return removed;
        }
        public void clearLastStates()
        {
            if (this.newStates!=null)
            {
                this.states=this.newStates;
                this.newStates=null;
            }
        }
        
    }
    
    final protected DeviceSession deviceSession;
    final private HashMap<String,PageStateSet> pageStateSets;
    private String pageStateGroupName;
    
    public PageStateSession(DeviceSession deviceSession,Class<ROLE> roleType) throws Throwable
    {
        super(deviceSession,roleType);
        this.pageStateSets=new HashMap<>();
        this.deviceSession=deviceSession;
        this.pageStateGroupName="";
    }
    
    public DeviceSession getDeviceSession()
    {
        return this.deviceSession;
    }
    
    public <T> T getState(String stateKey) throws Throwable
    {
        return getState(this.pageStateGroupName,stateKey);
    }
    @SuppressWarnings({
            "unused", "unchecked"
    })
    private <T> T getState(String groupKey,String stateKey) throws Throwable
    {
        var pageStateSet=this.pageStateSets.get(groupKey);
        
        if (Debug.ENABLE && DEBUG && DEBUG_PAGESTATE)
        {
            if (pageStateSet!=null)
            {
                if (pageStateSet.states.containsKey(stateKey)==false)
                {
                    Debugging.log(LOG_DEBUG_CATEGORY,"No page state: key="+stateKey);
                    for (Entry<String, Object> entry:pageStateSet.states.entrySet())
                    {
                        Debugging.log(LOG_DEBUG_CATEGORY,"Object:key="+entry.getKey()+", value="+entry.getValue());
                    }
                }
            }
        }
        if (pageStateSet==null)
        {
            throw new Exception("No PageStateGroupName annotation");
        }
        return (T)pageStateSet.getState(stateKey);
    }
    
    @Override
    final public void setState(String key,Object state) throws Throwable
    {
        if (this.pageStateGroupName==null)
        {
            throw new Exception();
        }
        var pageStateSet=this.pageStateSets.get(this.pageStateGroupName);
        if (Debug.ENABLE && DEBUG)
        {
            if (key==null)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"setPageState: key=null");
            }
        }            
        if (pageStateSet==null)
        {
            pageStateSet=new PageStateSet();
            this.pageStateSets.put(this.pageStateGroupName, pageStateSet);
        }
        if (state!=null)
        {
            pageStateSet.setState(key, state);
            if (Debug.ENABLE && DEBUG)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"setPageState: key="+key+", page="+state);
            }            
        }
    }
    
    @Override
    final public String getQueryBinding(TagElement<?> element)
    {
        return STATE_KEY+"="+element.id();
    }
    
    
    @Override
    final public <T> T getState(Context context) throws Throwable
    {
        String groupKey=context.getHttpServletRequest().getParameter(GROUP_KEY);
        String stateKey=context.getHttpServletRequest().getParameter(STATE_KEY);
        return getState(groupKey,stateKey);
    }
    final static public String STATE_KEY="@state";
    final static public String GROUP_KEY="@group";

    @Override
    final public <PATHANDQUERY extends PathAndQuery> PATHANDQUERY bind(String id,Object state,PATHANDQUERY pathAndQuery) throws Throwable
    {
        setState(id,state);
        pathAndQuery.addQuery(STATE_KEY, id);
        if (this.pageStateGroupName!=null)
        {
            pathAndQuery.addQuery(GROUP_KEY, this.pageStateGroupName);
        }
        return pathAndQuery;
    }
  
    @Override
    final public void bind(FormElement<?> element) throws Throwable
    {
        setState(element.id(),element);
        element.addInner(new InputHidden(STATE_KEY,element.id()));
        if (this.pageStateGroupName!=null)
        {
            element.addInner(new InputHidden(GROUP_KEY,this.pageStateGroupName));
        }
    }

    
    @Override
    public AbnormalResult beginRequest(Trace parent,Context context) throws Throwable
    {
       AbnormalResult result=super.beginRequest(parent, context);
       if (result!=null)
       {
           return result;
       }
       this.pageStateGroupName=context.getRequestMethod().getPageStateGroupName();
       return null;
    }
    @Override
    public void endRequest(Trace parent,Context context,Response<?> response)
    {
        super.endRequest(parent, context, response);
        if (response==null)
        {
            return;
        }
        if (response.getContent() instanceof InitializationPage)
        {
            var pageStateSet=this.pageStateSets.get(this.pageStateGroupName);
            if (pageStateSet!=null)
            {
                pageStateSet.clearLastStates();
            }
        }
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
