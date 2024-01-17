package org.nova.proxy;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.nova.tracing.Trace;
import org.nova.tracing.TraceRunnable;

class HostConnection implements TraceRunnable
{
    final InsideServer server;
    final private long created;
    final private int outputPort;
    final private OutputStream outputStream;
    long totalReceived;
    long totalSent;
    long lastReceived;
    long lastSent;
    private Socket socket;
    
    public HostConnection(InsideServer server,int outsidePort) throws Throwable
    {
        this.created=System.currentTimeMillis();
        this.server=server;
        this.outputPort=outsidePort;

        InsideConfiguration configuration=this.server.getConfiguration();
        this.socket=new Socket(configuration.hostEndPoint,configuration.hostPort);
        socket.setReceiveBufferSize(configuration.hostReceiveBufferSize);
        socket.setSendBufferSize(configuration.hostSendBufferSize);
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(configuration.hostReadTimeout);
        this.outputStream=socket.getOutputStream();
        this.totalReceived=0;
        this.totalSent=0;
    }
    @Override
    public void run(Trace parent) throws Throwable
    {
        try
        {
            InsideConfiguration configuration=this.server.getConfiguration();
            InputStream inputStream=socket.getInputStream();
            Packet hostPacket=new Packet(configuration.hostReceiveBufferSize, this.outputPort);

            for (;;)
            {
                int read;
                try
                {
                    read=hostPacket.readFromStream(inputStream);
                }
                catch (SocketTimeoutException ex)
                {
                    continue;
                }
                if (read<0)
                {
                    break;
                }
                if (read>0)
                {
                    this.server.sendToProxy(hostPacket);
                    synchronized(this)
                    {
                        this.totalReceived+=read;
                        this.lastReceived=System.currentTimeMillis();
                    }
                }
            }
            
        }
        finally
        {
            this.server.removeHostConnection(this.outputPort);
            close();
        }
    }

    public void writeToHost(Packet proxyPacket) throws Throwable
    {
        synchronized(this)
        {
            //this.outputStream cannot be null. If so, then implementation error.
            proxyPacket.writeToStream(this.outputStream);
            this.totalSent+=proxyPacket.size()-4;
            this.lastSent=System.currentTimeMillis();
        }
    }
    
    public long getTotalReceived()
    {
        synchronized(this)
        {
            return this.totalReceived;
        }
    }
    public long getTotalSent()
    {
        synchronized(this)
        {
            return this.totalSent;
        }
    }
    public long getLastSent()
    {
        synchronized(this)
        {
            return this.lastSent;
        }
    }
    public long getLastReceived()
    {
        synchronized(this)
        {
            return this.lastReceived;
        }
    }
    
    public long getCreated()
    {
        return this.created;
    }
    public void close()
    {
        try
        {
            synchronized(this)
            {
                if (this.socket!=null)
                {
                    this.socket.close();
                    this.socket=null;
                }
            }
        }
        catch (Throwable t)
        {
            this.server.getLogger().log(t);
            synchronized(this)
            {
                this.socket=null;
            }
        }
    }
}