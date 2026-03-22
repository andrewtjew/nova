package org.nova.html.remote;

//Submits to a method handler returning RemoteResponse. Allows submits while staying on the same page. 

import org.nova.html.elements.Composer;
import org.nova.html.elements.FormElement;
import org.nova.html.enums.method;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.InputHidden;
import org.nova.html.ext.JsObject;
import org.nova.html.enums.enctype;
public class RemoteForm2 extends FormElement<RemoteForm2> 
{
    private boolean usePost=false;
    public RemoteForm2(String id,RemoteStateElement<?> remoteStateElement) throws Throwable
    {
        super(method.post);
        if (id==null)
        {
            id();
        }
        else
        {
            id(id);
        }
        if (remoteStateElement!=null)
        {
            RemoteStateBinding binding=remoteStateElement.getRemoteStateBinding();
            addInner(new InputHidden(binding.getStateKey(),id()));
            binding.setPageState(id(), remoteStateElement);
        }
    }
    public RemoteForm2(RemoteStateElement<?> remoteStateElement) throws Throwable
    {
        this(null,remoteStateElement);
    }
    public RemoteForm2(String id) throws Throwable
    {
        this(id,null);
    }
    public RemoteForm2() throws Throwable
    {
        this((String)null);
    }
    
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