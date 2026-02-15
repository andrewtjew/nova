package org.nova.html.remote;

import java.util.concurrent.atomic.AtomicLong;

import org.nova.html.elements.HtmlElementWriter;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.BrotliContentEncoder;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.Filters;
import org.nova.services.SimpleDeviceSessionFilter;

@ContentWriters({ HtmlElementWriter.class, RemoteResponseWriter.class, JSONContentWriter.class })
@ContentReaders({ JSONContentReader.class })
@ContentEncoders({ BrotliContentEncoder.class, DeflaterContentEncoder.class, GzipContentEncoder.class })
@Filters({ SimpleDeviceSessionFilter.class })
public class RemoteState
{
    final private RemoteStateBinding binding;
    final private String id;
    static private AtomicLong ID=new AtomicLong();
    
    public RemoteState(String id,RemoteStateBinding binding) throws Throwable
    {
        if (id==null)
        {
            id="RemoteState_"+ID.getAndIncrement();
        }
        this.id=id;
        binding.setPageState(id,this);
        this.binding=binding;
    }
    public RemoteState(RemoteStateBinding binding) throws Throwable
    {
        this(null, binding);
    }
    public String id()
    {
        return this.id;
    }
    public RemoteStateBinding getRemoteStateBinding()
    {
        return this.binding;
    }
  
    public String js_postStatic(PathAndQuery pathAndQuery) throws Throwable
    {
        pathAndQuery.addQuery(binding.getStateKey(),this.id);
        return Remote.js_postStatic(pathAndQuery.toString());
    }

    public String js_get(PathAndQuery pathAndQuery) throws Throwable
    {
        pathAndQuery.addQuery(binding.getStateKey(),this.id);
        return pathAndQuery.toString();
    }
    

}
