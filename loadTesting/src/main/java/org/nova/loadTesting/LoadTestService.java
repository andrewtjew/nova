package org.nova.loadTesting;

import java.io.File;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.configuration.Configuration;
import org.nova.frameworks.CoreEnvironment;
import org.nova.frameworks.ServerApplication;
import org.nova.frameworks.ServerApplicationRunner;
import org.nova.html.bootstrap.localization.StringHandleEditor;
import org.nova.html.ext.Redirect;
import org.nova.html.ext.Text;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.html.templating.TemplateCache;
import org.nova.http.client.JSONClient;
import org.nova.http.server.Context;
import org.nova.http.server.FileDownloader;
import org.nova.http.server.HttpTransport;
import org.nova.http.server.Response;
import org.nova.proxy.InsideConfiguration;
import org.nova.proxy.InsideServer;
import org.nova.services.AbnormalSessionRequestHandling;
import org.nova.services.FavIconController;
import org.nova.services.Session;
import org.nova.services.SessionFilter;
import org.nova.services.SessionServerApplication;
import org.nova.services.ToHttpsController;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.MySqlConnector;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;


import jakarta.servlet.http.HttpServletResponse;
import xp.nova.sqldb.graph.GraphTransaction;
import xp.nova.sqldb.graph.Query;

public class LoadTestService extends ServerApplication
{
    public static <STATE extends State> void run(String[] args,Settings settings,Class<STATE> stateType,Actor.Act<STATE> runnable,int instances) throws Throwable
    {
        Text.SAFE_ESCAPE = true;
        var service=new ServerApplicationRunner().start(args, (coreEnvironment, operatorServer) ->
        {
            return new LoadTestService(coreEnvironment, operatorServer);
        });

        Actor<STATE>[] runners=(Actor<STATE>[])new Actor<?>[instances];
        
        var constructor=stateType.getDeclaredConstructor(int.class);
        for (int i=0;i<runners.length;i++)
        {
            STATE state=constructor.newInstance(i);
            runners[i]=new Actor<STATE>(i,settings,state,runnable);
        }
        Trace trace=new Trace(service.getTraceManager(),"loadTest");
        var progress=service.getMultiTaskScheduler().schedule(trace, "loadTest", runners);
        progress.waitAll();
        service.stop();
    }
        
    public LoadTestService(CoreEnvironment coreEnvironment, HttpTransport transport) throws Throwable
    {
        super("LoadTester", coreEnvironment, transport);
    }

    public void onStart(Trace parent) throws Throwable
    {
        Configuration configuration = this.getConfiguration();
        
    }

    @Override
    public void onStop() throws Throwable
    {
        this.getOperatorServer();
    }
    
}
