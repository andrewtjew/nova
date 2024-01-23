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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.nova.frameworks.ServerApplication;
import org.nova.http.server.Context;
import org.nova.http.server.RequestHandler;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;

import com.amazonaws.services.auditmanager.model.Role;

public abstract class RoleSession <ROLE extends Enum> extends Session
{
    static private HashMap<String,Boolean> DENY_MAP=new HashMap<String, Boolean>();

    final private HashSet<ROLE> roles;
    final private Class<ROLE> roleType;
    private String partialKey;
    
    public RoleSession(Class<ROLE> roleType,String token, String user)
    {
        super(token, user);
        this.roleType=roleType;
        this.roles=new HashSet<>();
    }
    
    @SuppressWarnings("rawtypes")
    private String computePartialKey()
    {
        StringBuilder sb=new StringBuilder();
        
        ArrayList<ROLE> list=new ArrayList<ROLE>();
        list.addAll(roles);
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
        this.partialKey=computePartialKey();
    }

    synchronized public boolean removeRole(ROLE role)
    {
        boolean result=this.roles.remove(role);
        this.partialKey=computePartialKey();
        return result;
    }

    synchronized public void clearRoles()
    {
        this.roles.clear();
        this.partialKey=computePartialKey();
    }
    @Override
    synchronized public boolean isAccessDenied(Trace trace, Context context) throws Throwable
    {
        RequestHandler handler=context.getRequestHandler();
        String key=this.partialKey+handler.getRunTimeKey();
        Boolean deny;
        synchronized (DENY_MAP)
        {
            deny=DENY_MAP.get(key);
        }
        if (deny==null)
        {
            deny=isAccessDenied(handler);
            synchronized (DENY_MAP)
            {
                DENY_MAP.put(key, deny);
            }
        }
        return deny;
    }

    public boolean isAccessDenied(RequestHandler handler) throws Throwable
    {
        Method method=handler.getMethod();
        ForbiddenRoles forbiddenRoles=method.getDeclaredAnnotation(ForbiddenRoles.class);
        if (forbiddenRoles==null)
        {
            forbiddenRoles=method.getDeclaringClass().getDeclaredAnnotation(ForbiddenRoles.class);
        }
        if (forbiddenRoles!=null)
        {
            if (forbiddenRoles.value().length==0)
            {
                return true; //deny all
            }
            for (String value:forbiddenRoles.value())
            {
                if (hasRole(value))
                {
                    return true;
                }
            }
        }

        RequiredRoles requiredRoles=method.getDeclaredAnnotation(RequiredRoles.class);
        if (requiredRoles==null)
        {
            requiredRoles=method.getDeclaringClass().getDeclaredAnnotation(RequiredRoles.class);
            if (requiredRoles==null)
            {
                throw new Exception("Missing RequiredRoles: "+handler.getKey()+", class="+handler.getMethod().getDeclaringClass());
            }
        }
        if (requiredRoles.value().length==0)
        {
            return false; //allow all
        }
        for (String value:requiredRoles.value())
        {
            if (hasRole(value))
            {
                return false;
            }
        }
        return true;
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
    

}
