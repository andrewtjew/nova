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

import java.util.concurrent.atomic.AtomicLong;

import org.nova.tracing.Trace;

public class ConsoleLogger extends Logger
{
    final private Formatter formatter;
    final private AtomicLong number;
    public ConsoleLogger(String category,Formatter formatter)
    {
        super(category);
        this.formatter=formatter;
        this.number=new AtomicLong();
       
    }

    @Override
    public void write(Trace trace, Level logLevel, String category, Throwable throwable, String message, Item[] items)
    {
        try
        {
            String text=this.formatter.format(new LogEntry(this.number.getAndIncrement(),category,logLevel,System.currentTimeMillis(),throwable,trace,message,items));
            System.out.println(text);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

}
