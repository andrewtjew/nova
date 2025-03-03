package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Select;
import org.nova.html.elements.Element;

public class FormSelectComponent<SELECT extends Select> extends FormInputComponent<SELECT>
{
    public FormSelectComponent(FormCol formCol,String labelText,SELECT input,Element right)
    {
        super(formCol,labelText,input,right);
    }
    
    public FormSelectComponent(FormCol col,String labelText,SELECT input)
    {
        this(col,labelText,input,null);
    }

    public SELECT select()
    {
        return this.input();
    }
    
}
