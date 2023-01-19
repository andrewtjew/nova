/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.frameworks;

import java.io.PrintStream;

//11:47
import org.eclipse.jetty.http.HttpStatus;
import org.nova.configuration.Configuration;
import org.nova.configuration.ConfigurationReader;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.operator.AjaxQueryResultWriter;
import org.nova.http.server.Context;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.HtmlContentWriter;
import org.nova.http.server.HttpServer;
import org.nova.http.server.HttpServerConfiguration;
import org.nova.http.server.HttpTransport;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.http.server.JSONPatchContentReader;
import org.nova.http.server.JettyServerFactory;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Path;
import org.nova.logging.Level;
import org.nova.logging.Logger;
import org.nova.tracing.TraceManager;
import org.nova.utils.FileUtils;
import org.nova.utils.Utils;


public class ServerApplicationRunner //
{
    public static interface ServerApplicationInstantiator
    {
        ServerApplication instantiate(CoreEnvironment coreEnvironment,HttpTransport operatorTransport) throws Throwable;
    }

    public static void main(String[] args,ServerApplicationInstantiator instantiator) throws Throwable
    {
        new ServerApplicationRunner().run(args,instantiator);
    }

    
    private Throwable startupException;
    
    private HttpTransport startOperatorTransport(Configuration configuration,TraceManager traceManager,Logger logger) throws Exception
    {
        int threads=configuration.getIntegerValue("HttpServer.operator.threads",20);
        int operatorPort=configuration.getIntegerValue("HttpServer.operator.port",10051);
        boolean test=configuration.getBooleanValue("System.test",false);
        System.out.println("http://"+Utils.getLocalHostName()+":"+operatorPort);
       
        HttpServer operatorServer=new HttpServer(traceManager,logger, test,new HttpServerConfiguration());
        
        operatorServer.addContentDecoders(new GzipContentDecoder());
        operatorServer.addContentEncoders(new GzipContentEncoder());
        operatorServer.addContentReaders(new JSONContentReader(),new JSONPatchContentReader());
        operatorServer.addContentWriters(new HtmlContentWriter(),new HtmlElementWriter(),new JSONContentWriter(),new AjaxQueryResultWriter());
        HttpTransport operatorTransport=new HttpTransport(operatorServer, JettyServerFactory.createServer(threads, operatorPort));
        operatorTransport.start();
        return operatorTransport;
    }
    
    private void showNotice(Logger logger,String message)
    {
        System.out.println();
        System.out.println(message);
        logger.log(Level.NOTICE, message);
    }
    
    private void showException(Logger logger,Throwable t)
    {
        this.startupException=t;
        t.printStackTrace(System.err);
        logger.log(t);
    }
    
    public void run(String[] args,String configurationFileKey,String defaultConfigurationFileName,ServerApplicationInstantiator instantiator) throws Throwable
    {
        Configuration configuration=ConfigurationReader.read(args,configurationFileKey,defaultConfigurationFileName);
        run(configuration,instantiator);
    }
    public void run(String[] args,ServerApplicationInstantiator instantiator) throws Throwable
    {
        run(args,"config",FileUtils.toNativePath("./resources/application.cnf"),instantiator);
    }
    
    public void start(String[] args,String configurationFileKey,String defaultConfigurationFileName,ServerApplicationInstantiator instantiator) throws Throwable
    {
        Configuration configuration=ConfigurationReader.read(args,configurationFileKey,defaultConfigurationFileName);
        start(configuration,instantiator);
    }

    public ServerApplication start(Configuration configuration,ServerApplicationInstantiator instantiator)
    {
        CoreEnvironment coreEnvironment=null;
        try
        {
            //  //System.out.println("Starting CoreEnvironment...");
            coreEnvironment=new CoreEnvironment(configuration);
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
            return null;
        }
        
        HttpTransport operatorTransport;
        try
        {
            showNotice(coreEnvironment.getLogger(),"Starting Operator HttpServer...");
            operatorTransport=startOperatorTransport(configuration, coreEnvironment.getTraceManager(), coreEnvironment.getLogger("HttpServer.Operator"));
            operatorTransport.getHttpServer().registerHandlers(this);
        }
        catch (Throwable t)
        {
            showException(coreEnvironment.getLogger(),t);
            return null;
        }
        
        ServerApplication serverApplication=null;
        try
        {
            showNotice(coreEnvironment.getLogger(),"New ServerApplication...");
            serverApplication=instantiator.instantiate(coreEnvironment,operatorTransport);
        }
        catch (Throwable t)
        {
            showException(coreEnvironment.getLogger(),t);
            return null;
        }

        try
        {
            showNotice(coreEnvironment.getLogger(),"Start ServerApplication...");
            serverApplication.start();
        }
        catch (Throwable t)
        {
            showException(coreEnvironment.getLogger(),t);
            return null;
        }
        showNotice(coreEnvironment.getLogger(),"Started");
        return serverApplication;
    }
    
    public void run(Configuration configuration,ServerApplicationInstantiator instantiator) throws Throwable
    {
        ServerApplication serverApplication=start(configuration,instantiator);
        serverApplication.waitForStop();
    } 

    @GET 
    @Path("/operator/exception")
    public void startupException(Context context) throws Throwable
    {
        if (this.startupException!=null)
        {
            this.startupException.printStackTrace(new PrintStream(context.getHttpServletResponse().getOutputStream()));
        }
        else
        {
            context.getHttpServletResponse().setHeader("Location","/operator/noStartupExceptions");
            context.getHttpServletResponse().setStatus(HttpStatus.TEMPORARY_REDIRECT_307);
        }
    }
}
