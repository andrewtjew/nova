package org.nova.html.bootstrap.ext.input;

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
        boolean selected=false;
        if (years<0)
        {
            for (int i=0;i<-years;i++)
            {
                int year=baseYear-i;
                option option=returnAddInner(new option()).value(year);
                option.addInner(year);
                boolean equal=TypeUtils.equals(value,year);
                if (equal)
                {
                    selected=true;
                }
                option.selected(equal);
            }
        }
        else
        {
            for (int i=0;i<years;i++)
            {
                int year=baseYear+i;
                option option=returnAddInner(new option()).value(year);
                option.addInner(year);
                boolean equal=TypeUtils.equals(value,year);
                if (equal)
                {
                    selected=true;
                }
                option.selected(equal);
            }
        }
        if (selected==false)
        {
            option option=returnAddInner(new option()).value(value);
            option.addInner(value);
            option.selected(true);
        }
        return this;
    }
}
