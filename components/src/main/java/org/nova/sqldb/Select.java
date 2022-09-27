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

public class Select 
{
    final private StringBuilder columns;
    final private String source; //Table with optional JOINs
    private String categoryOverride;
    private int numberOfColumns;
    private String orderBy;
    
    public static Select source(String source)
    {
        return new Select(source);
    }
    
    public Select(String source)
    {
        this.columns=new StringBuilder();
        this.source=source;
        this.numberOfColumns=0;
    }
    
    public Select columns(String...columnNames)
    {
        for (String columnName:columnNames)
        {
            if (this.columns.length()>0)
            {
                this.columns.append(',');
            }
            this.columns.append(columnName);
        }
        this.numberOfColumns+=columnNames.length;
        return this;
    }
    
    public int getNumberOfColumns()
    {
        return this.numberOfColumns;
    }
    
    public Select categoryOverride(String categoryOverride)
    {
        this.categoryOverride=categoryOverride;
        return this;
    }
    
    public Select orderBy(String orderBy)
    {
        this.orderBy=orderBy;
        return this;
    }
    
    public RowSet execute(Trace parent,Accessor accessor,Integer max,String where,Object...parameters) throws Throwable
    {
        String sql=null;
        if (this.columns.length()==0)
        {
            this.columns.append("*");
        }
        if (max!=null)
        {
            Connector connector=accessor.getConnector();
            Object[] old=parameters;
            parameters=new Object[old.length+1];
            if (connector instanceof SqlServerConnector)
            {
                sql="SELECT TOP(?) "+this.columns.toString()+" FROM "+this.source+" WHERE "+where;
                parameters[0]=max;
                for (int i=0;i<old.length;i++)
                {
                    parameters[i+1]=old[i];
                }
            }
            else if (connector instanceof MySqlConnector)
            {
                sql="SELECT "+this.columns.toString()+" FROM "+this.source+" WHERE "+where+" LIMIT ?";
                for (int i=0;i<old.length;i++)
                {
                    parameters[i]=old[i];
                }
                parameters[old.length]=max;
            }
            else
            {
                throw new RuntimeException();
            }
        }
        else
        {
            sql="SELECT "+this.columns.toString()+" FROM "+this.source+" WHERE "+where;
        }
        if (this.orderBy!=null)
        {
            sql=sql+" ORDER BY "+this.orderBy;
        }
   
        System.out.println(sql);
        return accessor.executeQuery(parent, this.categoryOverride, parameters, sql);
    }

    public RowSet execute(Trace parent,Connector connector,Integer max,String where,Object...parameters) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return execute(parent, accessor,max,where,parameters);
        }
    }

    public RowSet execute(Trace parent,Accessor accessor,String where,Object...parameters) throws Throwable
    {
        return execute(parent,accessor,null,where,parameters);
    }

    public RowSet execute(Trace parent,Connector connector,String where,Object...parameters) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return execute(parent, accessor,where,parameters);
        }
    }

    public Row executeOne(Trace parent,Accessor accessor,String where,Object...parameters) throws Throwable
    {
        RowSet rowSet=execute(parent,accessor,1,where,parameters);
        if (rowSet.size()==1)
        {
            return rowSet.getRow(0);
        }
        if (rowSet.size()==0)
        {
            return null;
        }
        throw new Exception("rows="+rowSet.size());
    }

    public Row executeOne(Trace parent,Connector connector,String where,Object...parameters) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return executeOne(parent, accessor,where,parameters);
        }
    }

}
