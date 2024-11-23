package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputCheckbox;
import org.nova.html.bootstrap.InputComponent;
import org.nova.html.bootstrap.InputSwitch;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.Select;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.AlignSelf;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.elements.Composer;
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
