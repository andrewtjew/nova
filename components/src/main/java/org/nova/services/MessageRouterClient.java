package org.nova.services;

import java.io.UnsupportedEncodingException;

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
import org.nova.http.server.annotations.POST;
import org.nova.tracing.Trace;

import com.azure.core.annotation.QueryParam;
public abstract class MessageRouterClient
{
    static final boolean DEBUG=true;
    static final String DEBUG_CATEGORY=MessageRouterClient.class.getSimpleName();
    final private HttpClient client;
    final private String endPoint;
    final private String receiveEndPoint;
    
    
    public MessageRouterClient(String endPoint,String receiveEndPoint,HttpClientConfiguration configuration)
    {
        this.client=HttpClientFactory.createClient(configuration);
        this.endPoint=endPoint;
        this.receiveEndPoint=receiveEndPoint;
    }

    public MessageRouterClient(String endPoint,String receiveEndPoint)
    {
        this(endPoint,receiveEndPoint,new HttpClientConfiguration());
    }
    
    public void open(String identifier) throws Throwable, Throwable
    {
        post(new PathAndQuery(this.endPoint+"/open").addQuery("identifier", identifier).addQuery("endPoint", receiveEndPoint).toString(),null,null);
    }
    public void close(String identifier) throws Throwable, Throwable
    {
        post(new PathAndQuery(this.endPoint+"/close").addQuery("identifier", identifier).toString(),null,null);
    }
    
    private void post(String path,String contentType,String content) throws Throwable
    {
        HttpPost post=new HttpPost(path);
        if (content!=null)
        {
            StringEntity entity=new StringEntity(content);
            post.setEntity(entity);
        }
        if (contentType!=null)
        {
            post.setHeader("Content-Type",contentType);
        }
        this.client.execute(post);
    }

    public void send(String identifier,String endPoint,String contentType,String content) throws Throwable
    {
        post(new PathAndQuery(endPoint).addQuery("identifier", identifier).toString(),contentType,content);
    }
    
    public abstract void onReceive(Trace parent,Context context,String identifier) throws Throwable;
    
    @POST
    public void receive(Trace parent,Context context,@QueryParam("identifier") String identifier) throws Throwable
    {
        this.onReceive(parent,context,identifier);
    }
}
