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
package org.nova.tracing;

import org.nova.collections.RingBuffer;

public class TraceBuffer extends RingBuffer<Trace>
{
    public TraceBuffer(int capacity)
    {
        super(new Trace[capacity]);
    }
    /*
    private int readIndex;
    private int writeIndex;
    private int size;
    
    final private Trace[] array;
    final private int length;
    
    public TraceBuffer(int capacity)
    {
        this.array=new Trace[capacity];
        this.length=array.length;
    }
    public void add(Trace trace)
    {
        this.array[this.writeIndex]=trace;
        this.writeIndex=(this.writeIndex+1)%this.length;
        if (this.size<this.length)
        {
            this.size++;
        }
        else
        {
            this.readIndex=(this.readIndex+1)%this.length;
        }
    }
    public int size()
    {
        return this.size;
    }
    public Trace[] getSnapshot()
    {
        int size=size();
        Trace[] snapshot=new Trace[size];
        for (int i=0;i<size;i++)
        {
            snapshot[i]=this.array[(this.readIndex+i)%this.length];
        }
        return snapshot;
    }
    */
    public Trace[] getSnapshotAsArray()
    {
        int size=size();
        Trace[] snapshot=new Trace[size];
        for (int i=0;i<size;i++)
        {
            snapshot[i]=this.array[(this.readIndex+i)%this.length];
        }
        return snapshot;
    }
}
