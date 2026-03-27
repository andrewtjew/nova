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
package org.nova.sqldb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class GeneratedKeys
{
    private final List<Object> keys;
    GeneratedKeys(List<Object> keys)
    {
        this.keys=keys;
    }
    @SuppressWarnings("unchecked")
    public <TYPE> TYPE get(int index)
    {
        return (TYPE)this.keys.get(index);
    }
    public BigDecimal getBigDecimal(int index)
    {
        return (BigDecimal)this.keys.get(index);
    }
    public BigInteger getBigInteger(int index)
    {
        return (BigInteger)this.keys.get(index);
    }
    public long getLong(int index)
    {
        return (long)this.keys.get(index);
    }
    public int getInt(int index)
    {
        return (int)this.keys.get(index);
    }
    public long getAsLong(int index) throws Exception
    {
        Object key=this.keys.get(index);
        if (key instanceof Long)
        {
            return (long)key;
        }
        else if (key instanceof BigInteger)
        {
            return ((BigInteger)key).longValue();
        }
        else if (key instanceof BigDecimal)
        {
            return ((BigDecimal)key).longValue();
        }
        else if (key instanceof Integer)
        {
            return (Integer)key;
        }
        if (key!=null)
        {
            throw new Exception("class="+key.getClass().getName());
        }
        throw new Exception();
    }
    public int getAsInteger(int index) throws Exception
    {
        Object key=this.keys.get(index);
        if (key instanceof Long)
        {
            long value=(long)key;
            if ((value<Integer.MIN_VALUE)||(value>Integer.MAX_VALUE))
            {
                throw new Exception("value="+value);
            }
            return (int)value;
        }
        else if (key instanceof BigInteger)
        {
            return ((BigInteger)key).intValue();
        }
        else if (key instanceof BigDecimal)
        {
            return ((BigDecimal)key).intValue();
        }
        else if (key instanceof Integer)
        {
            return (Integer)key;
        }
        if (key!=null)
        {
            throw new Exception("class="+key.getClass().getName());
        }
        throw new Exception();
    }
    public int size()
    {
        return keys.size();
    }
}
