package org.nova.html.remote;

//Submits to a method handler returning RemoteResponse. Allows submits while staying on the same page. 

import org.nova.html.elements.Composer;
import org.nova.html.elements.FormElement;
import org.nova.html.enums.method;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.JsObject;
import org.nova.tracing.Trace;
import org.nova.html.enums.enctype;
public class RemoteForm extends FormElement<RemoteForm> 
{
    private boolean usePost=false;
    public RemoteForm(String id) throws Throwable
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
    }
    public RemoteForm() throws Throwable
    {
        this(null);
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
    public RemoteResponse render(Trace parent,RemoteResponse response) throws Throwable
    {
        if (response!=null)
        {
            response.outerHtml(this);
        }
        return response;
    }    
    
}