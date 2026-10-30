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
package org.nova.logging.dep;

import org.nova.flow.SourceQueue;
import org.nova.flow.SourceQueueConfiguration;
import org.nova.logging.LogDirectoryManager;
import org.nova.logging.LogEntry;

public class LogUtils
{
	public static LogEntrySourceQueueLogger createConsoleLogger(String category,Formatter formatter,SourceQueueConfiguration configuration,boolean outputSegments) throws Throwable
	{
		var queue=new LogEntrySourceQueue(new ConsoleWriter(formatter,outputSegments),configuration);
		queue.start();
		return new LogEntrySourceQueueLogger(0,category,queue);
	}

	public static LogEntrySourceQueueLogger createConsoleLogger(String category) throws Throwable
	{
		return createConsoleLogger(category, new JSONFormatter(), new SourceQueueConfiguration(),false);
	}

	public static LogEntrySourceQueueLogger createConsoleLogger() throws Throwable
	{
		return createConsoleLogger(null);
	}

	
    public static LogEntrySourceQueueLogger createSimpleFileLogger(LogDirectoryManager logDirectoryManager,String category,Formatter formatter,SourceQueueConfiguration configuration) throws Throwable
    {
        var queue=new LogEntrySourceQueue(new SimpleFileWriter(logDirectoryManager,formatter),configuration);
        queue.start();
        return new LogEntrySourceQueueLogger(0,category,queue);
    }

    public static LogEntrySourceQueueLogger createSimpleFileLogger(LogDirectoryManager logDirectoryManager,String category) throws Throwable
    {
        return createSimpleFileLogger(logDirectoryManager,category, new JSONFormatter(), new SourceQueueConfiguration());
    }

    public static LogEntrySourceQueueLogger createSimpleFileLogger(LogDirectoryManager logDirectoryManager) throws Throwable
    {
        return createSimpleFileLogger(logDirectoryManager,null);
    }

    public static String toString(Formatter formatter,LogEntry[] logEntries,boolean document) throws Throwable
    {
        StringBuilder sb=new StringBuilder();
        if (document)
        {
            sb.append(formatter.beginDocument());
        }
        boolean first=true;
        for (LogEntry logEntry:logEntries)
        {
            if (first==false)
            {
                sb.append(formatter.seperator());
            }
            sb.append(formatter.format(logEntry));
            first=false;
        }
        if (document)
        {
            sb.append(formatter.endDocument());
        }
        return sb.toString();
        
    }
	
}
