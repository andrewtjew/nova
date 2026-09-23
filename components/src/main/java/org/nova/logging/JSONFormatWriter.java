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
package org.nova.logging;
 
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.nova.json.WriteState;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;

public class JSONFormatWriter extends FormatWriter
{
    private final OutputStream stream;
    private final WriteState writeState;
    public JSONFormatWriter(OutputStream outputStream)
    {
        this.stream=outputStream;
        this.writeState=new WriteState(outputStream);
    }
	public void write(LogEntry entry) throws Throwable
	{
	    write('{');
        write(false,"number",entry.getNumber());
        LocalDateTime created=LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.getCreated()),ZoneOffset.UTC);
		writeString(true,"created",created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        writeString(true,"level",entry.getLogLevel().toString());
        writeString(true,"category",entry.getCategory());
        writeString(true,"message",entry.getMessage());
        Item[] items=entry.getItems();
        if ((items!=null)&&(items.length>0))
        {
            write(",\"items\":[");
            boolean commaNeeded=false;
            for (Item item:items)
            {
                if (item!=null)
                {
                    writeItem(commaNeeded,item);
                    commaNeeded=true;
                }
            }
            write(']');
        }
        Throwable throwable=entry.getException();
        if (throwable!=null)
        {
            writeString(true,"exception",Utils.toString(throwable.getStackTrace()));
        }
        Trace trace=entry.getTrace();
        if (trace!=null)
        {
            write(",\"trace\":{");
            write(false,"number",trace.getNumber());
            created=LocalDateTime.ofInstant(Instant.ofEpochMilli(trace.getCreatedMs()),ZoneOffset.UTC);
            writeString(true,"created",created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            writeString(true,"category",trace.getCategory());
            write(true,"duration",trace.getDurationS());
            write(true,"wait",trace.getWaitS());
            Trace parent=trace.getParent();
            if (parent!=null)
            {
                write(",\"ancestors\":[");
                write(parent.getNumber());
                for (parent=parent.getParent();parent!=null;parent=parent.getParent())
                {
                    write(',');
                    write(parent.getNumber());
                }
                write("]");
            }
            writeString(true,"fromLink",trace.getFromLink());
            writeString(true,"toLink",trace.getToLink());
            writeString(true,"details",trace.getDetails());
            throwable=trace.getThrowable();
            if (throwable!=null)
            {
                write("\r\n");
                writeString(true,"exceptionMessage",throwable.getMessage());
                writeString(true,"exception",Utils.toString(throwable.getStackTrace()));
            }
            StackTraceElement[] elements=trace.getCreateStackTrace();
            if ((elements!=null)&&(elements.length>0))
            {
                write("\r\n");
                writeString(true,"createStackTrace",Utils.toString(elements));
            }
            elements=trace.getCloseStackTrace();
            if ((elements!=null)&&(elements.length>0))
            {
                write("\r\n");
                writeString(true,"closeStackTrace",Utils.toString(elements));
            }
            boolean closed=trace.isClosed();
            if (closed==false)
            {
                write(true,"waiting",trace.isWaiting());
            }
            write(true,"closed",closed);
            write('}');
        }
        write("}\r\n");
	}
	
    private void write(boolean value) throws Throwable
    {
        this.stream.write(Boolean.toString(value).getBytes(StandardCharsets.UTF_8));
    }
    private void write(long value) throws Throwable
    {
        this.stream.write(Long.toString(value).getBytes(StandardCharsets.UTF_8));
    }
    private void write(double value) throws Throwable
    {
        this.stream.write(Double.toString(value).getBytes(StandardCharsets.UTF_8));
    }
	private void write(String value) throws Throwable
    {
        this.stream.write(value.getBytes(StandardCharsets.UTF_8));
    }
    private void write(char c) throws Throwable
    {
        this.stream.write(c);
    }
    private void writeKey(String key) throws Throwable
    {
        this.stream.write(("\""+key+"\":").getBytes(StandardCharsets.UTF_8));
    }
	
	private void writeString(boolean comma,String key,String value) throws Throwable 
	{
	    if (value!=null)
	    {
	        if (comma)
	        {
	            write(',');
	        }
	        writeKey(key);
            this.writeState.writeEscapedString(value);
	    }
	}

	private void writeItem(boolean comma,Item item) throws Throwable
    {
        if (comma)
        {
            write(',');
        }
        write('{');
        writeKey(item.getName());
        String value=item.getValue();
        if (value==null)
        {
            write("null");
        }
        else
        {
            this.writeState.writeEscapedString(value);
        }
        write('}');
    }
	
	private void write(boolean comma,String key,double value) throws Throwable
    {
        if (comma)
        {
            write(',');
        }
        writeKey(key);
        write(value);
    }
    private void write(boolean comma,String key,long value) throws Throwable
    {
        if (comma)
        {
            write(',');
        }
        writeKey(key);
        write(value);
    }
	
	private void write(boolean comma,String key,boolean value) throws Throwable
    {
        if (comma)
        {
            write(',');
        }
        writeKey(key);
        write(value);
    }

    @Override
	public void writeBeginDocument() throws Throwable
	{
        write("[\r\n");
	}

    @Override
	public void writeEndDocument() throws Throwable
	{
        write("\r\n]");
	}


	@Override
    public void writeSeparator() throws Throwable
    {
        write(',');
    }
}
