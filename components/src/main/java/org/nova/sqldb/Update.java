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
    
    public Update value(String columnName,Object value)
    {
        if (this.parameters.size()>0)
        {
            this.columns.append(',');
        }
        this.columns.append(columnName+"=?");
        this.parameters.add(value);
        return this;
    }
    
    public Update categoryOverride(String categoryOverride)
    {
        this.categoryOverride=categoryOverride;
        return this;
    }
    
    public int execute(Trace parent,Accessor accessor,String where,Object...parameters) throws Throwable
    {
        StringBuilder sql=new StringBuilder("UPDATE "+this.table+" SET "+this.columns.toString()+" WHERE "+where);
        
        for (Object parameter:parameters)
        {
            this.parameters.add(parameter);
        }
        return accessor.executeUpdate(parent, this.categoryOverride, this.parameters, sql.toString());
    }

    public int execute(Trace parent,Connector connector,String where,Object...parameters) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return execute(parent, accessor,where,parameters);
        }
    }
    public int getNumberOfColumns()
    {
        return this.parameters.size();
    }

}
