package org.nova.html.remote;

import org.nova.http.server.RemoteStateBinding;
import org.nova.http.server.annotations.ContentWriters;

@ContentWriters(RemoteResponseWriter.class)
public class RemoteStateContent<ELEMENT extends RemoteContent<ELEMENT>> extends RemoteContent<ELEMENT>
{
    final private RemoteStateBinding binding;
    
    public RemoteStateContent(String tag,RemoteStateBinding binding) throws Throwable
    {
        super(tag,null);
        binding.setState(this);
        this.binding=binding;
    }
    
    public RemoteStateBinding getRemoteStateBinding()
    {
        return this.binding;
    }
    

}