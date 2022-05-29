package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.ButtonComponent;
import org.nova.html.bootstrap.SubmitButton;
import org.nova.html.bootstrap.classes.ButtonState;
import org.nova.html.elements.TagElement;
import org.nova.html.tags.form_post;

public class SubmitPostButton extends ButtonComponent<SubmitPostButton>
{
    public SubmitPostButton(String label,String action)
    {
        super("input");
        attr("type","submit");
        attr("value",label);
        form_post form=this.returnAddInner(new form_post());
        form.action(action);
        attr("form",form.id());
    }
    
}
