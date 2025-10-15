package org.nova.loadTesting;

import org.nova.configuration.Configuration;
import org.nova.frameworks.CoreEnvironment;
import org.nova.frameworks.ServerApplication;
import org.nova.frameworks.ServerApplicationRunner;
import org.nova.html.ext.Text;
import org.nova.http.server.HttpTransport;
import org.nova.tracing.Trace;

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
