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

public class FormInputEmail extends FormInput<InputEmail>
{
    public FormInputEmail(FormCol col, String labelText,String name,String value,boolean required)
    {
        super(col, labelText, new InputEmail(), null);
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputEmail(FormCol col, String labelText,String name,String value)
    {
        this(col, labelText, name,value,false);
    }
    public FormInputEmail(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,null);
    }
    
//    public FormInputEmail(String labelText,String name,String value,boolean required)
//    {
//        this(null,labelText,name,value,required);
//    }
//    public FormInputEmail(String labelText,String name,String value)
//    {
//        this(labelText, name,value,false);
//    }
//    public FormInputEmail(String labelText,String name)
//    {
//        this(labelText,name,null);
//    }
}
