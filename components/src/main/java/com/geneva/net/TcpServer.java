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
package com.geneva.net;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nova.logging.Logger;
import org.nova.metrics.LongValueMeter;
import org.nova.testing.Debugging;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;

public class TcpServer
{
	final private int port;
	final private ExecutorService executorService;

	final Receivable receivable;
	final TraceManager traceManager;
	final String categoryPrefix;

	private Thread acceptThread;
	private ServerSocket acceptSocket;
	final Logger logger;
	final Connectable connectable;
	final private HashMap<String,ClientConnection> clientConnections;
	
	final LongValueMeter bytesSentMeter;
	final LongValueMeter bytesReceivedMeter;
//	final CountMeter closeConnectionFailedMeter;
//	final CountMeter closeConnectableFailedMeter;
//	final CountMeter acceptConnectableFailedMeter;
	
	public TcpServer(String categoryPrefix,Logger logger,TraceManager traceManager,ExecutorService executorService,int port,Receivable receivable,Connectable connectable) throws Throwable
	{
		this.logger=logger;
		this.connectable=connectable;
		this.categoryPrefix=categoryPrefix;
		this.traceManager=traceManager;
		this.executorService=executorService;
		this.port=port;
		this.receivable=receivable;
		this.bytesSentMeter=new LongValueMeter();
		this.bytesReceivedMeter=new LongValueMeter();
		this.clientConnections=new HashMap<>();
	}
	
	public TcpServer(Logger logger,TraceManager traceManager,int maximumThreads,int port,Receivable receivable,Connectable connectable) throws Throwable
	{
		this(TcpServer.class.getSimpleName(),logger,traceManager, maximumThreads>0?Executors.newFixedThreadPool(maximumThreads):Executors.newVirtualThreadPerTaskExecutor(), port, receivable,connectable);
	}
	
	public void start() throws Exception
	{
		synchronized(this)
		{
			if (this.acceptThread!=null)
			{
				throw new Exception();
			}
			this.acceptThread=new Thread(()->{acceptThread();});
			this.acceptThread.start();
			this.active=true;
		}			
	}
	
	public void stop() throws Throwable
	{
		synchronized(this)
		{
			if (this.acceptThread==null)
			{
				throw new Exception();
			}
			this.acceptSocket.close();
			this.acceptThread.join();
			this.acceptThread=null;
		}
	}
	
	class Request implements Runnable
	{
		final private Context context;
		
		Request(Context context)
		{
			this.context=context;
		}
		@Override
		public void run()
		{
			try
			{
				receivable.process(this.context.trace,this.context);
			}
			catch (Throwable t) 
			{
				t.printStackTrace();
				context.responseWithError();
			}
		}
	} 
	
	void schedule(Context context)
	{
		this.executorService.submit(new Request(context));
	}

	private boolean active=false;
	
	void closeClientConnection(Trace parent,String key)
	{
		ClientConnection connection;
		synchronized(this.clientConnections)
		{
			connection=this.clientConnections.remove(key);
		}
		if (connection!=null)
		{
			if ((this.connectable!=null))
			{
				try (Trace trace=new Trace(traceManager,parent, this.categoryPrefix+"@TcpServer.close"))
				{
					try
					{
						this.connectable.close(trace, connection);
					}
					catch (Throwable t)
					{
						trace.close(t);
						this.logger.log(trace);
					}
				}
			}
		}
	}

	boolean acceptClientConnection(Trace parent,ClientConnection connection)
	{
		if (this.connectable!=null)
		{
			try (Trace trace=new Trace(traceManager,parent, this.categoryPrefix+"@TcpServer.accept"))
			{
				try
				{
					return this.connectable.accept(trace, connection);
				}
				catch (Throwable t)
				{
					trace.close(t);
					this.logger.log(trace);
				}
			}
			return false;
		}
		return true;
	}
    private static boolean TESTING=false;
    
	
	private void acceptThread() 
	{
		try
		{
			this.acceptSocket=new ServerSocket(this.port);
			for (;;)
			{
				try (Trace trace=new Trace(this.traceManager, this.categoryPrefix+".accept",true))
				{
					Socket clientSocket=this.acceptSocket.accept();
					if (TESTING)
					{
						Debugging.log("host="+clientSocket.getInetAddress().getHostName());
					}
					trace.endWait();
					try
					{
						clientSocket.setReceiveBufferSize(65536);
						clientSocket.setSendBufferSize(65536);
						clientSocket.setTcpNoDelay(true);
						
						String key=clientSocket.getInetAddress().getHostAddress()+":"+clientSocket.getPort();
						ClientConnection existingConnection;
						synchronized(this.clientConnections)
						{
							existingConnection=this.clientConnections.get(key);
						}
						if (existingConnection!=null)
						{
							existingConnection.close();
						}
						ClientConnection clientConnection=new ClientConnection(key, this, clientSocket);
						if (acceptClientConnection(trace, clientConnection)==true)
						{
							synchronized(this.clientConnections)
							{
								this.clientConnections.put(key, clientConnection);
							}
							clientConnection.start();
						}
						else
						{
							clientSocket.close();
						}
					}					
					catch (Throwable t)
					{
						trace.close(t);
						this.logger.log(trace);
					}
				}
			}
		}
		catch (Throwable e)
		{
			return;
		}
		finally
		{
			synchronized(this)
			{
				this.active=false;
			}
		}
	}
	
	public boolean isActive()
	{
		synchronized(this)
		{
			return this.active;
		}
	}
	
	public ClientConnection[] getClientConnectionSnapshot()
	{
		synchronized(this.clientConnections)
		{
			return this.clientConnections.values().toArray(new ClientConnection[this.clientConnections.size()]);
		}
	}
}
