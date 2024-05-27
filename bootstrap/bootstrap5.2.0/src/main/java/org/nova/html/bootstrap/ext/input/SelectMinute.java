package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Select;
import org.nova.html.tags.option;
import org.nova.localization.CountryCode;
import org.nova.localization.CurrencyCode;
import org.nova.utils.TypeUtils;


public class SelectMinute extends Select
{
    final private int step;
    public SelectMinute(int step,Integer value)
    {
        this.step=step;
        set(value);
    }
    public SelectMinute(Integer value)
    {
        this(1,value);
    }
    
    public SelectMinute set(Integer value)
    {
        clearInners();
        option prompt=returnAddInner(new option()).selected().value("").addInner("Minute");
        if (this.isRequired())
        {
            prompt.disabled();
        }
        
        for (int i=0;i<60;i+=this.step)
        {
            option option=returnAddInner(new option()).value(i);
            option.addInner(String.format("%02d", i));    
            option.selected(TypeUtils.equals(value,i));
        }
        return this;
    }    
    
}
