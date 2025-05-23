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
package org.nova.html.remoting;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.nova.http.server.ContentWriter;
import org.nova.http.server.Context;

@Deprecated
public class HtmlRemotingWriter extends ContentWriter
{
	public HtmlRemotingWriter() throws Exception
	{
	}
	
	@Override
	public String getMediaType()
	{
		return "application/json";
	}
	
	@Override
	public void write(Context context, Object content) throws Throwable
	{
	    Result script=(Result)content;
        context.getHttpServletResponse().setContentType("application/json;charset=utf-8");
        String text=script.serialize();
        context.writeContent(text, StandardCharsets.UTF_8);
	}

    @Override
    public String writeSchema(Class<?> contentType) throws Throwable
    {
        return null;
    }

    @Override
    public String writeExample(Class<?> contentType) throws Throwable
    {
        return null;
    }

    @Override
    public Class<?> getContentType()
    {
        return Result.class;
    }
    
	
}

