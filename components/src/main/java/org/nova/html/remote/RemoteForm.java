package org.nova.html.remote;


import org.nova.html.elements.FormElement;
import org.nova.html.enums.method;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.InputHidden;
import org.nova.html.ext.JsObject;
import org.nova.html.enums.enctype;

public class RemoteForm extends FormElement<RemoteForm> 
{
    public RemoteForm(String id,RemoteStateElement<?> remoteStateElement) throws Throwable
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
            binding.setState(id(), remoteStateElement);
        }
        this.onsubmit(HtmlUtils.js_call("nova.remote.submit",new JsObject("event")));
    }
    public RemoteForm(RemoteStateElement<?> remoteStateElement) throws Throwable
    {
        this(null,remoteStateElement);
    }
    public RemoteForm(String id) throws Throwable
    {
        this(id,null);
    }
    public RemoteForm() throws Throwable
    {
        this((String)null);
    }
    
    public String js_post() throws Throwable
    {
        return js_post(action());
    }
    
    public String js_post(String action) throws Throwable
    {
        if (enctype()==enctype.data)
        {
            return HtmlUtils.js_call("nova.remote.postFormData",id(),action);
        }
        else
        {
            return HtmlUtils.js_call("nova.remote.postFormUrlEncoded",id(),action);
        }
                
    }
}