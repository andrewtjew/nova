package org.nova.tracing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.nova.annotations.Description;
import org.nova.logging.Logger;
import org.nova.metrics.CountMeter;
import org.nova.metrics.TraceMeter;
import org.nova.metrics.TraceSample;
import org.nova.metrics.RateMeter;
import org.nova.metrics.RateSample;
import org.nova.operations.OperatorVariable;

// Don't track Trace in Thread Local Store. The problem is figuring out after closing a Trace which trace to make current
public class TraceManager
{
	final private HashMap<Long,Trace> currentTraces;
    final private Object managerLock=new Object();
    final private HashMap<String,TraceNode> traceRoots;
	final private TraceBuffer lastExceptions;
    final private TraceBuffer lastTraces;
    final private TraceBuffer watchTraces;
	final private HashSet<String> watchCategories;
    final private RateMeter rateMeter;
    final private CountMeter gapMeter;
	final private Logger logger;
	final private int maximumActives;
    final private HashMap<String,TraceMeter> lastTraceMeters;
    final private HashMap<String,TraceMeter> watchTraceMeters;
    private long number;
    private long currentTracesOverflowCount;

	
    @OperatorVariable(description="Capture the stackTrace when trace is closed. WARNING: Application performance may drop.")
    private boolean captureCreateStackTrace;

    @OperatorVariable(description="Capture the stackTrace when trace is closed. WARNING: Application performance may drop.")
    private boolean captureCloseStackTrace;
	
    @OperatorVariable(description="Log traces. WARNING: Application performance may drop. Logger may become stressed.")
	private boolean logTraces;
	
	@OperatorVariable(description="Log exception traces. WARNING: Logger may become stressed.")
	private boolean logExceptionTraces;
	
	@OperatorVariable(description="Enable last trace watching. WARNING: Application performance may drop.")
	private boolean enableLastTraceWatching;

    @OperatorVariable(description="Log traces with greater duration in milliseconds. A negative value disables this form of logging. WARNING: Application performance may drop for small durations.")
    private long logTracesWithGreaterDuration;
	
	public TraceManager(Logger logger,TraceManagerConfiguration configuration)
	{
		this.currentTraces=new HashMap<>();
		this.lastTraceMeters=new HashMap<>();
		this.traceRoots=new HashMap<>();
		this.rateMeter=new RateMeter();
		this.gapMeter=new CountMeter();
		this.lastExceptions=new TraceBuffer(configuration.lastExceptionBufferSize);
		this.lastTraces=new TraceBuffer(configuration.lastTraceBufferSize);
        this.watchTraces=new TraceBuffer(configuration.lastTraceBufferSize);
        this.watchCategories=new HashSet<>();
        this.watchTraceMeters=new HashMap<>();
		this.logExceptionTraces=configuration.logExceptionTraces;
		this.logTraces=configuration.logTraces;
		this.enableLastTraceWatching=configuration.enableLastTraceWatching;
        this.captureCreateStackTrace=configuration.captureCreateStackTrace;
        this.captureCloseStackTrace=configuration.captureCloseStackTrace;
		this.maximumActives=configuration.maximumActives;
		this.logTracesWithGreaterDuration=configuration.logSlowTraceDurationMs;
		this.logger=logger;
	}
	public TraceManager(Logger logger)
	{
        this(logger, new TraceManagerConfiguration());
	}
	public TraceManager()
	{
		this(null);
	}
	
	
	TraceContext open(Trace trace)
	{
	    long number;
		synchronized(this.managerLock)
		{
		    this.rateMeter.increment();
			number=this.number++;
			if (this.currentTraces.size()<this.maximumActives)
			{
				this.currentTraces.put(number, trace);
			}
			else
			{
				this.currentTracesOverflowCount++;
			}
		}
        return new TraceContext(number,this.captureCreateStackTrace,this.captureCloseStackTrace);
	}
	

	TraceNode getTraceNode(String category,Trace parent)
    {
        if (parent!=null)
        {
            return parent.traceNode.getOrCreateChildTraceNode(category);
        }
        synchronized (this.managerLock)
        {
            TraceNode traceNode=this.traceRoots.get(category);
            if (traceNode==null)
            {
                traceNode=new TraceNode(); //Make sure constructor does not use locks
                this.traceRoots.put(category, traceNode);
            }
            return traceNode;
        }
    }
	
	void close(Trace trace)
	{
	    boolean log=false;
	    TraceMeter meter;
	    TraceMeter watchMeter=null;
	    synchronized(this.managerLock)
	    {
	        this.currentTraces.remove(trace.getNumber());
   			this.lastTraces.add(trace);
            meter=this.lastTraceMeters.get(trace.getCategory());
            if (meter==null)
            {
                meter=new TraceMeter();
                this.lastTraceMeters.put(trace.getCategory(), meter);
            }
    		if (this.enableLastTraceWatching)
    		{
                if (this.watchCategories.contains(trace.getCategory()))
                {
                    watchMeter=this.watchTraceMeters.get(trace.getCategory());
                    if (watchMeter==null)
                    {
                        watchMeter=new TraceMeter();
                        this.watchTraceMeters.put(trace.getCategory(), watchMeter);
                    }
                    this.watchTraces.add(trace);
    			}
    		}
		
			if (trace.getThrowable()!=null)
			{
				this.lastExceptions.add(trace);
			}
    		log=logTraces||logExceptionTraces||((logTracesWithGreaterDuration>=0)&&(trace.getDurationNs()/10000000>=logTracesWithGreaterDuration));
		}
        if (log)
        {
            if (this.logger!=null)
            {
                this.logger.log(trace);
            }
        }
        meter.update(trace);
        if (watchMeter!=null)
        {
            watchMeter.update(trace);
        }
	}
	
	public Trace[] getCurrentTraces()
	{
		synchronized (this.managerLock)
		{
			return this.currentTraces.values().toArray(new Trace[this.currentTraces.size()]);
		}
	}
	
	public RateMeter getRateMeter()
	{
	    return this.rateMeter;
	}
	public CategorySample[] getLastTraceCategorySamples()
	{
        synchronized (this.managerLock)
		{
			try
			{
			    ArrayList<CategorySample> samples=new ArrayList<>(this.lastTraceMeters.size());
			    for (Entry<String, TraceMeter> entry:this.lastTraceMeters.entrySet())
			    {
			        samples.add(new CategorySample(entry.getKey(),entry.getValue().sample()));
			    }
			    return samples.toArray(new CategorySample[samples.size()]);
			}
			finally
			{
				this.lastTraceMeters.clear();
			}
		}
	}
	public Map<String,TraceNode> getTraceMeterCategories()
	{
		HashMap<String,TraceNode> traceRoots=new HashMap<>();
        synchronized (this.managerLock)
		{
		    traceRoots.putAll(this.traceRoots);
		}
		return traceRoots;
		
	}
    public Map<String,TraceNode> getTraceTreeRootsSnapshot()
    {
        HashMap<String,TraceNode> traceRoots=new HashMap<>();
        synchronized (this.managerLock)
        {
            traceRoots.putAll(this.traceRoots);
        }
        return traceRoots;
        
    }

    public void resetTraceGraph()
    {
        synchronized (this.managerLock)
        {
            this.traceRoots.clear();
        }
    }
    /*
    public void resetTraceGraphNodes(String[] categories)
    {
        synchronized (this.managerLock)
        {
            for (int entry:this.traceRoots)
            {
                
            }
        }
    }
    */
	public String[] getWatchList()
	{
        synchronized (this.managerLock)
	    {
	        if (this.watchCategories==null)
	        {
	            return new String[0];
	        }
	        return this.watchCategories.toArray(new String[this.watchCategories.size()]);
	    }
	}
	public boolean isCaptureCreateStackTrace()
	{
        synchronized (this.managerLock)
		{
			return this.captureCreateStackTrace;
		}
	}
	public void setCaptureCreateStackTrace(boolean captureCreateStackTrace)
	{
        synchronized (this.managerLock)
		{
			this.captureCreateStackTrace=captureCreateStackTrace;
		}
	}
    public boolean isCaptureCloseStackTrace()
    {
        synchronized (this.managerLock)
        {
            return this.captureCloseStackTrace;
        }
    }
    public void setCaptureCloseStackTrace(boolean captureCloseStackTrace)
    {
        synchronized (this.managerLock)
        {
            this.captureCloseStackTrace=captureCloseStackTrace;
        }
    }
	public boolean isLogTraces()
	{
        synchronized (this.managerLock)
		{
			return logTraces;
		}
	}
	public void setLogTraces(boolean logTraces)
	{
        synchronized (this.managerLock)
		{
			this.logTraces = logTraces;
		}
	}
	public boolean isLogExceptionTraces()
	{
        synchronized (this.managerLock)
		{
			return logExceptionTraces;
		}
	}
	public void setLogExceptionTraces(boolean logExceptionTraces)
	{
        synchronized (this.managerLock)
		{
			this.logExceptionTraces = logExceptionTraces;
		}
	}
	public boolean isEnableLastTraceWatching()
	{
        synchronized (this.managerLock)
		{
			return enableLastTraceWatching;
		}
	}
	public void enableWatchListLastTraces(String[] categories)
	{
        synchronized (this.managerLock)
	    {
	        for (String category:categories)
	        {
	            this.watchCategories.add(category);
	        }
			this.enableLastTraceWatching = true;
		}
	}
    public void disableWatchListLastTraces()
    {
        synchronized (this.managerLock)
        {
            this.enableLastTraceWatching = false;
            this.watchCategories.clear();
            this.watchTraceMeters.clear();
        }
    }
	public void setLogTracesWithGreaterDuration(long durationMs)
	{
        synchronized (this.managerLock)
	    {
	        this.logTracesWithGreaterDuration=durationMs;
	    }
	}
	public long getLogTracesWithGreaterDuration()
	{
        synchronized (this.managerLock)
        {
            return this.logTracesWithGreaterDuration;
        }
	}
	
	public Trace[] getLastExceptionTraces()
	{
        synchronized (this.managerLock)
		{
			return this.lastExceptions.getSnapshotAsArray();
		}
	}
	
	public Trace[] getLastTraces()
	{
        synchronized (this.managerLock)
		{
		    return this.lastTraces.getSnapshotAsArray();
		}
	}
	
    public void clearLastTraces()
    {
        synchronized (this.managerLock)
        {
            this.lastTraces.clear();
        }
    }

    public void clearLastExceptionTraces()
    {
        synchronized (this.managerLock)
        {
            this.lastExceptions.clear();
        }
    }
    
    public CategorySample[] sampleLastCategories()
    {
        synchronized (this.managerLock)
        {
            CategorySample[] samples=new CategorySample[this.lastTraceMeters.size()];
            {
                int index=0;
                for (Entry<String, TraceMeter> entry:this.lastTraceMeters.entrySet())
                {
                    samples[index++]=new CategorySample(entry.getKey(), entry.getValue().sample());
                }
            }
            return samples;
        }
    }

    public CategorySample[] sampleWatchCategories()
    {
        synchronized (this.managerLock)
        {
            CategorySample[] samples=new CategorySample[this.watchTraceMeters.size()];
            {
                int index=0;
                for (Entry<String, TraceMeter> entry:this.watchTraceMeters.entrySet())
                {
                    samples[index++]=new CategorySample(entry.getKey(), entry.getValue().sample());
                }
            }
            return samples;
        }
    }
    public CategorySample[] sampleAndResetLastCategories()
    {
        synchronized (this.managerLock)
        {
            CategorySample[] samples=new CategorySample[this.lastTraceMeters.size()];
            {
                int index=0;
                for (Entry<String, TraceMeter> entry:this.lastTraceMeters.entrySet())
                {
                    samples[index++]=new CategorySample(entry.getKey(), entry.getValue().sample());
                }
                this.lastTraceMeters.clear();
            }
            return samples;
        }
    }

    public CategorySample[] sampleAndResetWatchCategories()
    {
        synchronized (this.managerLock)
        {
            CategorySample[] samples=new CategorySample[this.watchTraceMeters.size()];
            {
                int index=0;
                for (Entry<String, TraceMeter> entry:this.watchTraceMeters.entrySet())
                {
                    samples[index++]=new CategorySample(entry.getKey(), entry.getValue().sample());
                }
                this.watchTraceMeters.clear();
            }
            return samples;
        }
    }

    public CategorySample[] sampleLastWatchTraces()
    {
        if (this.enableLastTraceWatching==false)
        {
            return new CategorySample[0];
        }
        Trace[] traces;
        synchronized (this.managerLock)
        {
            traces=this.watchTraces.getSnapshotAsArray();
        }
        return sampleCategories(traces, false);
    }
    
    public CategorySample[] sampleLastTraces()
    {
        Trace[] traces;
        synchronized (this.managerLock)
        {
            traces=this.lastTraces.getSnapshotAsArray();
        }
        return sampleCategories(traces, false);
    }
    
    private CategorySample[] sampleCategories(Trace[] traces,boolean excludeWaiting)
    {
        HashMap<String,CategorySample> categories=new HashMap<>(traces.length);
        for (Trace trace:traces)
        {
            if ((excludeWaiting)&&trace.isWaiting())
            {
                continue;
            }
            TraceMeter meter=new TraceMeter();
            meter.update(trace);
            CategorySample sample=categories.get(trace.getCategory());
            if (sample!=null)
            {
                meter.update(sample.getSample());
            }
            categories.put(trace.getCategory(), new CategorySample(trace.getCategory(),meter.sample()));
        }
        return categories.values().toArray(new CategorySample[categories.size()]);
    }        
    
    public CategorySample[] sampleCurrentTraceCategories(boolean excludeWaiting)
    {
        Trace[] traces=null;
        synchronized (this.managerLock)
        {
            traces=this.currentTraces.values().toArray(new Trace[this.currentTraces.size()]);
        }
        return sampleCategories(traces, excludeWaiting);
    }
    
    public Trace[] getLastWatchTraces()
    {
        if (this.watchTraces!=null)
        {
            synchronized(this.managerLock)
            {
                return this.watchTraces.getSnapshotAsArray();
            }
        }
        return new Trace[0];
    }
	
    public long getCurrentTracesOverflowCount()
    {
        synchronized (this)
        {
            return this.currentTracesOverflowCount;
        }
    }
    
}
