package org.nova.html.remote;

import org.nova.http.client.PathAndQuery;
import org.nova.http.server.RemoteStateBinding;
import org.nova.http.server.annotations.ContentWriters;

@ContentWriters(RemoteResponseWriter.class)
public class RemoteStateContent<ELEMENT extends RemoteContent<ELEMENT>> extends RemoteContent<ELEMENT>
{
    final private RemoteStateBinding binding;
    
    public RemoteStateContent(String tag,String id,RemoteStateBinding binding) throws Throwable
    {
        super(tag,id);
        binding.setState(this);
        this.binding=binding;
    }
    
    public RemoteStateBinding getRemoteStateBinding()
    {
        return this.binding;
    }
    public String js_postStatic(String action) throws Exception, Throwable
    {
        return Remote.js_postStatic(new RemoteStatePathAndQuery(this,action).addQuery(binding.getKey(),id()).toString());
    }    
    

}