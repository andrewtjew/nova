package org.sample;

import java.io.File;

import org.nova.frameworks.CoreEnvironment;
import org.nova.frameworks.ServerApplicationRunner;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.http.server.FileDownloader;
import org.nova.http.server.HttpServer;
import org.nova.http.server.HttpTransport;
import org.nova.services.DeviceSessionService;
import org.nova.services.SessionServerApplication;
import org.nova.tracing.Trace;

public class Service extends DeviceSessionService<UserSession>
{
    private FileDownloader fileDownloader;

    public static void main(String[] args) throws Throwable
    {
        new ServerApplicationRunner().run(args,(coreEnvironment,operatorServer)->{return new Service(coreEnvironment,operatorServer);});
    }

    public Service(CoreEnvironment coreEnvironment,HttpTransport transport) throws Throwable
    {
        super("Sample", coreEnvironment,transport);
        
//    public FileDownloader(String rootDirectory, boolean enableLocalCaching, String cacheControl, long maxAge, long maxSize, long freeMemory) throws Throwable
        
//        this.getOperatorServer().registerFrontServletHandlers(new FileDownloader(getBaseDirectory()+File.separatorChar+"operator", false, null, 0, 10,0));
        this.fileDownloader=new FileDownloader(getBaseDirectory()+File.separatorChar+"client", !isTest(), 1000000,0); 
        this.getPublicServer().registerBackServletHandlers(this.fileDownloader);
        this.getPrivateServer().registerBackServletHandlers(this.fileDownloader);

        this.getPrivateServer().addContentWriters(new RemoteResponseWriter());
        this.getPublicServer().addContentWriters(new RemoteResponseWriter());

        this.getPublicServer().addTopFilters(new UserDeviceSessionFilter(this));
        this.getPublicServer().registerHandlers(new UserController(this));
        this.getPublicServer().registerHandlers(new DeviceController(this));
    }
    
    public void onStart(Trace parent) throws Throwable
    {
    }
    
    public void onStop()
    {
    }
    
}
