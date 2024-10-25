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

abstract public class ContentCache<KEY,VALUE>
{
	final private long maxAgeMs;
	final private long contentCapacity;
	private long totalContentSize;
	private long freeMemoryCapacity;
	
	public static class Entry<KEY,VALUE>
	{
		final KEY key;
		final ValueSize<VALUE> valueSize;
		Entry<KEY,VALUE> previous;
		Entry<KEY,VALUE> next;
		
        long accessed;
        long created;
		public Entry(KEY key,ValueSize<VALUE> valueSize)
		{
			this.valueSize=valueSize;
			this.key=key;
			this.created=this.accessed=System.currentTimeMillis();
		}
		public KEY getKey()
		{
		    return this.key;
		}
		public VALUE getValue()
		{
		    return valueSize.value;
		}
	}
    public static boolean DEBUG=true;
	
	public static class ValueSize<VALUE>
	{
		final VALUE value;
		final long size;
        public ValueSize(VALUE value,long size)
        {
            this.value=value;
            this.size=size;
        }
        public ValueSize(VALUE value)
        {
            this.value=value;
            this.size=0;
        }
        public VALUE value()
        {
            return this.value;
        }
	}

	public ContentCache()
    {
	    this(0,0,0,1024L*1024L*64L);
    }

	public ContentCache(int capacity)
    {
        this(capacity,0,0,1024L*1024L*64L);
    }
	
	public ContentCache(int capacity,long maxAgeMs,long contentCapacity,long freeMemoryCapacity)
	{
		this.hits=new CountMeter();
		this.misses=new CountMeter();
		this.ageMisses=new CountMeter();
		this.sizeEvicts=new CountMeter();
		this.entries=new HashMap<>();
		this.first=null;
		this.capacity=capacity;
		this.maxAgeMs=maxAgeMs;
		this.contentCapacity=contentCapacity;
		this.freeMemoryCapacity=freeMemoryCapacity;
		        
	}
	
	final private HashMap<KEY,Entry<KEY,VALUE>> entries;
	
	@Description("Cache hits.")
	final private CountMeter hits;
	
	@Description("Cache misses due to key not found")
	final private CountMeter misses;
	
	@Description("Cache misses due to max age exceeded.")
	final private CountMeter ageMisses;
	
	@Description("Cache evicts due to max size exceeded.")
	final private CountMeter sizeEvicts;
	
	final private int capacity;
	private Entry<KEY,VALUE> first;
	private Entry<KEY,VALUE> last;
	

	public VALUE get(KEY key) throws Throwable
	{
		return get(null,key);
	}
	public VALUE get(Trace parent,KEY key) throws Throwable
	{
	    ValueSize<VALUE> valueSize=getFromCache(key);
	    if (valueSize!=null)
	    {
	        return valueSize.value;
	    }
		return fill(parent,key);
	}

    public VALUE getValueFromCache(KEY key) throws Throwable
    {
        ValueSize<VALUE> valueSize=getFromCache(key);
        if (valueSize!=null)
        {
            return valueSize.value;
        }
        return null;
    }

	public ValueSize<VALUE> getFromCache(KEY key) throws Throwable
    {
        synchronized(this.entries)
        {
            Entry<KEY,VALUE> entry=this.entries.get(key);
            if (entry!=null)
            {
                long now=System.currentTimeMillis();
                if ((this.maxAgeMs<=0)||(now-entry.created<this.maxAgeMs))
                {
                    if (entry.previous!=null)
                    {
                        entry.previous.next=entry.next;
                        if (entry.next!=null)
                        {
                            entry.next.previous=entry.previous;
                        }
                        else
                        {
                            this.last=entry.previous;
                        }
                        entry.previous=null;
                        entry.next=this.first;
                        this.first=entry;
                    }
                    entry.accessed=now;
                    this.hits.increment();
                    return entry.valueSize;
                }
                remove(key);
            }
        }
        if (Debugging.ENABLE && DEBUG)
        {
            Debugging.log("Cache miss:"+key+",size="+this.entries.size());
        }
        this.misses.increment();
        return null;
    }

	public VALUE fill(Trace parent,KEY key) throws Throwable
	{
		ValueSize<VALUE> valueSize=load(parent,key);
		if (valueSize==null)
		{
		    return null;
		}
		VALUE value=put(parent,key,valueSize);
        if (Debugging.ENABLE && DEBUG)
        {
            Debugging.log("Cache fill:"+key+",size="+this.entries.size());
        }
        return value;
	}
    public void put(Trace parent,KEY key,VALUE value) throws Throwable
    {
        put(parent,key,new ValueSize<VALUE>(value,0));
    }
    
    boolean needEvicting(Entry<KEY,VALUE> entry)
    {
        if (this.entries.size()==0)
        {
            return false;
        }
        if ((this.contentCapacity>0)&&(this.totalContentSize+entry.valueSize.size>this.contentCapacity))
        {
            return true;
        }
        if ((this.capacity>0)&&(this.entries.size()==this.capacity))
        {
            return true;
        }
        if (this.freeMemoryCapacity>0)
        {
            long freeMemory=Runtime.getRuntime().freeMemory();
            if (freeMemory<this.freeMemoryCapacity)
            {
                Runtime.getRuntime().gc();
//                freeMemory=Runtime.getRuntime().freeMemory();
//                if (freeMemory<this.freeMemoryCapacity)
//                {
//                    return true;
//                }
            }
        }
        
        return false;
    }
    
	public VALUE put(Trace parent,KEY key,ValueSize<VALUE> valueSize) throws Throwable
    {
	    if (valueSize==null)
	    {
	        throw new Exception("No value for key:"+key);
	    }
        Entry<KEY,VALUE> entry=new Entry<KEY,VALUE>(key,valueSize);
        synchronized(this.entries)
        {
            while (needEvicting(entry))
            {
                Entry<KEY,VALUE> removed=this.entries.remove(this.last.key);
                this.onEvict(parent,removed.key,removed.valueSize.value);
                this.totalContentSize-=this.last.valueSize.size;
                this.sizeEvicts.increment();
                this.last=last.previous;
                if (this.last!=null)
                {
                    this.last.next=null;
                }
                else //last==null
                {
                    this.first=null;
                    break;
                }
            }
            this.entries.put(key, entry);
            this.totalContentSize+=entry.valueSize.size;
            entry.next=this.first;
            if (this.first!=null)
            {
                this.first.previous=entry;
            }
            this.first=entry;
            if (this.last==null)
            {
                this.last=entry;
            }
        }
        return valueSize.value;
    }
	
	public VALUE remove(KEY key)
	{
		synchronized(this.entries)
		{
			Entry<KEY,VALUE> node=this.entries.remove(key);
			if (node==null)
			{
				return null;
			}
			this.totalContentSize-=node.valueSize.size;
			if (node.previous==null)
			{
				this.first=node.next;
				if (this.first!=null)
				{
				    this.first.previous=null;
				}
			}
			else
			{
				node.previous.next=node.next;
			}
			if (node.next==null)
			{
				this.last=node.previous;
                if (this.last!=null)
                {
                    this.last.next=null;
                }
			}
			else
			{
				node.next.previous=node.previous;
			}
			
			if (Debugging.ENABLE && DEBUG)
			{
  	            Debugging.log("Cache remove:"+key+",size="+this.entries.size());
			}
			return node.valueSize.value;
		}
	}
	
	public void clear()
	{
		synchronized(this.entries)
		{
			this.entries.clear();
			this.last=this.first=null;
			this.totalContentSize=0;
		}		
	}
	
	public void resetMeters()
	{
		this.hits.set(0);
		this.misses.set(0);
		this.ageMisses.set(0);
	}
	
	public int size()
	{
		synchronized(this.entries)
		{
			return this.entries.size();
		}
	}
	public long getTotalContentSize()
	{
		synchronized(this.entries)
		{
			return this.totalContentSize;
		}
	}
	
	public CountMeter getHits()
	{
		return this.hits;
	}
	
	public CountMeter getMisses()
	{
		return this.misses;
	}
	public CountMeter getAgeMisses()
	{
		return this.ageMisses;
	}
	public CountMeter getSizeEvicts()
	{
		return this.sizeEvicts;
	}
	public Collection<Entry<KEY, VALUE>> getAllFromCache()
	{
	    return this.entries.values();
	}
	
	
    abstract protected ValueSize<VALUE> load(Trace parent,KEY key) throws Throwable; //Don't return null. return new ValueSize(null) instead. 
    abstract protected void onEvict(Trace parent,KEY key,VALUE value) throws Throwable;  
}
