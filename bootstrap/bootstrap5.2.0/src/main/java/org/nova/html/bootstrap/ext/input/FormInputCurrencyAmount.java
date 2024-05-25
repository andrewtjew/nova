package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.InputComponent;
import org.nova.html.bootstrap.InputPassword;
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
import org.nova.localization.CurrencyCode;


public class FormInputCurrencyAmount extends FormInput<InputCurrencyAmount>
{
    public FormInputCurrencyAmount(FormCol col, String labelText,String name,CurrencyCode currencyCode,Double value,boolean required,Element right)
    {
        super(col, labelText, new InputCurrencyAmount(currencyCode), right);
        input().name(name).required(required);
        
        if (value!=null)
        {
            input().value(value);
        }
    }
    public FormInputCurrencyAmount(FormCol col, String labelText,String name,CurrencyCode currencyCode,Double value,boolean required)
    {
        this(col,labelText,name,currencyCode,value,required,null);
    }
    public FormInputCurrencyAmount(FormCol col, String labelText,String name,CurrencyCode currencyCode,Double value)
    {
        this(col, labelText, name,currencyCode,value,false);
    }
//    public FormInputCurrencyAmount(String labelText,String name,CurrencyCode currencyCode,Double value,boolean required)
//    {
//        this(null,labelText,name,currencyCode,value,required);
//    }
//    public FormInputCurrencyAmount(String labelText,String name,CurrencyCode currencyCode,Double value)
//    {
//        this(labelText, name,currencyCode,value,false);
//    }
//    public FormInputCurrencyAmount(String labelText,String name,CurrencyCode currencyCode)
//    {
//        this(labelText,name,currencyCode,null);
//    }
}
