package org.nova.html.bootstrap.ext.input;

import org.nova.html.elements.Element;
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
