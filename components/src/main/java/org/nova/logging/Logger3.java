package org.nova.logging;

import java.util.ArrayList;
import java.util.List;

import org.nova.collections.RingBuffer;
import org.nova.flow.SourceQueue;
import org.nova.metrics.CountMeter;
import org.nova.metrics.RateMeter;
import org.nova.tracing.Trace;

public class Logger3 
{
    private boolean active;
    final private String category;
    final private RingBuffer<LogEntry> lastLogEntries;
    final LogWriter logWriter;
    
    public Logger3(String category,LogWriter queue,int bufferSize)
    {
        this.category=category;
        this.active=true;
        this.logWriter=queue;
        if (bufferSize>0)
        {
            this.lastLogEntries=new RingBuffer<LogEntry>(new LogEntry[bufferSize]);
        }
        else
        {
            this.lastLogEntries=null;
        }
    }
    public Logger3(String category,MultiThreadLogWriter queue)
    {
        this(category,queue,1000);
    }
    public void log(Trace trace,Level logLevel,String category,Throwable throwable,String message,Item[] items)
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
    public void log(Throwable throwable,String message,Item...items)
    {
        log(null,throwable!=null?Level.EXCEPTION:Level.NORMAL,this.category,throwable,message,items);
    }
    public void log(Throwable throwable)
    {
        log(throwable,null);
    }
    
    public void log(Level logLevel,String message,Item...items)
    {
        log(null,logLevel,this.category,null,message,items);
    }
    public void log(String message,Item...items)
    {
        log(Level.NORMAL,message,items);
    }

    public void log(Trace trace,String message,Item...items)
    {
        log(trace,trace.getThrowable()!=null?Level.EXCEPTION:Level.NORMAL,this.category,null,message,items);
    }
    public void log(Trace trace,Item...items)
    {
        log(trace,null,items);
    }
    
    public String getCategory()
    {
        return this.category;
    }

//    public static Item[] toArray(ArrayList<Item> items)
//    {
//        return items.toArray(new Item[items.size()]);
//    }
    
}
