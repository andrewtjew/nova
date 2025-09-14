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
import java.util.HashMap;
import org.nova.frameworks.ServerApplication;
import org.nova.http.server.Context;
import org.nova.http.server.RequestMethod;
import org.nova.tracing.Trace;

public abstract class AccessSession <SERVICE extends ServerApplication> extends Session
{
    HashMap<String,Boolean> denyMap;
    
    public AccessSession(String token, String user)
    {
        super(token, user);
        this.denyMap=new HashMap<>();
    }

    @Override
    public AbnormalAccept acceptRequest(Trace trace, Context context) throws Throwable
    {
        Boolean deny=this.denyMap.get(context.getRequestMethod().getKey());
        if (deny==null)
        {
            deny=isAccessDenied(context);
            this.denyMap.put(context.getRequestMethod().getKey(),deny);
        }
        if (deny)
        {
            return new AbnormalAccept();
        }
        return null;
    }
    
    boolean isAccessDenied(Context context)
    {
        RequestMethod requestMethod=context.getRequestMethod();
        ForbiddenRoles forbiddenGroups=requestMethod.getForbiddenRoles();
        if (forbiddenGroups!=null)
        {
            if (forbiddenGroups.value().length==0)
            {
                return true; //deny all
            }
            for (String value:forbiddenGroups.value())
            {
                if (isInGroup(value))
                {
                    return true;
                }
            }
        }

        RequiredRoles requiredRoles=requestMethod.getRequiredRoles();
        if (requiredRoles==null)
        {
            return true; //deny all
        }
        if (requiredRoles.value().length==0)
        {
            return false; //allow all
        }
        for (String value:requiredRoles.value())
        {
            if (isInGroup(value))
            {
                return false;
            }
        }
        return true;
    }

    public abstract boolean isInGroup(String group); 
}
