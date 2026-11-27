package org.nova.services;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.eclipse.jetty.http.HttpStatus;
import org.nova.concurrent.TimeBase;
import org.nova.concurrent.TimerScheduler;
import org.nova.http.client.HttpClientConfiguration;
import org.nova.http.client.HttpClientFactory;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.Context;
import org.nova.http.server.HeaderParam;
import org.nova.http.server.annotations.POST;
import org.nova.json.ObjectMapper;
import org.nova.tracing.Trace;

import oshi.SystemInfo;
public abstract class MessageRouterService
{
    static final String IDENTIFIER_HEADER="X-Identifier";

    static final boolean DEBUG=true;
    static final String DEBUG_CATEGORY=MessageRouterService.class.getSimpleName();
    final private HttpClient client;
    final private String endPoint;
    final private String receiveEndPoint;
    final private SystemInfo systemInfo;
    final private String workStore;
    
    private ServerStatus status;
    
    public MessageRouterService(TimerScheduler scheduler,long keepAliveInterval,String workStore,String endPoint,String receiveEndPoint,HttpClientConfiguration configuration) throws Throwable
    {
        this.systemInfo=new SystemInfo();
        this.workStore=workStore;
        this.status=new ServerStatus();
        
        var hardware=this.systemInfo.getHardware();
        this.status.computerName=InetAddress.getLocalHost().getHostName();
        this.status.computerModel=hardware.getComputerSystem().getModel();
        this.status.logicalProcessors=hardware.getProcessor().getLogicalProcessorCount();
        this.status.physicalProcessors=hardware.getProcessor().getPhysicalProcessorCount();
        this.status.processorMaximumFrequency=hardware.getProcessor().getMaxFreq();
        this.status.processor=hardware.getProcessor().getProcessorIdentifier().getName();
        this.status.physicalMemoryCapacity=hardware.getMemory().getTotal();
        this.status.runtimeMemoryCapacity=Runtime.getRuntime().maxMemory();
        
        this.status.storageCapacity=Files.getFileStore(Paths.get(workStore)).getTotalSpace();
        
        
        this.client=HttpClientFactory.createClient(configuration);
        this.endPoint=endPoint;
        this.receiveEndPoint=receiveEndPoint;
        scheduler.schedule(this.getClass().getSimpleName(), TimeBase.FIXED,0, keepAliveInterval, (parent,event)->
        {
            update(parent);
        });
    }

    public void update(Trace parent) throws Throwable
    {
        try
        {
            this.status.runtimeMemoryAvailable=Runtime.getRuntime().freeMemory();
            this.status.physicalMemoryAvailable=this.systemInfo.getHardware().getMemory().getAvailable();
            this.status.storageAvailable=Files.getFileStore(Paths.get(workStore)).getUsableSpace();
            this.status.load=this.systemInfo.getHardware().getProcessor().getSystemCpuLoad(1000);
            post(parent,"/update",this.status);
        }
        catch (Throwable t)
        {
            parent.close(t);
        }
    }
    
    public MessageRouterService(TimerScheduler scheduler,String workStore,String endPoint,String receiveEndPoint) throws Throwable
    {
        this(scheduler,30000,workStore,endPoint,receiveEndPoint,new HttpClientConfiguration());
    }
    
    public void open(Trace parent,String identifier) throws Throwable, Throwable
    {
        post(parent,identifier,new PathAndQuery("/open").addQuery("endPoint", receiveEndPoint).toString(),null,null);
    }
    public void close(Trace parent,String identifier) throws Throwable, Throwable
    {
        post(parent,identifier,new PathAndQuery("/close").toString(),null,null);
    }

    private boolean post(Trace parent,String pathAndQueries,Object object) throws Throwable
    {
        return post(parent,null,pathAndQueries,"application/json",ObjectMapper.writeObjectToString(object));
    }
    private boolean post(Trace parent,String identifier,String pathAndQueries,String contentType,String content) throws Throwable
    {
        try (Trace trace=new Trace(parent,this))
        {
            HttpPost post=new HttpPost(this.endPoint+pathAndQueries);
            if (content!=null)
            {
                StringEntity entity=new StringEntity(content);
                post.setEntity(entity);
            }
            if (contentType!=null)
            {
                post.setHeader("Content-Type",contentType);
            }
            if (identifier!=null)
            {
                post.setHeader(IDENTIFIER_HEADER,identifier);
            }
            var response=this.client.execute(post);
            return response.getStatusLine().getStatusCode()==HttpStatus.OK_200;
            
        }
    }

    public boolean send(Trace parent,String identifier,String contentType,String content) throws Throwable
    {
        return post(parent,identifier,new PathAndQuery("/send").toString(),contentType,content);
    }
    public boolean send(Trace parent,String identifier,Object content) throws Throwable
    {
        return post(parent,identifier,new PathAndQuery("/send").toString(),"application/json",ObjectMapper.writeObjectToString(content));
    }
    
    public abstract void onReceive(Trace parent,Context context,String identifier) throws Throwable;
    
    @POST
    public void receive(Trace parent,Context context,@HeaderParam(IDENTIFIER_HEADER) String identifier) throws Throwable
    {
        this.onReceive(parent,context,identifier);
    }
    public Method getReceiveMethod() throws Throwable
    {
        return this.getClass().getMethod("receive", Trace.class,Context.class,String.class);
    }

    public static double adjustLoad(ServerStatus status,double hyperThreadingFactor)
    {
        double efficiency=(status.logicalProcessors-status.physicalProcessors)*hyperThreadingFactor+status.physicalProcessors;
        return status.load*status.logicalProcessors/efficiency;
    }
}
