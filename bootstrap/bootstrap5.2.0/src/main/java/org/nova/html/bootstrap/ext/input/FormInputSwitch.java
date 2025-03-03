package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputSwitch;

public class FormInputSwitch extends FormInputComponent<InputSwitch>
{
    public FormInputSwitch(FormCol col, String labelText,String name,boolean checked)
    {
        super(col, labelText, new InputSwitch(), null);
        input().name(name).checked(checked);
    }
    public FormInputSwitch(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,false);
    }
//    public FormInputSwitch(String labelText,String name,boolean checked)
//    {
//        this(null,labelText,name,checked);
//    }
//    public FormInputSwitch(String labelText,String name)
//    {
//        this(labelText,name,false);
//    }
}
