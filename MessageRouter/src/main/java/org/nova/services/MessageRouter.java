package org.nova.services;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.eclipse.jetty.http.HttpStatus;
import org.nova.collections.ExpireMap;
import org.nova.configuration.Configuration;
import org.nova.frameworks.CoreEnvironment;
import org.nova.frameworks.ServerApplication;
import org.nova.frameworks.ServerApplicationRunner;
import org.nova.http.client.HttpClientConfiguration;
import org.nova.http.client.HttpClientFactory;
import org.nova.http.server.Context;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.HeaderParam;
import org.nova.http.server.HttpTransport;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.ext.Page;
import org.nova.html.tags.p;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentParam;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.Test;
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
    private MessageRouterService testMessageRouterService;
    static final String RECEIVE_PATH="/$/receive"; 
    static final boolean DEBUG=true;

    static public record Server(ServerStatus status, LocalDateTime lastUpdated)
    {
    }
    
    private ExpireMap<String, Server> servers;
    
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
        this.getOperatorServer().registerHandlers(new OperatorController(this));
        Configuration configuration=this.getConfiguration();
        
        long endPointExpiration=configuration.getIntegerValue("MessageRouter.endPointExpirationDays",400)*24*60*60*1000L;
        this.endPoints=new ExpireMap<>("routeMap",this.getTimerScheduler(),endPointExpiration,4);
        
        long serverExpiration=configuration.getIntegerValue("MessageRouter.serverExpirationSeconds",40)*1000L;
        this.servers=new ExpireMap<>("serverMap",this.getTimerScheduler(),serverExpiration,3);
        
        this.getPublicServer().registerHandlers(this);
        
        int port=configuration.getIntegerValue("HttpServer.public.port");
        
        HttpClientConfiguration httpClientConfiguration=new HttpClientConfiguration();
        httpClientConfiguration.maxConnections=1000;
        httpClientConfiguration.maxPerRoute=2;
        
        httpClientConfiguration=configuration.getJSONObject("Application.httpClientConfiguration", httpClientConfiguration, HttpClientConfiguration.class);
        this.client=HttpClientFactory.createClient(httpClientConfiguration);

        if (isTest())
        {
            this.testMessageRouterService=new MessageRouterService(this.getTimerScheduler(),this.getBaseDirectory(),"http://localhost:"+port,"http://localhost:"+port+RECEIVE_PATH)
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
            var method=testMessageRouterService.getClass().getMethod("receive", Trace.class,Context.class,String.class);
            this.getPublicServer().registerHandler(RECEIVE_PATH, testMessageRouterService, method);
        }
//        void receive(Trace parent,Context context,@QueryParam("identifier") String identifier) throws Throwable

        
    }

    public void onStop()
    { 
    }
    
    public Map<String,String> getEndPoints()
    {
        return this.endPoints.getEntries();
    }

    public Map<String,Server> getServers()
    {
        return this.servers.getEntries();
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
    public void open(Trace parent,Context context,@HeaderParam("X-Identifier") String identifier,@QueryParam("endPoint") String endPoint)
    {
        if (identifier==null)
        {
            return;
        }
        this.endPoints.put(identifier,endPoint);
    }
    
    @POST
    @Path("/close")
    public void close(Trace parent,@HeaderParam("X-Identifier") String identifier)
    {
        this.endPoints.remove(identifier);
    }

    @POST
    @Path("/send")
    public void send(Trace parent,Context context,@HeaderParam(MessageRouterService.IDENTIFIER_HEADER) String identifier) throws Throwable
    {
        try
        {
            String endPoint=this.endPoints.get(identifier);
            if (endPoint==null)
            {
                context.getHttpServletResponse().setStatus(HttpStatus.NOT_FOUND_404);
                return;
            }
            var request=context.getHttpServletRequest();
            InputStreamEntity entity=new InputStreamEntity(request.getInputStream());
            HttpPost post=new HttpPost(endPoint);
            post.setEntity(entity);
            post.setHeader("Content-Type",request.getContentType());
            post.setHeader(MessageRouterService.IDENTIFIER_HEADER,identifier);
            var response=this.client.execute(post);
            if (response.getStatusLine().getStatusCode()!=HttpStatus.OK_200)
            {
                context.getHttpServletResponse().setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
                return;
            }
        }
        catch (Throwable t)
        {
            context.getHttpServletResponse().setStatus(HttpStatus.BAD_GATEWAY_502);
            throw t;
        }
        
    }
    
    @GET
    @Path("/endPoint")
    public String endPoint(Trace parent,Context context,@HeaderParam(MessageRouterService.IDENTIFIER_HEADER) String identifier) throws Throwable
    {
        String endPoint=this.endPoints.get(identifier);
        if (endPoint==null)
        {
            context.getHttpServletResponse().setStatus(HttpStatus.NOT_FOUND_404);
            return null;
        }
        return endPoint;
    }
    

    @POST
    @Path("/update")
    public void update(Trace parent,Context context,@QueryParam("server") String server,@ContentParam ServerStatus status)
    {
        String host=context.getHttpServletRequest().getRemoteHost();
        this.servers.put(host, new Server(status,LocalDateTime.now()));
    }
    
    @GET
    @Test
    @Path("/test")
    public Element test(Trace parent) throws Throwable
    {
        var page=new Page();
        String identifier="testIdentifier";
        this.testMessageRouterService.open(parent,identifier);
        page.body().returnAddInner(new p()).addInner("value="+this.endPoints.get(identifier));
        this.testMessageRouterService.close(parent,identifier);
        page.body().returnAddInner(new p()).addInner("value="+this.endPoints.get(identifier));
        this.testMessageRouterService.open(parent,identifier);
        page.body().returnAddInner(new p()).addInner("value="+this.endPoints.get(identifier));
        
        this.testMessageRouterService.send(parent,identifier,"text/plain","Test MessageRouterClient");
        page.body().addInner("sent: "+LocalDateTime.now().toString());
        return page;
    }   
}
