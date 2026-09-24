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

public class MultiThreadLogWriter extends LogWriter
{
    static public enum FileFormat
    {
        JSON(".json"),
        XML(".xml"),
        ;
        String extension;
        private FileFormat(String value)
        {
            this.extension=value;
        }
    }
    static public enum CompressionFormat
    {
        LZ4,
        NONE,
    }
    static public class Configuration
    {
        static public Configuration MinimalMemoryConfiguration()
        {
            Configuration configuration=new Configuration();
            configuration.directToFile=true;
            configuration.bufferSize=10000;
            var threads=Runtime.getRuntime().availableProcessors();
            if (threads<4)
            {
                configuration.threads=1;
            }
            else
            {
                configuration.threads=2;
            }
            return configuration;
            
        }
        
        static public Configuration HighPerformanceConfiguration()
        {
            Configuration configuration=new Configuration();
            configuration.directToFile=false;
            configuration.bufferSize=200000;
            
            var threads=Runtime.getRuntime().availableProcessors();
            configuration.threads=threads/3;
            if (configuration.threads==0)
            {
                configuration.threads=1;
            }
            return configuration;
        }
        static public Configuration ServerConfiguration()
        {
            Configuration configuration=new Configuration();
            configuration.directToFile=false;
            configuration.bufferSize=100000;
            var threads=Runtime.getRuntime().availableProcessors();
            configuration.threads=threads/6;
            if (configuration.threads==0)
            {
                configuration.threads=1;
            }
            return configuration;
        }
        
        public Configuration()
        {
            
        }

        public long stallWaitMs=3000;
        public long rollOverWait_ms=60000;
        public int fileBufferCapacity=65536;
        public int bufferSize=100000;
        public int threads=6;
        public CompressionFormat compressionFormat=CompressionFormat.LZ4;
        public FileFormat fileFormat=FileFormat.JSON;
        public boolean directToFile=false;
    }
    final private static boolean DEBUG=false;
    final private static boolean DEBUG_WRITE_FILE=false;
    final private static boolean DEBUG_WAITING_IN_QUEUE=false;
    final private static boolean DEBUG_QUEUE_ORDERING=false;
    final private static boolean DEBUG_QUEUE_TRACING=false;
    static final String DEBUG_CATEGORY=MultiThreadLogWriter.class.getSimpleName();

    static class LogEntryBuffer
    {
        public LogEntry[] entries;
        public int index;
        public long started;
        public long number;
        public LogEntryBuffer(int length)
        {
            this.entries=new LogEntry[length];
            this.index=0;
        }
        public void start(long number)
        {
            this.number=number;
            this.started=System.currentTimeMillis();
            Arrays.fill(this.entries, 0,this.index, null);
            this.index=0;
        }
        public boolean addAndCheckFull(LogEntry entry)
        {
            this.entries[this.index++]=entry;
            return this.index==this.entries.length;
        }
    }
    final private LogDirectoryManager logDirectoryManager;
    
    final private RingBuffer<LogEntryBuffer> logEntryBuffers;
    final private RingBuffer<LogEntryBuffer> logEntryQueue;
    private LogEntryBuffer currentBuffer;
    final private Object currentBufferLock;
    final private Object fileWriteLock;
    
    final private CountMeter droppedMeter;
    final private CountMeter stalledMeter;
    final private LevelMeter waitingMeter;
    final private RateMeter writeMeter;
    final private long stallWait;

    final private Configuration configuration;
    private Thread[] threads;
    private Throwable throwable;
    private boolean stop;
    
    private long number = 0;
    private long bufferNumber = 0;
    private long writeBufferNumber = 0;
    private long testNumber = 0;
    public MultiThreadLogWriter(LogDirectoryManager logDirectoryManager,Configuration configuration)
    {
        this.logDirectoryManager=logDirectoryManager;
        this.configuration=configuration;
        this.droppedMeter = new CountMeter();
        this.stalledMeter = new CountMeter();
        this.waitingMeter = new LevelMeter();
        this.writeMeter = new RateMeter();
        this.stallWait = configuration.stallWaitMs;

        int buffers=configuration.threads+2;
        this.logEntryBuffers = new RingBuffer<>(new LogEntryBuffer[buffers]);
        this.logEntryQueue = new RingBuffer<>(new LogEntryBuffer[buffers]);
        for (int i = 0; i < buffers; i++)
        {
            LogEntryBuffer entryBuffer = new LogEntryBuffer(configuration.bufferSize);
            this.logEntryBuffers.add(entryBuffer);
        }
        this.currentBufferLock = new Object();
        this.fileWriteLock = new Object();
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
        synchronized (this.logEntryQueue)
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

    public void stop(long waitMs) 
    {
        synchronized(this)
        {
            if (this.threads == null)
            {
                return;
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
            synchronized (this.fileWriteLock)
            {
                this.fileWriteLock.notifyAll();
            }
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
            }
            this.threads = null;
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
                boolean wait=Synchronization.waitForNoThrow(this.logEntryBuffers, this.stallWait, () ->
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
                
                if (this.configuration.directToFile)
                {
                    //We need to save the following number because the buffer may be reused.
                    long bufferNumber=buffer.number;
                    long bufferStarted=buffer.started;
                    synchronized (this.fileWriteLock)
                    {
                        if (Debug.ENABLE && DEBUG && DEBUG_WRITE_FILE)
                        {
                            Debugging.log(DEBUG_CATEGORY,"thread "+threadIndex+":waiting writeBufferNumber="+this.writeBufferNumber+" bufferNumber="+bufferNumber);
                        }
                        Synchronization.waitForNoThrow(this.fileWriteLock, () ->
                        {
                            return bufferNumber==this.writeBufferNumber || this.stop;
                        });
                        if (Debug.ENABLE && DEBUG && DEBUG_WRITE_FILE)
                        {
                            Debugging.log(DEBUG_CATEGORY,"thread "+threadIndex+":ready writeBufferNumber="+this.writeBufferNumber+" bufferNumber="+bufferNumber);
                        }
                        if (this.stop)
                        {
                            if (Debug.ENABLE && DEBUG)
                            {
                                Debugging.log(DEBUG_CATEGORY,"thread "+threadIndex+":exit thread");
                            }
                            return;
                        }
                        try (var outputStream=new BufferedOutputStream(this.logDirectoryManager.openFileOutputStream(bufferStarted,configuration.fileFormat.extension),this.configuration.fileBufferCapacity))
                        {
                            writeWithCompression(outputStream,buffer);
                        }
                        this.writeBufferNumber=bufferNumber+1;
                        if (Debug.ENABLE && DEBUG && DEBUG_WRITE_FILE)
                        {
                            Debugging.log(DEBUG_CATEGORY,"thread "+threadIndex+":done writeBufferNumber="+this.writeBufferNumber+" bufferNumber="+bufferNumber);
                        }
                        this.fileWriteLock.notifyAll();
                    }
                    synchronized (this.logEntryBuffers)
                    {
                        this.logEntryBuffers.add(buffer);
                        this.logEntryBuffers.notify();
                    }
                }
                else
                {
                    try (var outputStream=new ByteArrayOutputStream(fileCapacity))
                    {
                        writeWithCompression(outputStream,buffer);
                        
                        //We need to save the following number because the buffer may be reused.
                        long bufferNumber=buffer.number;
                        long bufferStarted=buffer.started;
                        
                        synchronized (this.logEntryBuffers)
                        {
                            this.logEntryBuffers.add(buffer);
                            this.logEntryBuffers.notify();
                        }
                        int size=outputStream.size();
                        if (size>fileCapacity)
                        {
                            fileCapacity+=(size-fileCapacity)*2;
                            if (Debug.ENABLE && DEBUG)
                            {
                                Debugging.log(DEBUG_CATEGORY,"thread "+threadIndex+":capacity increased to "+fileCapacity);
                            }
                        }
                        synchronized (this.fileWriteLock)
                        {
                            Synchronization.waitForNoThrow(this.fileWriteLock, () ->
                            {
                                return bufferNumber==this.writeBufferNumber || this.stop;
                            });
                            if (this.stop)
                            {
                                if (Debug.ENABLE && DEBUG)
                                {
                                    Debugging.log(DEBUG_CATEGORY,"thread "+threadIndex+":exit thread");
                                }
                                return;
                            }
                            try (var fileOutputStream=this.logDirectoryManager.openFileOutputStream(bufferStarted,configuration.fileFormat.extension))
                            {
                                outputStream.writeTo(fileOutputStream);
                            }
                            this.writeBufferNumber=bufferNumber+1;
                            this.fileWriteLock.notifyAll();
                        }
                    }
                }
                
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            synchronized (this.logEntryQueue)
            {
                this.throwable = t;
            }
        }
    }
    
    private FormatWriter getFormatWriter(OutputStream outputStream) throws Throwable
    {
        if (this.configuration.fileFormat==FileFormat.JSON)
        {
            return new JSONFormatWriter(outputStream);
        }
        else if (this.configuration.fileFormat==FileFormat.XML)
        {
            return new XmlFormatWriter(outputStream);
        }
        else
        {
            return new JSONFormatWriter(outputStream);
        }
    }
    private void writeWithCompression(OutputStream outputStream,LogEntryBuffer buffer) throws Throwable
    {
        if (this.configuration.compressionFormat==CompressionFormat.LZ4)
        {
            try (LZ4BlockOutputStream lz4OutputStream=new LZ4BlockOutputStream(outputStream))
            {
                writeWithFormatWriter(lz4OutputStream,buffer);
            }
        }
        else
        {
            writeWithFormatWriter(outputStream,buffer);
        }
    }
    private void writeWithFormatWriter(OutputStream outputStream,LogEntryBuffer buffer) throws Throwable
    {
        FormatWriter writer=getFormatWriter(outputStream);
        writer.writeBeginDocument();
        for (int i=0;i<buffer.index;i++)
        {
            if (i>0)
            {
                writer.writeSeparator();
            }
            writer.write(buffer.entries[i]);
        }
        writer.writeEndDocument();
    }
    
    public Throwable getThrowable()
    {
        synchronized (this.logEntryBuffers)
        {
            return this.throwable;
        }
    }

    public CountMeter getDroppedMeter()
    {
        return droppedMeter;
    }

    public CountMeter getStalledMeter()
    {
        return stalledMeter;
    }

    public LevelMeter getWaitingMeter()
    {
        return waitingMeter;
    }

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
