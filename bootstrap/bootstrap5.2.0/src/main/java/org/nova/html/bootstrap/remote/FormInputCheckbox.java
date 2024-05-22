package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.InputCheckbox;
import org.nova.html.bootstrap.classes.BreakPoint;

public class FormInputCheckbox extends FormInput<InputCheckbox>
{
    public FormInputCheckbox(BreakPoint breakPoint,Integer columns, String labelText,String name,boolean checked)
    {
        super(breakPoint, columns, labelText, new InputCheckbox(), null);
        input().name(name).checked(checked);
    }
    public FormInputCheckbox(Integer columns, String labelText,String name,boolean checked)
    {
        this(null,columns,labelText,name,checked);
    }
    public FormInputCheckbox(BreakPoint breakPoint, String labelText,String name,boolean checked)
    {
        this(breakPoint,null,labelText,name,checked);
    }
    public FormInputCheckbox(String labelText,String name,boolean checked)
    {
        this(null,null,labelText,name,checked);
    }
}
