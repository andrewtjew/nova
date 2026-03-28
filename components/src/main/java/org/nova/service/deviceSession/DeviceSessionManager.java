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
package org.nova.service.deviceSession;

import java.util.Collection;
import java.util.HashMap;
import org.nova.annotations.Description;
import org.nova.collections.ExpireMap;
import org.nova.concurrent.Lock;
import org.nova.concurrent.LockManager;
import org.nova.concurrent.TimerScheduler;
import org.nova.logging.Logger;
import org.nova.metrics.CountMeter;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;

public class DeviceSessionManager 
{
    final private ExpireMap<String, DeviceSession> deviceSessions;
    final private LockManager<String> lockManager;
    final private long waitForLockTimeoutMs;
    final private long sessionTimeoutMs;
    final private Logger logger;
    
    @Description("Counts how many times onClose throws exceptions. The exceptions are in the logs.")
    final CountMeter onCloseFailMeter;
    
    @Description("Counts how many times sessions are created.")
    final CountMeter addMeter;

    @Description("Counts how many times sessions are removed. If there are zero active sessions, this value should be the same as the addSessionMeter value.")
    final CountMeter removeMeter;

    @Description("Counts how many session timeout occurred.")
    final CountMeter timeoutMeter;
    
    public DeviceSessionManager(TraceManager traceManager,Logger logger,TimerScheduler timerScheduler,long waitForLockTimeoutMs,long sessionTimeoutMs,int generations) throws Exception
    {
        this.sessionTimeoutMs=sessionTimeoutMs;
        this.deviceSessions=new ExpireMap<>(this.getClass().getSimpleName(), timerScheduler, sessionTimeoutMs, generations,(Trace parent,String key,DeviceSession session)->{timeoutSession(parent,session);});
        this.logger=logger;
        this.waitForLockTimeoutMs=waitForLockTimeoutMs;
        this.lockManager=new LockManager<>(traceManager, this.getClass().getSimpleName());
        
        this.onCloseFailMeter=new CountMeter();
        this.addMeter=new CountMeter();
        this.removeMeter=new CountMeter();
        this.timeoutMeter=new CountMeter();
    }
    public DeviceSessionManager(TraceManager traceManager,Logger logger,TimerScheduler timerScheduler,long timeoutMs) throws Exception
    {
        this(traceManager,logger,timerScheduler,10*1000,timeoutMs,2);
        enableExpiration();
    }
    
    public void enableExpiration() throws Exception
    {
        this.deviceSessions.start();
    }
    public void stop() throws Exception
    {
        this.deviceSessions.stop();
    }
    public void add(Trace parent,DeviceSession deviceSession)
    {
        synchronized(this)
        {
            remove(parent,deviceSession);
            this.deviceSessions.put(deviceSession.getToken(),deviceSession);
        }
        this.addMeter.increment();
    }
    
    public DeviceSession get(String token)
    {
        synchronized (this)
        {
            return this.deviceSessions.update(token);
        }
    }
    
    public boolean remove(Trace parent,String token)
    {
        return remove(parent,get(token));
    }
    
    public void clear()
    {
        synchronized(this)
        {
            this.deviceSessions.clear();
        }
    }
    
    public boolean remove(Trace parent,DeviceSession deviceSession)
    {
        if (deviceSession==null)
        {
            return false;
        }
        DeviceSession removed;
        synchronized(this)
        {
            removed=this.deviceSessions.remove(deviceSession.getToken());
        }
        if (removed!=null)
        {
            try
            {
                deviceSession.onClose(parent);
            }
            catch (Throwable t)
            {
                this.logger.log(t);
                this.onCloseFailMeter.increment();
            }
            this.removeMeter.increment();
            return true;
        }
        return false;
    }
    
    public Collection<DeviceSession> getSessionSnapshot()
    {
        synchronized (this)
        {
            return this.deviceSessions.values();
        }
    }
    public Lock<String> waitForLock(Trace parent,String user)
    {
        return lockManager.waitForLock(parent,user,this.waitForLockTimeoutMs);
    }
    
    private void timeoutSession(Trace trace, DeviceSession session) throws Throwable
    {
        this.timeoutMeter.increment();
        remove(trace,session);
    }
    public long getSessionTimeoutMs()
    {
        return this.sessionTimeoutMs;
    }
}
