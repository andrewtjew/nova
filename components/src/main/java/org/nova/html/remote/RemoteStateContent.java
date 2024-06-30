package org.nova.html.remote;

import org.nova.http.client.PathAndQuery;
import org.nova.http.client.SecurePathAndQuery;
import org.nova.http.server.RemoteStateBinding;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.security.QuerySecurity;

@ContentWriters(RemoteResponseWriter.class)
public class RemoteStateContent<ELEMENT extends RemoteContent<ELEMENT>> extends RemoteContent<ELEMENT>
{
    final private RemoteStateBinding binding;
    
    public RemoteStateContent(String tag,String id,RemoteStateBinding binding) throws Throwable
    {
        super(tag,id);
        binding.setState(id(),this);
        this.binding=binding;
    }
    
    public RemoteStateBinding getRemoteStateBinding()
    {
        return this.binding;
    }
    public String js_postStatic(QuerySecurity querySecurity,String action) throws Exception, Throwable
    {
        return Remote.js_postStatic(new SecurePathAndQuery(querySecurity,action).addQuery(binding.getStateKey(),id()).toString());
    }    
}