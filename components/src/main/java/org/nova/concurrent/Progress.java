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

import org.nova.logging.Logger;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceCallable;
import org.nova.tracing.TraceManager;

public class Progress<RESULT>
{
	final Task<?>[] tasks;
	final private long number;
	final private Trace trace;
    private int waiting;
    private int completed;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	Progress(TraceManager traceManager,Trace parent,String traceCategory,long number,TraceCallable<?>[] executables,Logger logger)
	{
		this.number=number;
		this.waiting=executables.length;
		
		this.tasks=new Task<?>[executables.length];
		
		this.trace=new Trace(traceManager,parent,"scheduler@"+traceCategory,true);
		for (int i=0;i<executables.length;i++)
		{
			this.tasks[i]=new Task(traceManager,this.trace,traceCategory+"@"+i,executables[i],logger);
		}
	}
	
	void startTask()
	{
        synchronized (this)
        {
            this.waiting--;
            if (this.waiting==0)
            {
                this.trace.close();
            }
        }
	}
	
	boolean completeTask()
	{
		synchronized (this)
		{
		    this.completed++;
		    this.notifyAll();
		    return this.completed>=this.tasks.length;
		}
	}
	@SuppressWarnings("unchecked")
	public Task<RESULT> getTask(int index)
	{
		return (Task<RESULT>)this.tasks[index];
	}

	@SuppressWarnings("unchecked")
	public RESULT get(int index) throws Throwable
	{
		return (RESULT)this.tasks[index].get();
	}

	@SuppressWarnings("unchecked")
	public RESULT getResult(int index)
	{
		return (RESULT)this.tasks[index].getResult();
	}

	public Throwable getThrowable(int index)
	{
		return this.tasks[index].getThrowable();
	}

	public RESULT get() throws Throwable 
	{
		return get(0);
	}

	public RESULT getResult() 
	{
		return getResult(0);
	}

	public Throwable getThrowable()
	{
		return getThrowable(0);
	}

	public Trace getTrace()
	{
		return trace;
	}
	public long getNumber()
	{
		return this.number;
	}
	
	public boolean waitAll(long timeout)
	{
		synchronized(this)
		{
			return Synchronization.waitForNoThrow(this,timeout, ()->{return this.completed>=this.tasks.length;});
		}
	}
	public void waitAll()
	{
		synchronized(this)
		{
			Synchronization.waitForNoThrow(this, ()->{return this.completed>=this.tasks.length;});
		}
	}
	public boolean waitAtLeast(long timeout,int count)
	{
		synchronized(this)
		{
			return Synchronization.waitForNoThrow(this,timeout, ()->{return this.completed>=count;});
		}
	}
	public void waitAtLeast(int count)
	{
		synchronized(this)
		{
			Synchronization.waitForNoThrow(this, ()->{return this.completed>=count;});
		}
	}

	public boolean waitAny(long timeout)
	{
		synchronized(this)
		{
			return Synchronization.waitForNoThrow(this,timeout, ()->{return this.completed>0;});
		}
	}
	public void waitAny()
	{
		synchronized(this)
		{
			Synchronization.waitForNoThrow(this, ()->{return this.completed>0;});
		}
	}

	public int getCompleted()
	{
		synchronized(this)
		{
			return this.completed;
		}
	}

    public int getWaiting()
    {
        synchronized(this)
        {
            return this.waiting;
        }
    }

	public int getExecuting()
	{
		synchronized(this)
		{
			return this.tasks.length-this.waiting-this.completed;
		}
	}
	
	public boolean allCompleted()
	{
		synchronized(this)
		{
			return this.completed>=this.tasks.length;
		}
	}
	public boolean anyCompleted()
	{
		synchronized(this)
		{
			return this.completed>0;
		}
	}
	public int size()
	{
		return this.tasks.length;
	}
}
