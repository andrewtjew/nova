package org.nova.html.remote;

import org.nova.html.elements.FormElement;
import org.nova.html.enums.method;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.JsObject;

public class RemoteForm extends FormElement<RemoteForm>
{
    public RemoteForm()
    {
        super(method.get);
        this.onsubmit(HtmlUtils.js_call("nova.remote.submit",new JsObject("event")));
    }
}