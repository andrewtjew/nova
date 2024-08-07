package org.nova.html.remote;

import org.nova.html.elements.FormElement;
import org.nova.html.enums.method;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.JsObject;
import org.nova.http.client.PathAndQuery;
import org.nova.html.enums.enctype;

public class RemoteFormElement<ELEMENT extends RemoteFormElement<ELEMENT>> extends FormElement<ELEMENT>
{
    public RemoteFormElement(String id,boolean submit)
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
        if (submit)
        {
            this.onsubmit(HtmlUtils.js_call("nova.remote.submit",new JsObject("event")));
        }
    }
    public RemoteFormElement(boolean submit)
    {
        this(null,submit);
    }
//    public RemoteFormElement(String id)
//    {
//        this(id,false);
//    }
 
    
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