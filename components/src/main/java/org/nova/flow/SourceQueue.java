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
import org.nova.metrics.CountMeter;
import org.nova.metrics.LevelMeter;
import org.nova.testing.Debugging;

public class SourceQueue<ITEM>
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
    private Thread thread;
    private ArrayList<Packet> buffer;
    private Throwable throwable;
    private boolean stop;

    final private int sendSizeThreshold;
    final private long flushWait;
    final private long sendWait;
    final private long endSegmentWait;


    private Packet current;

    private boolean noWait = false;

    public SourceQueue(Node receiver, SourceQueueConfiguration configuration)
    {
        this.droppedMeter = new CountMeter();
        this.stalledMeter = new CountMeter();
        this.waitingMeter = new LevelMeter();
        this.receiver = receiver;
        this.sendSizeThreshold = configuration.sendSizeThreshold;
        this.flushWait = configuration.flushWaitMs;
        this.sendWait = configuration.sendWaitMs;
        this.stallSizeThreshold = configuration.stallSizeThreshold;
        this.stallWait = configuration.stallWaitMs;
        this.endSegmentWait = configuration.rollOverWaitMs;
        this.maxQueueSize = configuration.maxQueueSize;

        this.lock = new Object();
        this.buffer = new ArrayList<>();
        this.current = new Packet(this.sendSizeThreshold);
    }

    public void start()
    {
        synchronized (this.lock)
        {
            if (this.thread == null)
            {
                this.stop = false;
                this.thread = new Thread(() ->
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
        synchronized (this.lock)
        {
            if (this.thread == null)
            {
                return;
            }
            thread=this.thread;
            this.stop = true;
            this.lock.notifyAll();
            this.thread = null;
        }
        try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
        }
    }

    public void send(ITEM item)
    {
        synchronized (this.lock)
        {
            long size = this.waitingMeter.getLevel();
            if (size >= this.maxQueueSize)
            {
                this.droppedMeter.increment();
                return;
            }
            if (size >= this.stallSizeThreshold)
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
            if (this.stop == true)
            {
                return;
            }
            boolean added = this.current.add(item);
            if (added == false)
            {
                this.buffer.add(this.current);
                this.current = new Packet(this.sendSizeThreshold);
            }
            this.waitingMeter.increment();
            size = this.waitingMeter.getLevel(); // need latest.
            if ((size == 1) || (size == this.sendSizeThreshold))
            {
                this.lock.notify();
                if (TESTING)
                {
                    Debugging.log("SourceQueue:notify, queue size=" + size);
                }
            }
            else
            {
                if (TESTING)
                {
                    Debugging.log("SourceQueue:send, queue size=" + size);
                }
            }
        }
    }

    public void flush()
    {
        synchronized (this.lock)
        {
            if (this.stop == true)
            {
                return;
            }
            long size = this.waitingMeter.getLevel(); // need latest.
            if (size > 0)
            {
                this.buffer.add(this.current);
                this.current = new Packet(this.sendSizeThreshold);
            }
            this.buffer.add(Packet.FLUSH_PACKET);
            this.noWait = true;
            this.lock.notify();
        }
    }

    public void rollOver()
    {
        synchronized (this.lock)
        {
            if (this.stop == true)
            {
                return;
            }
            long size = this.waitingMeter.getLevel(); // need latest.
            if (size > 0)
            {
                this.buffer.add(this.current);
                this.current = new Packet(this.sendSizeThreshold);
            }
            this.buffer.add(null);
            this.noWait = true;
            this.lock.notify();
        }
    }

    private void main()
    {
        try
        {
            boolean flush = false;
            long lastRollOver = System.currentTimeMillis();
            this.receiver.beginGroup(lastRollOver);

            if (TESTING)
            {
                Debugging.log("SourceQueue:enter");
            }
            for (;;)
            {
                ArrayList<Packet> toSendBuffer = null;
                synchronized (this.lock)
                {
                    if (this.waitingMeter.getLevel() < this.sendSizeThreshold)
                    {
                        long timeout = Long.MAX_VALUE;
                        long start = System.currentTimeMillis();
                        if (this.endSegmentWait > 0)
                        {
                            timeout = this.endSegmentWait - (start - lastRollOver);
                        }
                        if (this.flushWait > 0)
                        {
                            if (flush)
                            {
                                if (timeout > this.flushWait)
                                {
                                    timeout = this.flushWait;
                                }
                            }
                        }
                        if (TESTING)
                        {
                            Debugging.log("SourceQueue:start wait, timeout=" + timeout);
                        }
                        if (Synchronization.waitForNoThrow(this.lock, timeout,() ->
                            {
                                return this.waitingMeter.getLevel() > 0 || this.stop || this.noWait;
                            } ))
                        {
                            if (TESTING)
                            {
                                Debugging.log("SourceQueue:end wait, queue size=" + this.waitingMeter.getLevel());
                            }
                            if (this.waitingMeter.getLevel() < this.sendSizeThreshold) 
                            {
                                long waited = System.currentTimeMillis() - start;
                                if (waited < this.sendWait)
                                {
                                    timeout = this.sendWait - waited;
                                    if (TESTING)
                                    {
                                        Debugging.log("SourceQueue:start wait for sendSizeThreshold, current queue size=" + waitingMeter.getLevel() + ", timeout=" + timeout);
                                    }
                                    Synchronization.waitForNoThrow(this.lock, timeout, () ->
                                    {
                                        return this.waitingMeter.getLevel() >= this.sendSizeThreshold || this.stop;
                                    } );
                                    if (TESTING)
                                    {
                                        Debugging.log("SourceQueue:end wait for sendSizeThreshold");
                                    }
                                }
                            }
                        }
                    }
                    if (this.stop)
                    {
                        return;
                    }
                    this.noWait = false;
                    if (this.waitingMeter.getLevel() > 0)
                    {
                        if (TESTING)
                        {
                            Debugging.log("SourceQueue:switch buffers");
                        }
                        toSendBuffer = this.buffer;
                        if (this.current.sizeOrType() > 0)
                        {
                            toSendBuffer.add(this.current);
                            this.current = new Packet(this.sendSizeThreshold);
                        }
                        this.buffer = new ArrayList<>(toSendBuffer.size() * 2);
                        this.waitingMeter.set(0);
                    }
                } //end synchronized
                if (toSendBuffer != null)
                {
                    int sendSize = 0;
                    for (Packet packet : toSendBuffer)
                    {
                        if ((packet != null) && (packet.sizeOrType() > 0))
                        {
                            sendSize += packet.sizeOrType();
                        }
                    }
                    Packet sendPacket = new Packet(sendSize);
                    if (TESTING)
                    {
                        Debugging.log("SourceQueue:send size=" + sendSize);
                    }

                    for (Packet packet : toSendBuffer)
                    {
                        if (packet == null)
                        {
                            if (sendPacket.sizeOrType() > 0)
                            {
                                this.receiver.process(sendPacket);
                                sendPacket = new Packet((int) this.waitingMeter.getLevel());
                            }
                            long rollOver = System.currentTimeMillis();
                            if (rollOver <= lastRollOver)
                            {
                                rollOver = lastRollOver + 1;
                            }
                            lastRollOver = rollOver;
                            this.receiver.endGroup();
                            this.receiver.beginGroup(rollOver);
                            flush = false;
                        }
                        else if (packet.sizeOrType() == Packet.FLUSH)
                        {
                            if (sendPacket.sizeOrType() > 0)
                            {
                                this.receiver.process(sendPacket);
                                sendPacket = new Packet((int) this.waitingMeter.getLevel());
                            }
                            if (flush)
                            {
                                this.receiver.flush();
                            }
                            flush = false;
                        }
                        else
                        {
                            for (int i = 0; i < packet.sizeOrType(); i++)
                            {
                                sendPacket.add(packet.get(i));
                            }
                            flush = true;
                        }
                    }
                    if (sendPacket.sizeOrType() > 0)
                    {
                        this.receiver.process(sendPacket);
                        flush = true;
                    }
                }
                else if (flush)
                {
                    if (TESTING)
                    {
                        Debugging.log("SourceQueue:flush");
                    }
                    this.receiver.flush();
                    flush = false;
                }
                else if (this.endSegmentWait > 0)
                {
                    long rollOver = System.currentTimeMillis();
                    if (rollOver - lastRollOver >= this.endSegmentWait)
                    {
                        if (TESTING)
                        {
                            Debugging.log("SourceQueue:rollover");
                        }
                        this.receiver.endGroup();
                        this.receiver.beginGroup(rollOver);
                        lastRollOver = rollOver;
                    }
                }
            }
        }
        catch (Throwable t)
        {
            synchronized (this.lock)
            {
                this.throwable = t;
            }
        }
    }

    public Throwable getThrowable()
    {
        synchronized (this.lock)
        {
            return this.throwable;
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

    public LevelMeter getWaitingMeter()
    {
        return waitingMeter;
    }
}
