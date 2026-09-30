package org.nova.logsearch;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.nova.collections.LinkedTreeSet;
import org.nova.concurrent.MultiTaskScheduler;
import org.nova.json.ObjectMapper;
import org.nova.logging.ConsoleLogger;
import org.nova.logging.Item;
import org.nova.logging.Level;
import org.nova.logging.LogDirectoryManager;
import org.nova.logging.LogEntry;
import org.nova.logging.Logger;
import org.nova.logging.MultiThreadFileLogWriter;
import org.nova.logging.NullLogger;
import org.nova.logging.WriteLogger;
import org.nova.logsearch.LogRecord;
import org.nova.logsearch.SearchExpressionCompiler.CompilerError;
import org.nova.metrics.MeterStore;
import org.nova.sqldb.RowSet;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;
import org.nova.tracing.TraceRunnable;
import org.nova.utils.FileUtils;

import net.jpountz.lz4.LZ4BlockInputStream;

public class TestLogGenerator
{
    final private MultiTaskScheduler scheduler;
    final private WriteLogger writeLogger;
    final private MultiThreadFileLogWriter logWriter;
    final private Trace testTrace;
    public TestLogGenerator(String directory,int maximumThreads) throws Throwable
    {
        deleteFiles(new File(directory));
        var traceManager=new TraceManager();
        this.testTrace=new Trace(traceManager, "test");
        
        
        long maxFiles=0;
        long reserve=2_000_000_000L;
        long maxDirectorySize=1_000_000_000L;
        int maxMakeSpaceRetries=10;

        var logDirectoryManager=new LogDirectoryManager(directory, maxMakeSpaceRetries, maxFiles, maxDirectorySize, reserve);
        var logger=new NullLogger();
        this.scheduler = new MultiTaskScheduler(traceManager, maximumThreads, logger);
        
        var logQueueConfiguration=MultiThreadFileLogWriter.Configuration.HighPerformanceConfiguration();
        logWriter=new MultiThreadFileLogWriter(logDirectoryManager,logQueueConfiguration);
        logWriter.start();
        this.writeLogger=new WriteLogger("test",logWriter);
    }

    private void deleteFiles(File directory)
    {
        File[] files=directory.listFiles();
        if (files==null)
        {
            return;
        }
        for (File file:files)
        {
            if ((file.isDirectory()==false)&&(file.getName().endsWith(".lz4")))
            {
                file.delete();
            }
        }
    }
    
    static class GenerateLogTask implements TraceRunnable
    {
        final long count;
        final int threadIndex;
        final WriteLogger logger;
        final Trace testTrace;
        
        public GenerateLogTask(Trace testTrace,WriteLogger logger,int index,long count)   
        {
            this.logger=logger;
            this.threadIndex=index;
            this.count=count;
            this.testTrace=testTrace;
        }
        @Override
        public void run(Trace parent) throws Throwable
        {
            if (count==0)
            {
                return;
            }
            String message="This is a log entry with threadIndex="+this.threadIndex;
            try
            {
                this.logger.log(Level.NOTICE,message);
                for (long i=1;i<count;i++)
                {
                    this.logger.log(testTrace,message+" trace",new Item("index",i));
//                    this.logger.log(testTrace,message+" trace");
                }
            }
            catch (Throwable t)
            {
                t.printStackTrace();
                this.logger.log(parent);
            }
        }
    }

    public void testLogging(int taskCount,long entriesPerThread) throws Throwable
    {
        var tasks=new GenerateLogTask[taskCount];
        for (int i=0;i<taskCount;i++)
        {
            tasks[i]=new GenerateLogTask(this.testTrace,this.writeLogger,i,entriesPerThread);
        }
        var progress=this.scheduler.schedule("stress",tasks);
        progress.waitAll();
        logWriter.flush(1000);
        this.logWriter.stop(1000);
        this.scheduler.stop();
    }
    
    
}
