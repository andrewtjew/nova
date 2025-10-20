package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Select;
import org.nova.html.tags.option;
import org.nova.localization.ClockFormat;
import org.nova.utils.TypeUtils;


public class SelectHour extends Select
{
    final private ClockFormat clockFormat;
    public SelectHour(ClockFormat clockFormat,Integer value)
    {
        this.clockFormat=clockFormat;
        set(value);
    }
    public SelectHour(ClockFormat clockFormat)
    {
        this(clockFormat,null);
    }
    
    public SelectHour set(Integer value)
    {
        clearInners();
        option prompt=returnAddInner(new option()).selected().value("").addInner("Hour");
        if (this.isRequired())
        {
            prompt.disabled();
        }

        switch (this.clockFormat)
        {
            case AM_PM:
            if (value!=null)
            {
                value=value%12;
            }
            for (int i=1;i<13;i++)
            {
                option option=returnAddInner(new option()).value(i);
                option.addInner(i);
                option.selected(TypeUtils.equals(value,i));
            }
            break;

            case HOURS_24:
            for (int i=0;i<24;i++)
            {
                option option=returnAddInner(new option()).value(i);
                option.addInner(i);
                option.selected(TypeUtils.equals(value,i));
            }
            break;
        }
        
        return this;
    }    
    
}
