package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputText;
import org.nova.html.elements.Element;

public class FormInputText extends FormInputComponent<InputText>
{
    public FormInputText(FormCol col, String labelText,String name,String value,boolean required,Element right)
    {
        super(col, labelText, new InputText(), right);
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputText(FormCol col, String labelText,String name,String value,boolean required)
    {
        this(col,labelText,name,value,required,null);
    }
    public FormInputText(FormCol col, String labelText,String name,String value)
    {
        this(col, labelText, name,value,false);
    }
    public FormInputText(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,null);
    }
}
