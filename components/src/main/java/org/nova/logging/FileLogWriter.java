//package org.nova.logging;
//
//import java.io.BufferedOutputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.PrintStream;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.concurrent.atomic.AtomicLong;
//
//import org.nova.collections.RingBuffer;
//import org.nova.concurrent.Synchronization;
//import org.nova.debug.Debug;
//import org.nova.debug.Debugging;
//import org.nova.debug.LogLevel;
//import org.nova.metrics.CountMeter;
//import org.nova.metrics.LevelMeter;
//import org.nova.metrics.RateMeter;
//import org.nova.tracing.Trace;
//import org.nova.tracing.TraceManager;
//
//import net.jpountz.lz4.LZ4BlockOutputStream;
//
//public class FileLogWriter extends LogWriter 
//{
//    final private OutputStream fileOutputStream;
//    final private LZ4BlockOutputStream lz4OutputStream;
//    final private JSONFormatWriter jsonFormatWriter;
//
//    public FileLogWriter(LogDirectoryManager logDirectoryManager,long start) throws Throwable
//    {
//        this.fileOutputStream=logDirectoryManager.openFileOutputStream(start,".json.lz4");
//        this.lz4OutputStream=new LZ4BlockOutputStream(this.fileOutputStream);
//        this.jsonFormatWriter=new JSONFormatWriter(this.lz4OutputStream);
//    }
//
//
//    @Override
//    LogEntry write(Trace trace, Level logLevel, String category, Throwable throwable, String message, Item[] items)
//    {
//        // TODO Auto-generated method stub
//        return null;
//    }
//}
