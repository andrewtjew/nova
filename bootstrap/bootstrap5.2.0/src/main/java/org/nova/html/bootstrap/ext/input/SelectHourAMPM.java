package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Select;
import org.nova.html.tags.option;
import org.nova.localization.CountryCode;
import org.nova.localization.CurrencyCode;
import org.nova.utils.TypeUtils;


public class SelectHourAMPM extends Select
{
    final private int step;
    public SelectHourAMPM(int step,Integer value)
    {
        this.step=step;
        set(value);
    }
    public SelectHourAMPM(Integer value)
    {
        this(1,value);
    }
    
    public SelectHourAMPM set(Integer value)
    {
        clearInners();
        option prompt=returnAddInner(new option()).selected().value("").addInner("Hour");
        if (this.isRequired())
        {
            prompt.disabled();
        }
        for (int i=0;i<24;i++)
        {
            option option=returnAddInner(new option()).value(i);
            if (i==0)
            {
                option.addInner("12 AM");
            }
            else if (i<12)
            {
                option.addInner(i+" AM");
            }
            else if (i==12)
            {
                option.addInner("12 PM");
            }
            {
                option.addInner((i-12)+" PM");
            }
            option.selected(TypeUtils.equals(value,i));
        }
        return this;
    }    
    
}
