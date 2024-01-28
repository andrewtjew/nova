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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.nova.concurrent.Lock;
import org.nova.core.NameObject;
import org.nova.http.server.Context;
import org.nova.metrics.RateMeter;
import org.nova.metrics.RateSample;
import org.nova.tracing.Trace;

public abstract class Session
{
    final String token;
    final long created;
    private String user;
    private long lastAccess;
    private Lock<String> lock;
    private RateMeter accessRateMeter;
    
    public Session(String token,String user)
    {
        this.user=user;
        this.token=token;
        this.lastAccess=this.created=System.currentTimeMillis();
        this.accessRateMeter=new RateMeter();
    }

    public Session(String token)
    {
        this.token=token;
        this.lastAccess=this.created=System.currentTimeMillis();
        this.accessRateMeter=new RateMeter();
    }
    
    protected void setUser(String user)
    {
        this.user=user;
    }
    
    public void beginSessionProcessing(Lock<String> lock)
    {
        synchronized(this)
        {
            this.lock=lock;
            this.lastAccess=System.currentTimeMillis();
            this.accessRateMeter.increment();
        }
    }
    public Lock<String> freeLock()
    {
        synchronized (this)
        {
            Lock<String> lock=this.lock;
            this.lock=null;
            return lock;
        }
    }
    public void endSessionProcessing()
    {
        synchronized (this)
        {
            if (this.lock!=null)
            {
                this.lock.close();
            }
            this.lock=null;
        }
    }
    public long getLastAccess()
    {
        return lastAccess;
    }
    public String getUser()
    {
        return user;
    }
    public long getCreated()
    {
        return created;
    }
    public String getToken()
    {
        return token;
    }
    public RateMeter getAccessRateMeter()
    {
        return this.accessRateMeter;
    }
    
    protected void collectDisplayItems(List<NameObject> list)
    {
        ZoneId zoneId=ZoneId.of("UTC");
        list.add(new NameObject("Token",this.token));
        list.add(new NameObject("User",this.user));
        list.add(new NameObject("Created",LocalDateTime.ofInstant(Instant.ofEpochMilli(this.created),zoneId)));
        list.add(new NameObject("Last Access",LocalDateTime.ofInstant(Instant.ofEpochMilli(this.lastAccess),zoneId)));
        RateSample rate=this.accessRateMeter.sample();
        list.add(new NameObject("Access Rate",rate.getRate()));
        list.add(new NameObject("Accesses",rate.getTotalCount()));
    }
    
    
    abstract public void onClose(Trace trace) throws Throwable;
    abstract public NameObject[] getDisplayItems();
    abstract public boolean isAccessDenied(Trace trace,Context context) throws Throwable;
    
}
