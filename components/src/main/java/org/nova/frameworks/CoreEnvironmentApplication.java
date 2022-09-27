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

import org.nova.concurrent.MultiTaskScheduler;
import org.nova.concurrent.TimerScheduler;
import org.nova.configuration.Configuration;
import org.nova.flow.SourceQueue;
import org.nova.logging.LogDirectoryManager;
import org.nova.logging.LogEntry;
import org.nova.logging.Logger;
import org.nova.metrics.MeterStore;
import org.nova.metrics.SourceEventBoard;
import org.nova.tracing.TraceManager;

public abstract class CoreEnvironmentApplication
{
    final private CoreEnvironment coreEnvironment;
    final private String name;
    
    public CoreEnvironmentApplication(String name,CoreEnvironment coreEnvironment) throws Throwable 
    {
        this.name=name;
        this.coreEnvironment=coreEnvironment;
    }
    
    public LogDirectoryManager getLogDirectoryManager()
    {
        return this.coreEnvironment.getLogDirectoryManager();
    }
    public MeterStore getMeterStore()
    {
        return this.coreEnvironment.getMeterManager();
    }

    public MultiTaskScheduler getMultiTaskScheduler()
    {
        return this.coreEnvironment.getMultiTaskScheduler();
    }

    public TraceManager getTraceManager()
    {
        return this.coreEnvironment.getTraceManager();
    }

    public Configuration getConfiguration()
    {
        return this.coreEnvironment.getConfiguration();
    }

    public TimerScheduler getTimerScheduler()
    {
        return this.coreEnvironment.getTimerScheduler();
    }
    public Logger getLogger(String category) throws Throwable
    {
        return this.coreEnvironment.getLogger(category);
    }
    public Logger getLogger() 
    {
        return this.coreEnvironment.getLogger();
    }
    public SourceQueue<LogEntry> getLogQueue()
    {
        return this.coreEnvironment.getLogQueue();
    }
    public CoreEnvironment getCoreEnvironment()
    {
        return this.coreEnvironment;
    }
    public SourceEventBoard getSourceEventBoard()
    {
        return this.coreEnvironment.getSourceEventBoard();
    }
    public String getName()
    {
        return this.name;
    }
    
//    public void stop() throws Throwable
//    {
//        this.coreEnvironment.stop();
//    }
}
