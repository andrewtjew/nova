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

import java.util.HashMap;

import org.nova.metrics.LevelMeter;

public class SingleWaiterBoard
{
    private final HashMap<String,Message> messages;
    
    private final LevelMeter actives;
    
    public SingleWaiterBoard()
    {
        this.messages=new HashMap<>();
        this.actives=new LevelMeter();
        
    }
    
    public boolean post(String name,Object content,long lifeTimeMs)
    {
        Message message;
        synchronized(this)
        {
            message=this.messages.get(name);
            if (message!=null)
            {
                return false;
            }
            message=new Message(content);
            this.messages.put(name, message);
            this.actives.increment();
            this.notifyAll();
        }
        synchronized(message)
        {
            final Message m=message;
            if (Synchronization.waitForNoThrow(message,lifeTimeMs, ()->{return m.receivedMs!=null;})==false)
            {
                synchronized(this)
                {
                    if (message.receivedMs!=null)
                    {
                        return true;
                    }
                    this.messages.remove(name);
                    this.actives.decrement();
                    return false;
                }                
            }
            return true;
        }
    }

    public Message waitForMessage(String name,long waitMs)
    {
        Message message;
        synchronized (this)
        {
            if (Synchronization.waitForNoThrow(this,waitMs, ()->{return this.messages.containsKey(name);})==false)
            {
                return null;
            }                
            message=this.messages.remove(name);
            this.actives.decrement();
        }
        synchronized(message)
        {
            message.receivedMs=System.currentTimeMillis();
            message.notify();
        }
        return message;
    }
    
}
