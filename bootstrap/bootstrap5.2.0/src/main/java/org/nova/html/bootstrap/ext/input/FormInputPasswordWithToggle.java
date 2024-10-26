package org.nova.html.bootstrap.ext.input;

import org.nova.html.elements.Element;


public class FormInputPasswordWithToggle extends FormInput<InputPasswordWithToggle>
{
    public FormInputPasswordWithToggle(FormCol col, String labelText,String name,String value,boolean required,Element right)
    {
        super(col, labelText, new InputPasswordWithToggle(), right);
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputPasswordWithToggle(FormCol col, String labelText,String name,String value,boolean required)
    {
        this(col,labelText,name,value,required,null);
    }
    public FormInputPasswordWithToggle(FormCol col, String labelText,String name,String value)
    {
        this(col, labelText, name,value,false);
    }
    public FormInputPasswordWithToggle(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,null);
    }
//    public FormInputPasswordWithToggle(String labelText,String name,String value,boolean required)
//    {
//        this(null,labelText,name,value,required);
//    }
//    public FormInputPasswordWithToggle(String labelText,String name,String value)
//    {
//        this(labelText, name,value,false);
//    }
//    public FormInputPasswordWithToggle(String labelText,String name)
//    {
//        this(labelText,name,null);
//    }
}
