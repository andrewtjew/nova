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
package org.nova.collections;


import org.nova.tracing.Trace;

public abstract class Resource implements AutoCloseable
{
	final private Pool<?> pool;
    final private long identifier;
	private Trace parent;
	private long recentlyUsedCount;
    private Thread activateThread;
    private StackTraceElement[] activateStackTrace;
    private boolean activated;
	
	public Resource(Pool<?> pool)
	{
		this.pool=pool;
		this.recentlyUsedCount=0;
		this.identifier=pool.getNextIdentifier();
	}
	
	void activate(Trace parent) throws Throwable
	{
		synchronized(this)
		{
		    this.activated=true;
			this.parent=parent;
			this.activateThread=Thread.currentThread();
	        if (pool.captureActiveStackTrace())
	        {
	            this.activateStackTrace=this.activateThread.getStackTrace();
	        }
			activate();
		}
	}

	abstract protected void activate() throws Throwable;

	abstract protected void park() throws Exception;
	
	public long getIdentifier()
	{
	    return this.identifier;
	}
	
	boolean canAddLast(long recentActivateMaximumCount)
	{
	    if (++this.recentlyUsedCount>=recentActivateMaximumCount)
	    {
	        this.recentlyUsedCount=0;
	        return true;
	    }
	    return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception
	{
		synchronized(this)
		{
			if (this.activated)
			{
                park();
                pool.release(this);
				this.parent=null;
				this.activateThread=null;
				this.activateStackTrace=null;
				this.activated=false;
			}
		}
	}
	
	public Trace getParent()
	{
        synchronized(this)
        {
            return this.parent;
        }
	}
	public Thread getActivateThread()
	{
	    synchronized(this)
	    {
	        return this.activateThread;
	    }
	}
	public StackTraceElement[] getActivateStackTrace()
	{
        synchronized(this)
        {
            return this.activateStackTrace;
        }
	}
}
