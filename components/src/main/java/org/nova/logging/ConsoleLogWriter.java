package org.nova.logging;

import java.util.concurrent.atomic.AtomicLong;

import org.nova.tracing.Trace;

public class ConsoleLogWriter extends LogWriter
{
    final private FormatWriter formatWriter;
    final private AtomicLong number=new AtomicLong();
    public ConsoleLogWriter()
    {
        this.formatWriter=new JSONFormatWriter(System.out);
    }
    
    @Override
    public LogEntry write(Trace trace,Level logLevel,String category,Throwable throwable,String message,Item[] items)
    {
        this.writeMeter.increment();
        LogEntry entry=new LogEntry(this.number.getAndIncrement(),category,logLevel,System.currentTimeMillis(),throwable,trace,message,items);
        try
        {
            this.formatWriter.write(entry);
        }
        catch (Throwable t)
        {
            System.err.println("ConsoleLogWriter error");
            t.printStackTrace();
        }
        return entry;
    }

    @Override
    public boolean flush(long wait_ms)
    {
        return true;
    }
}
