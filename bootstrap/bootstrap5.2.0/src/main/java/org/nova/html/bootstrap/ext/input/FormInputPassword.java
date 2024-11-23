package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputPassword;
import org.nova.html.elements.Element;


public class FormInputPassword extends FormInputComponent<InputPassword>
{
    public FormInputPassword(FormCol col, String labelText,String name,String value,boolean required,Element right)
    {
        super(col, labelText, new InputPassword(), right);
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputPassword(FormCol col, String labelText,String name,String value,boolean required)
    {
        this(col,labelText,name,value,required,null);
    }
    public FormInputPassword(FormCol col, String labelText,String name,String value)
    {
        this(col, labelText, name,value,false);
    }
//    public FormInputPassword(String labelText,String name,String value,boolean required)
//    {
//        this(null,labelText,name,value,required);
//    }
//    public FormInputPassword(String labelText,String name,String value)
//    {
//        this(labelText, name,value,false);
//    }
//    public FormInputPassword(String labelText,String name)
//    {
//        this(labelText,name,null);
//    }
}
