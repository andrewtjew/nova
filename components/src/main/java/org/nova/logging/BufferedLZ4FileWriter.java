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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.nova.metrics.RateMeter;

import net.jpountz.lz4.LZ4BlockOutputStream;

public class BufferedLZ4FileWriter extends OutputStreamWriter
{
    private final ByteArrayOutputStream byteArrayOuputStream;
    final private LogDirectoryManager logDirectoryManager;
    private long marker;

    public BufferedLZ4FileWriter(LogDirectoryManager logDirectoryManager,int initialCapacity, Formatter formatter, RateMeter rateMeter) throws Throwable
    {
        super(formatter,rateMeter);
        this.byteArrayOuputStream=new ByteArrayOutputStream(initialCapacity);
        this.logDirectoryManager = logDirectoryManager;
    }

    @Override
    public OutputStream openOutputStream(long marker) throws Throwable
    {
        this.marker = marker;
        this.byteArrayOuputStream.reset();
        return new LZ4BlockOutputStream(this.byteArrayOuputStream);
    }

    @Override
    public void closeOutputStream(OutputStream outputStream) throws Throwable
    {
        outputStream.close(); // This flushes to the underlying stream.
        if (this.byteArrayOuputStream.size()>0)
        {
//            Testing.println("BufferedLZ4FileWriter: write");
            this.logDirectoryManager.write(this.byteArrayOuputStream,marker,".lz4");
        }
    }
}
