package org.nova.html.remote;

import org.nova.html.elements.FormElement;
import org.nova.html.enums.enctype;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.tags.script;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.BrotliContentEncoder;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.Filters;
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
        binding.setPageState(id(),this);
        this.binding=binding;
    }
    
    public RemoteStateBinding getRemoteStateBinding()
    {
        return this.binding;
    }
  
    public String js_postStatic(PathAndQuery pathAndQuery) throws Throwable
    {
        return Remote.js_postStatic(addBinding(pathAndQuery).toString());
    }
    
    public PathAndQuery addBinding(PathAndQuery pathAndQuery) throws Throwable
    {
        pathAndQuery.addQuery(binding.getStateKey(),id());
        return pathAndQuery;
    }

    public String js_post(FormElement<?> form,PathAndQuery pathAndQuery) throws Throwable
    {
        String action=addBinding(pathAndQuery).toString();
        if (form.enctype()==enctype.data)
        {
            return HtmlUtils.js_call("nova.remote.postFormData",form.id(),action);
        }
        else
        {
            return HtmlUtils.js_call("nova.remote.postFormUrlEncoded",form.id(),action);
        }                
    }
    
    public void addScript(RemoteResponse response,String js_script)
    {
        response.script(js_script);
    }
    
    public void addScript(String js_script)
    {
        returnAddInner(new script()).addInner(new LiteralHtml(js_script));
    }
    
//    public RemoteResponse render(RemoteResponse response) throws Throwable
//    {
//        if (response!=null)
//        {
//            response.outerHtml(this);
//        }
//        return response;
//    }
//    
//    @Override
//    public void compose(Composer composer) throws Throwable
//    {
//        render(null);
//        super.compose(composer);
//    }
    
}