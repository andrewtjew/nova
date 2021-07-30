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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.nova.collections.Pool;
import org.nova.collections.Resource;
import org.nova.logging.Item;
import org.nova.sqldb.FieldMaps.ConstructorFieldMap;
import org.nova.sqldb.Param.Direction;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;

public class Accessor extends Resource
{
	final private long connectionIdleTimeoutMs;
	final Connector connector;
	Connection connection;
	private long lastExecuteMs;
	private Transaction transaction;
    private Transaction retireTransaction;
    private StackTraceElement[] retireStackTrace;

	Accessor(Pool<Accessor> pool, Connector connector, long connectionIdleTimeoutMs)
	{
		super(pool);
		this.connectionIdleTimeoutMs = connectionIdleTimeoutMs;
		this.connector = connector;
		this.retireTransaction=null;
	}

	public Transaction beginTransaction(String traceCategory) throws Throwable
	{
		synchronized (this)
		{
			if (this.transaction != null)
			{
                Exception ex=new Exception("Cannot begin more than one transaction. Existing transaction category=" + transaction.trace.getCategory());
			    
                //Bug testing: Should not retire the accessor
                this.retireTransaction=this.transaction;
                this.retireStackTrace=Thread.currentThread().getStackTrace();
                this.retire(ex);
			    throw ex;
			}
			this.connection.setAutoCommit(false);
            this.transaction = new Transaction(this, new Trace(this.connector.traceManager,this.getTrace(), traceCategory));
            this.connector.beginTransactionRate.increment();
			return this.transaction;
		}
	}
    public Transaction getRetireTransaction()
    {
        return this.retireTransaction;
    }
    public StackTraceElement[] getRetireStackTrace()
    {
        return this.retireStackTrace;
    }

	void commit() throws Throwable
	{
		synchronized (this)
		{
			if (transaction != null)
			{
				try
				{
					this.connection.commit();
                    this.connector.commitTransactionRate.increment();
					this.connection.setAutoCommit(true);
				}
				finally
				{
					this.transaction = null;
				}
			}
		}
	}

	void rollback() throws Throwable
	{
		synchronized (this)
		{
			try
			{
				this.connection.rollback();
                this.connector.rollbackTransactionRate.increment();
				this.connection.setAutoCommit(true);
			}
			finally
			{
				this.transaction = null;
			}
		}
	}

    @Override
    protected void activate() throws Throwable
    {
	    long now=System.currentTimeMillis();
		if (this.connection != null)
		{
			if (now - this.lastExecuteMs < this.connectionIdleTimeoutMs)
			{
				return;
			}
			try
			{
				this.connection.close();
			}
			catch (SQLException e)
			{
	            this.connector.logger.log(e,this.connector.getName()+":connection.close failed");
				this.connector.closeConnectionExceptions.increment();
			}
			finally
			{
			    this.connection = null;
			}
		}
		try
		{
			this.connection = this.connector.createConnection();
			this.lastExecuteMs = now;
		}
		catch (Throwable e)
		{
            this.connector.logger.log(e,this.connector.getName()+":connector.createConnection failed");
			this.connector.createConnectionExceptions.increment();
			throw e;
		}
	}
	
    private void setParameters(AccessorTraceContext context,PreparedStatement statement,Object[] parameters) throws SQLException
    {
        for (int i = 0; i < parameters.length; i++)
        {
            Object parameter=parameters[i];
            statement.setObject(i + 1, parameter);
            context.addLogItem(new Item("param"+i,parameter));
        }
    }

    @Override
	protected void park() throws Exception
	{
        synchronized (this)
        {
            Transaction transaction=this.transaction;
            if (transaction!=null)
            {
                try (Trace trace=new Trace(getTrace(),"ParkRolledbackTransactions",false,true))
                {
                    try
                    {
                        this.connector.getParkRolledbacks(transaction).increment();
                        transaction.rollback();
                    }
                    catch (Throwable t)
                    {
                        trace.close(t);
                        throw new Exception(t);
                    }
                }
            }
        }
	}
    
	public int executeUpdate(Trace parent, String traceCategoryOverride, String sql, Object... parameters) throws Throwable
	{
		return executeUpdate(parent, traceCategoryOverride, parameters, sql);
	}
    public int executeUpdate(Trace parent, String traceCategoryOverride, List<?> parameters,String sql) throws Throwable
    {
        return executeUpdate(parent, traceCategoryOverride, parameters.toArray(new Object[parameters.size()]), sql);
    }
	public int executeUpdate(Trace parent, String traceCategoryOverride, Object[] parameters,String sql) throws Throwable
	{
		try (AccessorTraceContext context = new AccessorTraceContext(this, parent, traceCategoryOverride,sql))
		{
			try
			{
				try (PreparedStatement statement = this.connection.prepareStatement(sql))
				{
				    setParameters(context,statement,parameters);
					try
					{
                        return context.logRowsUpdated(statement.executeUpdate());
					}
					finally
					{
						this.lastExecuteMs = System.currentTimeMillis();
					}
				}
			}
			catch (Throwable ex)
			{
			    throw context.handleThrowable(ex);
			}
		}
	}
    public GeneratedKeys executeUpdateAndReturnGeneratedKeys(Trace parent, String traceCategoryOverride, String sql, Object... parameters) throws Throwable
    {
        return executeUpdateAndReturnGeneratedKeys(parent, traceCategoryOverride,parameters, sql);
    }
    public GeneratedKeys executeUpdateAndReturnGeneratedKeys(Trace parent, String traceCategoryOverride, List<?> parameters,String sql) throws Throwable
    {
        return executeUpdateAndReturnGeneratedKeys(parent, traceCategoryOverride,parameters.toArray(new Object[parameters.size()]), sql);
    }
    public GeneratedKeys executeUpdateAndReturnGeneratedKeys(Trace parent, String traceCategoryOverride, Object[] parameters,String sql) throws Throwable
    {
        try (AccessorTraceContext context= new AccessorTraceContext(this, parent, traceCategoryOverride,sql))
        {
            try
            {
                try (PreparedStatement statement = this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS))
                {
                    setParameters(context,statement,parameters);
                    try
                    {
                        int updated=context.logRowsUpdated(statement.executeUpdate());
                        if (updated<=0)
                        {
                            return null;
                        }
                        ResultSet resultSet=statement.getGeneratedKeys();
                        ArrayList<Object> list=new ArrayList<>();
                        while (resultSet.next())
                        {
                            list.add(resultSet.getObject(1));
                        }
                        return new GeneratedKeys(list);
                    }
                    finally
                    {
                        this.lastExecuteMs = System.currentTimeMillis();
                    }
                }
            }
            catch (Throwable ex)
            {
                throw context.handleThrowable(ex);
            }
        }
    }
    public int[] executeBatchUpdate(Trace parent, String traceCategoryOverride,List<List<Object>> batchParameters,String sql) throws Throwable
    {
        Object[][] arrays=new Object[batchParameters.size()][];
        for (int i=0;i<batchParameters.size();i++)
        {
            List<?> list=batchParameters.get(i);
            arrays[i]=list.toArray(new Object[list.size()]);
        }
        return executeBatchUpdate(parent, traceCategoryOverride, arrays, sql);
    }    
	public int[] executeBatchUpdate(Trace parent, String traceCategoryOverride,Object[][] batchParameters,String sql) throws Throwable
    {
        try (AccessorTraceContext context = new AccessorTraceContext(this, parent, traceCategoryOverride,sql))
        {
            try
            {
                try (PreparedStatement statement = this.connection.prepareStatement(sql))
                {
                    int total=0;
                    for (int j = 0; j < batchParameters.length; j++)
                    {
                        Object[] parameters=batchParameters[j];
                        total+=parameters.length;
                        if (j==0)
                        {
                            setParameters(context, statement, parameters);
                        }
                        else
                        {
                            for (int i = 0; i < parameters.length; i++)
                            {
                                statement.setObject(i + 1, parameters[i]);
                            }
                        }
                        statement.addBatch();
                    }
                    context.addLogItem(new Item("totalBatchParameters",total));
                    try
                    {
                        return context.logRowsUpdated(statement.executeBatch());
                    }
                    finally
                    {
                        this.lastExecuteMs = System.currentTimeMillis();
                    }
                }
            }
            catch (Throwable ex)
            {
                throw context.handleThrowable(ex);
            }
        }
    }
	
    public static <TYPE> TYPE[] map(RowSet rowSet,Class<TYPE> type) throws Throwable
    {
        ArrayList<TYPE> list = new ArrayList<>();
        int columns = rowSet.getColumns();
        
        ConstructorFieldMap constructorFieldMap=FieldMaps.get(type);
        HashMap<String,Field> map=constructorFieldMap.map;
        Field[] fields=new Field[columns];
        for (int columnIndex = 0; columnIndex < columns; columnIndex++)
        {
            String name = rowSet.getColumnName(columnIndex);
            Field field=map.get(name);
            fields[columnIndex]=field;
        }       

        for (Row row:rowSet.rows())
        {
            TYPE item = (TYPE)constructorFieldMap.newInstance();
            list.add(item);
            for (int columnIndex = 0; columnIndex < columns; columnIndex++)
            {
                Field field = fields[columnIndex];
                if (field != null)
                {
                    field.set(item, row.get(columnIndex));
                }
            }
        }
        return list.toArray((TYPE[]) Array.newInstance(type, list.size()));
    }
	@SuppressWarnings("unchecked")
	private <TYPE> TYPE[] convert(ResultSet resultSet,Class<TYPE> type) throws Throwable
	{
	    if (type==String.class)
	    {
	        ArrayList<String> list = new ArrayList<>();
	        while (resultSet.next())
	        {
	            list.add(resultSet.getString(1));
	        }
	        return (TYPE[])list.toArray(new String[list.size()]);
	    }
	    //TODO: add rest
		ArrayList<TYPE> list = new ArrayList<>();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columns = metaData.getColumnCount();
		
        ConstructorFieldMap constructorFieldMap=FieldMaps.get(type);
        HashMap<String,Field> map=constructorFieldMap.map;
		Field[] fields=new Field[columns];
		for (int columnIndex = 0; columnIndex < columns; columnIndex++)
		{
			String name = metaData.getColumnName(columnIndex + 1);
			Field field=map.get(name);
			fields[columnIndex]=field;
		}		
		
		while (resultSet.next())
		{
            TYPE item = (TYPE)constructorFieldMap.newInstance();
			list.add(item);
			for (int columnIndex = 0; columnIndex < columns; columnIndex++)
			{
				Field field = fields[columnIndex];
				if (field != null)
				{
					field.set(item, resultSet.getObject(columnIndex + 1));
				}
			}
		}
		return list.toArray((TYPE[]) Array.newInstance(type, list.size()));
	}
	private <TYPE> TYPE convertOne(ResultSet resultSet,Class<TYPE> type) throws Throwable
	{
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columns = metaData.getColumnCount();
		
        ConstructorFieldMap constructorFieldMap=FieldMaps.get(type);
        HashMap<String,Field> map=constructorFieldMap.map;
		if (resultSet.next())
		{
            @SuppressWarnings("unchecked")
            TYPE item = (TYPE)constructorFieldMap.newInstance();
			for (int columnIndex = 0; columnIndex < columns; columnIndex++)
			{
				String name = metaData.getColumnName(columnIndex + 1);
				Field field = map.get(name);
				if (field != null)
				{
					field.set(item, resultSet.getObject(columnIndex + 1));
				}
			}
			if (resultSet.next())
			{
		        throw new Exception("Multiple results");
			}
			return item;
		}
		return null;
	}
	static RowSet convert(ResultSet resultSet) throws SQLException
	{
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columns = metaData.getColumnCount();
		String[] columnNames = new String[columns];
		for (int columnIndex = 0; columnIndex < columns; columnIndex++)
		{
			columnNames[columnIndex] = metaData.getColumnName(columnIndex + 1);
		}
		ArrayList<Object[]> list = new ArrayList<>();
		while (resultSet.next())
		{
			Object[] rowItems = new Object[columns];
			for (int columnIndex = 0; columnIndex < columns; columnIndex++)
			{
				rowItems[columnIndex] = resultSet.getObject(columnIndex + 1);
			}
			list.add(rowItems);
		}
		return new RowSet(columnNames, list);
	}

	public int executeInsert(Trace parent, String traceCategoryOverride, String table, Object object) throws Throwable
    {
	    Class<?> type=object.getClass();
        ConstructorFieldMap constructorFieldMap=FieldMaps.get(type);
        HashMap<String,Field> map=constructorFieldMap.map;
        StringBuilder sb=new StringBuilder();
        sb.append("INSERT INTO ").append(table).append(" (");
        ArrayList<Object> parameters=new ArrayList<>();
        boolean needComma=false;
        for (Field field:map.values())
        {
            if (needComma)
            {
                sb.append(',');
            }
            else
            {
                needComma=true;
            }
            sb.append(field.getName());
            parameters.add(field.get(object));
        }
        sb.append(") VALUES(");
        needComma=false;
        for (int i=0;i<map.size();i++)
        {
            if (needComma)
            {
                sb.append(',');
            }
            else
            {
                needComma=true;
            }
            sb.append('?');
        }
        sb.append(')');
        String sql=sb.toString();
        if (traceCategoryOverride==null)
        {
            traceCategoryOverride=sql;
        }
        return executeUpdate(parent, traceCategoryOverride, parameters,sql);
    
    }

    public RowSet executeQuery(Trace parent, String traceCategoryOverride, String sql, Object... parameters) throws Throwable
	{
		return executeQuery(parent, traceCategoryOverride, parameters, sql);
	}
    public RowSet executeQuery(Trace parent, String traceCategoryOverride, List<?> parameters, String sql) throws Throwable
    {
        return executeQuery(parent, traceCategoryOverride, parameters.toArray(new Object[parameters.size()]), sql);
    }
	public RowSet executeQuery(Trace parent, String traceCategoryOverride, Object[] parameters, String sql) throws Throwable
	{
		try (AccessorTraceContext context = new AccessorTraceContext(this, parent, traceCategoryOverride,sql))
		{
			try
			{
				try (PreparedStatement statement = this.connection.prepareStatement(sql))
				{
				    setParameters(context, statement, parameters);
                    this.connector.queryRate.increment();
					if (statement.execute() == false)
					{
						return null;
					}
					try (ResultSet resultSet = statement.getResultSet())
					{
						RowSet rowSet=convert(resultSet);
						context.logRowsQueried(rowSet.size());
						return rowSet;
					}
				}
			}
			catch (Throwable ex)
			{
			    throw context.handleThrowable(ex);
			}
		}
	}

	public <TYPE> TYPE[] executeQuery(Trace parent, String traceCategoryOverride, Class<TYPE> type, String sql, Object... parameters) throws Throwable
	{
		return executeQuery(parent, traceCategoryOverride, type, parameters, sql);
	}
    public <TYPE> TYPE[] executeQuery(Trace parent, String traceCategoryOverride, Class<TYPE> type, List<?> parameters, String sql) throws Throwable
    {
        return executeQuery(parent, traceCategoryOverride, type, parameters.toArray(new Object[parameters.size()]), sql);
    }
	public <TYPE> TYPE[] executeQuery(Trace parent, String traceCategoryOverride, Class<TYPE> type, Object[] parameters, String sql) throws Throwable
	{
		try (AccessorTraceContext context = new AccessorTraceContext(this, parent, traceCategoryOverride,sql))
		{
			try
			{
				try (PreparedStatement statement = this.connection.prepareStatement(sql))
				{
                    setParameters(context,statement,parameters);
					try
					{
						if (statement.execute() == false)
						{
                            context.logRowsQueried(0);
							return null;
						}
					}
					finally
					{
						this.lastExecuteMs = System.currentTimeMillis();
					}
					try (ResultSet resultSet = statement.getResultSet())
					{
						TYPE[] results=convert(resultSet,type);
                        context.logRowsQueried(results.length);
						return results;
					}
				}
			}
            catch (Throwable ex)
            {
                throw context.handleThrowable(ex);
            }
		}
	}
	public <TYPE> TYPE executeCallOne(Trace parent, String traceCategoryOverride, Class<TYPE> type, String sql, Object... parameters) throws Throwable
	{
		return executeCallOne(parent,traceCategoryOverride,type,parameters,sql);
	}
    public <TYPE> TYPE executeCallOne(Trace parent, String traceCategoryOverride, Class<TYPE> type, List<?> parameters, String sql) throws Throwable
    {
        return executeCallOne(parent,traceCategoryOverride,type,parameters.toArray(new Object[parameters.size()]),sql);
    }
	public <TYPE> TYPE executeCallOne(Trace parent, String traceCategoryOverride, Class<TYPE> type, Object[] parameters, String sql) throws Throwable
	{
        try (AccessorTraceContext context = new AccessorTraceContext(this, parent, traceCategoryOverride,sql))
 		{
			try
			{
				try (CallableStatement statement = this.connection.prepareCall(sql))
				{
                    setParameters(context,statement,parameters);
					try
					{
						if (statement.execute() == false)
						{
                            context.logRowsQueried(0);
							return null;
						}
					}
					finally
					{
						this.lastExecuteMs = System.currentTimeMillis();
					}
					try (ResultSet resultSet = statement.getResultSet())
					{
                        context.logRowsQueried(1);
						return convertOne(resultSet,type);
					}
				}
			}
            catch (Throwable ex)
            {
                throw context.handleThrowable(ex);
            }
        }
	}

    public <RETURN_TYPE> CallResult<RETURN_TYPE> executeCall(Trace parent, String traceCategoryOverride, Class<RETURN_TYPE> returnType,String name,Param...parameters) throws Throwable
    {
        return executeCall(parent,traceCategoryOverride,returnType,parameters,name);
    }
    public <RETURN_TYPE> CallResult<RETURN_TYPE> executeCall(Trace parent, String traceCategoryOverride, Class<RETURN_TYPE> returnType,List<Param> parameters,String name) throws Throwable
    {
        return executeCall(parent,traceCategoryOverride,returnType,parameters.toArray(new Param[parameters.size()]),name);
    }
    @SuppressWarnings("unchecked")
    public <RETURN_TYPE> CallResult<RETURN_TYPE> executeCall(Trace parent, String traceCategoryOverride, Class<RETURN_TYPE> returnType,Param[] parameters,String name) throws Throwable
	{
        try (AccessorTraceContext context = new AccessorTraceContext(this, parent, traceCategoryOverride,name))
        {
            try
            {
        		StringBuilder sb=new StringBuilder();
        		sb.append('{');
        		int offset=1;
        		if ((returnType!=Void.class)&&(returnType!=void.class))
        		{
        			offset=2;
        			sb.append("?=");
        		}
        		sb.append("call ").append(name);
        		if (parameters.length>0)
        		{
        			sb.append('(');
        			for (int i = 0; i < parameters.length; i++)
        			{
        				if (i>0)
        				{
        					sb.append(',');
        				}
        				sb.append('?');
        			}
        			sb.append(')');
        		}
        		sb.append('}');
        		String call=sb.toString();
        		context.addLogItem(new Item("Call",call));
        		ArrayList<Integer> outIndexes=new ArrayList<>(); 
				try (CallableStatement statement = this.connection.prepareCall(call))
				{
					if (offset==2)
					{
						int returnSqlType=Param.getSqlType(returnType);
						statement.registerOutParameter(1,returnSqlType);
					}
					for (int i = 0; i < parameters.length; i++)
					{
						Param param=parameters[i];
						if ((param.direction==Direction.IN)||(param.direction==Direction.IN_OUT))
						{
                            context.addLogItem(new Item("param"+i,param.inValue));
							statement.setObject(i + offset, param.inValue);
						}
						if ((param.direction==Direction.OUT)||(param.direction==Direction.IN_OUT))
						{
							statement.registerOutParameter(i+offset, param.sqlType);
							outIndexes.add(i);
						}
					}
					this.connector.callRate.increment();
					boolean hasResultSet=statement.execute();
                    ArrayList<RowSet> rowSetList=new ArrayList<>();
					ArrayList<Integer> updateCounts=new ArrayList<>();
                    RETURN_TYPE returnValue=null;
                    HashMap<Integer,Object> outValues=new HashMap<>();
                    for (;;)
                    {
                        if (hasResultSet)
                        {
                            try (ResultSet resultSet = statement.getResultSet())
                            {
                                RowSet rowSet=convert(resultSet);
                                rowSetList.add(rowSet);
                            }
                            hasResultSet=statement.getMoreResults();
                        }
                        else
                        {
                            int updateCount=statement.getUpdateCount();
                            if (updateCount==-1)
                            {
                                break;
                            }
                            updateCounts.add(updateCount);
                            hasResultSet=statement.getMoreResults();
                        }
                    }
                    if (offset==2)
                    {
                        returnValue=(RETURN_TYPE)statement.getObject(1);
                    }
                    for (int index:outIndexes)
                    {
                        outValues.put(index, statement.getObject(index+offset));
                    }
                    int[] updateCountArray=TypeUtils.intListToArray(updateCounts);
                    RowSet[] rowSetArray=rowSetList.toArray(new RowSet[rowSetList.size()]);
                    context.logRowsUpdated(updateCountArray);
                    context.logRowsQueried(rowSetArray);
                    return new CallResult<RETURN_TYPE>(returnValue,outValues,rowSetArray,updateCountArray);
					
				}
			}
            catch (Throwable ex)
            {
//                SQLServerException sql=(SQLServerException)ex;
//                System.out.println(sql.getSQLState());
                throw context.handleThrowable(ex);
            }
		}
	}


	@SuppressWarnings("unchecked")
    public <TYPE> DataReader<TYPE> openQuery(Trace parent, String traceCategoryOverride, Class<TYPE> type, String sql, Object... parameters) throws Throwable
    {
	    return openQuery(parent, traceCategoryOverride, type, parameters,sql);
    }
    public <TYPE> DataReader<TYPE> openQuery(Trace parent, String traceCategoryOverride, Class<TYPE> type, List<?> parameters, String sql) throws Throwable
    {
        return openQuery(parent, traceCategoryOverride, type, parameters.toArray(new Object[parameters.size()]),sql);
    }
    
    //TODO: add metrics and logging
	public <TYPE> DataReader<TYPE> openQuery(Trace parent, String traceCategoryOverride, Class<TYPE> type, Object[] parameters, String sql) throws Throwable
	{
        try (AccessorTraceContext context = new AccessorTraceContext(this, parent, traceCategoryOverride,sql))
        {
    		try
    		{
    			PreparedStatement statement = this.connection.prepareStatement(sql);
    			try
    			{
    			    setParameters(context,statement,parameters);
    				return new DataReader<TYPE>(context, statement.getResultSet(), type);
    			}
    			catch (Throwable t)
    			{
                    statement.close();
                    throw t;
    			}
    		}
    		catch (Throwable t)
    		{
    		    throw context.handleThrowable(t);
    		}
        }
	}
}
