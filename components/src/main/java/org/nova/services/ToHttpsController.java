package org.nova.services;

import org.eclipse.jetty.server.Server;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.ext.Redirect;
import org.nova.http.server.Context;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.HtmlContentWriter;
import org.nova.http.server.HttpServer;
import org.nova.http.server.HttpServerConfiguration;
import org.nova.http.server.HttpTransport;
import org.nova.http.server.JettyServerFactory;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Path;
import org.nova.logging.Logger;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;
import org.nova.utils.FileUtils;
import org.nova.utils.NetUtils;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

import org.nova.html.remoting.HtmlRemotingWriter;


@ContentDecoders(GzipContentDecoder.class)
@ContentEncoders(GzipContentEncoder.class)
@ContentWriters({HtmlContentWriter.class, HtmlElementWriter.class})
public class ToHttpsController
{
    final private String port;
    private HttpServer httpServer;
    private HttpTransport httpTransport;
    
    public ToHttpsController(TraceManager traceManager,Logger logger,int httpPort,int httpsPort) throws Throwable
    {
        Server server=JettyServerFactory.createServer(httpPort);
        HttpServerConfiguration httpServerConfiguration=new HttpServerConfiguration();
        
        this.httpServer=new HttpServer(traceManager, logger,false,httpServerConfiguration);
        this.httpServer.addContentWriters(new HtmlContentWriter(),new HtmlElementWriter());
        this.httpServer.addContentDecoders(new GzipContentDecoder());
        this.httpServer.addContentEncoders(new GzipContentEncoder());
        this.httpServer.registerHandlers(this);
        this.httpTransport=new HttpTransport(this.httpServer, server);
        this.httpTransport.start();

        if (httpsPort==443)
        {
            this.port="";
        }
        else
        {
            this.port=":"+httpsPort;
            
        }
    }

    public ToHttpsController(TraceManager traceManager,Logger logger) throws Throwable
    {
        this(traceManager,logger,80,443);
    }
    
    @GET
    @Path("/{*}")
    public Element main(Trace parent,Context context) throws Throwable
    {
        String name=context.getHttpServletRequest().getServerName();
        String URL=context.getHttpServletRequest().getRequestURI();
        String queryString=context.getHttpServletRequest().getQueryString();
        if (queryString!=null)
        {
            return new Redirect("https://"+name+this.port+URL+"?"+queryString);
        }
        return new Redirect("https://"+name+this.port+URL);
    }

 }
