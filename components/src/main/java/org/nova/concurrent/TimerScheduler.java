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

//TODO: stats are not threadsafe. 

import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.nova.logging.Logger;
import org.nova.tracing.TraceManager;

public class TimerScheduler
{

	static class Key
	{
		final long due;
		final long number;
	
		Key(long due, long number) 
		{
			this.due = due;
			this.number = number;
		}
	}
	final private Logger logger;
	final TraceManager traceManager;
	final private TreeMap<Key, TimerTask> map;
    final private ExecutorService executorService; 

    private AtomicLong number;
	private Thread thread;
	private RunState runState;

	public TimerScheduler(TraceManager traceManager, Logger logger) 
	{
	    this.executorService=Executors.newVirtualThreadPerTaskExecutor();
	       
		this.traceManager = traceManager;
		this.logger = logger;
		this.number=new AtomicLong();
		this.map = new TreeMap<>((Key a, Key b) ->
		{
			if (a.due < b.due)
			{
				return -1;
			}
			if (a.due > b.due)
			{
				return 1;
			}
			if (a.number < b.number)
			{
				return -1;
			}
			if (a.number > b.number)
			{
				return 1;
			}
			return 0;
		});
	}
	
	public void start() throws Exception
	{
		synchronized (this)
		{
			if (this.thread!=null)
			{
				throw new Exception();
			}
			this.thread=new Thread(()->{run();});
			this.thread.start();
			this.runState=RunState.RUNNING;
		}
	}
	
	public void stop() 
	{
	    Thread thread;
		synchronized (this)
		{
			if (this.thread==null)
			{
				return;
			}
			thread=this.thread;
			this.runState=RunState.STOPPING;
			this.notifyAll();
            this.thread=null;
		}
		try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
        }
	}
	
    class Runner implements java.lang.Runnable
    {
        final TimerTask task;

        Runner(TimerTask task)
        {
            this.task=task;
        }

        @Override
        public void run()
        {
            task.execute();
        }
    }
	

	void run()
	{
		for (;;)
		{
			Entry<Key, TimerTask> entry=null;
			synchronized(this)
			{
				for (;;)
				{
					long now=System.currentTimeMillis();
					entry=this.map.firstEntry();
					if (entry!=null)
					{
						Key key=entry.getKey();
						long wait=key.due-now;
						if (wait>0)
						{
							try
							{
								this.wait(wait);
							}
							catch (InterruptedException e)
							{
							}
							if (this.runState==RunState.STOPPING)
							{
							    this.runState=RunState.STOPPED;
							    this.notify();
								return;
							}
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						try
						{
							this.wait();
						}
						catch (InterruptedException e)
						{
						}
						if (this.runState==RunState.STOPPING)
						{
                            this.runState=RunState.STOPPED;
                            this.notify();
							return;
						}
					}
				}
				this.map.remove(entry.getKey());
			}
			TimerTask task=entry.getValue();
	        this.executorService.execute(() -> 
	        {
	            task.execute();
	        });
		}
	}
	
	void reschedule(Key key,TimerTask task)
	{
        synchronized(this)
        {
            this.map.put(key, task);
        }
	}
	
	void cancel(Key key)
	{
		synchronized(this)
		{
			this.map.remove(key);
		}
	}

	public TimerTask schedule(String traceCategory,TimeBase timeBase,long offset, long period, TimerRunnable executable) throws Exception
	{
	    TimerTask timerTask = new TimerTask(this.number.getAndIncrement(),traceCategory, this, timeBase, offset, period,executable);
	    
	    Key key=timerTask.getSchedule();
	    if (key==null)
	    {
	        throw new Exception("TimeBase="+timeBase+", offset="+offset+",period="+period);//bug
	    }
		synchronized(this)
		{
			this.map.put(key, timerTask);
			this.notify();
		}
		return timerTask;
	}
	
	public TimerTask[] getTimerTaskSnapshot()
	{
		synchronized(this)
		{
			return this.map.values().toArray(new TimerTask[this.map.size()]);
		}
	}
	Logger getLogger()
	{
	    return this.logger;
	}
}
