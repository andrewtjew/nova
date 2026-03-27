package org.samples;

import org.nova.frameworks.CoreEnvironment;
import org.nova.frameworks.ServerApplicationRunner;
import org.nova.http.server.HttpTransport;
import org.nova.tracing.Trace;

import com.evolve.legacy.services.SessionServerApplication;

public class Service extends SessionServerApplication<UserSession>
{
    
    public static void main(String[] args) throws Throwable
    {
        new ServerApplicationRunner().run(args,(coreEnvironment,operatorServer)->{return new Service(coreEnvironment,operatorServer);});
    }


    public Service(CoreEnvironment coreEnvironment,HttpTransport transport) throws Throwable
    {
        super("Sample", coreEnvironment,transport);
    }
    
    public void onStart(Trace parent) throws Throwable
    {
    }
    public void onStop()
    {
    }
    
}
