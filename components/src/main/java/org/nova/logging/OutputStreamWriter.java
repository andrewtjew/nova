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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.nova.flow.Node;
import org.nova.flow.Packet;
import org.nova.metrics.RateMeter;


public abstract class OutputStreamWriter extends Node
{
	private Throwable throwable;
	private OutputStream outputStream;
	final private Formatter formatter;
	final private RateMeter rateMeter;
	
    protected OutputStreamWriter(Formatter formatter) throws Throwable
    {
        this(formatter,new RateMeter());
    }

    protected OutputStreamWriter(Formatter formatter,RateMeter rateMeter) throws Throwable
    {
        this.formatter=formatter;
        this.rateMeter=rateMeter;
    }

    public abstract OutputStream openOutputStream(long marker) throws Throwable;
    public abstract void closeOutputStream(OutputStream outputStream) throws Throwable;
	
    public void write(String text) throws IOException
    {
        byte[] bytes=text.getBytes(StandardCharsets.UTF_8);
        this.outputStream.write(bytes);
        this.rateMeter.add(bytes.length);
    }
    
    @Override
    public void beginGroup(long groupIdentifier) throws Throwable
    {
        try
        {
            this.outputStream=openOutputStream(groupIdentifier);
            write(this.formatter.beginDocument());
        }
        catch (Throwable t)
        {
            synchronized(this)
            {
                this.throwable = t;
            }
            throw t;
        }
    }

    @Override
    public void endGroup() throws Throwable
    {
        synchronized(this)
        {
            try
            {
                write(this.formatter.endDocument());
                closeOutputStream(this.outputStream);
                this.outputStream = null;
                return;
            }
            catch (Throwable t)
            {
                this.throwable = t;
            }
            throw this.throwable;
        }
    }

    @Override
    public void process(Packet container) throws Throwable
    {
        synchronized(this)
        {
            if (this.throwable==null)
            {
                try
                {
                    for (int i = 0; i < container.sizeOrType(); i++)
                    {
                        Object object = container.get(i);
                        if ((object != null) && (object instanceof LogEntry))
                        {
                            write(this.formatter.format((LogEntry)object));
                        }
                    }
                    return;
                }
                catch (Throwable t)
                {
                    this.throwable=t;
                }
            }
            throw throwable;
        }
    }
    
	@Override
	public void flush() throws Throwable
	{
		synchronized(this)
		{
		    if (this.throwable==null)
		    {
	            try
	            {
	                this.outputStream.flush();
	                return;
	            }
	            catch (Throwable t)
	            {
	                this.throwable=t;
	            }
		    }
		    throw this.throwable;
		}
	}

	public RateMeter getRateMeter()
	{
		return this.rateMeter;
	}
	
	public Throwable getThrowable()
	{
	    synchronized(this)
	    {
	        return this.throwable;
	    }
	}
}
