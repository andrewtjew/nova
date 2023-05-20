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

public class SessionManager<SESSION extends Session> 
{
    final private ExpireMap<String, SESSION> tokenSessions;
    final private HashMap<String,SESSION> userSessions;
    final private LockManager<String> lockManager;
    final private long waitForLockTimeoutMs;
    final private long sessionTimeoutMs;
    final private Logger logger;
    
    @Description("Counts how many times onClose throws exceptions. The exceptions are in the logs.")
    final CountMeter onCloseFailMeter;
    
    @Description("Counts how many times sessions are created.")
    final CountMeter addSessionMeter;

    @Description("Counts how many times sessions are removed. If there are zero active sessions, this value should be the same as the addSessionMeter value.")
    final CountMeter removeSessionMeter;

    @Description("Counts how many session timeout occurred.")
    final CountMeter timeoutSessionMeter;
    
    public SessionManager(TraceManager traceManager,Logger logger,TimerScheduler timerScheduler,long waitForLockTimeoutMs,long sessionTimeoutMs,int generations) throws Exception
    {
        this.sessionTimeoutMs=sessionTimeoutMs;
        this.tokenSessions=new ExpireMap<>(this.getClass().getSimpleName(), timerScheduler, sessionTimeoutMs, generations,(Trace parent,String key,SESSION session)->{timeoutSession(parent,session);});
        this.logger=logger;
        this.userSessions=new HashMap<>();
        this.waitForLockTimeoutMs=waitForLockTimeoutMs;
        this.lockManager=new LockManager<>(traceManager, this.getClass().getSimpleName());
        
        this.onCloseFailMeter=new CountMeter();
        this.addSessionMeter=new CountMeter();
        this.removeSessionMeter=new CountMeter();
        this.timeoutSessionMeter=new CountMeter();
    }
    public SessionManager(TraceManager traceManager,Logger logger,TimerScheduler timerScheduler,long sessionTimeoutMs) throws Exception
    {
        this(traceManager,logger,timerScheduler,10*1000,sessionTimeoutMs,2);
        enableExpiration();
    }
    
    public void enableExpiration() throws Exception
    {
        this.tokenSessions.start();
    }
    public void stop() throws Exception
    {
        this.tokenSessions.stop();
    }
    public void addSession(Trace parent,SESSION session)
    {
        removeSessionByUser(parent, session.getUser());
        synchronized(this)
        {
            this.tokenSessions.put(session.getToken(),session);
            this.userSessions.put(session.getUser(),session);
        }
        this.addSessionMeter.increment();
    }
//    public SESSION getSessionByToken(String token)
//    {
//        synchronized (this)
//        {
//            return this.tokenSessions.update(token);
//        }
//    }
    public <S extends SESSION> S getSessionByToken(String token)
    {
        synchronized (this)
        {
            return (S)this.tokenSessions.update(token);
        }
    }
    public <S extends SESSION> S getSessionByUser(String user)
    {
        synchronized (this)
        {
            return (S)this.userSessions.get(user);
        }
    }
    
    
    public boolean removeSessionByToken(Trace trace,String token)
    {
        return removeSession(trace,getSessionByToken(token));
    }
    public boolean removeSessionByUser(Trace trace,String user)
    {
        return removeSession(trace,getSessionByUser(user));
    }
    
    private boolean removeSession(Trace trace,SESSION session)
    {
        if (session==null)
        {
            return false;
        }
        synchronized(this)
        {
            session=userSessions.get(session.getUser());
            if (session==null) //Need to perform this test inside synchronized to ensure onClose is called only once.
            {
                return false;
            }
            this.userSessions.remove(session.getUser());
            this.tokenSessions.remove(session.getToken());
        }
        this.removeSessionMeter.increment();
        try
        {
            session.onClose(trace);
        }
        catch (Throwable t)
        {
            this.logger.log(t);
        }
        return true;
    }
    
    public Collection<SESSION> getSessionSnapshot()
    {
        synchronized (this)
        {
            return this.userSessions.values();
        }
    }
    public Lock<String> waitForLock(Trace parent,String user)
    {
        return lockManager.waitForLock(parent,user,this.waitForLockTimeoutMs);
    }
    
    private void timeoutSession(Trace trace, SESSION session) throws Throwable
    {
        this.timeoutSessionMeter.increment();
        removeSession(trace,session);
    }
    public long getSessionTimeoutMs()
    {
        return this.sessionTimeoutMs;
    }
}
