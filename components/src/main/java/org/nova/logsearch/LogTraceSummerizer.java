package org.nova.logsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.nova.collections.LinkedTreeSet;
import org.nova.concurrent.MultiTaskScheduler;
import org.nova.json.ObjectMapper;
import org.nova.logging.LogEntry;
import org.nova.logging.Logger;
import org.nova.logging.NullLogger;
import org.nova.logging.WriteLogger;
import org.nova.logsearch.LogRecord;
import org.nova.logsearch.SearchExpressionCompiler.CompilerError;
import org.nova.parsing.ParsingUtils;
import org.nova.sqldb.RowSet;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;
import org.nova.tracing.TraceRunnable;
import org.nova.utils.FileUtils;

import net.jpountz.lz4.LZ4BlockInputStream;

public class LogTraceSummerizer implements AutoCloseable
{
    final private String directory;
    final private MultiTaskScheduler scheduler;
    final static double LOG2=Math.log(2);
    
    public LogTraceSummerizer(TraceManager traceManager,String directory,int maximumThreads) throws Throwable
    {
        this.directory = directory;
        this.scheduler = new MultiTaskScheduler(traceManager, maximumThreads, new NullLogger());
    }
    
    
    public static class TraceDurationDistribution
    {
        public int[] durations;
        public TraceRecord minimum;
        public TraceRecord maximum;
        private double totalDuration;
        private double totalDuration2;
        private double minimumBinDuration;
        
        public TraceDurationDistribution(int bins,double minimumBinDuration)
        {
            this.durations=new int[bins];
            this.minimumBinDuration=minimumBinDuration;
        }
        synchronized public void addDuration(TraceRecord traceRecord)
        {
            if (this.minimum==null)
            {
                this.minimum=traceRecord;
                this.maximum=traceRecord;
            }
            else
            {
                if (traceRecord.duration<this.minimum.duration)
                {
                    this.minimum=traceRecord;
                }
                if (traceRecord.duration>this.maximum.duration)
                {
                    this.maximum=traceRecord;
                }
            }
            this.totalDuration+=traceRecord.duration;
            this.totalDuration2+=traceRecord.duration*traceRecord.duration;
            int index=(int)Math.ceil(Math.log(traceRecord.duration*(1000/this.minimumBinDuration))/LOG2);
            if (index<0)
            {
                index=0;
            }
            else if (index>=this.durations.length)
            {
                index=this.durations.length-1;
            }
            this.durations[index]++;
        }
        public double getAverageDuration()
        {
            int count=0;
            for (int i=0;i<this.durations.length;i++)
            {
                count+=this.durations[i];
            }
            return this.totalDuration/count;
        }
        public double getStandardDeviationDuration()
        {
            int count=0;
            for (int i=0;i<this.durations.length;i++)
            {
                count+=this.durations[i];
            }
            double b=((double)this.totalDuration/count)*this.totalDuration;
            double d = this.totalDuration2 - b;
            if (d <= 0.0)
            {
                return 0;
            }
            return Math.sqrt(d/count);
        }
    }
    
    public static class LogTraceResult
    {
        final HashMap<String,TraceDurationDistribution> distributions;
        final private int bins;
        final private double minimumBinDuration;
        private boolean hasMore=false;
        int filesSearched;
        long entriesExamined;
        
        public LogTraceResult(int bins,double minimumBinDuration)
        {
            this.bins=bins;
            this.minimumBinDuration=minimumBinDuration;
            this.distributions=new HashMap<>();
        }
        public int getFilesSearched()
        {
            return this.filesSearched;
        }
        synchronized public void addEntriesExamined(int count)
        {
            this.entriesExamined+=count;
        }
        public long getEntriesExamined()
        {
            return this.entriesExamined;
        }
        
        public void add(TraceRecord traceRecord)
        {
            String key=traceRecord.category;
            TraceDurationDistribution distribution;
            synchronized(this)
            {
                distribution=this.distributions.get(key);
                if (distribution==null)
                {
                    distribution=new TraceDurationDistribution(bins,minimumBinDuration);
                    this.distributions.put(key, distribution);
                }
            }
            distribution.addDuration(traceRecord);
        }
        
        public Map<String,TraceDurationDistribution> getTraceDurationDistributions()
        {
            return this.distributions;
        }
    }
    
    private static class Task implements TraceRunnable
    {
        final File file;
        final LogTraceResult result;
        
        public Task(LogTraceResult result,File file)   
        {
            this.file=file;
            this.result=result;
        }
        @Override
        public void run(Trace parent) throws Throwable
        {
            try (LZ4BlockInputStream inputStream=new LZ4BlockInputStream(new FileInputStream(this.file)))
            {
                String string=FileUtils.readString(inputStream);
                try
                {
                    LogRecord[] logRecords=ObjectMapper.readObject(string, LogRecord[].class);
                    for (int i=0;i<logRecords.length;i++)
                    {
                        LogRecord logRecord=logRecords[i];
                        if ((logRecord.trace!=null)&&(logRecord.trace.closed))
                        {
                            this.result.add(logRecord.trace);
                        }
                    }
                    this.result.addEntriesExamined(logRecords.length);
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                }
            }
        }
    }
    
    public LogTraceResult summerize(int bins,double minimumBinDuration,String from, String to)
    {
        return summerize(bins,minimumBinDuration,LogSearcher.parseFileDateTime(from),LogSearcher.parseFileDateTime(to));
    }
    
    public LogTraceResult summerize(int bins,double minimumBinDuration,LocalDateTime startDateTime, LocalDateTime endDateTime)
    {
        LogTraceResult logTraceResult=new LogTraceResult(bins,minimumBinDuration);
        
        File[] files=LogSearcher.getFilesInRange(this.directory, startDateTime, endDateTime);
        var tasks=new Task[files.length];

        for (int i=0;i<files.length;i++)
        {
            tasks[i]=new Task(logTraceResult,files[i]);
        }
        logTraceResult.filesSearched=tasks.length;
        var progress=this.scheduler.schedule("trace",tasks);
        progress.waitAll();
        return logTraceResult;
    }  
    
    @Override
    public void close()
    {
        this.scheduler.stop();
    }
    
}
