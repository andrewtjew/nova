package org.nova.proxy;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.nova.json.ObjectMapper;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceRunnable;
import org.nova.utils.TypeUtils;

class ProxyConnection implements TraceRunnable
{
    static final boolean TEST=true;
    
        final OutsideServer server;
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;
        private ProxyConfiguration proxyConfiguration; 
        HashMap<Integer,OutsideConnection> outsideConnections;
        private long created;
        private long lastKeepAliveReceived;
        private long lastActivity;
        private ServerSocket serverSocket;
        private int remotePort;
        final private String remoteAddress;
        private AtomicLong in;
        private AtomicLong out;
        
        public ProxyConnection(OutsideServer server,Socket socket)
        {
            this.socket=socket;
            this.server=server;
            this.outsideConnections=new HashMap<>();
            this.created=System.currentTimeMillis();
            this.lastKeepAliveReceived=System.currentTimeMillis();

            InetSocketAddress socketAddress=(InetSocketAddress)this.socket.getRemoteSocketAddress();
            this.remoteAddress=socketAddress.getHostString()+":"+socketAddress.getPort();
            this.in=new AtomicLong(0);
            this.out=new AtomicLong(0);
        }
        
        public String getRemoteSocketAddress()
        {
            return this.remoteAddress;
        }
        public OutsideServer getServer()
        {
            return this.server;
        }
        public ProxyConfiguration getProxyConfiguration()
        {
            synchronized(this)
            {
                return this.proxyConfiguration;
            }
        }
        
        public int getOutsideConnectionSize()
        {
            synchronized(this.outsideConnections)
            {
                return this.outsideConnections.size();
            }
        }
        public OutsideConnection[] getOutsideConnections()
        {
            synchronized(this.outsideConnections)
            {
                return this.outsideConnections.values().toArray(new OutsideConnection[this.outsideConnections.size()]);
            }
        }
        
        public long getCreated()
        {
            return this.created;
        }
        public long getLastKeepAliveReceived()
        {
            return this.lastKeepAliveReceived;
        }
        public long getLastActivity()
        {
            return this.lastKeepAliveReceived;
        }
        
        @Override
        public void run(Trace parent) throws Throwable
        {
            try
            {
                OutsideConfiguration configuration=this.server.getConfiguration();
                socket.setReceiveBufferSize(configuration.insideReceiveBufferSize);
                socket.setSendBufferSize(configuration.insideSendBufferSize);
                socket.setSoTimeout(configuration.outsideReadTimeout);
                socket.setTcpNoDelay(true);
                this.inputStream=socket.getInputStream();
                this.outputStream=socket.getOutputStream();

                try (Trace readHeaderTrace=new Trace(parent,"ProxyHeader"))
                {
                    byte[] lengthBytes=new byte[4];
                    Packet.read(this.inputStream, lengthBytes, 0, 4);
                    int length=TypeUtils.bigEndianBytesToInt(lengthBytes);
                    
                    if (TEST)
                    {
                        System.out.println("header length:"+length);
                    }
                    byte[] bytes=new byte[length];
                    Packet.read(this.inputStream, bytes,0,length);
                    String text=new String(bytes);
                    if (TEST)
                    {
                        System.out.println("header text:"+text);
                    }
    
                    this.proxyConfiguration=ObjectMapper.readObject(text, ProxyConfiguration.class);
                    parent.setDetails("insideName="+this.proxyConfiguration.insideName+",outsideListenPort="+this.proxyConfiguration.outsideListenPort);
                    this.server.addProxyConnection(this.proxyConfiguration.outsideListenPort,this);
                }
                this.server.getMultiTaskSheduler().schedule(null, "proxyAcceptOutsideConnections", (trace)->{handleOutsideConnections(trace);});
                
                
                //Reads from inside 
                for (;;)
                {
                    Packet proxyPacket=Packet.readFromProxyStream(this.inputStream);
                    if (proxyPacket==null)
                    {
                        continue;
                    }
                    int dataSize=proxyPacket.size();
                    if (dataSize==0)
                    {
                        try (Trace trace=new Trace(parent,"proxy:respondToKeepAlive"))
                        {
                            this.lastKeepAliveReceived=System.currentTimeMillis();
                            synchronized (this)
                            {
                                if (this.outputStream!=null)
                                {
                                    proxyPacket.writeToProxyStream(this.outputStream);
                                }
                            }
                        }
                        continue;
                    }
                    try (Trace trace=new Trace(parent,"proxy:sendToOutside"))
                    {
//                        System.out.println("Proxy:sendToOutside");
                        sendToOutside(trace,proxyPacket);
                    }
                }
            }
            catch (Throwable t)
            {
                parent.close(t);
            }
            finally
            {
                this.server.removeProxyConnection(this);
                close();
            }
        }
        
        public void close()
        {
            synchronized(this)
            {
                try
                {
                    if (this.socket!=null)
                    {
                        this.socket.close();
                        this.socket=null;
                    }
                }
                catch (Throwable t)
                {
                    this.server.getLogger().log(t);
                    this.socket=null;
                }
                try
                {
                    if (this.serverSocket!=null)
                    {
                        this.serverSocket.close();
                        this.serverSocket=null;
                    }
                }
                catch (Throwable t)
                {
                    this.server.getLogger().log(t);
                    this.serverSocket=null;
                }
            }
            synchronized(this.outsideConnections)
            {
                for (OutsideConnection outsideConnection:this.outsideConnections.values())
                {
                    outsideConnection.close();
                }
            }
        }
//        public void closeHost(int port)
//        {
//            try
//            {
//                System.out.println("Proxy:CloseHost");
//                sendToInside(new Packet(port));
//            }
//            catch (Throwable t)
//            {
//                this.server.getLogger().log(t);
//            }
//        }

        
        public void sendToInside(Packet outsidePacket) throws Throwable
        {
            synchronized(this) //For this.outputStream. Used concurrently to respond to keep alive.
            {
                outsidePacket.writeToProxyStream(this.outputStream);
            }
            this.in.addAndGet(outsidePacket.size());
        }
        
        private void handleOutsideConnections(Trace parent) throws Throwable
        {
            OutsideConfiguration configuration=this.server.getConfiguration();
            ProxyConfiguration proxyConfiguration;
            synchronized(this.proxyConfiguration)
            {
                proxyConfiguration=this.proxyConfiguration;
            }
            try
            {
                
                try (ServerSocket serverSocket=new ServerSocket(proxyConfiguration.outsideListenPort,configuration.outsideBacklog))
                {
                    synchronized(this)
                    {
                        if (this.socket==null)
                        {
                            return;
                        }
                        this.serverSocket=serverSocket;
                    }
                    for (;;)
                    {
                       Socket socket = serverSocket.accept();
                       int port=socket.getPort();
//                       System.out.println("Proxy:accept,removeOutsideConnection,port="+port);
                       removeAndCloseOutsideConnection(port);
                       OutsideConnection connection;
                       synchronized(this.outsideConnections)
                       {
                           connection=new OutsideConnection(this, socket,port);
                           this.outsideConnections.put(port, connection);
                       }
                       this.server.getMultiTaskSheduler().schedule(parent, "OutsideConnection", connection);
                    }
                }
            }
            catch (Throwable t)
            {
                parent.setDetails("insideName="+proxyConfiguration.insideName+",outsideListenPort="+proxyConfiguration.outsideListenPort);
            }
            finally
            {
                close();
            }
        }
        
        public void removeAndCloseOutsideConnection(int port) throws Throwable
        {
            OutsideConnection connection;
            synchronized(this.outsideConnections)
            {
                connection=this.outsideConnections.remove(port);
            }
            if (connection!=null)
            {
//                System.out.println("Proxy:removeOutsideConnection closes outside,port="+connection.getPort());
                connection.close();
            }
//            closeHost(port);
        }
        
        private void sendToOutside(Trace parent,Packet proxyPacket) throws Throwable
        {
            int port=proxyPacket.getPort();
            OutsideConnection outsideConnection;
            synchronized (this.outsideConnections)
            {
                this.lastActivity=System.currentTimeMillis();
                outsideConnection=this.outsideConnections.get(port);
            }
            if (outsideConnection==null)
            {
                return;
            }
            try
            {
                int dataSize=proxyPacket.size();
                if (dataSize==4)
                {
                    synchronized (this.outsideConnections)
                    {
                        this.outsideConnections.remove(port);
                    }
                    outsideConnection.close();
                    return;
                }
                outsideConnection.sendToOutside(proxyPacket);
            }
            catch (Throwable t)
            {
                this.server.getLogger().log(t);
                removeAndCloseOutsideConnection(port);
            }
        }

        public void updateOut(long size)
        {
            this.out.addAndGet(size);
        }
        
        public long getIn()
        {
            return this.in.get();
        }
        public long getOut()
        {
            return this.out.get();
        }
        
        
        
    }