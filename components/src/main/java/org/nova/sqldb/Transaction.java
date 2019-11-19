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
import org.nova.core.MultiException;
import org.nova.tracing.Trace;

public class Transaction implements AutoCloseable
{
	private Accessor accessor;
	final Trace trace;
	
	Transaction(Accessor accessor,Trace trace)
	{
		this.trace=trace;
		this.accessor=accessor;
	}
	
	private Throwable closeConnection(Throwable throwable)
	{
        try
        {
            Connection connection=this.accessor.connection;
            this.accessor.connection=null;
            connection.close();
        }
        catch (Throwable t)
        {
            throwable=new MultiException(t,throwable);
        }
        this.accessor.connector.logger.log(this.trace,this.accessor.connector.getName());
        this.trace.close(throwable);
        return throwable;
	}
	
	public void commit() throws Throwable
	{
		synchronized(this)
		{
			if (this.accessor!=null)
			{
				try
				{
					this.accessor.commit();
				}
                catch (Throwable t)
                {
                    this.accessor.connector.commitFailures.increment();
                    throw closeConnection(t);
                }
				finally
				{
					this.trace.close();
					this.accessor=null;
				}
			}
		}
	}
	
	public void rollback() throws Throwable
	{
		synchronized(this)
		{
			if (this.accessor!=null)
			{
				try
				{
					this.accessor.rollback();
				}
				catch (Throwable t)
				{
                    this.accessor.connector.rollbackFailures.increment();
                    throw closeConnection(t);
				}
				finally
				{
                    this.trace.close();
					this.accessor=null;
				}
			}
		}
			
	}
	
	@Override
	public void close() throws Exception
	{
		try
		{
			rollback();
		}
		catch (Throwable t)
		{
			throw new Exception(t);
		}
	}

}
