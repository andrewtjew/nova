package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Select;

public class FormSelect extends FormInput<Select>
{
    public FormSelect(FormCol col, String labelText,String name,boolean required)
    {
        super(col, labelText, new Select(), null);
        input().name(name).required(required);
    }
    public FormSelect(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,false);
    }
    
    
}
