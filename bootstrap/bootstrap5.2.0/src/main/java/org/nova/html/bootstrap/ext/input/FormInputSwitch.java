package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.InputComponent;
import org.nova.html.bootstrap.InputEmail;
import org.nova.html.bootstrap.InputSwitch;
import org.nova.html.bootstrap.InputText;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.InputElement;
import org.nova.html.ext.Content;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.tags.div;

public class FormInputSwitch extends FormInput<InputSwitch>
{
    public FormInputSwitch(Integer columns, String labelText,String name,boolean checked)
    {
        super(null, columns, labelText, new InputSwitch(), null);
        input().name(name).checked(checked);
    }
    public FormInputSwitch(Integer columns, String labelText,String name)
    {
        this(columns, labelText, name,false);
    }
    public FormInputSwitch(String labelText,String name,boolean checked)
    {
        this(null,labelText,name,checked);
    }
    public FormInputSwitch(String labelText,String name)
    {
        this(labelText,name,false);
    }
}
