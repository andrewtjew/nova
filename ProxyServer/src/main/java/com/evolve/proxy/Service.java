package com.evolve.proxy;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.nova.configuration.Configuration;
import org.nova.frameworks.CoreEnvironment;
import org.nova.frameworks.ServerApplication;
import org.nova.frameworks.ServerApplicationRunner;
import org.nova.html.elements.Element;
import org.nova.html.ext.BasicPage;
import org.nova.html.ext.Redirect;
import org.nova.http.server.Context;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.HttpTransport;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.http.server.WebSocketTransport;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentParam;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.PathParam;
import org.nova.proxy.InsideConfiguration;
import org.nova.proxy.InsideServer;
import org.nova.proxy.OutsideConfiguration;
import org.nova.proxy.OutsideServer;
import org.nova.services.SessionServerApplication;
import org.nova.services.ToHttpsController;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;
import org.nova.utils.Utils;

@ContentDecoders(GzipContentDecoder.class)
@ContentEncoders(GzipContentEncoder.class)
@ContentReaders(
{
        JSONContentReader.class
})
@ContentWriters({JSONContentWriter.class,HtmlElementWriter.class})
public class Service extends ServerApplication
{
    String directoryFileName;
    public static void main(String[] args) throws Throwable
    {
        new ServerApplicationRunner().run(args,(coreEnvironment,operatorServer)->{return new Service(coreEnvironment,operatorServer);});
    }


    public Service(CoreEnvironment coreEnvironment,HttpTransport operatorServer) throws Throwable
    {
        super("ProxyServer", coreEnvironment,operatorServer);
    }

    OutsideServer outsideServer;
    public void onStart(Trace parent) throws Throwable
    {
        Configuration configuration=this.getConfiguration();
        this.directoryFileName=configuration.getValue("Application.directoryFileName","./resources/local.cnf");
        OutsideConfiguration serverConfiguration=new OutsideConfiguration();
        this.outsideServer=new OutsideServer(this.getMultiTaskScheduler(), this.getLogger(), serverConfiguration,this);
        this.outsideServer.start();

        this.getOperatorServer().registerHandlers(this.outsideServer);

        
//        if (configuration.getBooleanValue("Application.insideServer",false))
//        {
//            InsideConfiguration hostConfiguration=new InsideConfiguration();
//            InsideServer inside=new InsideServer(this.getMultiTaskScheduler(), this.getTimerScheduler(), this.getLogger(), hostConfiguration,this);
//            inside.start();
//        }
//        this.getPublicServer().registerHandlers(this);
     
  //      ToHttpsController controller=new ToHttpsController(getTraceManager(), getLogger());
        
    }

    public void onStop()
    { 
    }
    
}
