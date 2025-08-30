package org.sample;

import java.io.File;

import org.nova.frameworks.CoreEnvironment;
import org.nova.frameworks.ServerApplicationRunner;
import org.nova.html.ext.Text;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.http.server.BrotliContentEncoder;
import org.nova.http.server.FileDownloadHandler;
import org.nova.http.server.HttpTransport;
import org.nova.services.DeviceSessionService;
import org.nova.services.FavIconController;
import org.nova.tracing.Trace;

public class Service extends DeviceSessionService<UserSession>
{
    private FileDownloadHandler fileDownloader;

    public static void main(String[] args) throws Throwable
    {
        new ServerApplicationRunner().run(args,(coreEnvironment,operatorServer)->{return new Service(coreEnvironment,operatorServer);});
    }

    public Service(CoreEnvironment coreEnvironment,HttpTransport transport) throws Throwable
    {
        super("Sample", coreEnvironment,transport);
        Text.SAFE_ESCAPE=true;
        
        boolean cache=!isTest();
        this.fileDownloader=new FileDownloadHandler(getBaseDirectory()+File.separatorChar+"client", cache, cache?"public":null,1000000,0);
        this.getPublicServer().registerBackServletHandlers(this.fileDownloader);
        this.getPrivateServer().registerBackServletHandlers(this.fileDownloader);
        
        FavIconController favIconController=new FavIconController("./client/favicon.ico"); 
        this.getPublicServer().addContentEncoders(new BrotliContentEncoder());
        this.getPublicServer().registerHandlers(favIconController);
        this.getPrivateServer().registerHandlers(favIconController);

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
