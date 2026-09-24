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
import org.nova.logging.MultiThreadFileLogWriter.CompressionFormat;
import org.nova.logging.MultiThreadFileLogWriter.Configuration;
import org.nova.logging.MultiThreadFileLogWriter.FileFormat;
import org.nova.metrics.CountMeter;
import org.nova.metrics.LevelMeter;
import org.nova.metrics.RateMeter;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;

import net.jpountz.lz4.LZ4BlockOutputStream;

public abstract class MultiThreadLogWriter extends LogWriter
{
    static public class Configuration
    {
        public long stallWaitMs=3000;
        public long rollOverWait_ms=60000;
        public int fileBufferCapacity=65536;
        public int bufferSize=100000;
        public int threads=6;
    }

    final protected static boolean DEBUG=false;
    final protected static boolean DEBUG_WRITE_FILE=false;
    final protected static boolean DEBUG_WAITING_IN_QUEUE=false;
    final protected static boolean DEBUG_QUEUE_ORDERING=false;
    final protected static boolean DEBUG_QUEUE_TRACING=false;
    static final String DEBUG_CATEGORY=MultiThreadLogWriter.class.getSimpleName();

    final private RingBuffer<LogEntryBuffer> logEntryBuffers;
    final private RingBuffer<LogEntryBuffer> logEntryQueue;
    private LogEntryBuffer currentBuffer;
    final private Object currentBufferLock;
    
    final private Configuration configuration;
    private Thread[] threads;
    protected boolean stop;
    private Throwable throwable;

    private long testNumber=0;
    private long number = 0;
    private long bufferNumber = 0;

    abstract protected void write(int threadIndex,LogEntryBuffer buffer) throws Throwable;
    abstract protected void signalStop() throws Throwable;
    
    public MultiThreadLogWriter(Configuration configuration)
    {
        this.configuration=configuration;

        int buffers=configuration.threads+2;
        this.logEntryBuffers = new RingBuffer<>(new LogEntryBuffer[buffers]);
        this.logEntryQueue = new RingBuffer<>(new LogEntryBuffer[buffers]);
        for (int i = 0; i < buffers; i++)
        {
            LogEntryBuffer entryBuffer = new LogEntryBuffer(configuration.bufferSize);
            this.logEntryBuffers.add(entryBuffer);
        }
        this.currentBufferLock = new Object();
        synchronized (this.currentBufferLock)
        {
            synchronized (this.logEntryBuffers)
            {
                this.currentBuffer = this.logEntryBuffers.remove();
                this.currentBuffer.start(this.bufferNumber++);
            }
        }
    }
    
    public void start()
    {
        synchronized (this)
        {
            if (this.threads == null)
            {
                this.stop = false;
                this.threads = new Thread[this.configuration.threads];
                for (int i = 0; i < this.threads.length; i++)
                {
                    final int index = i;
                    var thread= this.threads[i] = new Thread(() ->
                    {
                        main(index);
                    });
                    thread.start();
                }
            }
        }
    }

    public int stop(long waitMs) throws Throwable 
    {
        int aliveThreads=0;
        synchronized(this)
        {
            if (this.threads == null)
            {
                return 0;
            }
            this.stop = true;
            synchronized (this.logEntryQueue)
            {
                this.logEntryQueue.notifyAll();
            }
            synchronized (this.logEntryBuffers)
            {
                this.logEntryBuffers.notifyAll();
            }
            signalStop();
            for (int i = 0; i < this.threads.length; i++)
            {
                var thread = this.threads[i];
                try
                {
                    thread.join(waitMs);
                }
                catch (InterruptedException e)
                {
                }
                if (thread.isAlive())
                {
                    aliveThreads++;
                    if (Debug.ENABLE && DEBUG)
                    {
                        Debugging.log(DEBUG_CATEGORY,"thread "+i+" did not stop");
                    }
                }
            }
            this.threads = null;
            return aliveThreads;
        }
    }
    
    public void flush()
    {
        synchronized (this)
        {
            if (this.stop == true)
            {
                return;
            }
        }
        synchronized(this.logEntryQueue)
        {
            this.logEntryQueue.notify();
        }
    }
    public LogEntry writeToCurrentLogEntryBuffer(Trace trace,Level logLevel,String category,Throwable throwable,String message,Item[] items)
    {
        if (this.currentBuffer==null)
        {
            return null;
        }
        LogEntry entry=new LogEntry(this.number++,category,logLevel,System.currentTimeMillis(),throwable,trace,message,items);
        boolean full=this.currentBuffer.addAndCheckFull(entry);
        if (full==false)
        {
            return entry;
        }
        synchronized (this.logEntryQueue)
        {
            this.logEntryQueue.add(this.currentBuffer);
            this.logEntryQueue.notify();
            if (Debug.ENABLE && DEBUG && DEBUG_QUEUE_TRACING)
            {
                Debugging.log(DEBUG_CATEGORY,"notify buffer");
            }
        }
        synchronized (this.logEntryBuffers)
        {
            this.currentBuffer=this.logEntryBuffers.remove();
        }
        if (this.currentBuffer!=null)
        {
            this.currentBuffer.start(this.bufferNumber++);
        }
        return entry;
    }    

    @Override
    public LogEntry write(Trace trace,Level logLevel,String category,Throwable throwable,String message,Item[] items)
    {
        this.writeMeter.increment();
        synchronized (this.currentBufferLock)
        {
            var entry=writeToCurrentLogEntryBuffer(trace,logLevel,category,throwable,message,items);
            if (entry!=null)
            {
                return entry;
            }
        }
        this.stalledMeter.increment();
        if (Debug.ENABLE && DEBUG)
        {
            Debugging.log(DEBUG_CATEGORY,"stalled="+this.stalledMeter.getCount());
        }
        synchronized (this.currentBufferLock)
        {
            var entry=writeToCurrentLogEntryBuffer(trace,logLevel,category,throwable,message,items);
            if (entry!=null)
            {
                return entry;
            }
            synchronized (this.logEntryBuffers)
            {
                boolean wait=Synchronization.waitForNoThrow(this.logEntryBuffers, this.configuration.stallWaitMs, () ->
                {
                    return this.logEntryBuffers.size() > 0 || this.stop;
                });
                if (this.stop)
                {
                    return null;
                }
                if (wait==false)
                {
                    this.droppedMeter.increment();
                    if (Debug.ENABLE && DEBUG)
                    {
                        Debugging.log(DEBUG_CATEGORY,"dropped1="+this.droppedMeter.getCount());
                    }
                    return null;
                }
                this.currentBuffer=this.logEntryBuffers.remove();
                this.currentBuffer.start(this.bufferNumber++);
            }
            entry=writeToCurrentLogEntryBuffer(trace,logLevel,category,throwable,message,items);
            if (entry!=null)
            {
                return entry;
            }
            this.droppedMeter.increment();
            if (Debug.ENABLE && DEBUG)
            {
                Debugging.log(DEBUG_CATEGORY,"dropped2="+this.droppedMeter.getCount());
            }
            return null;
        }
    }

    private void main(int threadIndex)
    {
        try
        {
            boolean flush = false;
            long lastRollOver_ms = System.currentTimeMillis();
            long rollOverWait_ms = this.configuration.rollOverWait_ms;
            int  fileCapacity = this.configuration.bufferSize * 10; // rough estimate of file size in bytes.
            for (;;)
            {
                LogEntryBuffer buffer = null;
                boolean wait;
                synchronized (this.logEntryQueue)
                {
                    this.waitingMeter.increment();
                    wait=Synchronization.waitForNoThrow(this.logEntryQueue, rollOverWait_ms, () ->
                    {
                        return this.logEntryQueue.size() > 0 || this.stop;
                    });
                    this.waitingMeter.decrement();
                    if (this.stop)
                    {
                        if (Debug.ENABLE && DEBUG)
                        {
                            Debugging.log(DEBUG_CATEGORY,"thread "+threadIndex+":exit thread");
                        }
                        return;
                    }
                    if (wait)
                    {
                        buffer=this.logEntryQueue.remove();
                        if (buffer==null)
                        {
                            continue;
                        }
                    }
                    if (Debug.ENABLE && DEBUG && DEBUG_QUEUE_ORDERING)
                    {
//                        Thread.sleep(2000);
                        if (buffer!=null)
                        {
                            for (int i=0;i<buffer.index;i++)
                            {
                                long logEntryNumber=buffer.entries[i].getNumber();
                                if (this.testNumber!=logEntryNumber)
                                {
                                    Debugging.log(DEBUG_CATEGORY,"thread "+threadIndex+":testNumber="+this.testNumber+" logEntryNumber="+logEntryNumber,LogLevel.ERROR);
                                }
                                testNumber=logEntryNumber+1;
                            }
                        }
                    }
                }
                if (wait==false)
                {
                    synchronized (this.currentBufferLock)
                    {
                        if (this.currentBuffer==null)
                        {
                            //This case may never happen.
                            if (Debug.ENABLE && DEBUG)
                            {
                                Debugging.log(DEBUG_CATEGORY,"thread "+threadIndex+":rollOver:no buffer");
                            }
                            continue;
                        }
                        if (this.currentBuffer.index==0)
                        {
                            if (Debug.ENABLE && DEBUG)
                            {
                                Debugging.log(DEBUG_CATEGORY,"thread "+threadIndex+":rollOver:empty buffer");
                            }
                            continue;
                        }
                        if (Debug.ENABLE && DEBUG)
                        {
                            Debugging.log(DEBUG_CATEGORY,"thread "+threadIndex+":rollOver: size="+this.currentBuffer.index);
                        }
                        buffer=this.currentBuffer;
                        synchronized (this.logEntryBuffers)
                        {
                            this.currentBuffer=this.logEntryBuffers.remove();
                            if (this.currentBuffer!=null)
                            {
                                this.currentBuffer.start(this.bufferNumber++);  
                            }
                        }
                    }
                }
                if (Debug.ENABLE && DEBUG && DEBUG_QUEUE_TRACING)
                {
                    Debugging.log(DEBUG_CATEGORY,"thread "+threadIndex+":buffer size="+buffer.index);
                }
                
                if (Debug.ENABLE && DEBUG && DEBUG_WAITING_IN_QUEUE)
                {
                    Debugging.log(DEBUG_CATEGORY,"thread "+threadIndex+":waiting="+this.waitingMeter.getLevel());
                }
                
                write(threadIndex,buffer);
            }
        }
        catch (Throwable t)
        {
            if (Debug.ENABLE && DEBUG)
            {
                Debugging.log(DEBUG_CATEGORY,t);
            }
            synchronized (this)
            {
                this.throwable = t;
            }
        }
    }
    
    protected void returnBuffer(LogEntryBuffer buffer)
    {
        synchronized (this.logEntryBuffers)
        {
            this.logEntryBuffers.add(buffer);
            this.logEntryBuffers.notify();
        }
    }
    
    public Throwable getThrowable()
    {
        synchronized (this)
        {
            return this.throwable;
        }
    }
    @Deprecated
    //Make tester keep track of entries generated and verify using getWriteMeter().getCount() and getDroppedMeter().getCount();
    public void testVerify(long totalEntries) throws Throwable
    {
        Thread.sleep(this.configuration.rollOverWait_ms+1000);
        System.out.println("testNumber="+this.testNumber+" number="+this.number+", totalEntries="+totalEntries);
        System.out.println("dropped:"+this.droppedMeter.getCount());
        System.out.println("stalled:"+this.stalledMeter.getCount());
        if (this.testNumber==0)
        {
            this.testNumber=this.number;
        }
        if ((this.testNumber==totalEntries)&&(this.number==totalEntries))
        {
            System.out.println("testVerify passed");
        }
    }
    
}
