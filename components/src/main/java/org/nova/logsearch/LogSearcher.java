package org.nova.logsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

public class LogSearcher implements AutoCloseable
{
    final private String directory;
    final private MultiTaskScheduler scheduler;
    private SearchExpressionEvaluator evaluator;
    
    public LogSearcher(TraceManager traceManager,String directory,int maximumThreads) throws Throwable
    {
        this.directory = directory;
        this.scheduler = new MultiTaskScheduler(traceManager, maximumThreads, new NullLogger());
    }
    
    public List<CompilerError> setSearchExpression(String searchExpression) throws Throwable
    {
        if (searchExpression==null)
        {
            this.evaluator=null;
            return null;
        }
        SearchExpressionCompiler compiler=new SearchExpressionCompiler();
        this.evaluator=compiler.compile(searchExpression);
        if (this.evaluator==null)
        {
            return compiler.getErrors();
        }
        return null;
    }

    public static LocalDateTime parseFileDateTime(String dateTimeString)
    {
        if (dateTimeString==null)
        {
            return null;
        }
        int index=dateTimeString.indexOf(".");
        if (index>0)
        {
            dateTimeString=dateTimeString.substring(0,index);
        }
        
        String[] parts = dateTimeString.split("_");
        if (parts.length!=7) 
        {
            try
            {
                return LocalDateTime.parse(dateTimeString);
            }
            catch (Exception e)
            {
                return null;
            }
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
            return LocalDateTime.of(year, month, day, hour, minute, second, millisecond);
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    public static File[] getFilesInRange(String name, LocalDateTime startDate, LocalDateTime endDate)
    {
        File directory = new File(name);
        File[] files = directory.listFiles();
        Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
        ArrayList<File> matchingFiles=new ArrayList<>();
        
        File lastFileBeforeStartDate=null;
        File firstFileAfterEndDate=null;
        
        for (File file : files) 
        {
            if (file.isFile())
            {
                if (isInRange(file, startDate, endDate))
                {
                    matchingFiles.add(file);
                }
                else 
                {
                    if (startDate!=null)
                    {
                        lastFileBeforeStartDate=file;
                    }
                    if ((endDate!=null)&&(firstFileAfterEndDate==null))
                    {
                        firstFileAfterEndDate=file;
                    }
                }
            }
        }
        if (lastFileBeforeStartDate!=null)
        {
            matchingFiles.add(0,lastFileBeforeStartDate);
        }
        if (firstFileAfterEndDate!=null)
        {
            matchingFiles.add(firstFileAfterEndDate);
        }
        return matchingFiles.toArray(new File[matchingFiles.size()]);
    }
    
    private static boolean isInRange(File file, LocalDateTime startDate, LocalDateTime endDate)
    {
        var fileName = file.getName();
        if (fileName.lastIndexOf(".lz4")!=fileName.length()-4)
        {
            return false;
        }
        LocalDateTime fileDateTime=parseFileDateTime(fileName);
        if (fileDateTime==null)
        {
            return false;
        }
        
        try
        {
            if (startDate==null && endDate==null)
            {
                return true;
            }
            else if (startDate != null && endDate != null) 
            {
                return !fileDateTime.isBefore(startDate) && !fileDateTime.isAfter(endDate);
            }
            else if (startDate != null) 
            {
                return !fileDateTime.isBefore(startDate);
            } 
            return !fileDateTime.isAfter(endDate);
        }
        catch (Exception e)
        {
            return false;
        }
    }

    static public record FileLogEntry(LogRecord logRecord,File file)
    {
    }
    
    public static class SearchResult
    {
        final TreeMap<String,FileLogEntry> foundLogEntries=new TreeMap<>();
        private boolean hasMore=false;
        final private int maximumFoundLogEntries; 
        
        int filesSearched;
        long entriesExamined;
        
        public SearchResult(int maximumFoundLogEntries)
        {
            this.maximumFoundLogEntries=maximumFoundLogEntries;
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
        
        synchronized public boolean addFoundLogEntry(LogRecord logRecord,File file)
        {
            String key=file.getName()+logRecord.created+":"+logRecord.number;
            if (this.foundLogEntries.size()>=this.maximumFoundLogEntries)
            {
                this.hasMore=true;
                return false;
            }
            this.foundLogEntries.put(key,new FileLogEntry(logRecord,file));
            return true;
        }
        public boolean hasMore()
        {
            return this.hasMore;
        }
        
        public FileLogEntry[] getFoundEntries()
        {
            return this.foundLogEntries.values().toArray(new FileLogEntry[this.foundLogEntries.size()]);
        }
    }
    
    static class LogSearchTask implements TraceRunnable
    {
        final File file;
        final SearchResult result;
        final SearchExpressionEvaluator evaluator;
        
        public LogSearchTask(SearchResult result,File file,SearchExpressionEvaluator evaluator)   
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
                    for (int i=0;i<logRecords.length;i++)
                    {
                        LogRecord logRecord=logRecords[i];
                        boolean match=true;
                        if (this.evaluator!=null)
                        {
                            match=this.evaluator.evaluate(logRecord);
                        }
                        if (match)
                        {
                            if (this.result.addFoundLogEntry(logRecord, this.file)==false)
                            {
                                this.result.addEntriesExamined(i);
                                return;
                            }
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
    
    public SearchResult search(int maximumResults,String from, String to)
    {
        return search(maximumResults,parseFileDateTime(from),parseFileDateTime(to));
    }
    
    public SearchResult search(int maximumResults,LocalDateTime startDateTime, LocalDateTime endDateTime)
    {
        SearchResult searchResult=new SearchResult(maximumResults);
        
        File[] files=getFilesInRange(this.directory, startDateTime, endDateTime);
        var tasks=new LogSearchTask[files.length];

        for (int i=0;i<files.length;i++)
        {
            tasks[i]=new LogSearchTask(searchResult,files[i],this.evaluator);
        }
        searchResult.filesSearched=tasks.length;
        var progress=this.scheduler.schedule("search",tasks);
        progress.waitAll();
        return searchResult;
    }
    @Override
    public void close()
    {
        this.scheduler.stop();
    }
    
}
