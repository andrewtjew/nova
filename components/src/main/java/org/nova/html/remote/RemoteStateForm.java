package org.nova.html.remote;

import org.nova.html.elements.Composer;
import org.nova.html.enums.enctype;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.JsObject;
import org.nova.http.client.PathAndQuery;

//Use to implement complex, stateful and re-usable form. Allows sub class forms to have method handlers returning RemoteResponse results. Subclass form lifetime is page lifetime and it is ended if the next page does not use it and is extended when next page uses it by calling getPageState(). 
//Register using register(PATH,SubClassOfRemoteStateForm.class), then new subclass in method handler.  

public class RemoteStateForm extends RemoteForm 
{
    RemoteStateBinding binding;
    private boolean usePost=false;

    public RemoteStateForm(String id,RemoteStateBinding binding) throws Throwable
    {
        super(id);
        this.binding=binding;
        binding.bind(this);
//        binding.setState(id(),this);
//        addInner(new InputHidden(binding.getStateKey(),id()));
    }
    public RemoteStateForm(RemoteStateBinding binding) throws Throwable
    {
        this(null,binding);
    }
    
//    public RemoteStateBinding getRemoteStateBinding()
//    {
//        return this.binding;
//    }


//    public String js_postStatic(String action) throws Exception, Throwable
//    {
//        return Remote.js_postStatic(new PathAndQuery(action).addQuery(binding.getStateKey(),id()).toString());
//    }    
    
    public String js_postStatic(PathAndQuery pathAndQuery) throws Throwable
    {
////<<<<<<< HEAD
////        return Remote.js_postStatic(pathAndQuery.addQuery(this.getRemoteStateBinding().getStateKey(), this.id()).toString());
////=======
//        return Remote.js_postStatic(pathAndQuery.addQuery(this.binding.getStateKey(), this.id()).toString());
////>>>>>>> 69de9a61908c65f215dac363c7df2a92b7c28a23
///this.
        return Remote.js_postStatic(this.binding.bind(this,pathAndQuery).toString());
        
    }
//----------------------------------
    public String js_post() throws Throwable
    {
        return js_post(action());
    }
    
    public String js_post(String action) throws Throwable
    {
        this.usePost=true;
        if (enctype()==enctype.data)
        {
            return HtmlUtils.js_call("nova.remote.postFormData",id(),action);
        }
        else
        {
            return HtmlUtils.js_call("nova.remote.postFormUrlEncoded",id(),action);
        }
                
    }

    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (this.usePost==false)
        {
            this.onsubmit(HtmlUtils.js_call("nova.remote.submit",new JsObject("event")));
        }
        else
        {
            this.onsubmit(HtmlUtils.js_call("nova.remote.preventDefault",new JsObject("event")));
        }
        super.compose(composer);
    }
    
    
    
    
}