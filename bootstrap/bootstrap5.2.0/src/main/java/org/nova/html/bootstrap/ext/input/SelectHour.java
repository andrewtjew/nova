package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Select;
import org.nova.html.tags.option;
import org.nova.utils.TypeUtils;


public class SelectHour extends Select
{
    final private int step;
    public SelectHour(int step,Integer value)
    {
        this.step=step;
        set(value);
    }
    public SelectHour(Integer value)
    {
        this(1,value);
    }
    
    public SelectHour set(Integer value)
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
            option.addInner(i);
            option.selected(TypeUtils.equals(value,i));
        }
        return this;
    }    
    
}
