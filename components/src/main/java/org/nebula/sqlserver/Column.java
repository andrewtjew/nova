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
package org.nebula.sqlserver;

public class Column
{
    private final String name;
    private final String type;
    private final String size;
    private final boolean identity;
    private final long identityStart;
    private final long identityIncrement;
    private final boolean nullAllowed;
    
    public Column(String name,String type,String size,boolean identity,long identityStart,long identityIncrement,boolean nullAllowed)
    {
        this.name=name;
        this.type=type;
        this.size=size;
        this.identity=identity;
        this.identityStart=identityStart;
        this.identityIncrement=identityIncrement;
        this.nullAllowed=nullAllowed;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public String getSize()
    {
        return size;
    }

    public boolean isIdentity()
    {
        return identity;
    }

    public long getIdentityStart()
    {
        return identityStart;
    }
    public long getIdentityIncrement()
    {
        return identityIncrement;
    }
    public boolean isNullAllowed()
    {
        return nullAllowed;
    }
    
    
}
