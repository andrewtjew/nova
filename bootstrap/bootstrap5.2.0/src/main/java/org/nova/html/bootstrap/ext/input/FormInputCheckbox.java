package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputCheckbox;
import org.nova.html.bootstrap.classes.BreakPoint;

public class FormInputCheckbox extends FormInput<InputCheckbox>
{
    public FormInputCheckbox(FormCol col, String labelText,String name,boolean checked)
    {
        super(col, labelText, new InputCheckbox(), null);
        input().name(name).checked(checked);
    }
    public FormInputCheckbox(FormCol col,String labelText,String name)
    {
        this(col,labelText,name,false);
    }
}
