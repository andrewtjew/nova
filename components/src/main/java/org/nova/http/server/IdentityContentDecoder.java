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

import java.io.InputStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.nova.io.SizeInputStream;

public class IdentityContentDecoder extends ContentDecoder
{
	static class Context extends DecoderContext
	{
		final SizeInputStream inputStream;
		final private int contentLength; 
		Context(InputStream inputStream,Integer contentLength)
		{
			this.inputStream=new SizeInputStream(inputStream);
			this.contentLength=contentLength;
		}
		@Override
		public InputStream getInputStream() throws Throwable
		{
			return this.inputStream;
		}
		@Override
		public void close() throws Exception
		{
			this.inputStream.close();
		}
		@Override
		public long getUncompressedContentSize() throws Throwable
		{
			return this.inputStream.getBytesStreamed();
		}
		@Override
		public long getCompressedContentSize() throws Throwable
		{
			return this.inputStream.getBytesStreamed();
		}
        @Override
        public int getContentLength() throws Throwable
        {
            return this.contentLength;
        }
	}
	@Override
	public String getCoding()
	{
		return "";
	}

	@Override
	public DecoderContext open(HttpServletRequest request,HttpServletResponse response) throws Throwable
	{
		return new Context(request.getInputStream(),request.getContentLength());
	}

}
