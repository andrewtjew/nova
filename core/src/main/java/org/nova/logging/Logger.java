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
package org.nova.logging;

import java.util.ArrayList;

import org.nova.tracing.Trace;

public abstract class Logger
{
    final private String category;
    
    public Logger(String category)
    {
        this.category=category;
    }
    
    public void log(Throwable throwable,String message,Item...items)
    {
        log(null,throwable!=null?Level.EXCEPTION:Level.NORMAL,this.category,throwable,message,items);
    }
	public void log(Throwable throwable)
	{
		log(throwable,null);
	}
	
	public void log(Level logLevel,String message,Item...items)
	{
		log(null,logLevel,this.category,null,message,items);
	}
    public void log(String message,Item...items)
    {
        log(Level.NORMAL,message,items);
    }

    public void log(Trace trace,String message,Item...items)
    {
        log(trace,trace.getThrowable()!=null?Level.EXCEPTION:Level.NORMAL,this.category,null,message,items);
    }
    public void log(Trace trace,Item...items)
    {
        log(trace,null,items);
    }
	
    public String getCategory()
	{
	    return this.category;
	}

    public boolean enabled=true;
    
    
    private void log(Trace trace,Level logLevel,String category,Throwable throwable,String message,Item[] items)
    {
        if (this.enabled)
        {
            write(trace,logLevel,category,throwable,message,items);
        }
    }
    
    abstract public void write(Trace trace,Level logLevel,String category,Throwable throwable,String message,Item[] items);
	
	public static Item[] toArray(ArrayList<Item> items)
	{
	    return items.toArray(new Item[items.size()]);
	}
}
