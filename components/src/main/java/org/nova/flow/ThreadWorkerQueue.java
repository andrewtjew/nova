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
package org.nova.flow;

import java.util.ArrayList;
import org.nova.concurrent.Synchronization;
import org.nova.logging.ThrowableEvents;
import org.nova.metrics.CountMeter;
import org.nova.metrics.LevelMeter;
import org.nova.testing.Debugging;

public class ThreadWorkerQueue extends Node
{
    final private static boolean TESTING=false;
	final private CountMeter droppedMeter;
	final private CountMeter stalledMeter;
    final private LevelMeter waitingMeter;
    final private int maxQueueSize;
    final private long stallWait;
    final private int stallSizeThreshold;
    final private Node receiver;
    final private Object lock;
    final private ThrowableEvents throwablesLog;
    private Thread thread;
    private ArrayList<Packet> queue;
    private boolean stop;

    final private LevelMeter threadInUseMeter;
    final private int id;
    

	
	public ThreadWorkerQueue(Node receiver,long stallWait,int stallSizeThreshold,int maxQueueSize,int id,CountMeter droppedMeter,CountMeter stalledMeter,LevelMeter threadInUseMeter,LevelMeter waitingMeter)
	{
	    this.throwablesLog=new ThrowableEvents();
		this.droppedMeter=droppedMeter;
		this.stalledMeter=stalledMeter;
		this.threadInUseMeter=threadInUseMeter;
		this.waitingMeter=waitingMeter;
		this.receiver=receiver;
		this.stallWait=stallWait;
		this.stallSizeThreshold=stallSizeThreshold;
		this.maxQueueSize=maxQueueSize;
		this.lock=new Object();
		this.queue=new ArrayList<>();
		this.id=id;
	}

	public void start()
	{
		synchronized(this.lock)
		{
			if (this.thread==null)
			{
				this.stop=false;
				this.thread=new Thread(()->
				{
					main();
				});
				this.thread.start();
			}
		}
	}
	public void stop() 
	{
	    Thread thread;
		synchronized(this.lock)
		{
		    if (this.thread==null)
		    {
		        return;
		    }
		    thread=this.thread;
			this.stop=true;
			this.lock.notify();
			this.thread=null;
		}
	    try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
        }
	}

    public void flush()
    {
        synchronized (this.lock)
        {
            if (this.stop==true)
            {
                return;
            }
            this.queue.add(Packet.FLUSH_PACKET);
            this.lock.notify();
        }
    }
    
	private void main() 
	{
        this.threadInUseMeter.increment();
        for (;;)
        {
    		try
    		{
    		    if (TESTING)
    		    {
    		        Debugging.log("ThreadWorkerQueue="+this.id+":enter");
    		    }
	            ArrayList<Packet> old=null;
				synchronized (this.lock)
				{
		            if (TESTING)
		            {
		                Debugging.log("ThreadWorkerQueue="+this.id+":start wait");
		            }
                    this.threadInUseMeter.decrement();
					Synchronization.waitForNoThrow(this.lock, ()->{return this.queue.size()>0||this.stop;});
                    this.threadInUseMeter.increment();
                    if (TESTING)
                    {
                        Debugging.log("ThreadWorkerQueue="+this.id+":end wait");
                    }
					if (this.stop)
					{
		                this.threadInUseMeter.decrement();
						return;
					}
					old=this.queue;
					this.queue=new ArrayList<>(old.size()*2);
				}
				for (Packet packet:old)
			    {
				    int size=packet.sizeOrType();
				    if (size<0)
				    {
                        if (size==Packet.BEGIN_SEGMENT)
                        {
                            if (TESTING)
                            {
                                Debugging.log("ThreadWorkerQueue="+this.id+":receiver.beginSegment="+packet.getSegmentMarker());
                            }
                            this.receiver.beginGroup((long)packet.getSegmentMarker());
                        }
                        else if (size==Packet.END_SEGMENT)
                        {
                            if (TESTING)
                            {
                                Debugging.log("ThreadWorkerQueue="+this.id+":receiver.endSegment");
                            }
                            this.receiver.endGroup();
                        }
                        else if (size==Packet.FLUSH)
                        {
                            this.receiver.flush();
                            if (TESTING)
                            {
                                Debugging.log("ThreadWorkerQueue="+this.id+":receiver.flush");
                            }
                        }
				    }
			        else 
                    {
	                    this.waitingMeter.add(-size);
                        this.receiver.process(packet);
                    }
			    }
			}
    		catch (Throwable t)
    		{
  			    this.throwablesLog.log(t);
    		}
        }
	}
	
	public ThrowableEvents getThrowablesLog()
	{
        return this.throwablesLog;
	}

    @Override
    public void process(Packet packet) throws Throwable
    {
        synchronized (this.lock)
        {
            if (this.stop==true)
            {
                return;
            }
            long size=this.waitingMeter.getLevel();
            if (size>=this.maxQueueSize)
            {
                this.droppedMeter.increment();
                return;
            }
            if (size>=this.stallSizeThreshold)
            {
                try
                {
                    this.lock.wait(this.stallWait);
                }
                catch (InterruptedException e)
                {
                }
                this.stalledMeter.increment();
            }
            this.queue.add(packet);
            if (packet.sizeOrType()>0)
            {
                this.waitingMeter.add(packet.sizeOrType());
            }
            this.lock.notify();
            if (TESTING)
            {
                Debugging.log("ThreadWorkerQueue="+this.id+":notify, queue size="+this.queue.size());
            }
        }
    }

    @Override
    public void beginGroup(long marker) throws Throwable
    {
        synchronized (this.lock)
        {
            int size=this.queue.size();
            if (size>=this.stallSizeThreshold)
            {
                try
                {
                    this.lock.wait(this.stallWait);
                }
                catch (InterruptedException e)
                {
                }
                this.stalledMeter.increment();
            }
            if (this.stop==true)
            {
                return;
            }
            this.queue.add(Packet.BeginSegmentPacket(marker));
            this.lock.notify();
        }
    }

    @Override
    public void endGroup() throws Throwable
    {
        synchronized (this.lock)
        {
            int size=this.queue.size();
            if (size>=this.stallSizeThreshold)
            {
                try
                {
                    this.lock.wait(this.stallWait);
                }
                catch (InterruptedException e)
                {
                }
                this.stalledMeter.increment();
            }
            if (this.stop==true)
            {
                return;
            }
            this.queue.add(Packet.END_SEGMENT_PACKET);
            this.lock.notify();
        }
    }
    public Node getReceiver()
    {
        return this.receiver;
    }
    
    public CountMeter getDroppedMeter()
    {
        return droppedMeter;
    }

    public CountMeter getStalledMeter()
    {
        return stalledMeter;
    }

    public LevelMeter getThreadInUseMeter()
    {
        return threadInUseMeter;
    }
    public LevelMeter getWaitingMeter()
    {
        return this.waitingMeter;
    }

    
}
