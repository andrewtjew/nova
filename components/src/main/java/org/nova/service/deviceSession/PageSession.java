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
import org.nova.html.remote.RemoteStateBinding2;
import org.nova.http.server.Context;
import org.nova.http.server.Filter;
import org.nova.http.server.RequestMethod;
import org.nova.http.server.Response;
import org.nova.localization.CountryCode;
import org.nova.security.QuerySecurity;
import org.nova.security.SecurityUtils;
import org.nova.service.deviceSession.DeviceSession.DeviceLocation;
import org.nova.tracing.Trace;


public abstract class PageSession<STATE extends PageSession<STATE,ROLE>,ROLE extends Enum<?>> implements RemoteStateBinding2,StateRequestHandling
{
    final static boolean DEBUG=false;
    final static boolean DEBUG_PAGESTATE=false;
    final static String LOG_DEBUG_CATEGORY=PageSession.class.getSimpleName();

    static class StateSet
    {
        HashMap<String,Object> states;
        HashMap<String,Object> newStates;

        public Object getPageState(String key)
        {
            Object state=this.states.get(key);
            if (state!=null)
            {
                //As long as the handler refers to the state, we keep it alive.
                if (this.newStates==null)
                {
                    this.newStates=new HashMap<String, Object>();
                }
                this.newStates.put(key, state);
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
    
    final protected DeviceSession<ROLE> deviceSession;
    final private HashMap<String,StateSet> stateSets;
    private StateSet stateSet;
    
    public PageSession(DeviceSession<ROLE> deviceSession) throws Throwable
    {
        this.stateSets=new HashMap<>();
        this.deviceSession=deviceSession;
    }
    
    public DeviceSession<ROLE> getDeviceSession()
    {
        return this.deviceSession;
    }
    
//    public void setPageState(TagElement<?> element) throws Throwable
//    {
//        if (element instanceof FormElement<?>)
//        {
//            element.returnAddInner(new InputHidden(STATE_KEY,element.id()));
//        }
//        setPageState(element.id(), element);
//    }
//
//    public void setPageState(long key,Object state) throws Throwable
//    {
//        setPageState(Long.toString(key),state);
//    }
//
//    @Override
//    public void setPageState(String key,Object state) throws Throwable
//    {
//        if (Debug.ENABLE && DEBUG)
//        {
//            if (key==null)
//            {
//                Debugging.log(LOG_DEBUG_CATEGORY,"setPageState: key=null");
//            }
//        }            
//        if (state!=null)
//        {
//            if (this.newStates==null)
//            {
//                this.newStates=new HashMap<String, Object>();
//            }
//            this.newStates.put(key, state);
//            this.states.put(key, state);
//            if (Debug.ENABLE && DEBUG)
//            {
//                Debugging.log(LOG_DEBUG_CATEGORY,"setPageState: key="+key+", page="+state);
//            }            
//        }
//    }
//    
//    public void clearLastStates()
//    {
//        if (this.newStates!=null)
//        {
//            this.states=this.newStates;
//            this.newStates=null;
//        }
//    }
//
//    @SuppressWarnings("unused")
//    public <T> T getPageState(String key)
//    {
//        if (Debug.ENABLE && DEBUG && DEBUG_PAGESTATE)
//        {
//            if (this.states.containsKey(key)==false)
//            {
//                Debugging.log(LOG_DEBUG_CATEGORY,"No page state: key="+key);
//                for (Entry<String, Object> entry:this.states.entrySet())
//                {
//                    Debugging.log(LOG_DEBUG_CATEGORY,"Object:key="+entry.getKey()+", value="+entry.getValue());
//                }
//            }
//        }
//        @SuppressWarnings("unchecked")
//        T state=(T)this.states.get(key);
//        if (state!=null)
//        {
//            //As long as the handler refers to the state, we keep it alive.
//            if (this.newStates==null)
//            {
//                this.newStates=new HashMap<String, Object>();
//            }
//            this.newStates.put(key, state);
//        }
//        return state;
//    }
//
//    @SuppressWarnings("unused")
//    public <T> T getPageState(long key)
//    {
//        return getPageState(Long.toString(key));
//    }
//    
//    @SuppressWarnings({"unused", "unchecked"})
//    public <T> T removePageState(long key)
//    {
//        return removePageState(Long.toString(key));
//    }    
//    
//    @SuppressWarnings({"unused", "unchecked"})
//    public <T> T removePageState(String key)
//    {
//        if (Debug.ENABLE && DEBUG && DEBUG_PAGESTATE)
//        {
//            if (this.states.containsKey(key)==false)
//            {
//                Debugging.log(LOG_DEBUG_CATEGORY,"No page state: key="+key);
//                for (Entry<String, Object> entry:this.states.entrySet())
//                {
//                    var object=entry.getValue();
//                    if (object instanceof Element)
//                    {
//                        Debugging.log(LOG_DEBUG_CATEGORY,"Object:key="+entry.getKey()+", class="+object.getClass().getName());
//                    }
//                    else
//                    {
//                        Debugging.log(LOG_DEBUG_CATEGORY,"Object:key="+entry.getKey()+", value="+entry.getValue());
//                    }
//                }
//            }
//        }
//        if (this.newStates!=null)
//        {
//            this.newStates.remove(key);
//        }
//        return (T)this.states.remove(key);
//        
//    }
//
//    @Override
//    public <T> T getPageState(Context context) throws Throwable
//    {
//        String id=context.getHttpServletRequest().getParameter(getStateKey());
//        return getPageState(id);
//    }
//    final static public String STATE_KEY="@";
//
//    @Override
//    public String getStateKey()
//    {
//        return STATE_KEY;
//    }

    @Override
    public AbnormalResult beginRequest(Trace parent,Context context)
    {
        return null;
    }
    @Override
    public void endRequest(Trace parent,Context context,Response<?> response)
    {
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
