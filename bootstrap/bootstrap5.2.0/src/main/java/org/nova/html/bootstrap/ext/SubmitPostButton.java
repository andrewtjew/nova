package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.ButtonComponent;
import org.nova.html.ext.InputHidden;
import org.nova.html.tags.form_post;

public class SubmitPostButton extends ButtonComponent<SubmitPostButton>
{
    final private form_post form;
    public SubmitPostButton(String label,String action)
    {
        super("input");
        attr("type","submit");
        attr("value",label);
        this.form=this.returnAddInner(new form_post());
        form.action(action);
        attr("form",form.id());
    }
    public SubmitPostButton inputHidden(String name,Object value)
    {
        this.form.returnAddInner(new InputHidden(name,value));
        return this;
    }
    
}
