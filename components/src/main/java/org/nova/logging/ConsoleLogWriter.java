package org.nova.logging;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import org.nova.collections.RingBuffer;
import org.nova.concurrent.Synchronization;
import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.debug.LogLevel;
import org.nova.metrics.CountMeter;
import org.nova.metrics.LevelMeter;
import org.nova.metrics.RateMeter;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;

import net.jpountz.lz4.LZ4BlockOutputStream;

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

}
