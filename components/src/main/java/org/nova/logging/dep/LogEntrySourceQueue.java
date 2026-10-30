package org.nova.logging.dep;

import org.nova.flow.Node;
import org.nova.flow.SourceQueue;
import org.nova.flow.SourceQueueConfiguration;
import org.nova.logging.Item;
import org.nova.logging.Level;
import org.nova.logging.LogEntry;
import org.nova.tracing.Trace;

public class LogEntrySourceQueue extends SourceQueue<LogEntry> 
{
    private long number;
    public LogEntrySourceQueue(Node receiver, SourceQueueConfiguration configuration)
    {
        super(receiver, configuration);
    }
    public LogEntry write(Trace trace,Level logLevel,String category,Throwable throwable,String message,Item[] items)
    {
        synchronized(this)
        {
            LogEntry entry=new LogEntry(number++,category,logLevel,System.currentTimeMillis(),throwable,trace,message,items);
            this.send(entry);
            return entry;
        }
    }

}
