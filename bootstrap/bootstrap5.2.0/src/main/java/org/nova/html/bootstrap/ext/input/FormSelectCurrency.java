package org.nova.html.bootstrap.ext.input;

import org.nova.localization.CurrencyCode;

public class FormSelectCurrency extends FormSelectComponent<SelectCurrency>
{
    public FormSelectCurrency(FormCol col, String labelText,String name,CurrencyCode[] currencyCodes,CurrencyCode value,boolean required)
    {
        super(col, labelText, new SelectCurrency(currencyCodes,value), null);
        input().name(name).required(required);
        
    }
    public FormSelectCurrency(FormCol col, String labelText,String name,CurrencyCode[] currencyCodes,CurrencyCode value)
    {
        this(col, labelText, name,currencyCodes,value,false);
    }
//    public FormSelectCurrency(String labelText,String name,CurrencyCode[] currencyCodes,CurrencyCode value,boolean required)
//    {
//        this(null,labelText,name,currencyCodes,value,required);
//    }
//    public FormSelectCurrency(String labelText,String name,CurrencyCode[] currencyCodes,CurrencyCode value)
//    {
//        this(labelText, name,currencyCodes,value,false);
//    }
    public FormSelectCurrency(FormCol col,String labelText,String name,CurrencyCode[] currencyCodes)
    {
        this(col,labelText,name,currencyCodes,null);
    }
    
}
