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
import org.nova.html.tags.div;
import org.nova.localization.CountryCode;

public class FormSelectCountry extends FormInput<SelectCountry>
{
    public FormSelectCountry(BreakPoint breakPoint,Integer columns, String labelText,String name,CountryCode[] countryCodes,CountryCode value,boolean required)
    {
        super(breakPoint, columns, labelText, new SelectCountry(countryCodes,value), null);
        input().name(name).required(required);
    }
    public FormSelectCountry(BreakPoint breakPoint,Integer columns, String labelText,String name,CountryCode[] countryCodes,CountryCode value)
    {
        this(breakPoint,columns, labelText, name,countryCodes,value,false);
    }
    
    public FormSelectCountry(Integer columns, String labelText,String name,CountryCode[] countryCodes,CountryCode value,boolean required)
    {
        this(null,columns, labelText, name,countryCodes,value,required);
    }
    public FormSelectCountry(Integer columns, String labelText,String name,CountryCode[] countryCodes,CountryCode value)
    {
        this(columns, labelText, name,countryCodes,value,false);
    }
    public FormSelectCountry(String labelText,String name,CountryCode[] countryCodes,CountryCode value,boolean required)
    {
        this(null,labelText,name,countryCodes,value,required);
    }
    public FormSelectCountry(String labelText,String name,CountryCode[] countryCodes,CountryCode value)
    {
        this(labelText, name,countryCodes,value,false);
    }
    public FormSelectCountry(String labelText,String name,CountryCode[] countryCodes)
    {
        this(labelText,name,countryCodes,null);
    }
    
}