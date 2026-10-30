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
    

    static public class TraceRateResult
    {
        public TraceRate[] rates;

        public double averageDuration;
        public double maximumDuration;
        public long maximumDurationEpochSecond;
        
        public double averageRate;
        public int maximumRate;
        public long maximumRateEpochSecond;
        
    }        
    
    static public class TraceRate //per second
    {
        public int rate;
        public double totalDuration;
        public double maximumDuration;
    }
    
    public static class TraceRateListMap
    {
        final private TreeMap<Long,TraceRate> traceRates;
        
        public TraceRateListMap()
        {
            this.traceRates=new TreeMap<>();
        }
        synchronized public void add(TraceRecord traceRecord)
        {
            long epochSeconds=traceRecord.created.toEpochSecond(java.time.ZoneOffset.UTC);
            var traceRate=this.traceRates.get(epochSeconds);
            if (traceRate==null)
            {
                traceRate=new TraceRate();
                this.traceRates.put(epochSeconds, traceRate);
            }
            traceRate.rate++;
            traceRate.totalDuration+=traceRecord.duration;
            if (traceRecord.duration>traceRate.maximumDuration)
            {
                traceRate.maximumDuration=traceRecord.duration;
            }
        }
        
        public TraceRateResult getTraceRateResult(DurationRange durationDurange)
        {
            TraceRateResult result=new TraceRateResult();
            result.rates=new TraceRate[(int)(durationDurange.end-durationDurange.begin+1)];
            
            result.maximumRate=0;
            result.maximumRateEpochSecond=0;
            
            result.maximumDuration=0;
            result.maximumDurationEpochSecond=0;
            

            int totalRate=0;
            double totalDuration=0;
            
            
            for (int i=0;i<result.rates.length;i++)
            {
                var traceRate=this.traceRates.get(durationDurange.begin+i);
                if (traceRate!=null)
                {
                    result.rates[i]=traceRate;
                    totalRate+=traceRate.rate;
                    if (traceRate.rate>result.maximumRate)
                    {
                        result.maximumRate=traceRate.rate;
                        result.maximumRateEpochSecond=durationDurange.begin+i;
                    }

                    totalDuration+=traceRate.totalDuration;
                    if (traceRate.maximumDuration>result.maximumDuration)
                    {
                        result.maximumDuration=traceRate.maximumDuration;
                        result.maximumDurationEpochSecond=durationDurange.begin+i;
                    }
                }
            }
            result.averageRate=totalRate/result.rates.length;
            result.averageDuration=totalDuration/totalRate;

            return result;
        }
    }

    public record DurationRange(long begin, long end)
    {
        public long getDuration()
        {
            return this.end-this.begin+1;
        }
    }
    
    public static class LogTraceCountResult
    {
        final HashMap<String,TraceRateListMap> processors;
        int filesSearched;
        long entriesExamined;
        
        public LogTraceCountResult()
        {
            this.processors=new HashMap<>();
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
            TraceRateListMap processor;
            synchronized(this)
            {
                processor=this.processors.get(key);
                if (processor==null)
                {
                    processor=new TraceRateListMap();
                    this.processors.put(key, processor);
                }
            }
            processor.add(traceRecord);
        }
        
        public DurationRange postProcess()
        {
            long beginEpochSecond=Long.MAX_VALUE;
            long lastEpochSecond=Long.MIN_VALUE;
            for (TraceRateListMap processor:this.processors.values())
            {
                long first=processor.traceRates.firstKey();
                long last=processor.traceRates.lastKey();
                if (first<beginEpochSecond)
                {
                    beginEpochSecond=first;
                }
                if (last>lastEpochSecond)
                {
                    lastEpochSecond=last;
                }
            }
            return new DurationRange(beginEpochSecond, lastEpochSecond);
        }
        
        public Map<String,TraceRateListMap> getTraceRates()
        {
            return this.processors;
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
        var progress=this.scheduler.schedule("task."+LogTraceRateCollector.class.getSimpleName(),tasks);
        progress.waitAll();
        return logTraceResult;
    }  
    
    @Override
    public void close()
    {
        this.scheduler.stop();
    }
    
}
