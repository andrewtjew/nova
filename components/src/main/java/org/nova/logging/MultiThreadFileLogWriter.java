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

public class MultiThreadFileLogWriter extends MultiThreadLogWriter
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
        LZ4(".lz4"),
        NONE(""),
        ;
        String extension;
        private CompressionFormat(String value)
        {
            this.extension=value;
        }
    }
    static public class Configuration extends MultiThreadLogWriter.Configuration
    {
        static public Configuration MinimalMemoryConfiguration()
        {
            Configuration configuration=new Configuration();
            configuration.directToFile=true;
            configuration.bufferSize=10000;
            configuration.rollOverWait_ms=30000;
            var threads=Runtime.getRuntime().availableProcessors();
            if (threads<4)
            {
                configuration.threads=1;
            }
            else
            {
                configuration.threads=2;
            }
            configuration.compressionFormat=CompressionFormat.LZ4;
            configuration.fileFormat=FileFormat.JSON;
            return configuration;
            
        }
        
        static public Configuration HighPerformanceConfiguration()
        {
            Configuration configuration=new Configuration();
            configuration.directToFile=false;
            configuration.bufferSize=200000;
            configuration.rollOverWait_ms=90000;
            
            var threads=Runtime.getRuntime().availableProcessors();
            configuration.threads=threads/3;
            if (configuration.threads==0)
            {
                configuration.threads=1;
            }
            configuration.compressionFormat=CompressionFormat.LZ4;
            configuration.fileFormat=FileFormat.JSON;
            return configuration;
        }
        static public Configuration ServerConfiguration()
        {
            Configuration configuration=new Configuration();
            configuration.directToFile=false;
            configuration.bufferSize=100000;
            configuration.rollOverWait_ms=60000;
            var threads=Runtime.getRuntime().availableProcessors();
            configuration.threads=threads/6;
            if (configuration.threads==0)
            {
                configuration.threads=1;
            }
            configuration.compressionFormat=CompressionFormat.LZ4;
            configuration.fileFormat=FileFormat.JSON;
            return configuration;
        }
        
        public Configuration()
        {
        }

        public CompressionFormat compressionFormat=CompressionFormat.LZ4;
        public FileFormat fileFormat=FileFormat.JSON;
        public boolean directToFile=false;
    }
    final private static boolean DEBUG_WRITE_FILE=false;
//    final private static boolean DEBUG=false;
//    final private static boolean DEBUG_WAITING_IN_QUEUE=false;
//    final private static boolean DEBUG_QUEUE_ORDERING=false;
//    final private static boolean DEBUG_QUEUE_TRACING=false;
//    static final String DEBUG_CATEGORY=MultiThreadFileLogWriter.class.getSimpleName();

    final private LogDirectoryManager logDirectoryManager;
    final private Object fileWriteLock;
    final private Configuration configuration;
    private long writeBufferNumber = 0;
    private int fileCapacity = 0;

    public MultiThreadFileLogWriter(LogDirectoryManager logDirectoryManager,Configuration configuration)
    {
        super(configuration);
        this.logDirectoryManager=logDirectoryManager;
        this.configuration=configuration;
        this.fileWriteLock=new Object();
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
    
    @Override
    protected void write(int threadIndex,LogEntryBuffer buffer) throws Throwable
    {
        if (this.configuration.directToFile)
        {
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
                try (var outputStream=new BufferedOutputStream(this.logDirectoryManager.openFileOutputStream(bufferStarted,configuration.fileFormat.extension+configuration.compressionFormat.extension),this.configuration.fileBufferCapacity))
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
            returnBuffer(buffer);
        }
        else
        {
            try (var outputStream=new ByteArrayOutputStream(fileCapacity))
            {
                writeWithCompression(outputStream,buffer);
                
                //We need to save the following numbers because the buffer is returned and may be reused.
                long bufferNumber=buffer.number;
                long bufferStarted=buffer.started;
                returnBuffer(buffer);

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
                    try (var fileOutputStream=this.logDirectoryManager.openFileOutputStream(bufferStarted,configuration.fileFormat.extension+configuration.compressionFormat.extension))
                    {
                        outputStream.writeTo(fileOutputStream);
                    }
                    this.writeBufferNumber=bufferNumber+1;
                    this.fileWriteLock.notifyAll();
                }
            }
        }
    }


    @Override
    protected void signalStop() throws Throwable
    {
        synchronized (this.fileWriteLock)
        {
            this.fileWriteLock.notifyAll();
        }
    }
}
