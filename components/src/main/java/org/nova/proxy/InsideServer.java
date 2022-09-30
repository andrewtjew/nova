package org.nova.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.nova.concurrent.MultiTaskScheduler;
import org.nova.concurrent.TimeBase;
import org.nova.concurrent.TimerRunnable;
import org.nova.concurrent.TimerScheduler;
import org.nova.concurrent.TimerTask;
import org.nova.frameworks.OperatorPage;
import org.nova.frameworks.ServerApplication;
import org.nova.frameworks.ServerApplicationPages.OperatorTable;
import org.nova.frameworks.ServerApplicationPages.WideTable;
import org.nova.html.deprecated.TableRow;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.operator.MenuBar;
import org.nova.html.operator.MoreButton;
import org.nova.html.operator.Panel1;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.HtmlContentWriter;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Path;
import org.nova.json.ObjectMapper;
import org.nova.logging.Logger;
import org.nova.tracing.Trace;
import org.nova.utils.DateTimeUtils;
import org.nova.utils.NetUtils;
import org.nova.utils.TypeUtils;

@ContentDecoders(GzipContentDecoder.class)
@ContentEncoders(GzipContentEncoder.class)
@ContentWriters({HtmlContentWriter.class, HtmlElementWriter.class})
public class InsideServer
{
    final private MultiTaskScheduler scheduler;
    final private TimerScheduler timer;
    final private InsideConfiguration configuration;
    final private Logger logger;
    final HashMap<Integer,HostConnection> hostConnections;
    private InputStream proxyInputStream;
    private OutputStream proxyOutputStream;
    Packet keepAlivePacket;
    private long lastKeepAliveSent;
    private long lastKeepAliveReceived;
    private long lastConnect;
    private long lastDisconnect;
    private final ServerApplication serverApplication;
    
    public InsideServer(MultiTaskScheduler scheduler,TimerScheduler timer,Logger logger,InsideConfiguration configuration,ServerApplication serverApplication)
    {
        this.configuration=configuration;
        this.scheduler=scheduler;
        this.logger=logger;
        this.timer=timer;
        this.keepAlivePacket=new Packet();
        this.hostConnections=new HashMap<>();
        this.lastConnect=0;
        this.lastDisconnect=0;
        if (serverApplication!=null)
        {
            this.serverApplication=serverApplication;
            MenuBar menuBar=serverApplication.getMenuBar();
            menuBar.add("/proxy/inside/viewConnections", "Proxy","View Host Connections");
        }
        else
        {
            this.serverApplication=null;
        }
        
    }

    public void start() throws IOException
    {
        this.scheduler.schedule(null, "Proxy", (trace)->{run(trace);});
        
    }
    
    public InsideConfiguration getConfiguration()
    {
        return this.configuration;
    }
    void read(byte[] buffer,int offset,int size) throws Throwable
    {
        int totalRead=0;
        while (totalRead!=size)
        {
            int read=this.proxyInputStream.read(buffer,offset+totalRead,size-totalRead);
            if (read<0)
            {
                throw new Exception();
            }
            totalRead+=read;
        }
    }
    
    
    public Logger getLogger()
    {
        return this.logger;
    }
    
    private void run(Trace parent) throws Throwable
    {
        synchronized(this)
        {
            this.proxyInputStream=null;
            this.proxyOutputStream=null;
        }
        this.timer.schedule("KeepAlive", TimeBase.FREE,1000,this.configuration.keepAliveInterval,(trace,event)->keepAlive());
        
        int reconnectDelay=this.configuration.reconnectDelay;
        for (;;)
        {
//            System.out.println("Connecting to outside");
            try
            {
                try (Socket socket=new Socket(this.configuration.proxyEndPoint,this.configuration.proxyPort))
                {
                    socket.setReceiveBufferSize(this.configuration.proxyReceiveBufferSize);
                    socket.setSendBufferSize(this.configuration.proxySendBufferSize);
                    socket.setTcpNoDelay(true);
                    synchronized(this)
                    {
                        this.proxyInputStream=socket.getInputStream();
                        this.proxyOutputStream=socket.getOutputStream();
                        this.lastConnect=System.currentTimeMillis();
                    }
                    ProxyConfiguration proxyConfiguration=new ProxyConfiguration();
                    proxyConfiguration.outsideListenPort=this.configuration.outsideListenPort;
                    proxyConfiguration.insideName=NetUtils.getLocalHostName();
                    proxyConfiguration.insideMacAddress=NetUtils.getMacAddress();
                    String text=ObjectMapper.writeObjectToString(proxyConfiguration);
                    byte[] bytes=text.getBytes();
                    this.proxyOutputStream.write(TypeUtils.bigEndianIntToBytes(bytes.length));
                    this.proxyOutputStream.write(bytes);
                    
                    for (;;)
                    {
                        Packet proxyPacket=Packet.readFromProxyStream(this.proxyInputStream);
                        int dataSize=proxyPacket.size();
                        if (dataSize==0)
                        {
                            this.lastKeepAliveReceived=System.currentTimeMillis();
                            reconnectDelay=this.configuration.reconnectDelay;
                            continue;
                        }
                        int outsidePort=proxyPacket.getPort();
                        if (dataSize==4)
                        {
                            this.closeOutside(outsidePort);
                        }
                        HostConnection connection;
                        synchronized(this.hostConnections)
                        {
                            connection=this.hostConnections.get(outsidePort);
                            if (connection==null)
                            {
                                connection=new HostConnection(this, outsidePort);
                                this.hostConnections.put(outsidePort, connection);
                                this.scheduler.schedule(parent, "HostConnection", connection);
                                System.out.println("Inside: new HostConnection, outsidePort="+outsidePort);
                            }
                            connection.writeToHost(proxyPacket);
                        }
                    }
                }
            }
            catch (Throwable t)
            {
                
                this.logger.log(t);
            }
            finally
            {
                synchronized(this)
                {
                    if (this.lastConnect>this.lastDisconnect)
                    {
                        this.lastDisconnect=System.currentTimeMillis();
                    }
                    this.proxyOutputStream=null;
                }
                synchronized (this.hostConnections)
                {
                    for (HostConnection connection:this.hostConnections.values())
                    {
                        connection.close();
                    }
                    this.hostConnections.clear();
                }
                
            }
//            System.out.println("Sleep before reconnect: "+reconnectDelay);
            Thread.sleep(reconnectDelay);
            
            reconnectDelay*=2;
            if (reconnectDelay>this.configuration.maxReconnectDelay)
            {
                reconnectDelay=this.configuration.maxReconnectDelay;
            }
        }
    }
    
    void removeHostConnection(int outsidePort) throws Throwable
    {
        HostConnection connection;
        synchronized (this.hostConnections)
        {
            connection=this.hostConnections.remove(outsidePort);
        }
        if (connection!=null)
        {
            connection.close();
        }
        closeOutside(outsidePort);
    }

    public void closeOutside(int outsidePort)
    {
        try
        {
            sendToProxy(new Packet(outsidePort));
        }
        catch (Throwable t)
        {
            this.getLogger().log(t);
        }
    }
    
    void sendToProxy(Packet hostPacket) throws Throwable
    {
        OutputStream outputStream;
        synchronized(this)
        {
            outputStream=this.proxyOutputStream;
        }
        hostPacket.writeToProxyStream(outputStream);
    }

    void keepAlive() throws Throwable
    {
        synchronized(this)
        {
            if (this.proxyOutputStream!=null)
            {
                this.keepAlivePacket.writeToProxyStream(this.proxyOutputStream);
                this.lastKeepAliveSent=System.currentTimeMillis();
            }
        }
    }
    @GET
    @Path("/proxy/inside/viewConnections")
    public Element viewConnections(Trace parent) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("View Proxy Connections");
        
        {      
            Panel1 panel=page.content().returnAddInner(new Panel1(page.head(),"Status"));
            WideTable table=panel.content().returnAddInner(new WideTable(page.head()));
            table.setHeader("Proxy","Last Connect","Last Disconnect","Last Keep Alive Sent","Last Keep Alive Received");
            TableRow tr=new TableRow();
            synchronized (this)
            {
                tr.add(this.lastConnect>this.lastDisconnect?"Connected":"Disconnected");
                tr.add(this.lastConnect>0?DateTimeUtils.toSystemDateTimeString(this.lastConnect):"");
                tr.add(this.lastDisconnect>0?DateTimeUtils.toSystemDateTimeString(this.lastDisconnect):"");
                tr.add(this.lastKeepAliveSent>0?DateTimeUtils.toSystemDateTimeString(this.lastKeepAliveSent):"");
                tr.add(this.lastKeepAliveReceived>0?DateTimeUtils.toSystemDateTimeString(this.lastKeepAliveReceived):"");
                table.addRow(tr);
            }
            
        }
        
       
        Panel1 panel=page.content().returnAddInner(new Panel1(page.head(),"Host Connections"));
        OperatorTable table=panel.content().returnAddInner(new OperatorTable(page.head()));
        table.setHeader("Host Port","Created","Total Received","Total Sent","Last Received","Last Sent");
        synchronized (this.hostConnections)
        {
            for (Entry<Integer, HostConnection> entry:this.hostConnections.entrySet())
            {
                TableRow tr=new TableRow();
                tr.add(entry.getKey());
                HostConnection connection=entry.getValue();
                tr.add(DateTimeUtils.toSystemDateTimeString(connection.getCreated()));
                tr.add(connection.getTotalReceived());
                tr.add(connection.getTotalSent());
                long lastReceived=connection.getLastReceived();
                tr.add(lastReceived>0?DateTimeUtils.toSystemDateTimeString(lastReceived):"");
                long lastSent=connection.getLastSent();
                tr.add(lastSent>0?DateTimeUtils.toSystemDateTimeString(lastSent):"");
                table.addRow(tr);
            }
        }
        
        return page;
    }   
}
