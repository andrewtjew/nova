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

import java.util.HashMap;

import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;

public class LockManager<KEY>
{
	final private HashMap<KEY,LockState<KEY>> slots;
	private String category;
	private final TraceManager traceManager;
	
	public LockManager(TraceManager traceManager,String category)
	{
		this.slots=new HashMap<>();
		this.traceManager=traceManager;
		this.category=category+"@"+this.getClass().getSimpleName();
	}
	
	public Lock<KEY> waitForLock(Trace parent,KEY key) throws Exception
	{
	    return waitForLock(parent,key,Long.MAX_VALUE);
	}

	public Lock<KEY> waitForLock(Trace parent,KEY key,long timeoutMs)
    {
        LockState<KEY> slot;
        Trace trace=new Trace(parent, this.category,true);
        trace.setDetails(key.toString());
        synchronized (this)
        {
            slot=this.slots.get(key);
            if (slot==null)
            {
                slot=new LockState<KEY>(key);
                this.slots.put(key, slot);
            }
        }
        final LockState<KEY> slot_=slot;
        synchronized (slot_)
        {
            if (slot_.locked)
            {
                slot.waiting++;
                try
                {
                    if (Synchronization.waitForNoThrow(slot, ()->{return slot_.locked==false;},timeoutMs)==false)
                    {
                        trace.close();
                        return null;
                    }
                }
                finally
                {
                    slot.waiting--;
                }
            }
            slot_.locked=true;
            slot.created=System.currentTimeMillis();
        }

        
        trace.endWait();
        return new Lock<KEY>(this,slot,trace);
    }

	void release(LockState<KEY> slot)
	{
		synchronized (slot)
		{
		    slot.locked=false;
            if (slot.waiting==0)
            {
                synchronized (this)
                {
                    this.slots.remove(slot.key);
                    
                    return;
                }
            }
			slot.notify();
		}
	}
	
	public LockState<KEY>[] getSnapshot()
	{
        synchronized (this)
        {
            return this.slots.values().toArray(new LockState[this.slots.size()]); 
        }	    
	}
}
