package org.nova.html.bootstrap.ext.input;

import java.time.LocalDate;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Select;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.tags.option;
import org.nova.localization.CountryCode;
import org.nova.localization.CurrencyCode;
import org.nova.utils.TypeUtils;

public class MultiSelectYearMonth extends Item
{
    final private String namePrefix;
    final private int baseYear;
    final private int years;

    public MultiSelectYearMonth(String namePrefix,int baseYear,int years,Integer yearValue,Integer monthValue)
    {
        input_group().d(Display.flex);
        this.namePrefix=namePrefix;
        this.baseYear=baseYear;
        this.years=years;

        set(yearValue,monthValue);
    }
    public MultiSelectYearMonth(String namePrefix,int baseYear,int years)
    {
        this(namePrefix,baseYear,years,null,null);
    }
    
    public MultiSelectYearMonth set(Integer yearValue,Integer monthValue)
    {
        clearInners();
        SelectYear selectYear=new SelectYear(this.baseYear,years,yearValue);
        SelectMonth selectMonth=new SelectMonth(monthValue);

        selectYear.name(namePrefix+"-year").form_control();
        selectMonth.name(namePrefix+"-month").form_control();

        addInner(selectYear);
        addInner(selectMonth);
        
        return this;
    }    
}
