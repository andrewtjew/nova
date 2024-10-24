package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputNumber;

public class FormInputNumber extends FormInput<InputNumber>
{
    public FormInputNumber(FormCol col, String labelText,String name,Double value,boolean required)
    {
        super(col, labelText, new InputNumber(), null);
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputNumber(FormCol col, String labelText,String name,Double value)
    {
        this(col, labelText, name,value,false);
    }
    public FormInputNumber(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,null);
    }
   
//    public FormInputNumber(String labelText,String name,Double value,boolean required)
//    {
//        this(null,labelText,name,value,required);
//    }
//    public FormInputNumber(String labelText,String name,Double value)
//    {
//        this(labelText, name,value,false);
//    }
//    public FormInputNumber(String labelText,String name,boolean required)
//    {
//        this(labelText, name,null,required);
//    }
//    public FormInputNumber(String labelText,String name)
//    {
//        this(labelText,name,null);
//    }
}
