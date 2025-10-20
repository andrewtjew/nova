/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.nova.core.NameObject;
import org.nova.http.server.Context;
import org.nova.http.server.RequestMethod;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;

/*
 * Optimizes access deny determination based on current session roles and handler RequiredRoles and ForbiddenRoles.
 * Two deny maps are used for two level lookups. The maps are built dynamically.
*/
public abstract class RoleSession <ROLE extends Enum> extends Session
{
    static record AbnormalAcceptBox(AbnormalAccept abnormalAccept)
    {
    }
    
    static final boolean DEBUG=false;
    static private HashMap<String,AbnormalAcceptBox> DENY_MAP=new HashMap<String, AbnormalAcceptBox>();

    private HashMap<Long,AbnormalAcceptBox> denyMap;
    final private HashSet<ROLE> roles;
    final private Class<ROLE> roleType;
    private String partialKey;
    
    public RoleSession(Class<ROLE> roleType,String token, String user)
    {
        super(token, user);
        this.roleType=roleType;
        this.roles=new HashSet<>();
        this.denyMap=new HashMap<Long, AbnormalAcceptBox>();
    }
    
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
    
    @Override
    synchronized public AbnormalAccept acceptRequest(Trace trace, Context context) throws Throwable
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

    public AbnormalAccept isAccessDenied(ForbiddenRoles forbiddenRoles,RequiredRoles requiredRoles,long runTimeKey) throws Throwable
    {
        //Implements two level of caching, first per session using this.denyMap, then global using DENY_MAP.
        
        AbnormalAcceptBox result=this.denyMap.get(runTimeKey); //We assume the page request are serialized by the session filter.
        if (result!=null)
        {
            return result.abnormalAccept;
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
        return result.abnormalAccept;
    }

    private AbnormalAccept isAccessDenied(ForbiddenRoles forbiddenRoles,RequiredRoles requiredRoles) throws Throwable
    {
        if (forbiddenRoles!=null)
        {
            if (forbiddenRoles.value().length==0)
            {
                return new AbnormalAccept();
            }
            for (String value:forbiddenRoles.value())
            {
                if (hasRole(value))
                {
                    return new AbnormalAccept();
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
        return new AbnormalAccept(requiredRoles.redirect());
    }
    private boolean hasRole(String value)
    {
        @SuppressWarnings("unchecked")
        ROLE role=(ROLE)Enum.valueOf(this.roleType, value);
        return this.roles.contains(role);
    }
    public boolean hasRole(ROLE role)
    {
        return this.roles.contains(role);
    }
    @Override
    protected void collectDisplayItems(List<NameObject> list)
    {
        super.collectDisplayItems(list);
        String roles=Utils.combine(this.roles.iterator(), ", ");
        list.add(new NameObject("Roles",roles));
    }    

}
