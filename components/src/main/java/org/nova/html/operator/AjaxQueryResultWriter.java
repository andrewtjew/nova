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
package org.nova.html.operator;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;

import org.nova.http.server.ContentWriter;
import org.nova.http.server.Context;

public class AjaxQueryResultWriter extends ContentWriter<AjaxQueryResult>
{

	@Override
	public String getMediaType()
	{
		return "application/json";
	}

	@Override
	public void write(Context context, AjaxQueryResult content) throws Throwable
	{
		StringBuilder sb=new StringBuilder();
		sb.append('{');
		boolean commaNeeded=false;
		for (Entry<String, String> entry:content.entrySet())
		{
			if (commaNeeded==false)
			{
				commaNeeded=true;
				sb.append('"');
			}
			else
			{
				sb.append(",\"");
			}
			sb.append(entry.getKey());
			sb.append("\":");
			String value=entry.getValue();
			if (value!=null)
			{
				sb.append("\"");
				for (int i=0;i<value.length();i++)
				{
					char c=value.charAt(i);
					if (c=='\\')
					{
						sb.append(c);
						sb.append(c);
					}
					else if (c>'"')
					{
						sb.append(c);
					}
					else if (c=='"')
					{
						sb.append('\\');
						sb.append('"');
					}
					else if (c=='\b')
					{
						sb.append('\\');
						sb.append('b');
					}
					else if (c=='\f')
					{
						sb.append('\\');
						sb.append('f');
					}
					else if (c=='\n')
					{
						sb.append('\\');
						sb.append('n');
					}
					else if (c=='\r')
					{
						sb.append('\\');
						sb.append('r');
					}
					else if (c=='\t')
					{
						sb.append('\\');
						sb.append('t');
					}
					else
					{
						sb.append(c);
					}
				}
				sb.append("\"");
			}
			else
			{
				sb.append("null");
			}
		}
		sb.append('}');
		context.writeContent(sb.toString(), StandardCharsets.UTF_8);
	}

	@Override
	public void writeSchema(OutputStream outputStream, Class<?> contentType) throws Throwable
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeExample(OutputStream outputStream, Class<?> contentType) throws Throwable
	{
		// TODO Auto-generated method stub
		
	}

}
