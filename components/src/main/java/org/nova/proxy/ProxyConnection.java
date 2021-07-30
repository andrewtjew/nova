package org.nova.proxy;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import org.nova.json.ObjectMapper;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceRunnable;
import org.nova.utils.TypeUtils;

class ProxyConnection implements TraceRunnable
    {
        final OutsideServer server;
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;
        private ProxyConfiguration proxyConfiguration; 
        HashMap<Integer,OutsideConnection> outsideConnections;
        private long created;
        final private String key;
        private long lastKeepAliveReceived;
        private ServerSocket serverSocket;
        
        
        public ProxyConnection(OutsideServer server,Socket socket,String key)
        {
            this.socket=socket;
            this.server=server;
            this.outsideConnections=new HashMap<>();
            this.created=System.currentTimeMillis();
            this.key=key;
            this.lastKeepAliveReceived=System.currentTimeMillis();
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
        
        @Override
        public void run(Trace parent) throws Throwable
        {
            try
            {
                OutsideConfiguration configuration=this.server.getConfiguration();
                socket.setReceiveBufferSize(configuration.insideReceiveBufferSize);
                socket.setSendBufferSize(configuration.insideSendBufferSize);
                socket.setTcpNoDelay(true);
                this.inputStream=socket.getInputStream();
                this.outputStream=socket.getOutputStream();

                byte[] lengthBytes=new byte[4];
                Packet.read(this.inputStream, lengthBytes, 0, 4);
                int length=TypeUtils.bigEndianBytesToInt(lengthBytes);
                byte[] bytes=new byte[length];
                Packet.read(this.inputStream, bytes,0,length);
                String text=new String(bytes);

                synchronized(this)
                {
                    this.proxyConfiguration=ObjectMapper.readObject(text, ProxyConfiguration.class);
                }
                this.server.getMultiTaskSheduler().schedule(null, "handleOutsideConnections", (trace)->{handleOutsideConnections(trace);});

                
                for (;;)
                {
                    Packet proxyPacket=Packet.readFromProxyStream(this.inputStream);
                    int dataSize=proxyPacket.getDataSize();
                    if (dataSize==0)
                    {
                        this.lastKeepAliveReceived=System.currentTimeMillis();
                        synchronized (this)
                        {
                            if (this.outputStream!=null)
                            {
                                proxyPacket.writeToProxyStream(this.outputStream);
                            }
                        }
                        continue;
                    }
                    sendToOutside(proxyPacket);
                }
            
            }
            finally
            {
                this.server.removeProxyConnection(this.key);
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
        public void closeHost(int port)
        {
            try
            {
                sendToProxy(new Packet(port));
            }
            catch (Throwable t)
            {
                this.server.getLogger().log(t);
            }
        }
        
        public void sendToProxy(Packet outsidePacket) throws Throwable
        {
            synchronized(this)
            {
                outsidePacket.writeToProxyStream(this.outputStream);
            }
        }
        
        private void handleOutsideConnections(Trace parent) throws Throwable
        {
            OutsideConfiguration configuration=this.server.getConfiguration();
            int outsideListenPort;
            synchronized(this.proxyConfiguration)
            {
                outsideListenPort=this.proxyConfiguration.outsideListenPort;
            }
            try
            {
                
                try (ServerSocket serverSocket=new ServerSocket(outsideListenPort,configuration.outsideBacklog))
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
                       removeOutsideConnection(port);
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
                throw new Throwable("outsideListenPort="+outsideListenPort,t);
            }
            finally
            {
                close();
            }
        }
        
        public void removeOutsideConnection(int port) throws Throwable
        {
            OutsideConnection connection;
            synchronized(this.outsideConnections)
            {
                connection=this.outsideConnections.remove(port);
            }
            if (connection!=null)
            {
                connection.close();
            }
            closeHost(port);
        }
        
        public void sendToOutside(Packet proxyPacket) throws Throwable
        {
            int port=proxyPacket.getPort();
            OutsideConnection outsideConnection;
            synchronized (this.outsideConnections)
            {
                outsideConnection=this.outsideConnections.get(port);
            }
            if (outsideConnection==null)
            {
                closeHost(port);
                return;
            }
            try
            {
                int dataSize=proxyPacket.getDataSize();
                if (dataSize==4)
                {
                    synchronized (this.outsideConnections)
                    {
                        this.outsideConnections.remove(port);
                    }
                    outsideConnection.close();
                    return;
                }
                outsideConnection.writeToOutside(proxyPacket);
            }
            catch (Throwable t)
            {
                this.server.getLogger().log(t);
                removeOutsideConnection(port);
            }
        }
        
        
    }