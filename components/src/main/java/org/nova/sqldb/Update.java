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

import java.util.ArrayList;

import org.nova.tracing.Trace;

public class Update 
{
    final private StringBuilder columns;
    final ArrayList<Object> parameters;
    final private String table;
    private String categoryOverride;
    private String whereExpression;
    private Object[] whereParameters;
    public static Update table(String table)
    {
        return new Update(table);
    }
    
    public Update(String table)
    {
        this.columns=new StringBuilder();
        this.parameters=new ArrayList<Object>();
        this.table=table;
    }
    
    public Update where(String whereExpression,Object...whereParameters)
    {
        this.whereExpression=whereExpression;
        this.whereParameters=whereParameters;
        return this;
    }
    
    public Update setValue(String columnName,Object value)
    {
        if (this.parameters.size()>0)
        {
            this.columns.append(',');
        }
        this.columns.append(columnName+"=?");
        this.parameters.add(value);
        return this;
    }
    public Update set(String expression,Object...values)
    {
        if (this.parameters.size()>0)
        {
            this.columns.append(',');
        }
        this.columns.append(expression);
        for (Object value:values)
        {
            this.parameters.add(value);
        }
        return this;
    }
    
    public Update categoryOverride(String categoryOverride)
    {
        this.categoryOverride=categoryOverride;
        return this;
    }
    public int execute(Trace parent,Accessor accessor) throws Throwable
    {
        return execute(parent,accessor,false);
    }    
    public int execute(Trace parent,Accessor accessor,boolean whereNotNeeded) throws Throwable
    {
        StringBuilder sql=new StringBuilder("UPDATE "+this.table+" SET "+this.columns.toString());
        if (whereNotNeeded==false)
        {
            if (this.whereExpression!=null)
            {
                sql.append(" WHERE ");
                sql.append(this.whereExpression);
            }
            else
            {
                throw new Exception("Missing where");
            }
        }
        
        for (Object parameter:this.whereParameters)
        {
            this.parameters.add(parameter);
        }
        if (this.parameters!=null)
        {
            return accessor.executeUpdate(parent, this.categoryOverride, this.parameters, sql.toString());
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
