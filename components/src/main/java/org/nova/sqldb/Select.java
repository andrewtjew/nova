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
import org.nova.utils.Utils;

public class Select 
{
    final private String source; //Table with optional JOINs
    private String categoryOverride;
    private String orderBy;
    private String groupBy;
    private String whereExpression;
    private Object[] whereParameters;
    private Long maximumRows;
    private String[] columns;
    
    public static Select source(String source)
    {
        return new Select(source);
    }
    
    public Select(String source)
    {
        this.source=source;
    }
    
    public Select maximumRows(long maximumRows)
    {
        this.maximumRows=maximumRows;
        return this;
                
    }
    public Select limit(Long maximumRows)
    {
        this.maximumRows=maximumRows;
        return this;
    }
    
    public Select columns(String...columnNames) throws Exception
    {
        if (this.columns!=null)
        {
            throw new Exception();
        }
        this.columns=columnNames;
        return this;
    }
    
    public Select categoryOverride(String categoryOverride)
    {
        this.categoryOverride=categoryOverride;
        return this;
    }

    public Select where(String whereExpression,Object...whereParameters)
    {
        this.whereExpression=whereExpression;
        this.whereParameters=whereParameters;
        return this;
    }
    public Select orderBy(String orderBy)
    {
        this.orderBy=orderBy;
        return this;
    }
    public Select groupBy(String groupBy)
    {
        this.groupBy=groupBy;
        return this;
    }
    
//    @Deprecated
//    public RowSet execute(Trace parent,Accessor accessor,Integer max,String where,Object...parameters) throws Throwable
//    {
//        String sql=null;
//        if (this.columns.length()==0)
//        {
//            this.columns.append("*");
//        }
//        if (max!=null)
//        {
//            Connector connector=accessor.getConnector();
//            Object[] old=parameters;
//            parameters=new Object[old.length+1];
//            if (where!=null)
//            {
//                if (connector instanceof SqlServerConnector)
//                {
//                    sql="SELECT TOP(?) "+this.columns.toString()+" FROM "+this.source+" WHERE "+where;
//                    parameters[0]=max;
//                    for (int i=0;i<old.length;i++)
//                    {
//                        parameters[i+1]=old[i];
//                    }
//                }
//                else if (connector instanceof MySqlConnector)
//                {
//                    sql="SELECT "+this.columns.toString()+" FROM "+this.source+" WHERE "+where+" LIMIT ?";
//                    for (int i=0;i<old.length;i++)
//                    {
//                        parameters[i]=old[i];
//                    }
//                    parameters[old.length]=max;
//                }
//                else
//                {
//                    throw new RuntimeException();
//                }
//            }
//            else
//            {
//                if (connector instanceof SqlServerConnector)
//                {
//                    sql="SELECT TOP(?) "+this.columns.toString()+" FROM "+this.source;
//                    parameters[0]=max;
//                    for (int i=0;i<old.length;i++)
//                    {
//                        parameters[i+1]=old[i];
//                    }
//                }
//                else if (connector instanceof MySqlConnector)
//                {
//                    sql="SELECT "+this.columns.toString()+" FROM "+this.source+" LIMIT ?";
//                    for (int i=0;i<old.length;i++)
//                    {
//                        parameters[i]=old[i];
//                    }
//                    parameters[old.length]=max;
//                }
//                else
//                {
//                    throw new RuntimeException();
//                }
//            }  
//        }
//        else
//        {
//            if (where!=null)
//            {
//                sql="SELECT "+this.columns.toString()+" FROM "+this.source+" WHERE "+where;
//            }
//            else
//            {
//                sql="SELECT "+this.columns.toString()+" FROM "+this.source;
//            }
//        }
//        if (this.orderBy!=null)
//        {
//            sql=sql+" ORDER BY "+this.orderBy;
//        }
//   
////        System.out.println(sql);
//        return accessor.executeQuery(parent, this.categoryOverride, parameters, sql);
//    }

    public RowSet execute(Trace parent,Accessor accessor) throws Throwable
    {
        Connector connector=accessor.getConnector();

        StringBuilder sb=new StringBuilder("SELECT");
        if (this.maximumRows!=null)
        {
            if (connector instanceof SqlServerConnector)
            {
                sb.append(" TOP("+this.maximumRows+")");
            }
        }
        if ((this.columns==null)||(this.columns.length==0))
        {
            sb.append(" *");
        }
        else
        {
            sb.append(" ");
            sb.append(Utils.combine(this.columns, ","));
        }
        sb.append(" FROM ");
        sb.append(this.source);
        if (this.whereExpression!=null)
        {
            sb.append(" WHERE ");
            sb.append(this.whereExpression);
        }
        if (this.orderBy!=null)
        {
            sb.append(" ORDER BY ");
            sb.append(this.orderBy);
        }
        if (this.groupBy!=null)
        {
            sb.append(" GROUP BY ");
            sb.append(this.groupBy);
        }
        if (this.maximumRows!=null)
        {
            if (connector instanceof MySqlConnector)
            {
                sb.append(" LMIT ");
                sb.append(this.maximumRows);
            }
        }
        String sql=sb.toString();
        try
        {
            if (this.whereParameters!=null)
            {
                return accessor.executeQuery(parent, this.categoryOverride, this.whereParameters, sql);
            }
            else
            {
                return accessor.executeQuery(parent, this.categoryOverride,sql);
            }
        }
        catch (Throwable t)
        {
            throw new Exception(sql,t);
        }
    }
    
    
    public RowSet execute(Trace parent,Connector connector) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return execute(parent, accessor);
        }
    }

    public Row executeOne(Trace parent,Accessor accessor) throws Throwable
    {
        RowSet rowSet=execute(parent,accessor);
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

    public Row executeOne(Trace parent,Connector connector) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return executeOne(parent, accessor);
        }
    }

}
