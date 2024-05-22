package org.nova.html.bootstrap.remote;

import java.text.DateFormatSymbols;
import java.time.Month;

import org.nova.html.bootstrap.Select;
import org.nova.html.tags.option;
import org.nova.utils.TypeUtils;


public class SelectMonth extends Select
{
    public SelectMonth(Integer value)
    {
        set(value);
    }
    public SelectMonth(Month value)
    {
        set(value!=null?value.getValue():null);
    }
    public SelectMonth()
    {
        set(null);
    }
    public SelectMonth set(Integer value)
    {
        clearInners();
        option prompt=returnAddInner(new option()).selected().value("").addInner("Month");
        if (this.isRequired())
        {
            prompt.disabled();
        }
        String[] months=new DateFormatSymbols().getMonths();
        for (int i=1;i<=12;i++)
        {
            int month=i;
            option option=returnAddInner(new option());
            option.addInner(months[i-1]);
            option.value(month);
            option.selected(TypeUtils.equals(month,value));
        }
        return this;
    }
}
