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

public class Task<RESULT>
{
	private TaskStatus status;
	private RESULT result;
	private Throwable throwable;
	final private Trace scheduleTrace;
	final private String traceCategory;
	final private TraceCallable<RESULT> callable;
	final private Logger logger;
	final private TraceManager traceManager;
	
	public Task(TraceManager traceManager,Trace scheduleTrace,String traceCategory,TraceCallable<RESULT> callable,Logger logger)
	{
	    this.traceManager=traceManager;
		this.traceCategory=traceCategory;
		this.scheduleTrace=scheduleTrace;
		this.callable=(TraceCallable<RESULT>)callable;
		this.status=TaskStatus.READY;
        this.logger=logger;
	}

	void execute()
	{
		synchronized (this)
		{
			if (this.status!=TaskStatus.READY) //We need this check to support abort methods.
			{
				return;
			}
			this.status=TaskStatus.EXECUTING;
		}
		RESULT result=null;
		Throwable throwable=null;
		try (Trace trace=new Trace(this.traceManager,this.scheduleTrace,this.traceCategory,false))
		{
			try
			{
				result=this.callable.call(trace);
			}
			catch (Throwable t)
			{
				throwable=t;
				trace.close(t);
				if (this.logger!=null)
				{
				    this.logger.log(trace);
				}
			}
		}
		synchronized (this)
		{
			this.result=result;
			this.throwable=throwable;
			this.status=TaskStatus.COMPLETED;
			this.notifyAll();
		}
	}
	public TaskStatus getExecutableStatus()
	{
		synchronized(this)
		{
			return this.status;
		}
	}
	public boolean waitForCompletion(long timeout)
	{
		synchronized(this)
		{
			return Synchronization.waitForNoThrow(this,timeout, ()->{return this.status==TaskStatus.COMPLETED;});
		}
	}
	public void waitForCompletion()
	{
		synchronized(this)
		{
			Synchronization.waitForNoThrow(this, ()->{return this.status==TaskStatus.COMPLETED;});
		}
	}
	
	public RESULT get() throws Throwable
	{
		synchronized(this)
		{
			if (this.throwable!=null)
			{
				throw this.throwable;
			}
			return this.result;
		}
	}

	public RESULT getResult()
	{
		synchronized(this)
		{
			return this.result;
		}
	}
	public Throwable getThrowable()
	{
		synchronized(this)
		{
			return this.throwable;
		}
	}

}
