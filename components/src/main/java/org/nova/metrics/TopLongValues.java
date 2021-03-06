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
package org.nova.metrics;

import java.util.TreeSet;

public class TopLongValues
{
    final TreeSet<Long> sorted;
    final int count;
    long total;
    
    public TopLongValues(int count)
    {
        this.count=count;
        this.sorted=new TreeSet<>();
    }
    
    public void update(long value)
    {
        if (this.count==0)
        {
            return;
        }
        if (this.sorted.size()<this.count)
        {
            this.sorted.add(value);
            return;
        }
        if (this.sorted.first().longValue()<value)
        {
            this.sorted.remove(this.sorted.first());
            this.sorted.add(value);
        }
        this.total+=value;
    }
    public boolean isInsideTop(long value)
    {
        if (this.sorted.size()==0)
        {
            return false;
        }
        return value>this.sorted.first().longValue();
            
    }
    public boolean isInTop(long value)
    {
        if (this.sorted.size()==0)
        {
            return false;
        }
        return value>=this.sorted.first().longValue();
            
    }
    public long getTotal()
    {
        return total;
    }
}
