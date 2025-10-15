package org.nova.html.remote;


<<<<<<< HEAD
=======
import org.nova.html.deprecated.ObjectBuilder;
import org.nova.html.elements.Composer;
>>>>>>> ea9674eae9f54d6ac5332311c85b321d4812f1a8
import org.nova.html.elements.FormElement;
import org.nova.html.enums.method;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.InputHidden;
import org.nova.html.ext.JsObject;
import org.nova.html.tags.script;
import org.nova.html.enums.enctype;

public class RemoteForm extends FormElement<RemoteForm> 
{
<<<<<<< HEAD
=======
    private boolean usePost=false;
>>>>>>> ea9674eae9f54d6ac5332311c85b321d4812f1a8
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
            binding.setPageState(id(), remoteStateElement);
        }
<<<<<<< HEAD
        this.onsubmit(HtmlUtils.js_call("nova.remote.submit",new JsObject("event")));
=======
>>>>>>> ea9674eae9f54d6ac5332311c85b321d4812f1a8
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
<<<<<<< HEAD
=======
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
>>>>>>> ea9674eae9f54d6ac5332311c85b321d4812f1a8
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