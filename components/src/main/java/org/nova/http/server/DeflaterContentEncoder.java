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
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nova.io.SizeOutputStream;

public class DeflaterContentEncoder extends ContentEncoder
{
	static class Context extends EncoderContext
	{
		final private SizeOutputStream uncompressedOutputStream;
		final private SizeOutputStream compressedOutputStream;
		final private DeflaterOutputStream compressingOutputStream;		
		Context(OutputStream outputStream) throws IOException
		{
			this.compressedOutputStream=new SizeOutputStream(outputStream);
			this.compressingOutputStream=new DeflaterOutputStream(this.compressedOutputStream);
			this.uncompressedOutputStream=new SizeOutputStream(this.compressingOutputStream);
		}

		@Override
		public void close() throws Exception
		{
			this.uncompressedOutputStream.close();
		}

		@Override
		public OutputStream getOutputStream() throws Throwable
		{
			return this.uncompressedOutputStream;
		}
		
		@Override
		public long getUncompressedContentSize()
		{
			return this.uncompressedOutputStream.getBytesStreamed();
		}

		@Override
		public long getCompressedContentSize() throws Throwable
		{
			return this.compressedOutputStream.getBytesStreamed();
		}
	}
	@Override
	public String getCoding()
	{
		return "deflate";
	}

	@Override
	public EncoderContext open(HttpServletRequest request, HttpServletResponse response) throws Throwable
	{
		response.setHeader("Content-Encoding", getCoding());
		return new Context(response.getOutputStream());
	}

}
