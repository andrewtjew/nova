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

import java.sql.Connection;
import java.sql.DriverManager;
import org.nova.logging.Logger;
import org.nova.security.UnsecureVault;
import org.nova.security.Vault;
import org.nova.tracing.TraceManager;

import com.nova.disrupt.Disruptor;

public class SqlServerConnector extends Connector
{
    final private String host;
	final private String user;
    final private Vault passwordVault;
    final private String passwordKey;
	final private int port;
	final private String database;
	final private String name;

	private String buildConnectionString() throws Exception
	{
        return "jdbc:sqlserver://" + this.host + ":" + this.port+";databaseName="+
                this.database+";user="+user+";password="+this.passwordVault.get(this.passwordKey)+
                ";sendStringParametersAsUnicode=false;"; //If String parameters are in Unicode, the DB becomes very slooowwwwww........
	}
	
	private static Vault buildUnsecuredVault(String password)
	{
        
      UnsecureVault unsecuredVault=new UnsecureVault();
      unsecuredVault.put("password", password);
      return unsecuredVault;
	}
	
    public SqlServerConnector(TraceManager traceManager, Logger logger,String user, String password,String host,String database)
            throws Throwable
    {
        this(traceManager,logger,null,user,password,new SqlServerConfiguration(host,database));
    }
    public SqlServerConnector(TraceManager traceManager, Logger logger,String user, String password, SqlServerConfiguration configuration)
            throws Throwable
    {
        this(traceManager,logger,null,user,password,configuration);
    }
    public SqlServerConnector(TraceManager traceManager, Logger logger,Disruptor disruptor,String user, String password, SqlServerConfiguration configuration)
            throws Throwable
    {
        this(traceManager,logger,disruptor,user,buildUnsecuredVault(password),"password",configuration);
    }
    public SqlServerConnector(TraceManager traceManager,Logger logger, String user, Vault vault,String passwordKey, SqlServerConfiguration configuration)
            throws Throwable
    {
        this(traceManager,logger,null,user,vault,passwordKey,configuration);
    }
	public SqlServerConnector(TraceManager traceManager,Logger logger, Disruptor disruptor,String user, Vault vault,String passwordKey, SqlServerConfiguration configuration)
            throws Throwable
    {
        super(traceManager,logger,disruptor,configuration.maximumLeastRecentlyUsedCount);
        this.passwordKey=passwordKey;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        this.user = user;
        this.passwordVault=vault;
        this.port=configuration.port;
        this.host=configuration.host;
        this.database=configuration.database;
        this.name = configuration.database+"@"+configuration.host;
        int poolSize = configuration.poolSize;
        long connectionKeepAliveMs = configuration.connectionKeepAlive;
        
        Accessor[] accessors=new Accessor[poolSize];
        for (int i = 0; i < poolSize; i++)
        {
            Accessor accessor = new Accessor(this.pool, this, connectionKeepAliveMs);
            if (configuration.connectImmediately)
            {
                try
                {
                    accessor.activate();
                }
                catch (Throwable e)
                {
                    this.initialConnectionExceptions.increment();
                }
            }
            accessors[i]=accessor;
        }
        this.pool.initialize(accessors);
    }

	@Override
	protected Connection createConnection() throws Throwable
	{
        String connectionString=buildConnectionString();
	    try
	    {
    		Connection connection = DriverManager.getConnection(connectionString);
    		this.openConnectionSuccesses.increment();
    		return connection;
	    }
	    catch (Throwable t)
	    {
	        throw new Exception("ConnectionString:"+connectionString,t);
//	        throw t;
	    }
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	/*
    @SuppressWarnings("unchecked")
    public <RETURN_TYPE> CallResult<RETURN_TYPE> executeSqlServerCall(Trace parent, String traceCategoryOverride, Class<RETURN_TYPE> returnType,Param[] parameters,String name) throws Throwable
    {
        try (Accessor accessor=this.openAccessor(parent, traceCategoryOverride))
        {
            try (AccessorTraceContext context = new AccessorTraceContext(accessor, parent, traceCategoryOverride,name))
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
                    try (CallableStatement statement = accessor.connection.prepareCall(call))
                    {
                        SQLServerCallableStatement sqlServerStatement=(SQLServerCallableStatement)(statement);
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
                                if (param.sqlType!=microsoft.sql.Types.STRUCTURED)
                                {
                                    context.addLogItem(new Item("param"+i,param.inValue));
                                    statement.setObject(i + offset, parameters[i].inValue);
                                }
                                else
                                {
                                    SQLServerDataTableParameter dataTableParameter=(SQLServerDataTableParameter)parameters[i].inValue; 
                                    context.addLogItem(new Item("param"+i,"SQLServerDataTable"));
                                    sqlServerStatement.setStructured(i+offset,dataTableParameter.getName(),dataTableParameter.getDataTable());
                                }
                            }
                            if ((param.direction==Direction.OUT)||(param.direction==Direction.IN_OUT))
                            {
                                statement.registerOutParameter(i+offset, param.sqlType);
                                outIndexes.add(i);
                            }
                        }
                        this.callRate.increment();
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
                                    RowSet rowSet=Accessor.convert(resultSet);
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
                        int[] updateCountArray=Utils.intArrayFromList(updateCounts);
                        RowSet[] rowSetArray=rowSetList.toArray(new RowSet[rowSetList.size()]);
                        context.logRowsUpdated(updateCountArray);
                        context.logRowsQueried(rowSetArray);
                        return new CallResult<RETURN_TYPE>(returnValue,outValues,rowSetArray,updateCountArray);
                    }
                }
                catch (Throwable ex)
                {
                    throw context.handleThrowable(ex);
                }
            }
        }
    }
	*/
	
}
