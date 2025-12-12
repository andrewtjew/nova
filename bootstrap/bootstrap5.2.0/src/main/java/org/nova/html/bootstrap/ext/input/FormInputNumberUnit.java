package org.nova.html.bootstrap.ext.input;

import org.nova.html.elements.Element;


public class FormInputNumberUnit extends FormInputComponent<InputNumberUnit>
{
    public FormInputNumberUnit(FormCol col, String labelText,String name,String unit,Double value,boolean required,Element right)
    {
        super(col, labelText, new InputNumberUnit(unit), right);
        input().name(name).required(required);
        
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputNumberUnit(FormCol col, String labelText,String name,String unit,Double value,boolean required)
    {
        this(col,labelText,name,unit,value,required,null);
    }
    public FormInputNumberUnit(FormCol col, String labelText,String name,String unit,Double value)
    {
        this(col, labelText, name,unit,value,false);
    }
}
