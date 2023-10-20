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
import java.time.format.DateTimeFormatter;

import org.nova.logging.Logger;
import org.nova.security.UnsecureVault;
import org.nova.security.Vault;
import org.nova.testing.Debugging;
import org.nova.tracing.TraceManager;

import com.nova.disrupt.Disruptor;

public class MySqlConnector extends Connector
{
	final private String name;
    final private String host;
    final private int port;
    final private String user;
    final private Vault passwordVault;
    final private String passwordKey;
    final private String schema;
	
	
	public static DateTimeFormatter DATETIME_FORMATTER=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
    private String buildConnectionString()
    {
//        return "jdbc:mysql://"+this.host+":"+this.port+"/"+this.schema+"?rewriteBatchedStatements=true";
        return "jdbc:mysql://"+this.host+":"+this.port+"/"+this.schema+"?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";
    }
    
    private static Vault buildUnsecuredVault(String password)
    {
        
      UnsecureVault unsecuredVault=new UnsecureVault();
      unsecuredVault.put("password", password);
      return unsecuredVault;
    }

    public MySqlConnector(TraceManager traceManager,Logger logger, Disruptor disruptor,String user, String password, boolean connect, MySqlConfiguration configuration)
            throws Throwable
    {
        this(traceManager,logger,disruptor,user,"password",buildUnsecuredVault(password),connect,configuration);
        
    }
    public MySqlConnector(TraceManager traceManager,Logger logger, String user, String password, boolean connect, MySqlConfiguration configuration)
            throws Throwable
    {
        this(traceManager,logger,null,user,password,connect,configuration);
    }
    public MySqlConnector(TraceManager traceManager,Logger logger,String user,String passwordKey,Vault vault,boolean connect,MySqlConfiguration configuration) throws Throwable 
    {
        this(traceManager,logger,null,user,passwordKey,vault,connect,configuration);
    }    	
	public MySqlConnector(TraceManager traceManager,Logger logger,Disruptor disruptor,String user,String passwordKey,Vault vault,boolean connect,MySqlConfiguration configuration) throws Throwable 
	{
		super(traceManager,logger,disruptor,configuration.maximumRecentlyUsedCount);
		this.user=user;
		this.schema=configuration.schema;
		this.port=configuration.port;
		this.name=configuration.host+"/"+configuration.schema;
		this.passwordVault=vault;
		this.passwordKey=passwordKey;
		this.host=configuration.host;
		int poolSize=configuration.poolSize;
		long connectionKeepAlive=configuration.connectionKeepAlive;
		Accessor[] accessors=new Accessor[poolSize];
		for (int i=0;i<poolSize;i++)
		{
			@SuppressWarnings("resource")
            Accessor accessor=new Accessor(this.pool, this, connectionKeepAlive);
			if (connect)
			{
				try
				{
					accessor.activate();
				}
				catch (Throwable e)
				{
					this.initialConnectionExceptions.increment();
					connect=false;
				}
			}
			accessors[i]=accessor;
		}
		this.pool.initialize(accessors);
	}

	final static private boolean TEST=true; 
	@Override
	protected Connection createConnection() throws Throwable
	{
	    String password=this.passwordVault.get(this.passwordKey);
	    String connectionString=this.buildConnectionString();
	    if (TEST)
	    {
	    	Debugging.log(null, connectionString);
	    }
	    	
	    //"jdbc:mysql://localhost:3306/YourDBName";
        Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection=DriverManager.getConnection(connectionString,user,password);
		this.openConnectionSuccesses.increment();
		return connection;
	}

	@Override
	public String getName() 
	{
		return this.name;
	}

}
