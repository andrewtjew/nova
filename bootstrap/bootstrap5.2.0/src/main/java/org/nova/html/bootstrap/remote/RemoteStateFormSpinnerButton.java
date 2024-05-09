package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.ext.SpinnerButton;
import org.nova.html.elements.Composer;
import org.nova.html.remote.RemoteForm;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.tags.script;

public class RemoteStateFormSpinnerButton extends SpinnerButton
{
    public RemoteStateFormSpinnerButton(RemoteForm form,String label) throws Throwable
    {
        super(label);
        
        style("height:2.5em;");
        color(StyleColor.primary);
        onclick("if (mira.ui.reportValidity('"+form.id()+"')){"+form.js_post()+";}else{"+js_hideSpinner()+";}");
    }
    
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        returnAddInner(new script()).addInner(js_hideSpinner());
        super.compose(composer);
    }            
        
}