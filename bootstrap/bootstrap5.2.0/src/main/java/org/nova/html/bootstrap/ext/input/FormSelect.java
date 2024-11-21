package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Select;
import org.nova.html.bootstrap.classes.AlignSelf;

public class FormSelect extends FormSelectComponent<Select>
{
    public FormSelect(FormCol col, String labelText,String name,boolean required)
    {
        super(col, labelText, new Select(), null);
        input().name(name).required(required).form_select();
    }
    public FormSelect(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,false);
    }
}
