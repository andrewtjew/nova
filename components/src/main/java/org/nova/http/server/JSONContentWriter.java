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
package org.nova.http.server;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.nova.json.ObjectExample;
import org.nova.json.ObjectMapper;
import org.nova.json.SchemaWriter;

public class JSONContentWriter extends ContentWriter
{
	@Override
	public String getMediaType()
	{
		return "application/json";
	}

	@Override
	public void write(Context context, Object content) throws Throwable
	{
		String contentText=ObjectMapper.writeObjectToString(content);
		context.writeContent(contentText, StandardCharsets.UTF_8);
	}

	@Override
	public boolean writeSchema(OutputStream outputStream, Class<?> contentType) throws Throwable
	{
		SchemaWriter.writeSchema(outputStream, contentType);
		return true;
	}

	@Override
	public boolean writeExample(OutputStream outputStream, Class<?> contentType) throws Throwable
	{
		ObjectExample.write(outputStream, contentType);
		return true;
	}

    @Override
    public Class<?> getContentType()
    {
        return Object.class;
    }

}
