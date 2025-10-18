package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.remote.RemoteForm;

public class SpinnerButton extends SpinnerButtonComponent<SpinnerButton>
{
    public SpinnerButton(String label,SpinnerType spinnerType)
    { 
        super(new Span().addInner(label),spinnerType);
    }
    public SpinnerButton(String label)
    { 
        this(label, SpinnerType.border);
    }
    public SpinnerButton(RemoteForm form,String label) throws Throwable
    {
        this(label);
        onclick("if (document.getElementById('"+form.id()+"').reportValidity()){"+form.js_post()+";}else{"+js_reset()+";}");
    }
    
}
