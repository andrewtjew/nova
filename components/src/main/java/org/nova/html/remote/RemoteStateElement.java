package org.nova.html.remote;

import org.nova.html.elements.Composer;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.tags.script;
import org.nova.http.client.PathAndQuery;
import org.nova.http.client.SecurePathAndQuery;
import org.nova.http.server.BrotliContentEncoder;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.RemoteStateBinding;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.Filters;
import org.nova.security.QuerySecurity;
import org.nova.services.DeviceSessionFilter;

@ContentWriters(RemoteResponseWriter.class)
@ContentReaders({JSONContentReader.class})
@ContentEncoders({BrotliContentEncoder.class,DeflaterContentEncoder.class,GzipContentEncoder.class})
@Filters({DeviceSessionFilter.class})
public class RemoteStateElement<ELEMENT extends RemoteElement<ELEMENT>> extends RemoteElement<ELEMENT>
{
    final private RemoteStateBinding binding;
    
    public RemoteStateElement(String tag,String id,RemoteStateBinding binding) throws Throwable
    {
        super(tag,id);
        binding.setState(id(),this);
        this.binding=binding;
    }
    
    public RemoteStateBinding getRemoteStateBinding()
    {
        return this.binding;
    }
  
    public String js_postStatic(PathAndQuery pathAndQuery) throws Exception, Throwable
    {
        pathAndQuery.addQuery(binding.getStateKey(),id());
        return Remote.js_postStatic(pathAndQuery.toString());
    }    
    public void addScript(RemoteResponse response,String js_script)
    {
        if (response!=null)
        {
            response.script(js_script);
        }
        else
        {
            returnAddInner(new script()).addInner(new LiteralHtml(js_script));
        }
    }
    public RemoteResponse render(RemoteResponse response) throws Throwable
    {
        if (response!=null)
        {
            response.outerHtml(this);
        }
        return response;
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        render(null);
        super.compose(composer);
    }
    
}