package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputRadio;

public class FormInputRadio extends FormInputComponent<InputRadio>
{
    public FormInputRadio(FormCol col, String labelText,String name,boolean checked)
    {
        super(col, labelText, new InputRadio(), null);
        input().name(name).checked(checked);
    }
    public FormInputRadio(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,false);
    }
}
