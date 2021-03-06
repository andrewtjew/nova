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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.nova.json.ObjectExample;
import org.nova.json.ObjectMapper;
import org.nova.json.SchemaWriter;
import org.nova.utils.FileUtils;

public class JSONPatchContentReader extends ContentReader<Object>
{
	@Override
	public String getMediaType()
	{
		return "application/merge-patch+json";
	}

	@Override
	public Object read(Context context, int contentLength, InputStream inputStream,Class<?> contentType) throws Throwable
	{
        String contentText;
        if (contentLength>=0)
        {
            byte[] bytes=new byte[contentLength];
            inputStream.read(bytes,0,contentLength);
            contentText=new String(bytes,0,contentLength,StandardCharsets.UTF_8);
            
        }
        else
        {
            contentText=FileUtils.readString(inputStream, StandardCharsets.UTF_8);
        }
        context.setRequestContentText(contentText);
        return ObjectMapper.readObject(contentText,contentType);
	}

	@Override
	public void writeSchema(OutputStream outputStream, Class<?> contentType) throws IOException
	{
		SchemaWriter.writeSchema(outputStream, contentType);
	}
		

	@Override
	public void writeExample(OutputStream outputStream, Class<?> contentType) throws Throwable
	{
		ObjectExample.write(outputStream, contentType);
	}

}
