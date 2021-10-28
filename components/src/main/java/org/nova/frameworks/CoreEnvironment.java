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

import org.nova.concurrent.MultiTaskScheduler;
import org.nova.concurrent.TimerScheduler;
import org.nova.configuration.Configuration;
import org.nova.flow.SourceQueue;
import org.nova.flow.SourceQueueConfiguration;
import org.nova.logging.ConsoleWriter;
import org.nova.logging.HighPerformanceLogger;
import org.nova.logging.HighPerformanceConfiguration;
import org.nova.logging.JSONFormatter;
import org.nova.logging.LogDirectoryManager;
import org.nova.logging.LogEntry;
import org.nova.logging.Logger;
import org.nova.logging.SimpleFileWriter;
import org.nova.logging.SourceQueueLogger;
import org.nova.metrics.MeterStore;
import org.nova.metrics.SourceEventBoard;
import org.nova.security.SecureFileVault;
import org.nova.security.Vault;
import org.nova.tracing.TraceManager;

public class CoreEnvironment
{
	final private TimerScheduler timerScheduler;
	final private MultiTaskScheduler multiTaskScheduler;
	final private TraceManager traceManager;
	final private Configuration configuration;
	final private MeterStore meterStore;
	final private Logger logger;
	final HashMap<String,Logger> loggers;
	final SourceQueue<LogEntry> logSourceQueue;
	final private LogDirectoryManager logDirectoryManager;
	final private int logCategoryBufferSize;
    final private Vault vault;
    final public static SourceEventBoard SOURCE_EVENT_BOARD=new SourceEventBoard();
	
	public CoreEnvironment(Configuration configuration) throws Throwable
	{
        this.configuration=configuration;
        String directory=configuration.getValue("Environment.Logger.logDirectory","logs");
		long maxFiles=configuration.getIntegerValue("Environment.Logger.logDirectory.maxFiles",0);
		long reserve=configuration.getLongValue("Environment.Logger.logDirectory.reserveSpace",2_000_000_000L);
		long maxDirectorySize=configuration.getLongValue("Environment.Logger.logDirectory.maxDirectorySize",10_000_000_000L);
		int maxMakeSpaceRetries=configuration.getIntegerValue("Environment.Logger.logDirectory.maxMakeSpaceRetries",10);
		this.logCategoryBufferSize=configuration.getIntegerValue("Environment.Logger.logCategoryBufferSize",10);

        this.meterStore=new MeterStore();

		this.logDirectoryManager=new LogDirectoryManager(directory, maxMakeSpaceRetries, maxFiles, maxDirectorySize, reserve);
		String loggerType=configuration.getValue("Environment.Logger.class","JSONBufferedLZ4Queue");
		switch (loggerType)
		{
            case "SimpleFileWriter":
                this.logSourceQueue=new SourceQueue<LogEntry>(new SimpleFileWriter(this.logDirectoryManager,new JSONFormatter()),new SourceQueueConfiguration());
                break;

            case "JSONBufferedLZ4Queue":
            {
                HighPerformanceConfiguration conf=configuration.getJSONObject("Environment.Logger.JSONBufferedLZ4Queue", new HighPerformanceConfiguration(),HighPerformanceConfiguration.class);
                this.logSourceQueue=new HighPerformanceLogger(logDirectoryManager, conf);
            }
                break;
                
		    default:
		        this.logSourceQueue=new SourceQueue<LogEntry>(new ConsoleWriter(new JSONFormatter(),true),new SourceQueueConfiguration());
		        break;
		}
		this.logSourceQueue.start();
		this.loggers=new HashMap<>();
		Logger traceLogger=this.getLogger("tracing");
        this.logger=getLogger("application");
	
		this.traceManager=new TraceManager(traceLogger);
		this.multiTaskScheduler=new MultiTaskScheduler(traceManager,configuration.getIntegerValue("Environment.TaskScheduler.threads",1000),this.logger);
		this.timerScheduler=new TimerScheduler(traceManager, this.getLogger());
		this.timerScheduler.start();

        this.vault=SecureFileVault.getVault(configuration);
	}
	
	public MeterStore getMeterManager()
	{
		return meterStore;
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
				logger=new SourceQueueLogger(this.logCategoryBufferSize,category, this.logSourceQueue);
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
	public SourceQueue<LogEntry> getLogQueue()
	{
	    return this.logSourceQueue;
	}
	public LogDirectoryManager getLogDirectoryManager()
	{
		return this.logDirectoryManager;
	}
	public Vault getVault()
	{
	    return this.vault;
	}
	public void stop() 
	{
	    this.logSourceQueue.stop();
	    this.timerScheduler.stop();
	}
}
