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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import org.nova.concurrent.Synchronization;
import org.nova.metrics.LongValueMeter;
import org.nova.utils.TypeUtils;

public class TcpClient
{
	final private Socket socket;
	final private InputStream inputStream;
	final private OutputStream outputStream;
	final HashMap<Long,Waiter> waiters=new HashMap<>();
	private Thread thread;
	private long id=0;

	final private LongValueMeter bytesSentMeter;
	final private LongValueMeter bytesReceivedMeter;
	
	public TcpClient(String host,int port,int sendBufferSize,int receiveBufferSize) throws Exception
	{
		this.socket=new Socket(host,port);
		this.socket.setTcpNoDelay(true);
		this.socket.setReceiveBufferSize(receiveBufferSize);
		this.socket.setSendBufferSize(sendBufferSize);
		this.inputStream=this.socket.getInputStream();
		this.outputStream=this.socket.getOutputStream();
		this.bytesSentMeter=new LongValueMeter();
		this.bytesReceivedMeter=new LongValueMeter();
		this.thread=new Thread(()->{main();});
		this.thread.start();
	}
	public TcpClient(String host,int port) throws Exception
	{
		this(host, port, 65536,65536);
	}

	public void close() throws Exception
	{
		synchronized(this)
		{
			if (this.thread==null)
			{
				throw new Exception();
			}
			this.socket.close();
			this.thread.join();
		}
	}
	
	
	static class Waiter
	{
		byte[] bytes=null;
		boolean completed=false;
		int size;
		
		void complete(int size,byte[] bytes)
		{
			synchronized (this)
			{
				this.bytes=bytes;
				this.completed=true;
				this.size=size;
				this.notify();
			}
		}
	}
	
	public Socket getSocket()
	{
		return this.socket;
	}
	
	public Response sendReceive(int type,byte[] bytes,long timeoutMs) throws Throwable
	{
		return sendReceive(type, bytes, 0, bytes.length, timeoutMs);
	}	
	public Response sendReceive(int type,byte[] bytes,int offset,int length,long timeoutMs) throws Throwable
	{
		byte[] header=new byte[16];
		TypeUtils.bigEndianIntToBytes(type,header,8);
		TypeUtils.bigEndianIntToBytes(bytes.length,header,12);
		Waiter waiter=new Waiter();
		long id=0;
		try
		{
			synchronized(this.waiters)
			{
				id=this.id++;
				this.waiters.put(id, waiter);
				TypeUtils.bigEndianLongToBytes(id,header,0);
				this.outputStream.write(header);
				this.outputStream.write(bytes,offset,length);
				this.outputStream.flush();
			}
			this.bytesSentMeter.update(length+header.length);
			
			synchronized(waiter)
			{
				if (Synchronization.waitForNoThrow(waiter,timeoutMs, ()->{return waiter.completed;})==false)
				{
					return new Response(Status.TIMEOUT,null);
				}
				if (waiter.size==-2)
				{
					return new Response(Status.SERVER_ERROR,null);
				}
				return new Response(Status.SUCCESS,waiter.bytes);
			}
		}
		finally
		{
			synchronized(this.waiters)
			{
				this.waiters.remove(id);
			}
		}
	}
	
	
	public void send(int type,byte[] bytes) throws Throwable
	{
		send(type,bytes,0,bytes.length);
	}
	public void send(int type,byte[] bytes,int offset,int length) throws Throwable
	{
		byte[] header=new byte[16];
		TypeUtils.bigEndianLongToBytes(0,header,0);
		TypeUtils.bigEndianIntToBytes(type,header,8);
		TypeUtils.bigEndianIntToBytes(bytes.length,header,12);
		synchronized(this.waiters)
		{
			this.outputStream.write(header);
			this.outputStream.write(bytes,offset,length);
			this.outputStream.flush();
		}
		this.bytesSentMeter.update(length+header.length);
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
	
	private void readResponseHeader(byte[] header) throws Exception
	{
		int total=0;
		while (total<12)
		{
			int length=this.inputStream.read(header,total,12-total);
			if (length==-1)
			{
				throw new Exception();
			}
			total+=length;
		}
	}
	
	private void main()
	{
		try
		{
			receive();
		}
		catch (Throwable t)
		{ 
		}
	}

	private void receive() throws Exception
	{
		byte[] header=new byte[12];
		for (;;)
		{
			readResponseHeader(header);
			long id=TypeUtils.bigEndianBytesToLong(header,0);
			int size=TypeUtils.bigEndianBytesToInt(header,8);
			byte[] response=null;
			if (size>=0)
			{
				response=read(size);
			}
			this.bytesReceivedMeter.update(size+header.length);
			Waiter waiter;
			synchronized (this.waiters)
			{
				waiter=this.waiters.get(id);
			}
			if (waiter!=null)
			{
				waiter.complete(size,response);
			}
		}
	}
}
