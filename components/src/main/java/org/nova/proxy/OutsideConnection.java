package org.nova.proxy;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Queue;

import org.nova.concurrent.Synchronization;
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
        private Long sending;
        private long lastReceived;
        private long totalReceived;
        private long totalSent;
        LinkedList<Packet> sendPackets;
        private long sendPacketsSize;
        
        public OutsideConnection(ProxyConnection proxyConnection,Socket socket,int port)
        {
            this.created=System.currentTimeMillis();
            this.socket=socket;
            this.proxyConnection=proxyConnection;
            this.port=port;
            this.lastReceived=System.currentTimeMillis();
            this.sendPackets=new LinkedList<>();
        }
        
        private void sender(Trace parent) throws Throwable
        {
            for (;;)
            {
                Packet packet=null;
                synchronized(this)
                {
                    Synchronization.waitFor(this, ()->{return this.sendPackets.size()>0||this.socket==null;});
                    if (this.socket==null)
                    {
                        return;
                    }
                    if (this.sendPackets.size()>0)
                    {
                        packet=this.sendPackets.remove();
                        if (this.sendPackets.size()==0)
                        {
                            this.sending=null;
                        }
                        else if (this.sending==null)
                        {
                            this.sending=System.currentTimeMillis();
                        }
                    }
                }
                if (packet!=null)
                {
                    try (Trace trace=new Trace(parent,"Outside:writeToOutside"))
                    {
                        packet.writeToStream(this.outputStream);
                    }
                    synchronized(this)
                    {
                        long sent=packet.size()-4;
                        this.totalSent+=sent;
                        this.lastSent=System.currentTimeMillis();
                        this.proxyConnection.updateOut(sent);
                        if (this.sendPackets.size()==0)
                        {
                            this.sendPacketsSize=0;
                        }
                        else
                        {
                            this.sendPacketsSize-=packet.size();
                        }
                     }
                }
            }
            
        }
        
        
        private OutsideConfiguration configuration;
        
        @Override
        public void run(Trace parent) throws Throwable
        {
            try
            {
                this.configuration=this.proxyConnection.getServer().getConfiguration();
                socket.setReceiveBufferSize(this.configuration.outsideReceiveBufferSize);
                socket.setSendBufferSize(this.configuration.outsideSendBufferSize);
                socket.setSoTimeout(this.configuration.outsideReadTimeout);
                socket.setTcpNoDelay(true);
                
                InputStream inputStream=socket.getInputStream();
                Packet packet=new Packet(this.configuration.outsideReceiveBufferSize+8, this.port);
                this.outputStream=socket.getOutputStream();

                this.proxyConnection.getServer().getMultiTaskSheduler().schedule(parent, "OutsideSender", (trace)->{sender(trace);});
                
                for (;;)
                {
                    int read;
                    try
                    {
                        read=packet.readFromStream(inputStream);
                    }
                    catch (SocketTimeoutException ex)
                    {
                        continue;
                    }
                    if (read<0)
                    {
//                        System.out.println("Outside:close,removeOutsideConnection,port="+port);
//                        this.proxyConnection.sendToInside(packet);
                        this.proxyConnection.removeAndCloseOutsideConnection(this.port);
                        break;
                    }
                    synchronized(this)
                    {
                        this.totalReceived+=read;
                        this.lastReceived=System.currentTimeMillis();
                    }
                    if (read>0)
                    {
//                        System.out.println("Outside:port="+this.port+",readSize="+read);
                        this.proxyConnection.sendToInside(packet);
                    }
                }
                
            }
            catch (Throwable t)
            {
//                System.out.println("Outside:exception,removeOutsideConnection,port="+port);
                ProxyConfiguration proxyConfiguration=this.proxyConnection.getProxyConfiguration();
                parent.setDetails("InsideName="+proxyConfiguration.insideName+",outsideListenPort="+proxyConfiguration.outsideListenPort+",port="+this.port);
                parent.close(t);
                this.proxyConnection.removeAndCloseOutsideConnection(this.port);
            }
        }
        
        public void sendToOutside(Packet packet) throws Exception
        {
            long now=System.currentTimeMillis();
            synchronized(this)
            {
                if (this.configuration.outsideSendTimeout>=0)
                {
                    if (this.sending!=null)
                    {
                        long span=now-this.sending;
                        if (span>this.configuration.outsideSendTimeout)
                        {
    //                        System.out.println("Outside:Timeout");
                            throw new Exception("SendTimeout");
                        }
                    }
                }
                this.sendPackets.add(packet);
                this.sendPacketsSize+=packet.size();
                if ((this.configuration.sendPacketsSize>=0)&&(this.sendPacketsSize>=this.configuration.sendPacketsSize))
                {
  //                  System.out.println("Outside:SendpacketSize,queue="+this.sendPackets.size());
                    throw new Exception("sendPacketsSize="+this.sendPacketsSize);
                }
                
                this.notifyAll();
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
                        this.notifyAll();
                    }
                }
            }
            catch (Throwable t)
            {
                this.proxyConnection.getServer().getLogger().log(t);
            }
        }
    }