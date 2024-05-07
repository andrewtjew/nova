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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.nova.concurrent.TimeBase;
import org.nova.concurrent.TimerScheduler;
import org.nova.concurrent.TimerTask;
import org.nova.core.Pair;
import org.nova.core.MultiException;
import org.nova.tracing.Trace;


public class ExpireMap<KEY,VALUE>
{
	
	final private HashMap<KEY,VALUE>[] generations;
	final private TimerScheduler timerScheduler;
	final private long maxAgeMs;
	final private String category;
	final private Expire<KEY, VALUE> expirable;
	private int generationIndex=0;
	private TimerTask task;
	

	@SuppressWarnings("unchecked")
	public ExpireMap(String categoryOverride,TimerScheduler timerScheduler,long maxAgeMs,int generations,Expire<KEY, VALUE> expirable) throws Exception
	{
		this.category=categoryOverride!=null?categoryOverride:ExpireMap.class.getSimpleName();
		this.generations=(HashMap<KEY,VALUE>[]) new HashMap<?,?>[generations];
		this.timerScheduler=timerScheduler;
		this.maxAgeMs=maxAgeMs;
		this.expirable=expirable;
	}

	public ExpireMap(String categoryOverride,TimerScheduler timerScheduler,long maxAgeMs,int generations) throws Exception
	{
		this(categoryOverride,timerScheduler,maxAgeMs,generations,null);
	}
	
	public void start() throws Exception
	{
		synchronized(this)
		{
			if (this.task!=null)
			{
				throw new Exception();
			}
			long checkInterval=this.maxAgeMs/(this.generations.length-1);
			this.task=timerScheduler.schedule(this.category+"@Timer",TimeBase.FREE,checkInterval,checkInterval,(Trace trace,TimerTask task)->
			{
				expire(trace);
			});
		}
	}
	
	public void stop() throws Exception
	{
		synchronized (this)
		{
			if (this.task==null)
			{
				throw new Exception();
			}
			this.task.cancel();
			this.task=null;
		}
	}
	
	public void put(KEY key,VALUE value)
	{
		synchronized(this)
		{
			for (int i=1;i<this.generations.length;i++)
			{
				int index=(this.generationIndex-i+this.generations.length)%this.generations.length;
				HashMap<KEY,VALUE> generation=this.generations[index];
				if (generation!=null)
				{
					if (generation.remove(key)!=null)
					{
						break;
					}
				}
			}

			HashMap<KEY,VALUE> current=this.generations[this.generationIndex];
			if (current==null)
			{
				current=new HashMap<>();
				this.generations[this.generationIndex]=current;
			}
			current.put(key, value);
		}
	}

    public VALUE update(KEY key)
    {
        synchronized (this)
        {
            for (int i=0;i<this.generations.length;i++)
            {
                int index=(this.generationIndex-i+this.generations.length)%this.generations.length;
                HashMap<KEY,VALUE> generation=this.generations[index];
                if (generation!=null)
                {
                    VALUE value=generation.remove(key);
                    if (value!=null)
                    {
                        HashMap<KEY,VALUE> current=this.generations[this.generationIndex];
                        if (current==null)
                        {
                            current=new HashMap<>();
                            this.generations[this.generationIndex]=current;
                        }
                        current.put(key, value);
                        return value;
                    }
                }
            }
            return null;
        }
    }

	public VALUE get(KEY key)
	{
		synchronized (this)
		{
			for (int i=0;i<this.generations.length;i++)
			{
				int index=(this.generationIndex-i+this.generations.length)%this.generations.length;
				HashMap<KEY,VALUE> generation=this.generations[index];
				if (generation!=null)
				{
					VALUE value=generation.get(key);
					if (value!=null)
					{
						return value;
					}
				}
			}
			return null;
		}
	}

	public boolean containsKey(KEY key)
	{
		synchronized (this)
		{
			for (int i=0;i<this.generations.length;i++)
			{
				int index=(this.generationIndex-i+this.generations.length)%this.generations.length;
				HashMap<KEY,VALUE> generation=this.generations[index];
				if (generation!=null)
				{
					if (generation.containsKey(key))
					{
						return true;
					}
				}
			}
			return false;
		}
	}
	
	public VALUE remove(KEY key)
	{
		synchronized (this)
		{
			for (int i=0;i<this.generations.length;i++)
			{
				int index=(this.generationIndex-i+this.generations.length)%this.generations.length;
				HashMap<KEY,VALUE> generation=this.generations[index];
				if (generation!=null)
				{
					VALUE value=generation.remove(key);
					if (value!=null)
					{
						return value;
					}
				}
			}
			return null;
		}
	}

	public int size()
	{
        int size=0;
	    synchronized(this)
	    {
            for (int i=0;i<this.generations.length;i++)
            {
                HashMap<KEY,VALUE> generation=this.generations[i];
                if (generation!=null)
                {
                    size+=generation.size();
                }
            }
	    }
        return size;
	}
	
	public Collection<VALUE> values()
	{
        int size=0;
        synchronized(this)
        {
            for (int i=0;i<this.generations.length;i++)
            {
                HashMap<KEY,VALUE> generation=this.generations[i];
                if (generation!=null)
                {
                    size+=generation.size();
                }
            }
            List<VALUE> values=new ArrayList<>(size);
            for (int i=0;i<this.generations.length;i++)
            {
                HashMap<KEY,VALUE> generation=this.generations[i];
                if (generation!=null)
                {
                    values.addAll(generation.values());
                }
            }
            return values;
        }
	}

	public List<Pair<KEY,VALUE>> getEntries()
    {
        int size=0;
        synchronized(this)
        {
            for (int i=0;i<this.generations.length;i++)
            {
                HashMap<KEY,VALUE> generation=this.generations[i];
                if (generation!=null)
                {
                    size+=generation.size();
                }
            }
            List<Pair<KEY,VALUE>> entries=new ArrayList<>(size);
            for (int i=0;i<this.generations.length;i++)
            {
                HashMap<KEY,VALUE> generation=this.generations[i];
                if (generation!=null)
                {
                    for (Entry<KEY, VALUE> entry:generation.entrySet())
                    {
                        entries.add(new Pair<KEY,VALUE>(entry.getKey(),entry.getValue()));
                    }
                }
            }
            return entries;
        }
    }
	
	private void expire(Trace parent) throws Throwable
	{
		HashMap<KEY,VALUE> oldestGeneration=null;
		synchronized (this)
		{
		    this.generationIndex=(this.generationIndex+1)%this.generations.length;
			oldestGeneration=this.generations[this.generationIndex];
			this.generations[this.generationIndex]=null;
		}
		if (oldestGeneration==null)
		{
			return;
		}
		if (this.expirable==null)
		{
			return;
		}
		ArrayList<Throwable> throwables=new ArrayList<>();
		for (Entry<KEY, VALUE> entry:oldestGeneration.entrySet())
		{
			try
			{
				this.expirable.expire(parent, entry.getKey(),entry.getValue());
			}
			catch (Throwable t)
			{
				throwables.add(t);
			}
		}
		if (throwables.size()>0)
		{
			throw new MultiException(throwables);
		}
		
	}
}
