package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.InputComponent;
import org.nova.html.bootstrap.InputNumber;
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

public class FormInputNumber extends FormInput<InputNumber>
{
    public FormInputNumber(FormCol col, String labelText,String name,Double value,boolean required)
    {
        super(col, labelText, new InputNumber(), null);
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputNumber(FormCol col, String labelText,String name,Double value)
    {
        this(col, labelText, name,value,false);
    }
    public FormInputNumber(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,null);
    }
   
//    public FormInputNumber(String labelText,String name,Double value,boolean required)
//    {
//        this(null,labelText,name,value,required);
//    }
//    public FormInputNumber(String labelText,String name,Double value)
//    {
//        this(labelText, name,value,false);
//    }
//    public FormInputNumber(String labelText,String name,boolean required)
//    {
//        this(labelText, name,null,required);
//    }
//    public FormInputNumber(String labelText,String name)
//    {
//        this(labelText,name,null);
//    }
}
