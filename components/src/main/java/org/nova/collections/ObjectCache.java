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
import org.nova.collections.ContentCache.Entry;
import org.nova.collections.ContentCache.ValueSize;
import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.metrics.CountMeter;
import org.nova.tracing.Trace;

public class ObjectCache<KEY,VALUE>
{
	public static class Entry<KEY,VALUE>
	{
		final KEY key;
		final ValueSize<VALUE> valueSize;
		Entry<KEY,VALUE> previous;
		Entry<KEY,VALUE> next;
		
		public Entry(KEY key,ValueSize<VALUE> valueSize)
		{
			this.valueSize=valueSize;
			this.key=key;
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
	
	public static record ValueSize<VALUE>(VALUE value,long size){}
	
	public ObjectCache(int capacity)
	{
		this.entries=new HashMap<>();
		this.first=null;
		this.capacity=capacity;
	}
	
	final private HashMap<KEY,Entry<KEY,VALUE>> entries;
	
	final private int capacity;
	private Entry<KEY,VALUE> first;
	private Entry<KEY,VALUE> last;
	
    public ValueSize<VALUE> get(KEY key) throws Throwable
    {
        synchronized(this.entries)
        {
            Entry<KEY,VALUE> entry=this.entries.get(key);
            if (entry!=null)
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
                return entry.valueSize;
            }
        }
        if (Debug.ENABLE && DEBUG)
        {
            Debugging.log("Cache miss:"+key+",size="+this.entries.size());
        }
        return null;
    }

    public void put(KEY key,VALUE value)
    {
        put(key,new ValueSize<VALUE>(value,0));
    }
    
    boolean needEvicting(Entry<KEY,VALUE> entry)
    {
        if (this.entries.size()==0)
        {
            return false;
        }
        if ((this.capacity>0)&&(this.entries.size()==this.capacity))
        {
            return true;
        }
        return false;
    }
    
	public VALUE put(KEY key,ValueSize<VALUE> valueSize) 
    {
	    if (valueSize==null)
	    {
	        return null;
	    }
        Entry<KEY,VALUE> entry=new Entry<KEY,VALUE>(key,valueSize);
        synchronized(this.entries)
        {
            while (needEvicting(entry))
            {
                Entry<KEY,VALUE> removed=this.entries.remove(this.last.key);
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
			
			if (Debug.ENABLE && DEBUG)
			{
  	            Debugging.log("Cache remove:"+key+",size="+this.entries.size());
			}
			return node.valueSize.value;
		}
	}
   public Collection<Entry<KEY, VALUE>> getAllFromCache()
    {
        synchronized(this)
        {
            return this.entries.values();
        }
    }

	public void clear()
	{
		synchronized(this.entries)
		{
			this.entries.clear();
			this.last=this.first=null;
		}		
	}
	
	public int size()
	{
		synchronized(this.entries)
		{
			return this.entries.size();
		}
	}
}
