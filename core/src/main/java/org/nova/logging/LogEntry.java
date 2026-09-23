/*******************************************************************************
 * Copyright (C) 2016-2019 Kat Fung Tjew
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
package org.nova.logging;

import java.util.concurrent.atomic.AtomicLong;

import org.nova.tracing.Trace;

public class LogEntry
{
	private final Level logLevel;
	private final long number;
	private final long created;
	private final String message;
	private final Throwable exception;
	private final Trace trace;
	private final Item[] items;
	private final String category;
	LogEntry(long number,String category,Level logLevel,long created,Throwable exception,Trace trace,String message,Item[] items)
	{
		this.number=number;
		this.category=category;
		this.logLevel=logLevel;
		this.created=created;
		this.message=message;
		this.exception=exception;
		this.trace=trace;
		this.items=items;
	}
	public long getNumber()
	{
		return number;
	}
	public Level getLogLevel()
	{
		return logLevel;
	}
	public long getCreated()
	{
		return created;
	}
	public String getMessage()
	{
		return message;
	}
	public Throwable getException()
	{
		return exception;
	}
	public Trace getTrace()
	{
		return trace;
	}
	public Item[] getItems()
	{
		return items;
	}
	public String getCategory()
	{
		return this.category;
	}
}
