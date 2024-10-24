package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputText;

public class FormInputText extends FormInput<InputText>
{
    public FormInputText(FormCol col, String labelText,String name,String value,boolean required)
    {
        super(col, labelText, new InputText(), null);
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputText(FormCol col, String labelText,String name,String value)
    {
        this(col, labelText, name,value,false);
    }
    public FormInputText(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,null);
    }
//    public FormInputText(String labelText,String name,String value,boolean required)
//    {
//        this(null,labelText,name,value,required);
//    }
//    public FormInputText(String labelText,String name,String value)
//    {
//        this(labelText, name,value,false);
//    }
//    public FormInputText(String labelText,String name,boolean required)
//    {
//        this(labelText, name,null,required);
//    }
//    public FormInputText(String labelText,String name)
//    {
//        this(labelText,name,null);
//    }
}
