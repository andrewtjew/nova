package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputNumber;

public class FormInputNumber extends FormInputComponent<InputNumber>
{
    public FormInputNumber(FormCol col, String labelText,String name,Double value,boolean required)
    {
        super(col, labelText, new InputNumber());
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputNumber(FormCol col, String labelText,String name,boolean required)
    {
        this(col,labelText,name,(Double)null,required);
    }
    public FormInputNumber(FormCol col, String labelText,String name,Double value)
    {
        this(col, labelText, name,value,false);
    }
    public FormInputNumber(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,(Long)null);
    }
    public FormInputNumber(FormCol col, String labelText,String name,Long value,boolean required)
    {
        super(col, labelText, new InputNumber());
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputNumber(FormCol col, String labelText,String name,Long value)
    {
        this(col, labelText, name,value,false);
    }
    public FormInputNumber(FormCol col, String labelText,String name,Integer value,boolean required)
    {
        super(col, labelText, new InputNumber());
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputNumber(FormCol col, String labelText,String name,Integer value)
    {
        this(col, labelText, name,value,false);
    }
    public FormInputNumber(FormCol col, String labelText,String name,Short value,boolean required)
    {
        super(col, labelText, new InputNumber());
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputNumber(FormCol col, String labelText,String name,Short value)
    {
        this(col, labelText, name,value,false);
    }
}
