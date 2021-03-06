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

public class Duplicator extends Node
{
	final private Node[] receivers;
	final private Object lock;
	public Duplicator(Object lock,Node...receivers)
	{
		this.receivers=receivers;
		this.lock=lock;
	}
	private void _flush() throws Throwable
	{
		for (Node receiver:this.receivers)
		{
			receiver.flush();
		}
	}
	@Override
	public void flush() throws Throwable
	{
		if (this.lock!=null)
		{
			synchronized (this.lock)
			{
				_flush();
			}
		}
		else
		{
			_flush();
		}
	}
	private void _endSegment() throws Throwable
	{
        for (Node receiver:this.receivers)
		{
			receiver.endGroup();
		}
	}
	@Override
	public void endGroup() throws Throwable
	{
		if (this.lock!=null)
		{
			synchronized (this.lock)
			{
			    _endSegment();
			}
		}
		else
		{
		    _endSegment();
		}
	}
    private void _send(Packet container) throws Throwable
    {
        for (Node receiver:this.receivers)
        {
            receiver.process(container);
        }
    }
    @Override
    public void process(Packet container) throws Throwable
    {
        if (this.lock!=null)
        {
            synchronized (this.lock)
            {
                _send(container);
            }
        }
        else
        {
            _send(container);
        }
    }

    private void _beginGroup(long groupSequenceNumber) throws Throwable
    {
        for (Node receiver:this.receivers)
        {
            receiver.beginGroup(groupSequenceNumber);
        }
    }
    @Override
    public void beginGroup(long marker) throws Throwable
    {
        if (this.lock!=null)
        {
            synchronized (this.lock)
            {
                _beginGroup(marker);
            }
        }
        else
        {
            _beginGroup(marker);
        }
    }
}
