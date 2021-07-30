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
package org.nova.concurrent;

import org.nova.tracing.Trace;

public class Lock<KEY> implements AutoCloseable
{
	final LockState<KEY> slot;
	LockManager<KEY> lockManager;
	final Trace trace;
	
	Lock(LockManager<KEY> lockManager,LockState<KEY> lockObject,Trace trace)
	{
		this.trace=trace;
		this.lockManager=lockManager;
		this.slot=lockObject;
	}
	
	@Override
	public void close()
	{
		synchronized (this)
		{
			if (this.lockManager==null)
			{
				return;
			}
			this.lockManager.release(this.slot);
			this.trace.close();
			this.lockManager=null;
		}
	}

	public KEY getKey()
	{
	    return this.slot.key;
	}
}
