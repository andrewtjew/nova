package org.nova.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;

import org.nova.concurrent.MultiTaskScheduler;
import org.nova.frameworks.OperatorPage;
import org.nova.frameworks.ServerApplication;
import org.nova.frameworks.ServerOperatorPages.OperatorTable;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.operator.MenuBar;
import org.nova.html.operator.MoreButton;
import org.nova.html.operator.TableRow;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.HtmlContentWriter;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.logging.Logger;
import org.nova.tracing.Trace;
import org.nova.utils.DateTimeUtils;

@ContentDecoders(GzipContentDecoder.class)
@ContentEncoders(GzipContentEncoder.class)
@ContentWriters({HtmlContentWriter.class, HtmlElementWriter.class})
public class OutsideServer
{
    final private MultiTaskScheduler scheduler;
    final private OutsideConfiguration configuration;
    final private Logger logger;
    private final ServerApplication serverApplication;
    final private HashMap<Integer,ProxyConnection> proxyConnections;

    public OutsideServer(MultiTaskScheduler scheduler,Logger logger,OutsideConfiguration configuration,ServerApplication serverApplication)
    {
        this.scheduler=scheduler;
        this.configuration=configuration;
        this.logger=logger;
        
        if (serverApplication!=null)
        {
            this.serverApplication=serverApplication;
            MenuBar menuBar=serverApplication.getMenuBar();
            menuBar.add("/proxy/outside/viewConnections", "Proxy","View Proxy Connections");
        }
        else
        {
            this.serverApplication=null;
        }
        this.proxyConnections=new HashMap<>();
    }
    
    public OutsideConfiguration getConfiguration()
    {
        return this.configuration;
    }
    
    public MultiTaskScheduler getMultiTaskSheduler()
    {
        return this.scheduler;
    }
    
    public void start() throws IOException
    {
        this.scheduler.schedule(null, "acceptProxyConnections", (trace)->{handleProxyConnection(trace);});
    }
    private void handleProxyConnection(Trace parent) throws Exception
    {
        for (;;)
        {
            try
            {
                try (ServerSocket serverSocket=new ServerSocket(this.configuration.insidePort,1))
                {
                    for (;;)
                    {
                       Socket socket = serverSocket.accept();
                       InetSocketAddress socketAddress=(InetSocketAddress)socket.getRemoteSocketAddress();
//                       ProxyConnection current;
                       ProxyConnection connection=new ProxyConnection(this,socket);
//                       synchronized(this.proxyConnections)
//                       {
//                           current=this.proxyConnections.remove(key);
//                           this.proxyConnections.put(key, connection);
//                       }
//                       if (current!=null)
//                       {
//                           current.close();
//                       }
                       this.scheduler.schedule(parent, "InsideConnection", connection);
                    }
                }
            }
            catch (Throwable t)
            {
                this.logger.log(t);
            }
        }
    }
    public void addProxyConnection(Integer port,ProxyConnection newConnection)
    {
        ProxyConnection old;
        synchronized(this.proxyConnections)
        {
            old=this.proxyConnections.put(port, newConnection);
        }
        System.out.println("addProxyConnection:"+newConnection.getRemoteSocketAddress());
        if (old!=null)
        {
            System.out.println("addProxyConnection:removed="+old.getRemoteSocketAddress());
            old.close();
        }
    }
    
    public void removeProxyConnection(ProxyConnection connection)
    {
        int port=connection.getProxyConfiguration().outsideListenPort;
        ProxyConnection existing;
        System.out.println("removeProxyConnection:"+connection.getRemoteSocketAddress());
        synchronized(this.proxyConnections)
        {
            existing=this.proxyConnections.get(port);
            if (existing!=null)
            {
                System.out.println("removeProxyConnection:existing="+existing.getRemoteSocketAddress());
                if (connection.getRemoteSocketAddress().equals(existing.getRemoteSocketAddress()))
                {
                    this.proxyConnections.remove(port);
                }
            }
        }
        connection.close();
    }
    public Logger getLogger()
    {
        return this.logger;
    }
    
    @GET
    @Path("/proxy/outside/viewConnections")
    public Element viewConnections(Trace parent) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("View Proxy Connections");
        OperatorTable table=page.content().returnAddInner(new OperatorTable(page.head()));
        table.setHeader("Port","Inside Name","MAC","Remote","In","Out","Created","KeepAlive","Activity","Sockets","");
        synchronized (this.proxyConnections)
        {
            for (Entry<Integer, ProxyConnection> entry:this.proxyConnections.entrySet())
            {
                ProxyConnection connection=entry.getValue();
                ProxyConfiguration configuration=connection.getProxyConfiguration();

                TableRow tr=new TableRow();
                tr.add(configuration.outsideListenPort,configuration.insideName,configuration.insideMacAddress);
                tr.add(connection.getRemoteSocketAddress());
                tr.add(connection.getIn());
                tr.add(connection.getOut());
                tr.add(DateTimeUtils.toSystemDateTimeString(connection.getCreated()));
                tr.add(DateTimeUtils.toSystemDateTimeString(connection.getLastKeepAliveReceived()));
                tr.add(DateTimeUtils.toSystemDateTimeString(connection.getLastActivity()));
                tr.add(connection.getOutsideConnectionSize());
                tr.add(new MoreButton(page.head(),new PathAndQuery("/proxy/outside/viewConnection").addQuery("port",entry.getKey()).toString()));
                table.addRow(tr);
            }
        }
        
        return page;
    }   

    @GET
    @Path("/proxy/outside/viewConnection")
    public Element viewConnection(Trace parent,@QueryParam("port") int port) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("View Outside Connection: "+port);
        ProxyConnection connection;
        synchronized (this.proxyConnections)
        {
            connection=this.proxyConnections.get(port);
        }
        if (connection!=null)
        {
            OperatorTable table=page.content().returnAddInner(new OperatorTable(page.head()));
            table.setHeader("Host","Port","Created","Total Received","Total Sent","Last Received","Last Sent");
            OutsideConnection[] outsideConnections=connection.getOutsideConnections();
            for (OutsideConnection outsideConnection:outsideConnections)
            {
                TableRow tr=new TableRow();
                tr.add(outsideConnection.getHost());
                tr.add(outsideConnection.getPort());
                tr.add(DateTimeUtils.toSystemDateTimeString(outsideConnection.getCreated()));
                tr.add(outsideConnection.getTotalReceived()); 
                tr.add(outsideConnection.getTotalSent());

                long lastReceived=outsideConnection.getLastReceived();
                tr.add(lastReceived>0?DateTimeUtils.toSystemDateTimeString(lastReceived):"");
                long lastSent=outsideConnection.getLastSent();
                tr.add(lastSent>0?DateTimeUtils.toSystemDateTimeString(lastSent):"");
                table.addRow(tr);
            }
        }
        else
        {
            page.content().addInner("Not found");
        }
        
        return page;
    }   
    
}
