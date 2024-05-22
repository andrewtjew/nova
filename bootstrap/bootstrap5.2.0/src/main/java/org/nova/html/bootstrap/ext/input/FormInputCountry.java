package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.InputComponent;
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
import org.nova.html.tags.datalist;
import org.nova.html.tags.div;
import org.nova.html.tags.option;
import org.nova.localization.CountryCode;

public class FormInputCountry extends FormInput<InputText>
{
    public FormInputCountry(Integer columns, String labelText,String name,String value,boolean required)
    {
        super(null, columns, labelText, new InputText(), null);
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
        datalist list=returnAddInner(new datalist());
        for (CountryCode item:CountryCode.values())
        {
            list.returnAddInner(new option()).value(item.getValue().getDisplayName());
        }
        input().list(list);
    }
    public FormInputCountry(Integer columns, String labelText,String name,String value)
    {
        this(columns, labelText, name,value,false);
    }
    public FormInputCountry(Integer columns, String labelText,String name,boolean required)
    {
        this(columns, labelText, name,null,required);
    }
    public FormInputCountry(String labelText,String name,String value,boolean required)
    {
        this(null,labelText,name,value,required);
    }
    public FormInputCountry(String labelText,String name,String value)
    {
        this(labelText, name,value,false);
    }
    public FormInputCountry(String labelText,String name,boolean required)
    {
        this(labelText, name,null,required);
    }
    public FormInputCountry(String labelText,String name)
    {
        this(labelText,name,null);
    }
}
