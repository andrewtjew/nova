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

import java.util.Collection;
import java.util.HashMap;

import org.nova.annotations.Description;
import org.nova.metrics.CountMeter;
import org.nova.testing.Debugging;
import org.nova.tracing.Trace;

abstract public class TemporaryCache<VALUE>
{
	private VALUE value;
	private long duration;
	private long lastRefresh;
	
	public TemporaryCache(long duration)
    {
	    this.duration=duration;
	    this.lastRefresh=System.currentTimeMillis()-this.duration*2;
    }

	public VALUE get(Trace parent) throws Throwable
	{
	    long now=System.currentTimeMillis();
	    synchronized(this)
	    {
	        long span=now-this.lastRefresh;
	        if (span>=this.duration)
	        {
	            this.value=load(parent);
	            this.lastRefresh=now;
	        }
	        return this.value;
	    }
	}
    public VALUE getFromCache(Trace parent) throws Throwable
    {
        synchronized(this)
        {
            if (this.value==null)
            {
                this.value=load(parent);
            }
            return this.value;
        }
    }
    abstract protected VALUE load(Trace parent) throws Throwable; //Don't return null. return new ValueSize(null) instead. 
}
