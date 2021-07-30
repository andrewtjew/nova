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
package org.nova.test;

import java.util.concurrent.atomic.AtomicLong;
@SuppressWarnings("unused")
public class Testing
{
    public final static boolean ENABLED=true;
    public final static boolean PRINT=true;
    public final static boolean OPRINT=true;
	public final static boolean TEST_TRACE_SERVER_PRINT=false;

    public static void println(String text)
    {
        if (ENABLED&&PRINT)
        {
            System.out.println(text);
        }
    }
    public static void print(String text)
    {
        if (ENABLED&&PRINT)
        {
            System.out.print(text);
        }
    }
    public static void printf(String format,Object...args)
    {
        if (ENABLED&&PRINT)
        {
            System.out.printf(format,args);
        }
    }

    private static volatile long TIME_BASE=System.currentTimeMillis();
    private static AtomicLong LAST=new AtomicLong(System.currentTimeMillis());
    
    public static void oset(long base)
    {
        if (ENABLED&&OPRINT)
        {
            TIME_BASE=base;
            LAST.set(base);
        }
    }
    public static void oset()
    {
        if (ENABLED&&OPRINT)
        {
            oset(System.currentTimeMillis());
        }
    }
    
    public static void oprintln(String text)
    {
        if (ENABLED&&OPRINT)
        {
            long now=System.currentTimeMillis();
            long baseOffset=now-TIME_BASE;
            long lastOffset=now-LAST.getAndSet(now);
            System.out.println(baseOffset+":"+lastOffset+":"+text);
        }
    }
    public static void oprint(String text)
    {
        if (ENABLED&&OPRINT)
        {
            long now=System.currentTimeMillis();
            long baseOffset=now-TIME_BASE;
            long lastOffset=now-LAST.getAndSet(now);
            System.out.print(baseOffset+":"+lastOffset+":"+text);
        }
    }
    public static void oprintf(String format,Object...args)
    {
        if (ENABLED&&OPRINT)
        {
            long now=System.currentTimeMillis();
            long baseOffset=now-TIME_BASE;
            long lastOffset=now-LAST.getAndSet(now);
            System.out.printf(baseOffset+":"+lastOffset+":"+format,args);
        }
    }

}
