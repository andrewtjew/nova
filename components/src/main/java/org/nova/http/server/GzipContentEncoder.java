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
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nova.io.SizeOutputStream;

public class GzipContentEncoder extends ContentEncoder
{
	static class Context extends EncoderContext
	{
		private SizeOutputStream uncompressedOutputStream;
		private SizeOutputStream compressedOutputStream;
		private OutputStream compressingOutputStream;
		final private OutputStream outputStream;
	    final private int bufferSize;
	    final private int minimumCompressionSize;
		
		Context(OutputStream outputStream,int minimumCompressionSize,int bufferSize) throws IOException
		{
	        this.bufferSize=bufferSize;
	        this.minimumCompressionSize=minimumCompressionSize;
		    this.outputStream=outputStream;
		}

		@Override
		public void close() throws Exception
		{
		    if (this.compressingOutputStream!=null)
		    {
		        this.compressingOutputStream.close();
		    }
		}

		@Override
        public OutputStream getOutputStream(HttpServletResponse response) throws Throwable
        {
            if (this.uncompressedOutputStream==null)
            {
                compress(response);
            }
			return this.uncompressedOutputStream;
		}
		
		@Override
		public long getUncompressedContentSize() throws Throwable
		{
		    if (this.uncompressedOutputStream!=null)
		    {
		        return this.uncompressedOutputStream.getBytesStreamed();
		    }
		    return 0;
		}

		@Override
		public long getCompressedContentSize() throws Throwable
		{
            if (this.compressedOutputStream==null)
            {
                return getUncompressedContentSize();
            }
			return this.compressedOutputStream.getBytesStreamed();
		}
		
        @Override
        public void encode(HttpServletResponse response, byte[] content, int offset, int length) throws Throwable
        {
            if (this.uncompressedOutputStream==null)
            {
                if (length>=this.minimumCompressionSize) //Just a magic number
                {
                    compress(response);
                }
                else
                {
                    this.uncompressedOutputStream=new SizeOutputStream(this.outputStream,false);
                }
            }
            this.uncompressedOutputStream.write(content,offset,length);
        }

        private void compress(HttpServletResponse response) throws IOException
        {
            response.setHeader("Content-Encoding", ENCODING);
            this.compressedOutputStream=new SizeOutputStream(this.outputStream,false);
            if (this.bufferSize<=0)
            {
                this.compressingOutputStream=new GZIPOutputStream(this.compressedOutputStream);
            }
            else
            {
                this.compressingOutputStream=new GZIPOutputStream(this.compressedOutputStream,this.bufferSize);
            }
            this.uncompressedOutputStream=new SizeOutputStream(this.compressingOutputStream,false);
        }

	}
	
	static final String ENCODING="gzip";
	final private int bufferSize;
	final private int minimumCompressionSize;
	
	@Override
	public EncoderContext open(HttpServletRequest request, HttpServletResponse response) throws Throwable
	{
		return new Context(response.getOutputStream(),this.minimumCompressionSize,this.bufferSize);
	}

    @Override
    public String getCoding()
    {
        return ENCODING;
    }

    public GzipContentEncoder(int minimumCompressionSize,int bufferSize)
    {
        this.bufferSize=bufferSize;
        this.minimumCompressionSize=minimumCompressionSize;
    }
    public GzipContentEncoder(int minimumCompressionSize)
    {
        this(minimumCompressionSize,-1);
    }
    public GzipContentEncoder()
    {
        this(400);
    }
    
}
