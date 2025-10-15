package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputCheckbox;

public class FormInputCheckbox extends FormInputComponent<InputCheckbox>
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
