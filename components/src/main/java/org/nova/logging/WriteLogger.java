package org.nova.logging;

import java.util.ArrayList;
import java.util.List;

import org.nova.collections.RingBuffer;
import org.nova.flow.SourceQueue;
import org.nova.metrics.CountMeter;
import org.nova.metrics.RateMeter;
import org.nova.tracing.Trace;

public class WriteLogger extends Logger 
{
    private boolean active;
    final private RingBuffer<LogEntry> lastLogEntries;
    final LogWriter logWriter;
    
    public WriteLogger(String category,LogWriter logWriter,int bufferSize)
    {
        super(category);
        this.active=true;
        this.logWriter=logWriter;
        if (bufferSize>0)
        {
            this.lastLogEntries=new RingBuffer<LogEntry>(new LogEntry[bufferSize]);
        }
        else
        {
            this.lastLogEntries=null;
        }
    }
    public WriteLogger(String category,LogWriter logWriter)
    {
        this(category,logWriter,1000);
    }
    public void write(Trace trace,Level logLevel,String category,Throwable throwable,String message,Item[] items)
    {
        synchronized(this)
        {
            if (!this.active)
            {
                return;
            }
        }
        LogEntry entry=this.logWriter.write(trace,logLevel,category,throwable,message,items);
        if (this.lastLogEntries!=null)
        {
            synchronized(this)
            {
                {
                    this.lastLogEntries.add(entry);
                }
            }
        }
    }
    public void setActive(boolean active)
    {
        synchronized(this)
        {
            this.active=active;
        }
    }
    public boolean isActive()
    {
        synchronized(this)
        {
            return this.active;
        }
    }
    public List<LogEntry> getLastLogEntries()
    {
        synchronized(this)
        {
            if (this.lastLogEntries==null)
            {
                return null;
            }
            return this.lastLogEntries.getSnapshot();
        }
    }
}
