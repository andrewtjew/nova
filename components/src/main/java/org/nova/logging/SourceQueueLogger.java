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
package org.nova.logging;

import java.util.List;

import org.nova.collections.RingBuffer;
import org.nova.flow.SourceQueue;
import org.nova.logging.Item;
import org.nova.logging.Level;
import org.nova.logging.LogEntry;
import org.nova.logging.Logger;
import org.nova.metrics.CountMeter;
import org.nova.metrics.RateMeter;
import org.nova.tracing.Trace;

public class SourceQueueLogger extends Logger
{
    private final SourceQueue<LogEntry> logQueue;
    private boolean active;
    private CountMeter logFailures;
    private Throwable logFailureThrowable;
    private long number;
    private RateMeter rateMeter;
    private RingBuffer<LogEntry> buffer;
    
    public SourceQueueLogger(int bufferSize,String category,SourceQueue<LogEntry> logQueue)
    {
        super(category);
        this.logQueue=logQueue;
        this.number=0;
        this.logFailures=new CountMeter();
        this.active=true;
        this.rateMeter=new RateMeter();
        if (bufferSize>0)
        {
            this.buffer=new RingBuffer<LogEntry>(new LogEntry[bufferSize]);
        }
        else
        {
            this.buffer=null;
        }
        
    }

    @Override
    public void write(Trace trace,Level logLevel,String category,Throwable throwable,String message,Item[] items)
    {
        synchronized(this)
        {
            LogEntry entry=new LogEntry(this.number++,category,logLevel,System.currentTimeMillis(),throwable,trace,message,items);
            if (this.buffer!=null)
            {
                this.buffer.add(entry);
            }
            if (this.active==false)
            {
                return;
            }
            try
            {
                this.logQueue.send(entry);
                this.rateMeter.increment();
            }
            catch (Throwable t)
            {
                logFailureThrowable=t;
                this.logFailures.increment();
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
    public CountMeter getLogFailures()
    {
        return this.logFailures;
    }
    public Throwable getLogFailureThrowable()
    {
        return this.logFailureThrowable;
    }
    public SourceQueue<LogEntry> getSourceQueue()
    {
        return this.logQueue;
    }
    public List<LogEntry> getLastLogEntries()
    {
        synchronized(this)
        {
            if (this.buffer==null)
            {
                return null;
            }
            return this.buffer.getSnapshot();
        }
    }
    public RateMeter getRateMeter()
    {
        return this.rateMeter;
    }

}
