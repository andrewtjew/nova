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
package org.nova.logging;

import org.nova.flow.Node;
import org.nova.flow.Packet;
import org.nova.tracing.Trace;

public class NodeLogger extends Logger
{
    final private Node[] receivers;
    final private ThrowableEvents throwablesLog; 
    private long number;
    
    public NodeLogger(String category,Node...receivers)
    {
        super(category);
        this.receivers=receivers;
        this.throwablesLog=new ThrowableEvents(); 
                
    }

    @Override
    public void write(Trace trace, Level logLevel, String category, Throwable throwable, String message, Item[] items)
    {
        synchronized (this)
        {
            Packet packet=new Packet(1);
            packet.add(new LogEntry(this.number++,category,logLevel,System.currentTimeMillis(),throwable,trace,message,items));
            for (Node receiver:this.receivers)
            {
                try
                {
                    receiver.process(packet);
                }
                catch (Throwable t)
                {
                    this.throwablesLog.log(t);
                }
            }
        }
    }
    public ThrowableEvents getThrowablesLog()
    {
        synchronized(this)
        {
            return this.throwablesLog;
        }
    }

}
