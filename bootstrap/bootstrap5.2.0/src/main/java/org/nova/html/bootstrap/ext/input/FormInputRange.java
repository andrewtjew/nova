package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputRange;
import org.nova.html.elements.Element;

public class FormInputRange extends FormInputComponent<InputRange>
{
    public FormInputRange(FormCol col, String labelText,String name,Double value,Double minimum,Double maximum,Double step,Element right)
    {
        super(col, labelText, new InputRange(), right);
        input().name(name);
        if (value!=null)
        {
            input().value(value);
        }
        if (minimum!=null)
        {
            input().min(minimum);
        }
        if (maximum!=null)
        {
            input().max(maximum);
        }
        if (step!=null)
        {
            input().step(step);
        }
    }
    public FormInputRange(FormCol col, String labelText,String name,Double value,Element right)
    {
        this(col,labelText,name,value,0.0,100.0,1.0,null);
    }
    public FormInputRange(FormCol col, String labelText,String name,Double value)
    {
        this(col,labelText,name,value,null);
    }
    public FormInputRange(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,null);
    }
}
