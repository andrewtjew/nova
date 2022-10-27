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
import java.util.LinkedList;
import java.util.List;

import org.nova.annotations.Description;
import org.nova.annotations.Metrics;
import org.nova.collections.Pool;
import org.nova.logging.Logger;
import org.nova.metrics.CountMeter;
import org.nova.metrics.LevelMeter;
import org.nova.metrics.LongValueMeter;
import org.nova.metrics.RateMeter;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;

import com.nova.disrupt.Disruptor;

public abstract class Connector
{
	final protected TraceManager traceManager;
    @Metrics
	final protected Pool<Accessor> pool;
    final protected Disruptor disruptor;
    final Logger logger;

    @Description("Number of close connection exceptions. Possible causes are database problems.")
	final CountMeter closeConnectionExceptions; 

    @Description("Number of create connection exceptions. Possible causes are database problems.")
    final CountMeter createConnectionExceptions; 
	final CountMeter initialConnectionExceptions; 
	final CountMeter openConnectionSuccesses; 
    final RateMeter rowsQueriedRate;
    final RateMeter rowsUpdatedRate; 
    final RateMeter queryRate;
    final RateMeter updateRate; 
    final RateMeter beginTransactionRate;
    final RateMeter commitTransactionRate;
    final RateMeter rollbackTransactionRate;

    final CountMeter commitFailures;
    final CountMeter rollbackFailures;
    final CountMeter executeFailures;
    final RateMeter callRate;
	final CountMeter parkRollbackTransactions;
	final LinkedList<Transaction> lastParkRolledbackTransactions;
	final int maxLastParkRolledbackTransactions;
	public Connector(TraceManager traceManager,Logger logger,Disruptor disruptor,long maximumRecentlyUsedCount)
	{
		this.traceManager=traceManager;
		this.pool=new Pool<Accessor>(traceManager,maximumRecentlyUsedCount);
		this.closeConnectionExceptions=new CountMeter();
		this.createConnectionExceptions=new CountMeter();
		this.initialConnectionExceptions=new CountMeter();
		this.openConnectionSuccesses=new CountMeter();
		this.logger=logger;
        this.rowsQueriedRate=new RateMeter();
        this.rowsUpdatedRate=new RateMeter();
        this.queryRate=new RateMeter();
        this.updateRate=new RateMeter();
        this.beginTransactionRate=new RateMeter();
        this.commitTransactionRate=new RateMeter();
        this.rollbackTransactionRate=new RateMeter();
        this.callRate=new RateMeter();
        this.commitFailures=new CountMeter();
        this.rollbackFailures=new CountMeter();
        this.executeFailures=new CountMeter();
		this.disruptor=disruptor;
		this.parkRollbackTransactions=new CountMeter();
		this.lastParkRolledbackTransactions=new LinkedList<Transaction>();
		this.maxLastParkRolledbackTransactions=100;
	}
	abstract protected Connection createConnection() throws Throwable;
	abstract public String getName();
	
	public Accessor openAccessor(Trace parent,String traceCategoryOverride,String catalog,long timeoutMs) throws Throwable
	{
		if (traceCategoryOverride==null)
		{
			traceCategoryOverride="Connector."+getName()+".openAccessor";
		}
		Accessor accessor=this.pool.waitForAvailable(parent, traceCategoryOverride, timeoutMs);
		try
		{
		    accessor.setCatalog(catalog);
		}
		catch (Throwable t)
		{
		    accessor.close();
		    throw new Exception(t);
		}
		return accessor;
	}
	public Accessor openAccessor(Trace parent,String traceCategoryOverride) throws Throwable
	{
		if (traceCategoryOverride==null)
		{
            traceCategoryOverride="Connector."+getName()+".openAccessor";
		}
		return this.pool.waitForAvailable(parent, traceCategoryOverride);
	}
    public Accessor openAccessor(Trace parent,String traceCategoryOverride,String catalog) throws Throwable
    {
        if (traceCategoryOverride==null)
        {
            traceCategoryOverride="Connector."+getName()+".openAccessor";
        }
        Accessor accessor=this.pool.waitForAvailable(parent, traceCategoryOverride);
        try
        {
            accessor.setCatalog(catalog);
        }
        catch (Throwable t)
        {
            accessor.close();
            throw new Exception(t);
        }
        return accessor;
    }
    public Accessor openAccessor(Trace parent) throws Throwable
    {
        return openAccessor(parent,null);
    }
    public Accessor openAccessor(Trace parent,long timeoutMs) throws Throwable
    {
        return openAccessor(parent,null,null,timeoutMs);
    }
    
    
	
    public RowSet executeQuery(Trace parent, String traceCategoryOverride, String sql, Object... parameters) throws Throwable
    {
        return executeQuery(parent, traceCategoryOverride, parameters, sql);
    }
    public RowSet executeQuery(Trace parent, String traceCategoryOverride, Object[] parameters,String sql) throws Throwable
    {
        try (Accessor accessor=openAccessor(parent))
        {
            return accessor.executeQuery(parent, traceCategoryOverride,  parameters,sql);
        }
    }
    public RowSet executeQuery(Trace parent, String traceCategoryOverride, List<Object> parameters,String sql) throws Throwable
    {
        try (Accessor accessor=openAccessor(parent))
        {
            return accessor.executeQuery(parent, traceCategoryOverride,  parameters,sql);
        }
    }

    public int executeUpdate(Trace parent, String traceCategoryOverride, String sql, Object... parameters) throws Throwable
	{
		return executeUpdate(parent, traceCategoryOverride, parameters, sql);
	}
    public int executeUpdate(Trace parent, String traceCategoryOverride, Object[] parameters,String sql) throws Throwable
    {
        try (Accessor accessor=openAccessor(parent))
        {
            return accessor.executeUpdate(parent, traceCategoryOverride, parameters,sql);
        }
    }

    
    public int[] executeBatchUpdate(Trace parent, String traceCategoryOverride, Object[][] parameters,String sql) throws Throwable
    {
        try (Accessor accessor=openAccessor(parent))
        {
            return accessor.executeBatchUpdate(parent, traceCategoryOverride, parameters,sql);
        }
    }
    public int[] executeUpdateBatch(Trace parent, String traceCategoryOverride, List<List<Object>> parameters,String sql) throws Throwable
    {
        try (Accessor accessor=openAccessor(parent))
        {
            return accessor.executeBatchUpdate(parent, traceCategoryOverride, parameters,sql);
        }
    }
    public CountMeter getCloseConnectionExceptions()
    {
        return closeConnectionExceptions;
    }
    public CountMeter getCreateConnectionExceptions()
    {
        return createConnectionExceptions;
    }
    public CountMeter getInitialConnectionExceptions()
    {
        return initialConnectionExceptions;
    }
    public CountMeter getOpenConnectionSuccesses()
    {
        return openConnectionSuccesses;
    }
    public RateMeter getRowsQueriedRate()
    {
        return rowsQueriedRate;
    }
    public RateMeter getRowsUpdatedRate()
    {
        return rowsUpdatedRate;
    }
    public RateMeter getQueryRate()
    {
        return queryRate;
    }
    public RateMeter getUpdateRate()
    {
        return updateRate;
    }
    public RateMeter getBeginTransactionRate()
    {
        return beginTransactionRate;
    }
    public RateMeter getCommitTransactionRate()
    {
        return commitTransactionRate;
    }
    public RateMeter getRollbackTransactionRate()
    {
        return rollbackTransactionRate;
    }
    public CountMeter getCommitFailures()
    {
        return commitFailures;
    }
    public CountMeter getRollbackFailures()
    {
        return rollbackFailures;
    }
    public CountMeter getExecuteFailures()
    {
        return executeFailures;
    }
    public RateMeter getCallRate()
    {
        return callRate;
    }
    
    public LevelMeter getWaitingForAcessorsMeter()
    {
        return this.pool.getWaitingMeter();
    }
    public LevelMeter getAccessorsAvailableMeter()
    {
        return this.pool.getAvailableMeter();
    }
    public LongValueMeter getAcessorsWaitNsMeter()
    {
        return this.pool.getWaitNsMeter();
    }
    public RateMeter getUseMeter()
    {
        return this.pool.getUseMeter();
    }
    public Accessor[] getSnapshotOfAccessorsInUse()
    {
        List<Accessor> list=this.pool.getSnapshotOfInUseResources();
        return list.toArray(new Accessor[list.size()]);
    }
    public Accessor[] getSnapshotOfRetiredConnectors()
    {
        List<Accessor> list=this.pool.getSnapshotRetiredResources();
        return list.toArray(new Accessor[list.size()]);
    }
    public void setCaptureActivateStackTrace(boolean captureActivateStackTrace)
    {
        this.pool.setCaptureActivateStackTrace(captureActivateStackTrace);
    }
    public CountMeter getParkRolledbacks(Transaction transaction)
    {
        if (transaction!=null)
        {
            synchronized(this)
            {
                this.lastParkRolledbackTransactions.addFirst(transaction);
                if (this.lastParkRolledbackTransactions.size()>this.maxLastParkRolledbackTransactions)
                {
                    this.lastParkRolledbackTransactions.removeLast();
                }
            }
        }
        return this.parkRollbackTransactions;
    }
    public Transaction[] getSnapshotOfLastParkRolledbackTransactions()
    {
        synchronized(this)
        {
            return this.lastParkRolledbackTransactions.toArray(new Transaction[this.lastParkRolledbackTransactions.size()]);
        }
    }
    public Logger getLogger()
    {
        return this.logger;
    }

}
