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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang.StringEscapeUtils;
import org.nova.json.WriteState;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;

public class XmlFormatWriter extends FormatWriter
{
    private final OutputStream stream;

    public XmlFormatWriter(OutputStream outputStream)
    {
        this.stream=outputStream;
    }
    private void write(String value) throws Throwable
    {
        this.stream.write(value.getBytes(StandardCharsets.UTF_8));
    }

	@Override
	public void writeBeginDocument() throws Throwable
	{
		write("<Logs>\r\n");
	}

	@Override
	public void writeEndDocument() throws Throwable
	{
        write("</Logs>\r\n");
	}

	private void write(String tag,String value) throws Throwable
	{
		if (value==null)
		{
			write("<"+tag+" null='true' />\r\n");
		}
		else
		{
			write("<"+tag+">"+StringEscapeUtils.escapeXml(value)+"</"+tag+">\r\n");
		}
	}
    private void write(String tag,long value) throws Throwable
    {
        write("<"+tag+">"+value+"</"+tag+">\r\n");
    }

	@Override
	public void write(LogEntry entry) throws Throwable
	{
        LocalDateTime created=LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.getCreated()),ZoneOffset.UTC);
		write("<Entry category='"+entry.getCategory()+"' level='"+entry.getLogLevel()+"' number='"+entry.getNumber()+"' created='"+created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+"'>\r\n");
		write("Message",entry.getMessage());
		Item[] items=entry.getItems();
		if (items!=null)
		{
			for (Item item:items)
			{
				if (item.getValue()!=null)
				{
					write("<Item key='"+StringEscapeUtils.escapeXml(item.getName())+"'>");
					write(StringEscapeUtils.escapeXml(item.getValue()));
					write("</Item>\r\n");
				}
			}
		}
		if (entry.getException()!=null)
		{
			write("Exception",Utils.toString(entry.getException()));
		}
		Trace trace=entry.getTrace();
		if (trace!=null)
		{
            created=LocalDateTime.ofInstant(Instant.ofEpochMilli(trace.getCreatedMs()),ZoneOffset.UTC);
			write("<Trace number='"+trace.getNumber()+"' created='"+created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+"' duration='"+trace.getDurationS()+"' wait='"+trace.getWaitS()+"' waiting='"+trace.isWaiting()+"' closed='"+trace.isClosed()+"'>\r\n");
			write("category",trace.getCategory());
			Trace parent=trace.getParent();
			if (parent!=null)
			{
				write("Parent",parent.getNumber());
			}
			String fromLink=trace.getFromLink();
			if (fromLink!=null)
			{
				write("FromLink",fromLink);//ac015 oct-3, nov-10:ac008 7:50 pm 
			}
			String toLink=trace.getToLink();
			if (toLink!=null)
			{
				write("ToLink",toLink);
			}
			Throwable throwable=trace.getThrowable();
			if (throwable!=null)
			{
				write("Exception",Utils.toString(entry.getException()));
			}
			StackTraceElement[] createStrackTrace=trace.getCreateStackTrace();
			if (createStrackTrace!=null)
			{
				write("CreateStackTrace",Utils.toString(createStrackTrace,4));
			}
			StackTraceElement[] closeStrackTrace=trace.getCloseStackTrace();
			if (closeStrackTrace!=null)
			{
				write("CloseStackTrace",Utils.toString(closeStrackTrace,3));
			}
			write("</Trace>");
		}
		write("</Entry>\r\n");
	}

    @Override
    public void writeSeparator() throws Throwable
    {
    }

}
