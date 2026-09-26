/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.frameworks;

import java.util.HashMap;

import org.nova.collections.ContentCache;
import org.nova.concurrent.MultiTaskScheduler;
import org.nova.concurrent.TimerScheduler;
import org.nova.configuration.Configuration;
import org.nova.flow.SourceQueue;
import org.nova.flow.SourceQueueConfiguration;
import org.nova.logging.LogDirectoryManager;
import org.nova.logging.LogEntry;
import org.nova.logging.LogWriter;
import org.nova.logging.Logger;
import org.nova.logging.WriteLogger;
import org.nova.logging.MultiThreadFileLogWriter;
import org.nova.logging.MultiThreadLogWriter;
import org.nova.logging.dep.ConsoleWriter;
import org.nova.logging.dep.Formatter;
import org.nova.logging.dep.JSONFormatter;
import org.nova.logging.dep.LogEntrySourceQueue;
import org.nova.logging.dep.LogEntrySourceQueueLogger;
import org.nova.logging.dep.MultiThreadedLogEntrySourceQueue;
import org.nova.logging.dep.MultiThreadededLogEntrySourceQueueConfiguration;
import org.nova.logging.dep.SimpleFileWriter;
import org.nova.metrics.MeterStore;
import org.nova.metrics.SourceEventBoard;
import org.nova.security.SecureFileVault;
import org.nova.security.Vault;
import org.nova.tracing.TraceManager;
import org.nova.tracing.TraceManagerConfiguration;

public class CoreEnvironment
{
	final private TimerScheduler timerScheduler;
	final private MultiTaskScheduler multiTaskScheduler;
	final private TraceManager traceManager;
	final private Configuration configuration;
	final private MeterStore meterStore;
	final private Logger logger;
	final HashMap<String,Logger> loggers;
	final private LogDirectoryManager logDirectoryManager;
    final private Vault vault;
//    final private String loggerType;
//    final LogEntrySourceQueue logSourceQueue;
//    final private int logEntryBufferSize;
//    final private Formatter logFormatter;
    final public static SourceEventBoard SOURCE_EVENT_BOARD=new SourceEventBoard();
	final public MultiThreadLogWriter logWriter;
	
	public CoreEnvironment(Configuration configuration) throws Throwable
	{
        this.configuration=configuration;
        String directory=configuration.getValue("Environment.Logger.logDirectory","logs");
		long maxFiles=configuration.getIntegerValue("Environment.Logger.logDirectory.maxFiles",0);
		long reserve=configuration.getLongValue("Environment.Logger.logDirectory.reserveSpace",2_000_000_000L);
		long maxDirectorySize=configuration.getLongValue("Environment.Logger.logDirectory.maxDirectorySize",1_000_000_000L);
		int maxMakeSpaceRetries=configuration.getIntegerValue("Environment.Logger.logDirectory.maxMakeSpaceRetries",10);
//		this.logEntryBufferSize=configuration.getIntegerValue("Environment.Logger.logEntryBufferSize",10);
        int traceBufferSize=configuration.getIntegerValue("Environment.Tracing.traceBufferSize",200);

        this.meterStore=new MeterStore();

  //      this.logFormatter=new JSONFormatter();
		this.logDirectoryManager=new LogDirectoryManager(directory, maxMakeSpaceRetries, maxFiles, maxDirectorySize, reserve);
		var logWriterConfiguration=MultiThreadFileLogWriter.Configuration.ServerConfiguration();
		this.logWriter=new MultiThreadFileLogWriter(this.logDirectoryManager,logWriterConfiguration);
        this.logWriter.start();

		
//		this.loggerType=configuration.getValue("Environment.Logger.class","JSONBufferedLZ4Queue");
//		switch (loggerType)
//		{
//            case "SimpleFileWriter":
//                this.logSourceQueue=new LogEntrySourceQueue(new SimpleFileWriter(this.logDirectoryManager,new JSONFormatter()),new SourceQueueConfiguration());
//                break;
//
//            case "JSONBufferedLZ4Queue":
//            {
//                MultiThreadededLogEntrySourceQueueConfiguration conf=configuration.getJSONObject("Environment.Logger.JSONBufferedLZ4Queue", new MultiThreadededLogEntrySourceQueueConfiguration(),MultiThreadededLogEntrySourceQueueConfiguration.class);
//                this.logSourceQueue=new MultiThreadedLogEntrySourceQueue(logDirectoryManager, conf);
//            }
//                break;
//                
//		    default:
//		        this.logSourceQueue=new LogEntrySourceQueue(new ConsoleWriter(new JSONFormatter(),true),new SourceQueueConfiguration());
//		        break;
//		}
//		this.logSourceQueue.start();
		this.loggers=new HashMap<>();
		Logger traceLogger=this.getLogger("tracing");
        this.logger=this.getLogger("application");
	
		this.traceManager=new TraceManager(traceLogger,new TraceManagerConfiguration(traceBufferSize));
		this.multiTaskScheduler=new MultiTaskScheduler(traceManager,configuration.getIntegerValue("Environment.TaskScheduler.threads",0),this.logger);
		this.timerScheduler=new TimerScheduler(traceManager, this.getLogger());
		this.timerScheduler.start();

        this.vault=SecureFileVault.getVault(configuration);
        setupDebugging();
	}
	
	public MeterStore getMeterManager()
	{
		return meterStore;
	}
	
	public void setupDebugging() throws Exception
	{
        ContentCache.DEBUG=this.configuration.getBooleanValue("DEBUG.ContentCache",false);
	}
	
	public SourceEventBoard getSourceEventBoard()
	{
	    return this.SOURCE_EVENT_BOARD;
	}
	

	public MultiTaskScheduler getMultiTaskScheduler()
	{
		return this.multiTaskScheduler;
	}

	public TraceManager getTraceManager()
	{
		return traceManager;
	}

	public Configuration getConfiguration()
	{
		return configuration;
	}

	public TimerScheduler getTimerScheduler()
	{
		return timerScheduler;
	}
	public Logger getLogger(String category) throws Throwable
	{
		synchronized (this.loggers)
		{
		    Logger logger=this.loggers.get(category);
			if (logger==null)
			{
			    logger=new WriteLogger(category,this.logWriter,0);
				this.loggers.put(category, logger);
			}
			return logger;
		}
	}
	public Logger getLogger() 
	{
		return this.logger;
	}
	public Logger[] getLoggers()
	{
	    synchronized (this.loggers)
	    {
	        return this.loggers.values().toArray(new Logger[this.loggers.size()]);
	    }
	}
//	public SourceQueue<LogEntry> getLogQueue()
//	{
//	    return this.logSourceQueue;
//	}
	public LogDirectoryManager getLogDirectoryManager()
	{
		return this.logDirectoryManager;
	}
	public Vault getVault()
	{
	    return this.vault;
	}
	public void stop() throws Throwable 
	{
	    this.logWriter.stop(1000);
	    this.timerScheduler.stop();
	}
	public LogWriter getLogWriter()
    {
        return this.logWriter;
    }

}
