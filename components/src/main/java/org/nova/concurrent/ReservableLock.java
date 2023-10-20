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
package org.nova.concurrent;

import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;

public class ReservableLock
{
    private long delayInstantMs;
    private Object object;
    private boolean reserved;
    private final long delayMs;
    private final TraceManager traceManager;
    
    public ReservableLock(TraceManager traceManager,long delayMs)
    {
        this.object=null;
        this.traceManager=traceManager;
        this.delayMs=delayMs;
        this.delayInstantMs=0;
    }
    public ReservableLock(TraceManager traceManager)
    {
        this(traceManager, 0);
    }
    
    
    public boolean isOpen()
    {
        synchronized (this)
        {
            if (this.reserved||this.object!=null)
            {
                return false;
            }
            if (this.delayMs>0)
            {
                if (System.currentTimeMillis()-this.delayInstantMs<=this.delayMs)
                {
                    return false;
                }
            }
            return true;
        }        
    }
    public LockReservedState openReservation(Trace parent,String categoryOverride)
    {
        if (categoryOverride==null)
        {
            categoryOverride=this.getClass().getSimpleName();
        }
        Trace trace=new Trace(this.traceManager,parent,categoryOverride);
        synchronized (this)
        {
            boolean open=this.reserved==false&&this.object==null;
            if (open&&(this.delayMs>0))
            {
                if (System.currentTimeMillis()-this.delayInstantMs<=this.delayMs)
                {
                    open=false;
                }
            }
            LockReservedState context=new LockReservedState(this, open, trace);
            this.reserved=true;
            return context;
        }        
    }
    
    public boolean waitForUnlock(long timeout)
    {
        synchronized(this)
        {
            return Synchronization.waitForNoThrow(this,timeout, ()->{return this.object==null;});
        }        
    }

    public void waitForUnlock()
    {
        synchronized(this)
        {
            Synchronization.waitForNoThrow(this, ()->{return this.object==null;});
        }        
    }

    
    void releaseReservation()
    {
        synchronized(this)
        {
            this.reserved=false;
        }
    }
    
    void setLockObject(Object object)
    {
        synchronized(this)
        {
            this.object=object;
        }
    }
    
    public Object getLockObject()
    {
        synchronized(this)
        {
            return this.object;
        }
    }

    @SuppressWarnings("unchecked")
    public <OBJECT> OBJECT getLockObject(Class<OBJECT> type)
    {
        synchronized(this)
        {
            if ((this.object==null)||(this.object.getClass()!=type))
            {
                return null;
            }
            return (OBJECT)this.object;
        }
    }

    public <OBJECT> OBJECT unlock(Class<OBJECT> type)
    {
        synchronized(this)
        {
            if (this.object==null)
            {
                return null;
            }
            
            @SuppressWarnings("unchecked")
            OBJECT object=(this.object.getClass()==type)?(OBJECT)this.object:null;
            this.object=null;
            if (this.delayMs>0)
            {
                this.delayInstantMs=System.currentTimeMillis();
            }
            return object;
        }
    }
    
    public void delayLock()
    {
        synchronized(this)
        {
            this.delayInstantMs=System.currentTimeMillis();
        }
    }
    
    public Object unlock()
    {
        Object object;
        synchronized(this)
        {
            object=this.object;
            this.object=null;
        }
        return object;
    }
    
}
