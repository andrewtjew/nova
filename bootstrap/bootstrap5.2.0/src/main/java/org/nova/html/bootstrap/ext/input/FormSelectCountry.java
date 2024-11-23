package org.nova.html.bootstrap.ext.input;

import org.nova.localization.CountryCode;

public class FormSelectCountry extends FormSelectComponent<SelectCountry>
{
    public FormSelectCountry(FormCol col, String labelText,String name,CountryCode[] countryCodes,CountryCode value,boolean required)
    {
        super(col, labelText, new SelectCountry(countryCodes,value), null);
        input().name(name).required(required);
    }
    public FormSelectCountry(FormCol col, String labelText,String name,CountryCode[] countryCodes,CountryCode value)
    {
        this(col, labelText, name,countryCodes,value,false);
    }
    
    public FormSelectCountry(FormCol col,String labelText,String name,CountryCode[] countryCodes)
    {
        this(col,labelText,name,countryCodes,null);
    }
    
}
