package org.nova.html.bootstrap.ext.input;

public class FormInputNumberGroup extends FormInputComponent<InputGroupNumber>
{
    public FormInputNumberGroup(FormCol col, String labelText,String name,Double value,boolean required)
    {
        super(col, labelText, new InputGroupNumber());
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputNumberGroup(FormCol col, String labelText,String name,Double value)
    {
        this(col, labelText, name,value,false);
    }
    public FormInputNumberGroup(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,null);
    }
}
