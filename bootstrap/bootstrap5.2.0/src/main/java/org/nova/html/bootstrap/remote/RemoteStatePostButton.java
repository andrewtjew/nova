package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.ext.SpinnerButton;
import org.nova.html.remote.RemoteForm;
import org.nova.html.remote.RemoteResponse;

public class RemoteStatePostButton extends SpinnerButton
{

    public RemoteStatePostButton(RemoteForm form,String label) throws Throwable
    {
        super(label);
        style("height:2.5em;");
        color(StyleColor.primary);
        onclick("if (mira.ui.reportValidity('"+form.id()+"')){"+form.js_post()+";}else{"+js_hideSpinner()+";}");
    }
    
    public RemoteResponse reset(RemoteResponse response)
    {
        response.script(js_hideSpinner());
        return response;
    }
}