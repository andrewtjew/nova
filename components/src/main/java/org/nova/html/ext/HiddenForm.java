package org.nova.html.ext;

import org.nova.html.tags.form;
import org.nova.html.elements.ButtonElement;
import org.nova.html.enums.method;

public class HiddenForm extends form
{

    public HiddenForm(method method,String action,ButtonElement<?> button,InputHidden...inputs)
    {
        super(method);
        action(action);
        button.form(this);
        for (InputHidden input:inputs)
        {
            addInner(input);
        }
    }
    public HiddenForm(method method,String action,InputHidden...inputs)
    {
        super(method);
        action(action);
        for (InputHidden input:inputs)
        {
            addInner(input);
        }
    }    
}
