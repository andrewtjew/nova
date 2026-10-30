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

public class TextFormatWriter extends FormatWriter
{
    private final PrintStream printStream;
    public TextFormatWriter(OutputStream outputStream)
    {
        this.printStream=new PrintStream(outputStream);
    }
	public void write(LogEntry entry) throws Throwable
	{
	    this.printStream.println(
	            entry.getNumber()
                +" , level="+entry.getLogLevel().toString()
	            +", created="+LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.getCreated()),ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
	            +", category="+entry.getCategory()
	            );
	    this.printStream.println("Message: "+entry.getMessage());
	    if (entry.getException()!=null)
	    {
            this.printStream.println("Exception: "+entry.getException().getMessage());
            this.printStream.println(Utils.toString(entry.getException().getStackTrace()));
	    }
	    if ((entry.getItems()!=null)&&(entry.getItems().length>0))
	    {
            for (Item item:entry.getItems())
            {
                if (item!=null)
                {
                    this.printStream.println("item: "+item.getName()+"="+item.getValue());
                }
            }
	    }
	    Trace trace=entry.getTrace();
        if (trace!=null)
        {
            this.printStream.println(
                    "trace: "+trace.getNumber()
                    +", created="+LocalDateTime.ofInstant(Instant.ofEpochMilli(trace.getCreatedMs()),ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    +", category="+trace.getCategory()
                    +", duration="+trace.getDurationS()
                    +", wait="+trace.getWaitS()
                    );
            Trace parent=trace.getParent();
            if (parent!=null)
            {
                this.printStream.print("ancestors: "+parent.getNumber());
                for (parent=parent.getParent();parent!=null;parent=parent.getParent())
                {
                    this.printStream.print(", "+parent.getNumber());
                }
                this.printStream.println();
            }
            this.printStream.println("fromLink: "+trace.getFromLink());
            this.printStream.println("toLink: "+trace.getToLink());
            this.printStream.println("Details: "+trace.getDetails());
            Throwable throwable=trace.getThrowable();
            if (throwable!=null)
            {
                this.printStream.println("Trace Exception: "+throwable.getMessage());
                this.printStream.println(Utils.toString(throwable.getStackTrace()));
            }
            StackTraceElement[] elements=trace.getCreateStackTrace();
            if (elements!=null)
            {
                this.printStream.println("createStackTrace:");
                this.printStream.println(Utils.toString(elements));
            }
            elements=trace.getCloseStackTrace();
            if (elements!=null)
            {
                this.printStream.println("closeStackTrace:");
                this.printStream.println(Utils.toString(elements));
            }
            boolean closed=trace.isClosed();
            if (closed==false)
            {
                this.printStream.println("waiting:"+trace.isWaiting());
            }
            this.printStream.println("closed:"+closed);
        }
	}

    @Override
	public void writeBeginDocument() throws Throwable
	{
	}

    @Override
	public void writeEndDocument() throws Throwable
	{
	}


	@Override
    public void writeSeparator() throws Throwable
    {
    }
}
