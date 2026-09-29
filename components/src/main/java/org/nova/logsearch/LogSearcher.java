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
import org.nova.logging.LogEntry;
import org.nova.logging.Logger;
import org.nova.logging.WriteLogger;
import org.nova.logsearch.LogRecord;
import org.nova.logsearch.SearchExpressionCompiler.CompilerError;
import org.nova.sqldb.RowSet;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;
import org.nova.tracing.TraceRunnable;
import org.nova.utils.FileUtils;

import net.jpountz.lz4.LZ4BlockInputStream;

public class LogSearcher
{
    final private String directory;
    final private MultiTaskScheduler scheduler;
    private SearchExpressionEvaluator evaluator;
    
    public LogSearcher(TraceManager traceManager,Logger logger,String directory,int maximumThreads,String searchExpression) throws Throwable
    {
        this.directory = directory;
        this.scheduler = new MultiTaskScheduler(traceManager, maximumThreads, logger);
        
        SearchExpressionCompiler compiler=new SearchExpressionCompiler();
        compiler.compile(searchExpression);
    }
    
    public List<CompilerError> setSearchExpression(String searchExpression) throws Throwable
    {
        SearchExpressionCompiler compiler=new SearchExpressionCompiler();
        this.evaluator=compiler.compile(searchExpression);
        if (this.evaluator==null)
        {
            return compiler.getErrors();
        }
        return null;
    }
    
    private boolean isInRange(File file, LocalDateTime startDate, LocalDateTime endDate)
    {
        var fileName = file.getName();
        if (fileName.lastIndexOf(".lz4")!=fileName.length()-4)
        {
            return false;
        }
        fileName=fileName.substring(0,fileName.length()-4);
        String[] parts = fileName.split("_");
        if (parts.length!=7) 
        {
            return false;
        }
        
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        int hour = Integer.parseInt(parts[3]);
        int minute = Integer.parseInt(parts[4]);
        int second = Integer.parseInt(parts[5]);
        int millisecond = Integer.parseInt(parts[6])*1000000;
        
        
        try
        {
            LocalDateTime fileDate = LocalDateTime.of(year, month, day, hour, minute, second, millisecond);
            if (startDate==null && endDate==null)
            {
                return true;
            }
            else if (startDate != null && endDate != null) 
            {
                return !fileDate.isBefore(startDate) && !fileDate.isAfter(endDate);
            }
            else if (startDate != null) 
            {
                return !fileDate.isBefore(startDate);
            } 
            return !fileDate.isAfter(endDate);
        }
        catch (Exception e)
        {
            return false;
        }
    }

    static public record FoundLogEntry(LogRecord logRecord,File file)
    {
    }
    
    static class SearchResult
    {
        final TreeMap<Long,FoundLogEntry> foundLogEntries=new TreeMap<>();
        private boolean hasMore=false;
        final private int maximumFoundLogEntries; 
        
        public SearchResult(int maximumFoundLogEntries)
        {
            this.maximumFoundLogEntries=maximumFoundLogEntries;
        }
        synchronized public void addFoundLogEntry(LogRecord logRecord,File file)
        {
            if (this.foundLogEntries.size()>=this.maximumFoundLogEntries)
            {
                this.hasMore=true;
                return;
            }
            this.foundLogEntries.put(logRecord.number,new FoundLogEntry(logRecord,file));
        }
        public boolean hasMore()
        {
            return this.hasMore;
        }
    }
    
    static class VerifyLogEntrySequencingTask implements TraceRunnable
    {
        final File file;
        final SearchResult result;
        final SearchExpressionEvaluator evaluator;
        
        public VerifyLogEntrySequencingTask(SearchResult result,File file,SearchExpressionEvaluator evaluator)   
        {
            this.file=file;
            this.result=result;
            this.evaluator=evaluator;
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
                    for (LogRecord logRecord:logRecords)
                    {
                        var result=this.evaluator.evaluate(logRecord);
                        if (result)
                        {
                            this.result.addFoundLogEntry(logRecord, this.file);
                        }
                    }
                }
                catch (Throwable t)
                {
                }
            }
        }
    }
    
    public SearchResult verifyLogEntrySequencing(int maximumResults,LocalDateTime startDateTime, LocalDateTime endDateTime)
    {
        File directory = new File(this.directory);
        File[] files = directory.listFiles();

        ArrayList<VerifyLogEntrySequencingTask> matchingFileTaskList = new ArrayList<>();
        SearchResult searchResult=new SearchResult(maximumResults);
        if (files != null) 
        {
            for (File file : files) 
            {
                if (file.isFile()&& isInRange(file, startDateTime, endDateTime)) 
                {
                    matchingFileTaskList.add(new VerifyLogEntrySequencingTask(searchResult,file,this.evaluator));   
                }
            }
        }
        
        var tasks=matchingFileTaskList.toArray(new VerifyLogEntrySequencingTask[matchingFileTaskList.size()]);
        var progress=this.scheduler.schedule("search",tasks);
        progress.waitAll();
        return searchResult;
    }

    static class StressLoggingTask implements TraceRunnable
    {
        final long count;
        final int index;
        final WriteLogger logger;
        final int work;
        
        public StressLoggingTask(WriteLogger logger,int index,long count,int work)   
        {
            this.logger=logger;
            this.index=index;
            this.count=count;
            this.work=work;
        }
        @Override
        public void run(Trace parent) throws Throwable
        {
            try
            {
                String prefix="Task"+index+":";
                double sum=0;
                for (long i=0;i<count;i++)
                {
                    for (int j=0;j<work;j++)
                    {
                        sum+=Math.sqrt(i+j);
                    }
                    this.logger.log(prefix+"\'\" LogEntry "+i+" sum="+sum);
                }
                System.out.println(prefix+" completed");
            }
            catch (Throwable t)
            {
                t.printStackTrace();
                this.logger.log(parent);
            }
        }
    }

    public void stressLogging(Trace parent,WriteLogger logger,int taskCount,long entriesPerTaskCount,int work) throws Throwable
    {
        var tasks=new StressLoggingTask[taskCount];
        for (int i=0;i<taskCount;i++)
        {
            tasks[i]=new StressLoggingTask(logger,i,entriesPerTaskCount,work);
        }
        var progress=this.scheduler.schedule("stress",tasks);
        progress.waitAll();
    }
    
    
}
