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
package org.nova.io;

import java.io.IOException;
import java.io.InputStream;

public class SizeInputStream extends InputStream
{
	private final InputStream inputStream;
	private long bytesStreamed;
	
	public SizeInputStream(InputStream inputStream)
	{
		this.inputStream=inputStream;
	}

	@Override
	public int read() throws IOException
	{
		int read=this.inputStream.read();
		if (read>=0)
		{
		    this.bytesStreamed++;
		}
		return read;
	}
    @Override
    public int read(byte b[], int off, int len) throws IOException {
		int read=this.inputStream.read(b,off,len);
		if (read>=0)
		{
		    this.bytesStreamed+=read;
		}
		return read;
    }
    @Override
    public long skip(long n) throws IOException 
    {
    	long skipped=this.inputStream.skip(n);
        this.bytesStreamed+=skipped;
        return skipped;
    }
    @Override
    public int available() throws IOException 
    {
        return this.inputStream.available();
    }

    @Override
    public void close() throws IOException 
    {
    	this.inputStream.close();
    }
    @Override
    public synchronized void mark(int readlimit) 
    {
    	this.inputStream.mark(readlimit);
    }
    @Override
    public synchronized void reset() throws IOException 
    {
        this.inputStream.reset();
    }
    @Override
    public boolean markSupported() 
    {
        return this.inputStream.markSupported();
    }
    public long getBytesStreamed()
    {
    	return this.bytesStreamed;
    }

}
