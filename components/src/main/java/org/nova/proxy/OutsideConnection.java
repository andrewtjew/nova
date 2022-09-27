package org.nova.proxy;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.nova.tracing.Trace;
import org.nova.tracing.TraceRunnable;

    class OutsideConnection implements TraceRunnable
    {
        final ProxyConnection proxyConnection;
        final private long created;
        final private int port;
        private Socket socket;
        private OutputStream outputStream;
        private long lastSent;
        private long lastReceived;
        private long totalReceived;
        private long totalSent;
        
        public OutsideConnection(ProxyConnection proxyConnection,Socket socket,int port)
        {
            this.created=System.currentTimeMillis();
            this.socket=socket;
            this.proxyConnection=proxyConnection;
            this.port=port;
            this.lastReceived=System.currentTimeMillis();
        }
        @Override
        public void run(Trace parent) throws Throwable
        {
            try
            {
                OutsideConfiguration configuration=this.proxyConnection.getServer().getConfiguration();
                socket.setReceiveBufferSize(configuration.outsideReceiveBufferSize);
                socket.setSendBufferSize(configuration.outsideSendBufferSize);
                socket.setTcpNoDelay(true);
                InputStream inputStream=socket.getInputStream();
                Packet packet=new Packet(configuration.outsideReceiveBufferSize+8, this.port);

                synchronized(this)
                {
                    this.outputStream=socket.getOutputStream();
                }
                
                for (;;)
                {
                    int read=packet.readFromStream(inputStream);
//                    System.out.println("OutsideConnection: port="+this.port+", read="+read);
                    if (read<0)
                    {
                        break;
                    }
                    synchronized(this)
                    {
                        this.totalReceived+=read;
                        this.lastReceived=System.currentTimeMillis();
                    }
                    if (read>0)
                    {
                        this.proxyConnection.sendToProxy(packet);
                    }
                }
                
            }
            finally
            {
                this.proxyConnection.removeOutsideConnection(this.port);
                close();
            }
        }
        
        public String getHost()
        {
            InetSocketAddress socketAddress=(InetSocketAddress)socket.getRemoteSocketAddress();
            return socketAddress.getHostString();
        }

        public int getPort()
        {
            return this.port;
        }

        public void writeToOutside(Packet proxyPacket) throws Throwable
        {
            synchronized(this)
            {
                //this.outputStream cannot be null. If so, then implementation error.
                proxyPacket.writeToStream(this.outputStream);
                this.totalSent+=proxyPacket.size()-4;
                this.lastSent=System.currentTimeMillis();
            }
        }
        
        public long getCreated()
        {
            return this.created;
        }
        public long getLastReceived()
        {
            synchronized(this)
            {
                return this.lastReceived;
            }
        }
        public long getLastSent()
        {
            synchronized(this)
            {
                return this.lastSent;
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
                this.proxyConnection.getServer().getLogger().log(t);
            }
        }
    }