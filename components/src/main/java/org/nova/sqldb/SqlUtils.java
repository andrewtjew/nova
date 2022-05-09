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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nova.annotations.Alias;
import org.nova.core.NameObject;
import org.nova.sqldb.FieldMaps.ConstructorFieldMap;
import org.nova.tracing.Trace;

public class SqlUtils
{
    static public Timestamp now()
    {
        return new Timestamp(System.currentTimeMillis());
    }
    static public Timestamp daysBeforeNow(int days)
    {
        return new Timestamp(System.currentTimeMillis()-days*3600L*1000L);
    }

    static public RowSet executeQuery(Trace parent, String traceCategoryOverride, Connector connector,String sql, Object... parameters) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return accessor.executeQuery(parent, traceCategoryOverride, parameters,sql);
        }
    }

    static public int executeUpdate(Trace parent, String traceCategoryOverride, Connector connector,String sql, Object... parameters) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return accessor.executeUpdate(parent, traceCategoryOverride, parameters,sql);
        }
    }

    static public GeneratedKeys executeUpdateAndReturnGeneratedKeys(Trace parent, String traceCategoryOverride, Connector connector, String sql, Object... parameters) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return accessor.executeUpdateAndReturnGeneratedKeys(parent, traceCategoryOverride, sql, parameters);
        }
    }
    
    public static RowSet executeCallOneRowSet(Trace parent,String categoryOverride,Accessor accessor,String call,Param...params) throws Throwable
    {
        CallResult<Void> result=accessor.executeCall(parent, categoryOverride, Void.class, call,params);
        int length=result.getRowSets().length;
        if (length==0)
        {
            return null;
        }
        if (length>1)
        {
            throw new Exception("Unexpected rowsets:"+length);
        }
        return result.getRowSet(0);
    }
    public static RowSet executeCallOneRowSet(Trace parent,String categoryOverride,Connector connector,String call,Param...params) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return executeCallOneRowSet(parent, categoryOverride, accessor, call, params);
        }
    }
    
    public static Row executeCallOne(Trace parent,String categoryOverride,Accessor accessor,String call,Param...params) throws Throwable
    {
        RowSet rowSet=executeCallOneRowSet(parent, categoryOverride, accessor, call, params);
        if (rowSet==null)
        {
            return null;
        }
        if (rowSet.size()>1)
        {
            throw new Exception("Unexpected row:"+rowSet.size());
        }
        return rowSet.getRow(0);
    }
    public static Row executeCallOne(Trace parent,String categoryOverride,Connector connector,String call,Param...params) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return executeCallOne(parent,categoryOverride,accessor,call,params);
        }
    }


    public static RowSet executeCallOneRowSet(Trace parent,String categoryOverride,Accessor accessor,String call,Object...parameters) throws Throwable
    {
        Param[] params=new Param[parameters.length];
        for (int i=0;i<parameters.length;i++)
        {
            params[i]=Param.In(parameters[i]);
        }
        CallResult<Void> result=accessor.executeCall(parent, categoryOverride, Void.class, call,params);
        int length=result.getRowSets().length;
        if (length==0)
        {
            return null;
        }
        if (length>1)
        {
            throw new Exception("Unexpected rowsets:"+length);
        }
        return result.getRowSet(0);
    }
    public static RowSet executeCallOneRowSet(Trace parent,String categoryOverride,Connector connector,String call,Object...parameters) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return executeCallOneRowSet(parent, categoryOverride, accessor, call, parameters);
        }
    }

    public static Row executeCallOne(Trace parent,String categoryOverride,Accessor accessor,String call,Object parameters) throws Throwable
    {
        RowSet rowSet=executeCallOneRowSet(parent, categoryOverride, accessor, call, parameters);
        if (rowSet==null)
        {
            return null;
        }
        if (rowSet.size()>1)
        {
            throw new Exception("Unexpected row:"+rowSet.size());
        }
        return rowSet.getRow(0);
    }
    public static Row executeCallOne(Trace parent,String categoryOverride,Connector connector,String call,Object...parameters) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return executeCallOne(parent,categoryOverride,accessor,call,parameters);
        }
    }
    
    
    public static Row executeQueryOne(Trace parent, String traceCategoryOverride, Accessor accessor,String sql, Object... parameters) throws Throwable
    {
        RowSet rowSet=accessor.executeQuery(parent, traceCategoryOverride, parameters, sql);
        if (rowSet.size()>1)
        {
            throw new SQLException("Rows returned: "+rowSet.size());
        }
        else if (rowSet.size()==0)
        {
            return null;
        }
        return rowSet.getRow(0);
    }
    public static Row executeQueryOne(Trace parent, String traceCategoryOverride, Connector connector,String sql, Object... parameters) throws Throwable
    {
        try (Accessor accessor=connector.openAccessor(parent))
        {
            return executeQueryOne(parent,traceCategoryOverride,accessor,sql,parameters);
        }
    }
    public static boolean isTrue(Row row,String columnName)
    {
        if (row==null)
        {
            return false;
        }
        Boolean value=row.getNullableBIT(columnName);
        if (value==null)
        {
            return false;
        }
        return value;
    }
    
//    public static String getVARCHAR(Row row,String columnName)
//    {
//        if (row==null)
//        {
//            return null;
//        }
//        return row.getVARCHAR(columnName);
//    }
//    public static Long getNullableBIGINT(Row row,String columnName)
//    {
//        if (row==null)
//        {
//            return null;
//        }
//        return row.getNullableBIGINT(columnName);
//    }
//    public static Integer getNullableINTEGER(Row row,String columnName)
//    {
//        if (row==null)
//        {
//            return null;
//        }
//        return row.getNullableINTEGER(columnName);
//    }
//    public static Short getNullableSHORT(Row row,String columnName)
//    {
//        if (row==null)
//        {
//            return null;
//        }
//        return row.getNullableSMALLINT(columnName);
//    }
//    public static Double getNullableDOUBLE(Row row,String columnName)
//    {
//        if (row==null)
//        {
//            return null;
//        }
//        return row.getNullableDOUBLE(columnName);
//    }
//    public static Float getNullableREAL(Row row,String columnName)
//    {
//        if (row==null)
//        {
//            return null;
//        }
//        return row.getNullableREAL(columnName);
//    }

    
      public static Boolean getBIT(Row row,String columnName)
	  {
	      if (row==null)
	      {
	          return null;
	      }
	      Integer columnIndex=row.getColumnIndex(columnName);
	      if (columnIndex==null)
	      {
	    	  return null;
	      }
	      return row.getNullableBIT(columnIndex);
	  }
      public static Time getTIME(Row row,String columnName)
	  {
	      if (row==null)
	      {
	          return null;
	      }
	      Integer columnIndex=row.getColumnIndex(columnName);
	      if (columnIndex==null)
	      {
	    	  return null;
	      }
	      return row.getTIME(columnIndex);
	  }
}
