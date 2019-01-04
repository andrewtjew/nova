package com.geneva.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.nova.concurrent.Synchronization;
import org.nova.test.Testing;
import org.nova.testing.TestTraceClient;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;

public class ClientConnection
{
	static private boolean TRACE=false;

	private Socket socket;
	final private InputStream inputStream;
	final private OutputStream outputStream;
	final private long created;
	final private TcpServer server;
	final private String key;
	private boolean active;
	final private String receiveCategory; 
	final private String processCategory; 
	
	ClientConnection(String key,TcpServer server,Socket socket) throws Throwable
	{
		this.created=System.currentTimeMillis();
		this.server=server;
		this.socket=socket;
		this.inputStream=socket.getInputStream();
		this.outputStream=socket.getOutputStream();
		this.key=key;
		this.receiveCategory=this.server.categoryPrefix+"@receive."+this.socket.getLocalPort();
		this.processCategory=this.server.categoryPrefix+"@process."+this.socket.getLocalPort();
	}

	public Socket getSocket()
	{
		return this.socket;
	}
	public long getCreated()
	{
		return created;
	}


	public TcpServer getServer()
	{
		return server;
	}


	public String getKey()
	{
		return key;
	}


	void start()
	{
		synchronized(this)
		{
			this.active=true;
			new Thread(()->{main();}).start();
		}
	}
	
	private void readRequestHeader(byte[] header) throws Exception
	{
		int total=0;
		while (total<16)
		{
			int length=this.inputStream.read(header,total,16-total);
			if (length==-1)
			{
				throw new Exception();
			}
			total+=length;
		}
	}
	
	private byte[] read(int size) throws Exception
	{
		byte[] bytes=new byte[size];
		int total=0;
		while (total<size)
		{
			int length=this.inputStream.read(bytes,total,size-total);
			if (length==-1)
			{
				throw new Exception();
			}
			total+=length;
		}
		return bytes;
	}
	
	void main() 
	{
		byte[] header=new byte[16];
		try
		{
			for (;;)
			{
				try (Trace trace=new Trace(this.server.traceManager,this.receiveCategory,true))
				{
					try
					{
						if ((Testing.ENABLED)&&(TRACE))
						{
							TestTraceClient.clientLog("host="+this.socket.getInetAddress().getHostName()+", waiting");
						}
						readRequestHeader(header);
						trace.endWait();
						long id=TypeUtils.bigEndianBytesToLong(header,0);
						int type=TypeUtils.bigEndianBytesToInt(header,8);
						int size=TypeUtils.bigEndianBytesToInt(header,12);
						byte[] content=read(size);
						this.server.bytesReceivedMeter.update(size+header.length);
						Trace processingTrace=new Trace(this.server.traceManager,trace, this.processCategory,true);
						if ((Testing.ENABLED)&&(TRACE))
						{
							TestTraceClient.clientLog("host="+this.socket.getInetAddress().getHostName()+", id="+id);
							
						}
						this.server.schedule(new Context(processingTrace,this,id,type,content));
					}
					catch (Throwable t)
					{
						try
						{
							this.socket.close();
						}
						catch (IOException e)
						{
							this.server.logger.log(e);
						}
						this.server.closeClientConnection(trace, this.key);
						trace.close(t);
						return;
					}
				}
			}
		}
		finally
		{
			synchronized(this)
			{
				this.active=false;
				this.notifyAll();
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

	
	public void close() throws IOException
	{
		if ((Testing.ENABLED)&&(TRACE))
		{
			TestTraceClient.clientLog("host="+this.socket.getInetAddress().getHostName());
		}
		synchronized(this)
		{
			if (this.active==false)
			{
				return;
			}
			this.socket.close();
			Synchronization.waitForNoThrow(this, ()->{return this.active==false;});
		}
	}
	
	void sendResponse(long id,byte[] responseContent) throws Throwable 
	{
		byte[] header=new byte[12];
		TypeUtils.bigEndianLongToBytes(id,header,0);
		if (responseContent!=null)
		{
		    TypeUtils.bigEndianIntToBytes(responseContent.length,header,8);
		}
		else
		{
		    TypeUtils.bigEndianIntToBytes(-1,header,8);
		}
		synchronized (this)
		{
			this.outputStream.write(header);
			if (responseContent!=null)
			{
				this.outputStream.write(responseContent);
			}
			this.outputStream.flush();
			this.server.bytesSentMeter.update(header.length+(responseContent!=null?responseContent.length:0));
		}
	}
	void sendErrorResponse(long id) throws Throwable 
	{
		byte[] header=new byte[12];
		TypeUtils.bigEndianLongToBytes(id,header,0);
		TypeUtils.bigEndianIntToBytes(-2,header,8);
		synchronized (this)
		{
			this.outputStream.write(header);
			this.outputStream.flush();
		}
		this.server.bytesSentMeter.update(header.length);
	}
	
}
