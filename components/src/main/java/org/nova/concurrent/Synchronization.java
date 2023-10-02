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

import org.nova.core.Condition;
import org.nova.core.NoThrowPredicate;
import org.nova.core.Predicate;
import org.nova.tracing.Trace;

public class Synchronization
{
    public static void wait(Object synchronizationObject)
    {
        try
        {
            synchronizationObject.wait();
        }
        catch (InterruptedException e)
        {
        }
    }
    public static void wait(Object synchronizationObject,long timeout)
    {
        if (timeout<0)
        {
            return;
        }
        try
        {
            synchronizationObject.wait(timeout);
            return;
        }
        catch (InterruptedException e)
        {
        }
    }

    public static void notifyAll(Object synchronizationObject,Condition condition) 
    {
        condition.set();
        synchronizationObject.notifyAll();
    }

    public static void notify(Object synchronizationObject,Condition condition)
    {
        condition.set();
        synchronizationObject.notify();
    }
    
    public static void waitFor(Object synchronizationObject,Predicate predicate)  throws Throwable
	{
		while (predicate.evaluate()==false)
		{
			try
			{
				synchronizationObject.wait();
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	public static boolean waitFor(Object synchronizationObject,long timeout,Predicate predicate) throws Throwable
	{
        if (timeout==Long.MAX_VALUE)
        {
            waitFor(synchronizationObject,predicate);
            return true;
        }
        else if (timeout<0)
        {
            return false;
        }
		long start=System.currentTimeMillis();
		while (predicate.evaluate()==false)
		{
            long remaining=timeout-(System.currentTimeMillis()-start);
            if (remaining<=0)
            {
                return false;
            }
			try
			{
				synchronizationObject.wait(remaining);
			}
			catch (InterruptedException e)
			{
			}
		}
		return true;
	}
	

	public static void waitForNoThrow(Object synchronizationObject,NoThrowPredicate predicate)
	{
		while (predicate.evaluate()==false)
		{
			try
			{
				synchronizationObject.wait();
			}
			catch (InterruptedException e)
			{
			}
		}
	}

	public static void waitForNoThrow(Trace trace,Object synchronizationObject,NoThrowPredicate predicate)
    {
	    trace.beginWait();
	    try
	    {
	       waitForNoThrow(synchronizationObject,predicate);
	    }
	    finally
	    {
	        trace.endWait();
	    }
    }

	public static boolean waitForNoThrow(Object synchronizationObject,long timeout,NoThrowPredicate predicate)
	{
        if (timeout==Long.MAX_VALUE)
        {
            waitForNoThrow(synchronizationObject,predicate);
            return true;
        }
        else if (timeout<0)
        {
            return false;
        }
		long start=System.currentTimeMillis();
		while (predicate.evaluate()==false)
		{
            long remaining=timeout-(System.currentTimeMillis()-start);
            if (remaining<=0)
            {
                return false;
            }
			try
			{
				synchronizationObject.wait(remaining);
			}
			catch (InterruptedException e)
			{
			}
		}
		return true;
	}

	public static boolean waitForNoThrow(Trace trace,Object synchronizationObject,long timeout,NoThrowPredicate predicate)
    {
	    trace.beginWait();
	    try
	    {
	        return waitForNoThrow(synchronizationObject,timeout,predicate);
	    }
	    finally
	    {
	        trace.endWait();
	    }
    }

	public static boolean waitFor(Trace trace,Object synchronizationObject,long timeout,Predicate predicate) throws Throwable
    {
        trace.beginWait();
        try
        {
            return waitFor(synchronizationObject,timeout,predicate);
        }
        finally
        {
            trace.endWait();
        }
    }

	public static void sleep(Object synchronizationObject,long timeout)
    {
	    if (timeout<0)
	    {
	        return;
	    }
        long start=System.currentTimeMillis();
        for (;;)
        {
            long remaining=timeout-(System.currentTimeMillis()-start);
            if (remaining<=0)
            {
                return;
            }
            try
            {
                synchronizationObject.wait(remaining);
            }
            catch (InterruptedException e)
            {
            }
        }
    }

	public static void sleep(Trace trace,Object synchronizationObject,long timeout)
    {
        trace.beginWait();
        try
        {
            sleep(synchronizationObject,timeout);
        }
        finally
        {
            trace.endWait();
        }
    }
}
