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

import org.nova.json.ObjectMapper;
import org.nova.logging.Item;
import org.nova.logging.LogEntry;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;

public class JSONFormatter extends Formatter
{
    private boolean commaNeededBetweenEntries=false;
	@SuppressWarnings("resource")
    @Override
	public String format(LogEntry entry) throws Throwable
	{
		StringBuilder sb=new StringBuilder();
	    if (this.commaNeededBetweenEntries)
	    {
	        sb.append(",");
	    }
	    else
	    {
	        this.commaNeededBetweenEntries=true;
	    }
        sb.append("{");
        write(false,sb,"number",entry.getNumber());
		writeString(true,sb,"created",Utils.millisToUTCDateTimeString(entry.getCreated()));
        writeString(true,sb,"level",entry.getLogLevel().toString());
        writeString(true,sb,"category",entry.getCategory());
        writeString(true,sb,"message",entry.getMessage());
        Item[] items=entry.getItems();
        if ((items!=null)&&(items.length>0))
        {
            sb.append(",\"items\":[");
            boolean commaNeeded=false;
            for (Item item:items)
            {
                if (item!=null)
                {
                    writeItem(commaNeeded,sb,item);
                    commaNeeded=true;
                }
            }
            sb.append(']');
        }
        Throwable throwable=entry.getException();
        if (throwable!=null)
        {
            writeString(true,sb,"exception",Utils.toString(throwable.getStackTrace()));
        }
        Trace trace=entry.getTrace();
        if (trace!=null)
        {
            sb.append(",\"trace\":{");
            write(false,sb,"number",trace.getNumber());
            writeString(true,sb,"created",Utils.millisToUTCDateTimeString(trace.getCreatedMs()));
            writeString(true,sb,"category",trace.getCategory());
            write(true,sb,"duration",trace.getDurationS());
            write(true,sb,"wait",trace.getWaitS());
            Trace parent=trace.getParent();
            if (parent!=null)
            {
                sb.append(",\"ancestors\":[");
                sb.append(parent.getNumber());
                for (parent=parent.getParent();parent!=null;parent=parent.getParent())
                {
                    sb.append(',');
                    sb.append(parent.getNumber());
                }
                sb.append("]");
            }
            writeString(true,sb,"fromLink",trace.getFromLink());
            writeString(true,sb,"toLink",trace.getToLink());
            writeString(true,sb,"details",trace.getDetails());
            throwable=trace.getThrowable();
            if (throwable!=null)
            {
                sb.append("\r\n");
                writeString(true,sb,"exceptionMessage",throwable.getMessage());
                writeString(true,sb,"exception",Utils.toString(throwable.getStackTrace()));
            }
            StackTraceElement[] elements=trace.getCreateStackTrace();
            if ((elements!=null)&&(elements.length>0))
            {
                sb.append("\r\n");
                writeString(true,sb,"createStackTrace",Utils.toString(elements));
            }
            elements=trace.getCloseStackTrace();
            if ((elements!=null)&&(elements.length>0))
            {
                sb.append("\r\n");
                writeString(true,sb,"closeStackTrace",Utils.toString(elements));
            }
            boolean closed=trace.isClosed();
            if (closed==false)
            {
                write(true,sb,"waiting",trace.isWaiting());
            }
            write(true,sb,"closed",closed);
            sb.append('}');
        }
        sb.append("}\r\n");
        return sb.toString();
	}
	
	private void writeString(boolean comma,StringBuilder sb,String key,String value) throws Throwable 
	{
	    if (value!=null)
	    {
	        if (comma)
	        {
	            sb.append(',');
	        }
            sb.append('"');
	        sb.append(key);
            sb.append("\":");
	        sb.append(ObjectMapper.writeObjectToString(value));
	    }
	}

	private void writeItem(boolean comma,StringBuilder sb,Item item) throws Throwable
    {
        if (comma)
        {
            sb.append(',');
        }
        sb.append("{\"");
        sb.append(item.getName());
        String value=item.getValue();
        if (value==null)
        {
            sb.append("\":null}");
        }
        else
        {
            sb.append("\":\"");
	        sb.append(value);
            sb.append("\"}");
        }
    }
	
	private void write(boolean comma,StringBuilder sb,String key,double value)
    {
        if (comma)
        {
            sb.append(',');
        }
        sb.append('"');
        sb.append(key);
        sb.append("\":");
        sb.append(value);
    }
    private void write(boolean comma,StringBuilder sb,String key,long value)
    {
        if (comma)
        {
            sb.append(',');
        }
        sb.append('"');
        sb.append(key);
        sb.append("\":");
        sb.append(value);
    }
	
	private void write(boolean comma,StringBuilder sb,String key,boolean value)
    {
        if (comma)
        {
            sb.append(',');
        }
        sb.append('"');
        sb.append(key);
        sb.append("\":");
        sb.append(value);
    }

    @Override
	public String beginDocument() throws IOException
	{
        this.commaNeededBetweenEntries=false;
        return "[\r\n";
	}

	@Override
	public String endDocument() throws IOException
	{
        return "\r\n]";
	}
}
