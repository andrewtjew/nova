package org.nova.html.remote;

import org.nova.html.elements.FormElement;
import org.nova.html.enums.method;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.JsObject;
import org.nova.html.enums.enctype;

public class RemoteForm extends FormElement<RemoteForm>
{
    public RemoteForm()
    {
        super(method.get);
        this.onsubmit(HtmlUtils.js_call("nova.remote.submit",new JsObject("event")));
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