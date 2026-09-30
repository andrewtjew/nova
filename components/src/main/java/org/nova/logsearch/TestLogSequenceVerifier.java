package org.nova.logsearch;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;

import org.nova.collections.LinkedTreeSet;
import org.nova.concurrent.MultiTaskScheduler;
import org.nova.json.ObjectMapper;
import org.nova.logging.LogEntry;
import org.nova.logging.Logger;
import org.nova.logging.NullLogger;
import org.nova.logging.WriteLogger;
import org.nova.sqldb.RowSet;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;
import org.nova.tracing.TraceRunnable;
import org.nova.utils.FileUtils;

import net.jpountz.lz4.LZ4BlockInputStream;

public class TestLogSequenceVerifier
{
    final private String directory;
    final private MultiTaskScheduler scheduler;

    public TestLogSequenceVerifier(String directory,int maximumThreads)
    {
        TraceManager traceManager=new TraceManager();
        this.directory = directory;
        this.scheduler = new MultiTaskScheduler(traceManager, maximumThreads, new NullLogger());
    }
    
    static class VerificationResult
    {
        long totalEntries;
        ArrayList<String> errors;
        
        public VerificationResult()
        {
            this.errors=new ArrayList<>();
        }
        public synchronized void logError(String error)
        {
            this.errors.add(error);
        }
        public synchronized void addEntries(long count)
        {
            this.totalEntries+=count;
        }
    }
    
    static class VerifyTask implements TraceRunnable
    {
        final File file;
        final VerificationResult result;
        Long start;
        long end;
        
        public VerifyTask(VerificationResult result,File file)   
        {
            this.file=file;
            this.result=result;
            this.start=null;
        }
        @Override
        public void run(Trace parent) throws Throwable
        {
            String string=null;
            try (LZ4BlockInputStream inputStream=new LZ4BlockInputStream(new FileInputStream(this.file)))
            {
                string=FileUtils.readString(inputStream);
            }
            try
            {
                LogRecord[] logRecords=ObjectMapper.readObject(string, LogRecord[].class);
                if (logRecords.length>0)
                {
                    this.start=this.end=logRecords[0].number;
                    for (int i=1;i<logRecords.length;i++)
                    {
                        LogRecord logRecord=logRecords[i];
                        if (logRecord.number!=this.end+1)
                        {
                            this.result.logError("LogEntry sequence error. File="+file.getName()+", Previous="+this.end+", Current="+logRecord.number);
                        }
                        this.end=logRecord.number;
                    }
                    this.result.addEntries(logRecords.length);
                }
            }
            catch (Throwable t)
            {
                t.printStackTrace();
                this.result.logError("LogEntry parsing error. File="+file.getName()+", number="+this.end);
            }
        }
    }
    
    
    public VerificationResult verify(LocalDateTime startDateTime, LocalDateTime endDateTime)
    {
        
        VerificationResult result=new VerificationResult();
        File[] files = LogSearcher.getFilesInRange(this.directory, startDateTime, endDateTime);

        VerifyTask[] tasks=new VerifyTask[files.length];
        for (int i=0;i<files.length;i++)
        {
            tasks[i]=new VerifyTask(result,files[i]);
        }
        var progress=this.scheduler.schedule("search",tasks);
        progress.waitAll();
        Long start=null;
        VerifyTask previous=null;
        long end=0;
        for (VerifyTask task:tasks)
        {
            if (task.start==null)
            {
                continue;
            }
            if (start==null)
            {
                start=task.start;
                end=task.end;
            }
            else if (end+1!=task.start)
            {
                result.logError("LogEntry sequence error. Previous="+end+" in "+previous.file.getName()+", Current="+task.start+" in "+task.file.getName());
            }
            previous=task;
            end=task.end;
        }
        this.scheduler.stop();
        return result;
    }
    
    public void printResult(long expected,VerificationResult result)
    {
        System.out.println("Entries tested: "+result.totalEntries);
        if (expected!=result.totalEntries)
        {
            result.logError("Error: Expected entries: "+expected+", Found entries: "+result.totalEntries);
        }
        System.out.println("Errors: "+result.errors.size());
        for (var error:result.errors)
        {
            System.out.println(error);
        }
    }
}
