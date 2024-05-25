package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Select;
import org.nova.html.tags.option;
import org.nova.localization.CountryCode;
import org.nova.localization.CurrencyCode;
import org.nova.utils.TypeUtils;


public class SelectCurrency extends Select
{
    final private CurrencyCode[] currencyCodes;
    public SelectCurrency(CurrencyCode[] currencyCodes,CurrencyCode value)
    {
        this.currencyCodes=currencyCodes;
        set(value);
    }
    public SelectCurrency(CurrencyCode[] currencyCodes)
    {
        this(currencyCodes,null);
    }
    
    public SelectCurrency set(CurrencyCode value)
    {
        clearInners();
        option prompt=returnAddInner(new option()).selected().value("").addInner("Select");
        if (this.isRequired())
        {
            prompt.disabled();
        }
        for (CurrencyCode item:this.currencyCodes)
        {
            option option=returnAddInner(new option()).value(item.getValue().alphabeticCode);
            option.addInner(item+" "+item.getValue().symbol);
            option.selected(value==item);
        } 
        return this;
    }    
}
