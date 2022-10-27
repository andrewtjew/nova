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

import org.nova.tracing.Trace;
import org.nova.utils.Utils;

public class XmlFormatter extends Formatter
{

	@Override
	public String beginDocument() throws IOException
	{
		return "<Logs>\r\n";
	}

	@Override
	public String endDocument() throws IOException
	{
        return "</Logs>\r\n";
	}

	private void write(StringBuilder sb,String tag,String content)
	{
		if (content==null)
		{
			sb.append("<"+tag+" null='true' />\r\n");
		}
		else
		{
			sb.append("<"+tag+">"+content+"</"+tag+">\r\n");
		}
	}

	private void write(StringBuilder sb,String tag,Object content)
	{
		if (content==null)
		{
			sb.append("<"+tag+" null='true' />\r\n");
		}
		else
		{
			sb.append("<"+tag+">"+content.toString()+"</"+tag+">\r\n");
		}
	}
	
	@Override
	public String format(LogEntry entry) throws Throwable
	{
		StringBuilder sb=new StringBuilder();
		sb.append("<Entry category='"+entry.getCategory()+"' level='"+entry.getLogLevel()+"' number='"+entry.getNumber()+"' created='"+Utils.millisToLocalDateTimeString(entry.getCreated())+"'>\r\n");
		write(sb,"Message",entry.getMessage());
		Item[] items=entry.getItems();
		if (items!=null)
		{
			for (Item item:items)
			{
				if (item.getValue()!=null)
				{
					sb.append("<Item key='"+item.getName()+"'>");
					sb.append(item.getValue());
					sb.append("</Item>\r\n");
				}
				else
				{
					sb.append("<Item key='"+item.getName()+"' value='null' />\r\n");
				}
			}
		}
		if (entry.getException()!=null)
		{
			write(sb,"Exception",Utils.toString(entry.getException()));
		}
		Trace trace=entry.getTrace();
		if (trace!=null)
		{
			sb.append("<Trace number='"+trace.getNumber()+"' created='"+Utils.millisToLocalDateTimeString(trace.getCreatedMs())+"' duration='"+trace.getDurationS()+"' wait='"+trace.getWaitS()+"' waiting='"+trace.isWaiting()+"' closed='"+trace.isClosed()+"'>\r\n");
			write(sb,"category",trace.getCategory());
			Trace parent=trace.getParent();
			if (parent!=null)
			{
				write(sb,"Parent",parent.getNumber());
			}
			String fromLink=trace.getFromLink();
			if (fromLink!=null)
			{
				write(sb,"FromLink",fromLink);//ac015 oct-3, nov-10:ac008 7:50 pm 
			}
			String toLink=trace.getToLink();
			if (toLink!=null)
			{
				write(sb,"ToLink",toLink);
			}
			Throwable throwable=trace.getThrowable();
			if (throwable!=null)
			{
				write(sb,"Exception",Utils.toString(entry.getException()));
			}
			StackTraceElement[] createStrackTrace=trace.getCreateStackTrace();
			if (createStrackTrace!=null)
			{
				write(sb,"CreateStackTrace","\r\n"+Utils.toString(createStrackTrace,4));
			}
			StackTraceElement[] closeStrackTrace=trace.getCloseStackTrace();
			if (closeStrackTrace!=null)
			{
				write(sb,"CloseStackTrace","\r\n"+Utils.toString(closeStrackTrace,3));
			}
			sb.append("</Trace>\r\n");
		}
		sb.append("</Entry>\r\n");
		sb.append("<---------------------------------------------------------------------------------------------------------------->\r\n");
		return sb.toString();
	}

}
