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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nova.logging.Logger;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceCallable;
import org.nova.tracing.TraceManager;
import org.nova.tracing.TraceRunnable;

public class MultiTaskScheduler
{
	private final ExecutorService executorService;
	private final TraceManager traceManager;
	private long number;
	private final HashMap<Long,Progress<?>> progresses;
	private final Logger logger;
	
	public MultiTaskScheduler(TraceManager traceManager,ExecutorService executorService,Logger logger)
	{
		this.traceManager=traceManager;
		this.executorService=executorService;
		this.progresses=new HashMap<>();
		this.logger=logger;
	}
    public MultiTaskScheduler(TraceManager traceManager,int maximumThreads,Logger logger)
    {
        this(traceManager,Executors.newFixedThreadPool(maximumThreads),logger);
    }

	class Runner implements java.lang.Runnable
	{
		final Progress<?> progress;
		final Task<?> task;

		Runner(Progress<?> progress,Task<?> task)
		{
			this.task=task;
			this.progress=progress;
		}

		@Override
		public void run()
		{
		    this.progress.startTask();
			this.task.execute();
			complete(progress);
		}
	}
	
	private void complete(Progress<?> futures)
	{
		synchronized(this)
		{
			if (futures.completeTask())
			{
				this.progresses.remove(futures.getNumber());
			}
		}		
	}
	
    public <RESULT> Progress<RESULT> schedule(Trace parent,String traceCategory,TraceCallable<RESULT>...callables)
	{
		Progress<RESULT> future=null;
		synchronized(this)
		{
			long number=this.number++;
			future=new Progress<RESULT>(this.traceManager,parent,traceCategory,number,callables,this.logger);
			this.progresses.put(number, future);
		}
		for (int i=0;i<callables.length;i++)
		{
			this.executorService.submit(new Runner(future, future.getTask(i)));
		}
		return future;
	}

	public Progress<Void> schedule(Trace parent,String traceCategory,TraceRunnable...runnables)
    {
        Progress<Void> progress=null;
        synchronized(this)
        {
            long number=this.number++;
            progress=new Progress<Void>(this.traceManager,parent,traceCategory,number,runnables,this.logger);
            this.progresses.put(number, progress);
        }
        for (int i=0;i<runnables.length;i++)
        {
            this.executorService.submit(new Runner(progress, progress.getTask(i)));
        }
        return progress;
    }


	public Progress<?>[] getProgressSnapshot()
	{
		synchronized(this)
		{
			return this.progresses.values().toArray(new Progress[this.progresses.size()]);
		}
	}
	
	public void stop()
	{
        synchronized(this)
        {
            this.executorService.shutdownNow();
        }
	}
}
