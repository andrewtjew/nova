package org.nova.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.nova.annotations.Description;
import org.nova.collections.ContentCache.ValueSize;
import org.nova.concurrent.MultiTaskScheduler;
import org.nova.concurrent.RunState;
import org.nova.concurrent.Synchronization;
import org.nova.frameworks.CoreEnvironment;
import org.nova.frameworks.OperatorPage;
import org.nova.frameworks.ServerApplication;
import org.nova.frameworks.ServerApplicationPages.OperatorTable;
import org.nova.html.attributes.Size;
import org.nova.html.attributes.unit;
import org.nova.html.deprecated.DataTable1_10;
import org.nova.html.deprecated.TableRow;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.operator.LegendFieldSetForm;
import org.nova.html.operator.MenuBar;
import org.nova.html.operator.MoreButton;
import org.nova.html.tags.ext.div_center;
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
import org.nova.logging.Item;
import org.nova.logging.Level;
import org.nova.logging.Logger;
import org.nova.net.SocketUtils;
import org.nova.tracing.Trace;
import org.nova.utils.DateTimeUtils;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

@ContentDecoders(GzipContentDecoder.class)
@ContentEncoders(GzipContentEncoder.class)
@ContentWriters({HtmlContentWriter.class, HtmlElementWriter.class})
public class OutsideServer
{
    final private MultiTaskScheduler scheduler;
    final private OutsideConfiguration configuration;
    final private Logger logger;
    private final ServerApplication serverApplication;
    final private HashMap<String,ProxyConnection> proxyConnections;

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
        this.scheduler.schedule(null, "handleProxyConnection", (trace)->{handleProxyConnection(trace);});
        
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
//                        System.out.println("Waiting for inside to connect");
                       Socket socket = serverSocket.accept();
//                       System.out.println("Accept inside connection");
                       InetSocketAddress socketAddress=(InetSocketAddress)socket.getRemoteSocketAddress();
                       String key=socketAddress.getHostString()+":"+socketAddress.getPort();
                       ProxyConnection current;
                       ProxyConnection connection=new ProxyConnection(this,socket,key);
                       synchronized(this.proxyConnections)
                       {
                           current=this.proxyConnections.remove(key);
                           this.proxyConnections.put(key, connection);
                       }
                       if (current!=null)
                       {
                           current.close();
                       }
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
    
    public void removeProxyConnection(String key)
    {
        ProxyConnection connection;
        synchronized(this.proxyConnections)
        {
            connection=this.proxyConnections.remove(key);
        }
        if (connection!=null)
        {
            connection.close();
        }
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
        table.setHeader("Inside Host","Created","Last Keep Alive","Inside Name","Inside Mac Address","Port","Connected","");
        synchronized (this.proxyConnections)
        {
            for (Entry<String, ProxyConnection> entry:this.proxyConnections.entrySet())
            {
                TableRow tr=new TableRow();
                tr.add(entry.getKey());
                ProxyConnection connection=entry.getValue();
                tr.add(DateTimeUtils.toSystemDateTimeString(connection.getCreated()));
                tr.add(DateTimeUtils.toSystemDateTimeString(connection.getLastKeepAliveReceived()));
                ProxyConfiguration configuration=connection.getProxyConfiguration();
                if (configuration!=null)
                {
                    tr.add(configuration.insideName,configuration.insideMacAddress,configuration.outsideListenPort);
                }
                else
                {
                    tr.add("","","");
                }
                tr.add(connection.getOutsideConnectionSize());
                tr.add(new MoreButton(page.head(),new PathAndQuery("/proxy/outside/viewConnection").addQuery("insideHost",entry.getKey()).toString()));
                table.addRow(tr);
            }
        }
        
        return page;
    }   

    @GET
    @Path("/proxy/outside/viewConnection")
    public Element viewConnection(Trace parent,@QueryParam("insideHost") String insideHost) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("View Outside Connection: "+insideHost);
        ProxyConnection connection;
        synchronized (this.proxyConnections)
        {
            connection=this.proxyConnections.get(insideHost);
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
