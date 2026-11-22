package org.nova.services;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.http.client.HttpClientConfiguration;
import org.nova.http.client.HttpClientFactory;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.Context;
import org.nova.http.server.HeaderParam;
import org.nova.http.server.annotations.POST;
import org.nova.json.ObjectMapper;
import org.nova.tracing.Trace;

import com.azure.core.annotation.QueryParam;
public abstract class MessageRouterService
{
    static final String IDENTIFIER_HEADER="X-Identifier";

    static final boolean DEBUG=true;
    static final String DEBUG_CATEGORY=MessageRouterService.class.getSimpleName();
    final private HttpClient client;
    final private String endPoint;
    final private String receiveEndPoint;
    
    
    public MessageRouterService(String endPoint,String receiveEndPoint,HttpClientConfiguration configuration)
    {
        this.client=HttpClientFactory.createClient(configuration);
        this.endPoint=endPoint;
        this.receiveEndPoint=receiveEndPoint;
    }

    public MessageRouterService(String endPoint,String receiveEndPoint)
    {
        this(endPoint,receiveEndPoint,new HttpClientConfiguration());
    }
    
    public void open(Trace parent,String identifier) throws Throwable, Throwable
    {
        post(parent,identifier,new PathAndQuery("/open").addQuery("endPoint", receiveEndPoint).toString(),null,null);
    }
    public void close(Trace parent,String identifier) throws Throwable, Throwable
    {
        post(parent,identifier,new PathAndQuery("/close").toString(),null,null);
    }
    
    private void post(Trace parent,String identifier,String pathAndQueries,String contentType,String content) throws Throwable
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
            post.setHeader(IDENTIFIER_HEADER,identifier);
            this.client.execute(post);
        }
    }

    public void send(Trace parent,String identifier,String contentType,String content) throws Throwable
    {
        post(parent,identifier,new PathAndQuery("/send").toString(),contentType,content);
    }
    public void send(Trace parent,String identifier,Object content) throws Throwable
    {
        post(parent,identifier,new PathAndQuery("/send").toString(),"application/json",ObjectMapper.writeObjectToString(content));
    }
    public void send(Trace parent,long identifier,Object content) throws Throwable
    {
        send(parent,Long.toString(identifier),content);
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

}
