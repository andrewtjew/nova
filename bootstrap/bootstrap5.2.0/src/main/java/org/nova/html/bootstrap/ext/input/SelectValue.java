package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Select;
import org.nova.html.tags.option;
import org.nova.localization.CountryCode;
import org.nova.localization.CurrencyCode;
import org.nova.utils.TypeUtils;


public class SelectValue extends Select
{
    final private Integer value;
    final private int start;
    final private int end;
    final private int increment;
    final private String unit;
    final private String none;
    public SelectValue(String unit,int start,int end,int increment,String none,Integer value)
    {
        this.value=value;
        this.unit=unit;
        this.start=start;
        this.end=end;
        this.increment=increment;
        this.none=none;
        set(value);
    }
    
    public SelectValue set(Integer value)
    {
        clearInners();
        option prompt=returnAddInner(new option()).selected().value("").addInner(this.unit);
        if (this.isRequired())
        {
            prompt.disabled();
        }
        if (none!=null)
        {
            option option=returnAddInner(new option()).addInner(none).value("");
            option.selected(TypeUtils.equals(value,null));
        }
        for (int i=start;i<=this.end;i+=increment)
        {
            option option=returnAddInner(new option()).value(i);
            option.addInner(i);    
            option.selected(TypeUtils.equals(value,i));
        }
        return this;
    }    
    
}
