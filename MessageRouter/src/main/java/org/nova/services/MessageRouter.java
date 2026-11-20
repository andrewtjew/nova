package org.nova.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.nova.collections.ExpireMap;
import org.nova.configuration.Configuration;
import org.nova.frameworks.CoreEnvironment;
import org.nova.frameworks.ServerApplication;
import org.nova.frameworks.ServerApplicationRunner;
import org.nova.http.Header;
import org.nova.http.client.HttpClientConfiguration;
import org.nova.http.client.HttpClientFactory;
import org.nova.http.client.JSONClient;
import org.nova.http.server.Context;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.HttpTransport;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.ext.Page;
import org.nova.html.tags.p;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.Test;
import org.nova.json.ObjectMapper;
import org.nova.logging.Item;
import org.nova.proxy.OutsideConfiguration;
import org.nova.proxy.OutsideServer;
import org.nova.service.deviceSession.DeviceSession;
import org.nova.tracing.Trace;

@ContentDecoders(GzipContentDecoder.class)
@ContentEncoders(GzipContentEncoder.class)
@ContentReaders(
{
        JSONContentReader.class
})
@ContentWriters({JSONContentWriter.class,HtmlElementWriter.class})
public class MessageRouter extends ServerApplication
{
    private ExpireMap<String, String> endPoints;
    private HttpClient client;
    private MessageRouterClient testMessageRouterClient;
    static final String RECEIVE_PATH="/$/receive"; 
    static final boolean DEBUG=true;
    
    public static void main(String[] args) throws Throwable
    {
        new ServerApplicationRunner().run(args,(coreEnvironment,operatorServer)->{return new MessageRouter(coreEnvironment,operatorServer);});
    }


    public MessageRouter(CoreEnvironment coreEnvironment,HttpTransport operatorServer) throws Throwable
    {
        super("MessageRouter", coreEnvironment,operatorServer);
    }

    public void onStart(Trace parent) throws Throwable
    {
        Configuration configuration=this.getConfiguration();
        long endPointExpiration=configuration.getIntegerValue("MessageRouter.endPointExpirationDays",400)*24*60*60*1000L;
        this.getPublicServer().registerHandlers(this);
        this.endPoints=new ExpireMap<>("routeMap",this.getTimerScheduler(),endPointExpiration,4);
        int port=configuration.getIntegerValue("HttpServer.public.port");
        
        HttpClientConfiguration httpClientConfiguration=new HttpClientConfiguration();
        httpClientConfiguration.maxConnections=1000;
        httpClientConfiguration.maxPerRoute=2;
        
        httpClientConfiguration=configuration.getJSONObject("Application.httpClientConfiguration", httpClientConfiguration, HttpClientConfiguration.class);
        this.client=HttpClientFactory.createClient(httpClientConfiguration);

        if (isTest())
        {
            this.testMessageRouterClient=new MessageRouterClient("http://localhost:"+port,"http://localhost:"+port+RECEIVE_PATH,httpClientConfiguration)
            {
                @Override
                public void onReceive(Trace parent,Context context,String identifier) throws Throwable
                {
                    var request=context.getHttpServletRequest();
                    var contentType=request.getContentType();
                    System.out.println("----- Receive: identifier="+identifier+" contentType="+contentType);
                    if (contentType!=null)
                    {
                        contentType=contentType.toLowerCase();
                        if (contentType.startsWith("text/")||contentType.startsWith("application/json")||contentType.startsWith("application/xml"))
                        {
                            request.getInputStream().transferTo(System.out);
                        }
                    }
                }
            };
            var method=testMessageRouterClient.getClass().getMethod("receive", Trace.class,Context.class,String.class);
            this.getPublicServer().registerHandler(RECEIVE_PATH, testMessageRouterClient, method);
        }
//        void receive(Trace parent,Context context,@QueryParam("identifier") String identifier) throws Throwable

        
    }

    public void onStop()
    { 
    }
    
    @GET
    @Path("/")
    public Element root(Trace parent)
    {
        var page=new Page();
        page.body().addInner(LocalDateTime.now().toString());
        return page;
    }
    
    @POST
    @Path("/open")
    public void open(Trace parent,Context context,@QueryParam("identifier") String identifier,@QueryParam("endPoint") String endPoint)
    {
        if (identifier==null)
        {
            return;
        }
        this.endPoints.put(identifier,endPoint);
    }
    
    @POST
    @Path("/close")
    public void close(Trace parent,Context context,@QueryParam("identifier") String identifier)
    {
        this.endPoints.remove(identifier);
    }

    @POST
    @Path("/send")
    public void send(Trace parent,Context context,@QueryParam("identifier") String identifier) throws Throwable
    {
        String endPoint=this.endPoints.get(identifier);
        if (endPoint==null)
        {
            return;
        }
        var request=context.getHttpServletRequest();
        InputStreamEntity entity=new InputStreamEntity(request.getInputStream());
        HttpPost post=new HttpPost(endPoint);
        post.setEntity(entity);
        post.setHeader("Content-Type",request.getContentType());
        this.client.execute(post);
        
    }

    
    @GET
    @Test
    @Path("/test")
    public Element test(Trace parent) throws Throwable
    {
        var page=new Page();
        String identifier="testIdentifier";
        this.testMessageRouterClient.open(identifier);
        page.body().returnAddInner(new p()).addInner("value="+this.endPoints.get(identifier));
        this.testMessageRouterClient.close(identifier);
        page.body().returnAddInner(new p()).addInner("value="+this.endPoints.get(identifier));
        this.testMessageRouterClient.open(identifier);
        page.body().returnAddInner(new p()).addInner("value="+this.endPoints.get(identifier));
        
        this.testMessageRouterClient.send(this.endPoints.get(identifier),this.endPoints.get(identifier),"text/plain","Test MessageRouterClient");
        page.body().addInner("sent: "+LocalDateTime.now().toString());
        return page;
    }   
}
