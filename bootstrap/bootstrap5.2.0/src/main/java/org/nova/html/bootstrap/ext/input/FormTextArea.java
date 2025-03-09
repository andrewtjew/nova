package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputText;
import org.nova.html.bootstrap.TextArea;
import org.nova.html.elements.Element;

public class FormTextArea extends FormInputComponent<TextArea>
{
    public FormTextArea(FormCol col, String labelText,String name,String value,Element right)
    {
        super(col, labelText, new TextArea(), right);
        input().name(name);
        if (value!=null)
        {
            input().addInner(value);
        }
    }
    public FormTextArea(FormCol col, String labelText,String name,String value)
    {
        this(col, labelText, name,value,null);
    }
    public FormTextArea(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,null);
    }
}
