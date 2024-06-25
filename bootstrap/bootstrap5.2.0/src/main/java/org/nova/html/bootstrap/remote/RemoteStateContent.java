package org.nova.html.bootstrap.remote;

import org.nova.html.remote.RemoteResponseWriter;
import org.nova.http.server.RemoteStateBinding;
import org.nova.http.server.annotations.ContentWriters;

@ContentWriters(RemoteResponseWriter.class)
public class RemoteStateContent extends RemoteContent
{
    final private RemoteStateBinding binding;
    
    public RemoteStateContent(RemoteStateBinding binding) throws Throwable
    {
        super(null);
        binding.setState(this);
        this.binding=binding;
    }
    
    public RemoteStateBinding getRemoteStateBinding()
    {
        return this.binding;
    }
    
//    public String js_postStatic(String action) throws Exception, Throwable
//    {
//        return Remote.js_postStatic(new PathAndQuery(action).addQuery(binding.getKey(),id()).toString());
//    }
}