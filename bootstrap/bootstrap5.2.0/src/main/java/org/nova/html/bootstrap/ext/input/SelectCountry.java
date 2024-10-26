package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Select;
import org.nova.html.tags.option;
import org.nova.localization.CountryCode;

public class SelectCountry extends Select
{
    final private CountryCode[] countryCodes;
    public SelectCountry(CountryCode[] countryCodes,CountryCode value)
    {
        this.countryCodes=countryCodes;
        set(value);
    }
    public SelectCountry(CountryCode[] countryCodes)
    {
        this(countryCodes,null);
    }
    
    public SelectCountry set(CountryCode value)
    {
        clearInners();
        option prompt=returnAddInner(new option()).selected().value("").addInner("Select");
        if (this.isRequired())
        {
            prompt.disabled();
        }
        for (CountryCode item:this.countryCodes)
        {
            option option=returnAddInner(new option()).value(item);
            option.addInner(item.getValue().displayName);
            option.selected(value==item);
        } 
        return this;
    }
}
