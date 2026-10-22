package org.nova.logsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.time.Duration;
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

public class LogTraceRateCollector implements AutoCloseable
{
    final private String directory;
    final private MultiTaskScheduler scheduler;
    final static double LOG2=Math.log(2);
    
    public LogTraceRateCollector(TraceManager traceManager,String directory,int maximumThreads) throws Throwable
    {
        this.directory = directory;
        this.scheduler = new MultiTaskScheduler(traceManager, maximumThreads, new NullLogger());
    }
    
    
    static public record TraceRateResult(int[] counts,int maximumCount,int totalCount)
    {
        public double averageRate()
        {
            return (double)this.totalCount/this.counts.length;
        }
    }
    
    public static class TraceRateProcessor
    {
        final private TreeMap<Long,Integer> counters;
        public TraceRateProcessor()
        {
            this.counters=new TreeMap<>();
        }
        synchronized public void process(TraceRecord traceRecord)
        {
            long epochSeconds=traceRecord.created.toEpochSecond(java.time.ZoneOffset.UTC);
            var counter=this.counters.get(epochSeconds);
            if (counter==null)
            {
                this.counters.put(epochSeconds, 1);
            }
            else
            {
                this.counters.put(epochSeconds, counter+1);
            }
        }
        public TraceRateResult getTraceRateResult()
        {
            long first=this.counters.firstKey();
            long last=this.counters.lastKey();
            int length=(int)(last-first+1);
            int[] rates=new int[length];
            int totalCount=0;
            int maximumCount=0;
            for (int i=0;i<rates.length;i++)
            {
                long epochSeconds=first+i;
                var value=this.counters.get(epochSeconds);
                if (value!=null)
                {
                    rates[i]=value;
                    totalCount+=value;
                    if (value>maximumCount)
                    {
                        maximumCount=value;
                    }
                }
            }
            return new TraceRateResult(rates, maximumCount, totalCount);
        }
    }
    
    public static class LogTraceCountResult
    {
        final HashMap<String,TraceRateProcessor> rates;
        int filesSearched;
        long entriesExamined;
        
        public LogTraceCountResult()
        {
            this.rates=new HashMap<>();
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
            TraceRateProcessor stats;
            synchronized(this)
            {
                stats=this.rates.get(key);
                if (stats==null)
                {
                    stats=new TraceRateProcessor();
                    this.rates.put(key, stats);
                }
            }
            stats.process(traceRecord);
        }
        
        public Map<String,TraceRateProcessor> getTraceRates()
        {
            return this.rates;
        }
    }
    
    private static class Task implements TraceRunnable
    {
        final File file;
        final LogTraceCountResult result;
        
        public Task(LogTraceCountResult result,File file)   
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
    
    public LogTraceCountResult collect(String from, String to)
    {
        return collect(LogSearcher.parseFileDateTime(from),LogSearcher.parseFileDateTime(to));
    }
    
    public LogTraceCountResult collect(LocalDateTime startDateTime, LocalDateTime endDateTime)
    {
        LogTraceCountResult logTraceResult=new LogTraceCountResult();
        
        File[] files=LogSearcher.getFilesInRange(this.directory, startDateTime, endDateTime);
        var tasks=new Task[files.length];

        for (int i=0;i<files.length;i++)
        {
            tasks[i]=new Task(logTraceResult,files[i]);
        }
        logTraceResult.filesSearched=tasks.length;
        var progress=this.scheduler.schedule(LogTraceRateCollector.class.getName(),tasks);
        progress.waitAll();
        return logTraceResult;
    }  
    
    @Override
    public void close()
    {
        this.scheduler.stop();
    }
    
}
