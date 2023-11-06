/*******************************************************************************
 * Copyright (C) 2016-2019 Kat Fung Tjew
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
package org.nova.metrics;

public class RateMeter 
{
    private RateSample lastSample;
    
    private long markNs;
    private long samples;
    private long totalCount;
	
	public RateMeter()
	{
		this.markNs=System.nanoTime();
	}
	
	public void add(long count)
	{
		synchronized (this)
		{
			this.samples+=count;
			this.totalCount+=count;
		}
	}
	
	public void increment()
	{
		synchronized (this)
		{
			this.samples++;
			this.totalCount++;
		}
	}
	
	public long getTotalCount()
	{
		synchronized (this)
		{
			return this.totalCount;
		}
	}
    
    public RateSample sample()
    {
        synchronized(this)
        {
            return sample(1);
        }
    }
    
    public RateSample sample(double minimalResetDurationS)
    {
        long nowNs=System.nanoTime();
        synchronized (this)
        {
            long durationNs=nowNs-this.markNs;
            if (durationNs<=0)
            {
                return this.lastSample;
            }
            if (this.lastSample!=null)
            {
                this.lastSample.lastSample=null;
            }
            RateSample sample=new RateSample(this.lastSample, durationNs, this.samples,this.totalCount);
            if (durationNs>(long)(minimalResetDurationS*1.0e9))
            {
                this.lastSample=sample;
                this.markNs=nowNs;
                this.samples=0;
            }
            return sample;
        }
    }
	
}
