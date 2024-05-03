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

import org.nova.tracing.Trace;

public class Delete 
{
    final private String table;
    private String whereExpression;
    private Object[] whereParameters;
    private String categoryOverride;
    
    public static Delete table(String table)
    {
        return new Delete(table);
    }
    
    public Delete(String table)
    {
        this.table=table;
    }
    
    public Delete categoryOverride(String categoryOverride)
    {
        this.categoryOverride=categoryOverride;
        return this;
    }
    public Delete where(String whereExpression,Object...whereParameters)
    {
        this.whereExpression=whereExpression;
        this.whereParameters=whereParameters;
        return this;
    }
    
    public int execute(Trace parent,Accessor accessor) throws Throwable
    {
        if (this.whereExpression==null)
        {
            throw new Exception("Do not use without WHERE");
        }
        StringBuilder sql=new StringBuilder("DELETE FROM "+this.table+" WHERE "+this.whereExpression);
        if (this.whereParameters!=null)
        {
            return accessor.executeUpdate(parent, this.categoryOverride, this.whereParameters, sql.toString());
        }
        return accessor.executeUpdate(parent, this.categoryOverride, sql.toString());
    }

    public int execute(Trace parent,Connector connector) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return execute(parent, accessor);
        }
    }
}
