package org.nova.service.deviceSession;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.Context;
import org.nova.http.server.Filter;
import org.nova.http.server.RequestMethod;
import org.nova.http.server.Response;
import org.nova.localization.CountryCode;
import org.nova.security.PathAndQueryAuthentication;
import org.nova.security.SecurityUtils;
import org.nova.service.deviceSession.DeviceSession.DeviceLocation;
import org.nova.services.AbnormalResult;
import org.nova.services.ForbiddenRoles;
import org.nova.services.RequiredRoles;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;

public abstract class RoleSession<ROLE extends Enum<?>> extends Session implements SessionRequestHandling
{
    final static boolean DEBUG=false;
    final static String LOG_DEBUG_CATEGORY=RoleSession.class.getSimpleName();

    public RoleSession(DeviceSession deviceSession,Class<ROLE> roleType) throws Throwable
    {
        super(deviceSession);
        this.roleType=roleType;
        this.roles=new HashSet<>();
        this.denyMap=new HashMap<Long, AbnormalAcceptBox>();
    }
    
    @Override
    public AbnormalResult beginRequest(Trace parent,Context context) throws Throwable
    {
        return verifyRequest(parent, context);
    }
    @Override
    public void endRequest(Trace parent,Context context,Response<?> response)
    {
    }

    static record AbnormalAcceptBox(AbnormalResult abnormalResult)
    {
    }
    static private HashMap<String,AbnormalAcceptBox> DENY_MAP=new HashMap<String, AbnormalAcceptBox>();

    private HashMap<Long,AbnormalAcceptBox> denyMap;
    final private HashSet<ROLE> roles;
    final private Class<ROLE> roleType;
    private String partialKey;
    
    
    @SuppressWarnings("rawtypes")
    private String computePartialKey()
    {
        StringBuilder sb=new StringBuilder();
        
        ArrayList<ROLE> list=new ArrayList<ROLE>();
        list.addAll(this.roles);
        list.sort(new Comparator<ROLE>()
        {

            @SuppressWarnings("unchecked")
            @Override
            public int compare(ROLE o1, ROLE o2)
            {
                Enum e1=(Enum)o1;
                Enum e2=(Enum)o2;
                return e1.compareTo(e2);
            }
        });
        for (ROLE role:list)
        {
            sb.append(((Enum)role).ordinal());
            sb.append("-");
        }
        return sb.toString();
    }
    
    synchronized public void addRole(ROLE role)
    {
        this.roles.add(role);
        this.denyMap.clear();
        this.partialKey=computePartialKey();
    }

    synchronized public boolean removeRole(ROLE role)
    {
        boolean result=this.roles.remove(role);
        this.denyMap.clear();
        this.partialKey=computePartialKey();
        return result;
    }

    synchronized public void clearRoles()
    {
        this.roles.clear();
        this.denyMap.clear();
        this.partialKey=computePartialKey();
    }
    

    static class AccessResult
    {
        final public String redirect;
        final public boolean denied;
        
        public AccessResult(boolean denied)
        {
            this.denied=denied;
            this.redirect=null;
        }
        public AccessResult(String redirect)
        {
            this.denied=true;
            this.redirect=redirect;
        }
    }
    
    synchronized public AbnormalResult verifyRequest(Trace trace, Context context) throws Throwable
    {
        RequestMethod handler=context.getRequestMethod();
        ForbiddenRoles forbiddenRoles=handler.getForbiddenRoles();
        RequiredRoles requiredRoles=handler.getRequiredRoles();
        if (requiredRoles==null)
        {
            throw new Exception("Missing RequiredRoles: "+handler.getKey()+", class="+handler.getMethod().getDeclaringClass());
        }
        return isAccessDenied(forbiddenRoles,requiredRoles,handler.getRunTimeKey());
    }    

    public AbnormalResult isAccessDenied(ForbiddenRoles forbiddenRoles,RequiredRoles requiredRoles,long runTimeKey) throws Throwable
    {
        //Implements two level of caching, first per session using this.denyMap, then global using DENY_MAP.
        
        AbnormalAcceptBox result=this.denyMap.get(runTimeKey); //We assume the page request are serialized by the session filter.
        if (result!=null)
        {
            return result.abnormalResult;
        }
        String key=this.partialKey+runTimeKey; 
        
        //We expect the combination of roles to be small so DENY_MAP won't grow too big.
        synchronized (DENY_MAP)
        {
            result=DENY_MAP.get(key);
        }
        if (result==null)
        {
            result=new AbnormalAcceptBox(isAccessDenied(forbiddenRoles,requiredRoles));
            synchronized (DENY_MAP)
            {
                DENY_MAP.put(key, result);
            }
        }
        this.denyMap.put(runTimeKey, result);
        return result.abnormalResult;
    }

    private AbnormalResult isAccessDenied(ForbiddenRoles forbiddenRoles,RequiredRoles requiredRoles) throws Throwable
    {
        if (forbiddenRoles!=null)
        {
            if (forbiddenRoles.value().length==0)
            {
                return new AbnormalResult();
            }
            for (String value:forbiddenRoles.value())
            {
                if (hasRole(value))
                {
                    return new AbnormalResult();
                }
            }
        }

        if (requiredRoles.value().length==0)
        {
            return null;
        }
        for (String value:requiredRoles.value())
        {
            if (hasRole(value))
            {
                return null;
            }
        }
        return new AbnormalResult(requiredRoles.redirect());
    }
    private boolean hasRole(String value)
    {
        @SuppressWarnings("unchecked")
        ROLE role=(ROLE)Enum.valueOf((Class)this.roleType, value);
        return this.roles.contains(role);
    }
    public boolean hasRole(ROLE role)
    {
        return this.roles.contains(role);
    }
//    @Override
//    protected void collectDisplayItems(List<NameObject> list)
//    {
//        super.collectDisplayItems(list);
//        String roles=Utils.combine(this.roles.iterator(), ", ");
//        list.add(new NameObject("Roles",roles));
//    }    

    
}
