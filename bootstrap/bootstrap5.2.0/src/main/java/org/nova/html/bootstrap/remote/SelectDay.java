package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Select;
import org.nova.html.tags.option;
import org.nova.localization.CountryCode;
import org.nova.localization.CurrencyCode;
import org.nova.utils.TypeUtils;


public class SelectDay extends Select
{
    final private int days;
    public SelectDay(int days,Integer value)
    {
        this.days=days;
        set(value);
    }
    public SelectDay(Integer value)
    {
        this(31,value);
    }
    
    public SelectDay set(Integer value)
    {
        clearInners();
        option prompt=returnAddInner(new option()).selected().value("").addInner("Day");
        if (this.isRequired())
        {
            prompt.disabled();
        }
        for (int i=1;i<this.days+1;i++)
        {
            option option=returnAddInner(new option()).value(i);
            option.addInner(i);    
            option.selected(TypeUtils.equals(value,i));
        }
        return this;
    }    
    
}
