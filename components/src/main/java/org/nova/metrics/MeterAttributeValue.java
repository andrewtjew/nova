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

import org.nova.pathStore.AttributeValue;
import org.nova.pathStore.Store;

public class MeterAttributeValue extends AttributeValue<MeterAttribute,Object> 
{
    
    public MeterAttributeValue(String description,StackTraceElement[] stackTrace, String path,Object value) throws Exception
    {
        super(Store.toPathElements(path), new MeterAttribute(description,stackTrace), value);
    }
    @SuppressWarnings("unchecked")
    public <T> T getMeter()
    {
        return (T)get();
    }
    
    public CountMeter getCountMeter()
    {
        if (get() instanceof CountMeter)
        {
            return (CountMeter)get();
        }
        return null;
    }
    public RateMeter getRateMeter()
    {
        if (get() instanceof RateMeter)
        {
            return (RateMeter)get();
        }
        return null;
    }
    public LevelMeter getLevelMeter()
    {
        if (get() instanceof LevelMeter)
        {
            return (LevelMeter)get();
        }
        return null;
    }
    public LongValueMeter getLongValueMeter()
    {
        if (get() instanceof LongValueMeter)
        {
            return (LongValueMeter)get();
        }
        return null;
    }
    public RecentSourceEventMeter getRecentStateEventMeter()
    {
        if (get() instanceof RecentSourceEventMeter)
        {
            return (RecentSourceEventMeter)get();
        }
        return null;
    }
}
