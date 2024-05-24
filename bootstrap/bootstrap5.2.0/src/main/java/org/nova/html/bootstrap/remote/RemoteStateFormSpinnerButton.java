package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.ext.Icon;
import org.nova.html.bootstrap.ext.SpinnerButton;
import org.nova.html.elements.Composer;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.remote.RemoteForm;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.tags.script;

public class RemoteStateFormSpinnerButton extends SpinnerButton
{
    static class SpinnerLabel extends Item
    {
        public SpinnerLabel(String label)
        {
            addInner(new Icon("check").me(1));
            addInner(label);
        }
    }
    
    public RemoteStateFormSpinnerButton(RemoteForm form,String label) throws Throwable
    {
        super(new SpinnerLabel(label),SpinnerType.border);
//        this.disabled();
        style("height:2.5em;");
        color(StyleColor.primary);
        onclick("if (mira.ui.reportValidity('"+form.id()+"')){"+HtmlUtils.js_classList_add(id(), "disabled")+";"+form.js_post()+";}else{"+js_hideSpinner()+";}");
    }
    
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        returnAddInner(new script()).addInner(new LiteralHtml(js_hideSpinner()));
        super.compose(composer);
    }            
        
}