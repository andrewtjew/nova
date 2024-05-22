package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.Select;
import org.nova.html.tags.option;
import org.nova.utils.TypeUtils;



public class SelectYear extends Select
{
    final private int baseYear;
    final private int years;
    public SelectYear(int baseYear,int years,Integer value)
    {
        this.baseYear=baseYear;
        this.years=years;
        set(value);
    }
    public SelectYear(int baseYear,int years)
    {
        this(baseYear,years,null);
    }
    public SelectYear set(Integer value)
    {
        clearInners();
        option prompt=returnAddInner(new option()).selected().value("").addInner("Year");
        if (this.isRequired())
        {
            prompt.disabled();
        }
        if (years<0)
        {
            for (int i=0;i<-years;i++)
            {
                int year=baseYear-i;
                option option=returnAddInner(new option()).value(year);
                option.addInner(year);
                option.selected(TypeUtils.equals(value,year));
            }
        }
        else
        {
            for (int i=0;i<years;i++)
            {
                int year=baseYear+i;
                option option=returnAddInner(new option()).value(year);
                option.addInner(year);
                option.selected(TypeUtils.equals(value,year));
            }
        }
        return this;
    }
}
