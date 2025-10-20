package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputEmail;

public class FormInputEmail extends FormInputComponent<InputEmail>
{
    public FormInputEmail(FormCol col, String labelText,String name,String value,boolean required)
    {
        super(col, labelText, new InputEmail(), null);
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputEmail(FormCol col, String labelText,String name,String value)
    {
        this(col, labelText, name,value,false);
    }
    public FormInputEmail(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,null);
    }
    
//    public FormInputEmail(String labelText,String name,String value,boolean required)
//    {
//        this(null,labelText,name,value,required);
//    }
//    public FormInputEmail(String labelText,String name,String value)
//    {
//        this(labelText, name,value,false);
//    }
//    public FormInputEmail(String labelText,String name)
//    {
//        this(labelText,name,null);
//    }
}
