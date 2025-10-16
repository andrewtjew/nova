package org.nova.html.remote;

import org.nova.html.ext.InputHidden;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.tracing.Trace;

//Use to implement complex, stateful and re-usable form. Allows sub class forms to have method handlers returning RemoteResponse results. Subclass form lifetime is page lifetime and it is ended if the next page does not use it and is extended when next page uses it by calling getPageState(). 
//Register using register(PATH,SubClassOfRemoteStateForm.class), then new subclass in method handler.  

@ContentWriters(RemoteResponseWriter.class)
public class RemoteStateForm extends RemoteForm
{
    RemoteStateBinding binding;
    public RemoteStateForm(String id,RemoteStateBinding binding, String action) throws Throwable
    {
        super(id);
        this.binding=binding;
        binding.setPageState(id(),this);
        addInner(new InputHidden(binding.getStateKey(),id()));
        if (action!=null)
        {
            action(action);
        }
    }
    public RemoteStateForm(RemoteStateBinding binding,String action) throws Throwable
    {
        this(null,binding, action);
    }

    public RemoteStateForm(RemoteStateBinding binding) throws Throwable
    {
        this(binding, null);
    }
    
    public RemoteStateBinding getRemoteStateBinding()
    {
        return this.binding;
    }


    public RemoteResponse render(Trace parent,RemoteResponse response) throws Throwable
    {
        if (response!=null)
        {
            response.outerHtml(this);
        }
        return response;
    }    
//    public String js_postStatic(String action) throws Exception, Throwable
//    {
//        return Remote.js_postStatic(new PathAndQuery(action).addQuery(binding.getStateKey(),id()).toString());
//    }    
    
    public String js_postStatic(PathAndQuery pathAndQuery) throws Throwable
    {
        return Remote.js_postStatic(pathAndQuery.addQuery(this.getRemoteStateBinding().getStateKey(), this.id()).toString());
        
    }

}