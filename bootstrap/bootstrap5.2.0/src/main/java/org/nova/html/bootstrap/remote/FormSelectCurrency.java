package org.nova.html.bootstrap.remote;

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
import org.nova.localization.CurrencyCode;

public class FormSelectCurrency extends FormInput<SelectCurrency>
{
    public FormSelectCurrency(Integer columns, String labelText,String name,CurrencyCode[] currencyCodes,CurrencyCode value,boolean required)
    {
        super(null, columns, labelText, new SelectCurrency(currencyCodes,value), null);
        input().name(name).required(required);
        
    }
    public FormSelectCurrency(Integer columns, String labelText,String name,CurrencyCode[] currencyCodes,CurrencyCode value)
    {
        this(columns, labelText, name,currencyCodes,value,false);
    }
    public FormSelectCurrency(String labelText,String name,CurrencyCode[] currencyCodes,CurrencyCode value,boolean required)
    {
        this(null,labelText,name,currencyCodes,value,required);
    }
    public FormSelectCurrency(String labelText,String name,CurrencyCode[] currencyCodes,CurrencyCode value)
    {
        this(labelText, name,currencyCodes,value,false);
    }
    public FormSelectCurrency(String labelText,String name,CurrencyCode[] currencyCodes)
    {
        this(labelText,name,currencyCodes,null);
    }
    
}
