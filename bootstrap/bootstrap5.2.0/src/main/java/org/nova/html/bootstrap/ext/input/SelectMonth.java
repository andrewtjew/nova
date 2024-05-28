package org.nova.html.bootstrap.ext.input;

import java.text.DateFormatSymbols;
import java.time.Month;

import org.nova.html.bootstrap.Select;
import org.nova.html.elements.Composer;
import org.nova.html.tags.option;
import org.nova.utils.TypeUtils;


public class SelectMonth extends Select
{
    private Integer value;
    public SelectMonth set(Integer value)
    {
        this.value=value;
        return this;
    }
    public SelectMonth set(Month value)
    {
        return set(value!=null?value.getValue():null);
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
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
        super.compose(composer);
    }
}
