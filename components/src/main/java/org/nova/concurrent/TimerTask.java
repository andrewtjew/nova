package org.nova.concurrent;

import java.util.Timer;

import org.nova.concurrent.TimerScheduler.Key;
import org.nova.tracing.Trace;

public class TimerTask
{
	static public enum TimeBase
	{
		FIXED,
		FREE,
		SECOND,
		MINUTE,
		HOUR,
		WEEK,
		MONTH,
		YEAR,
	}
	
	final private TimerScheduler timerScheduler;
	final private TimerRunnable executable;
	final private String category;
	final private long period;
	final private long delay;
	final private long number;
	final private long created;
	final TimeBase schedulingMode;
	private long due;
	private long totalDuration;
	private long attempts;
	private long successes;
	private long throwables;
	private Throwable lastThrowable;
	private TimerScheduler.Key key;
	private TaskStatus executeStatus;
	private long misses;
	private boolean cancel;
	
	TimerTask(long number,String category,TimerScheduler timerScheduler,TimeBase schedulingMode,long delay,long period,TimerRunnable executable)
	{
		this.created=System.currentTimeMillis();
		this.number=number;
		this.delay=delay;
		this.category=category;
		this.timerScheduler=timerScheduler;
		this.executable=executable;
		this.period=period;
		this.executeStatus=TaskStatus.READY;
		this.due=this.created+delay;
		this.schedulingMode=schedulingMode;
		this.key=new Key(this.due, this.number);
	}
	
	TimerScheduler.Key getKey()
	{
		return this.key;
	}
	
	Key execute()
	{
		try (Trace trace=new Trace(this.timerScheduler.traceManager,this.category))
		{
			try
			{
				synchronized(this)
				{
					if (this.cancel)
					{
						this.executeStatus=TaskStatus.COMPLETED;
						return null;
					}
					this.executeStatus=TaskStatus.EXECUTING;
				}
				this.attempts++;
				this.executable.run(trace, this);
				this.successes++;
			}
			catch (Throwable t)
			{
				this.lastThrowable=t;
				this.throwables++;
				trace.close(t);
			}
			this.totalDuration+=trace.getDurationNs();
			synchronized(this)
			{
				if (this.cancel)
				{
					this.executeStatus=TaskStatus.COMPLETED;
					return null;
				}
				if (period<=0)
				{
					this.executeStatus=TaskStatus.COMPLETED;
					return null;
				}
				this.executeStatus=TaskStatus.READY;
				
				switch (this.schedulingMode)
				{
				case FIXED:
				{
					long now=System.currentTimeMillis();
					long duration=now-this.due;
					long misses=duration/period;
					this.misses+=misses;
					this.due=this.due+this.period*(misses+1);
				}	
					break;
				case FREE:
				{
					long now=System.currentTimeMillis();
					long duration=now-this.due;
					this.misses+=duration/period;
					this.due=now+this.period;
				}	
					break;
				default:
					//TODO implement the others
					break;
				}
				this.key=new Key(this.due, this.number);
				return this.key;
			}
		}
	}
	
	public void cancel()
	{
		synchronized (this)
		{
			this.cancel=true;
			this.timerScheduler.cancel(this.key);
		}
	}

	public String getCategory()
	{
		return category;
	}

	public TimeBase getShedulingMode()
	{
		return this.schedulingMode;
	}
	public long getPeriod()
	{
		return period;
	}

	public long getNumber()
	{
		return number;
	}

	public long getCreated()
	{
		return created;
	}

	public long getDue()
	{
		return due;
	}

	public long getDelay()
	{
		return delay;
	}

	public long getTotalDuration()
	{
		return totalDuration;
	}

	public long getAttempts()
	{
		return attempts;
	}

	public long getSuccesses()
	{
		return successes;
	}

	public long getThrowables()
	{
		return throwables;
	}

	public Throwable getLastThrowable()
	{
		return lastThrowable;
	}
	public TaskStatus getExecutableStatus()
	{
		return executeStatus;
	}
	public long getMisses()
	{
		return misses;
	}
}