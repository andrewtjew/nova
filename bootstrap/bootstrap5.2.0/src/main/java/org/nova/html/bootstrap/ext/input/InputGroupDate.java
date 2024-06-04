package org.nova.html.bootstrap.ext.input;

import java.time.LocalDate;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Select;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.tags.option;
import org.nova.localization.CountryCode;
import org.nova.localization.CurrencyCode;
import org.nova.utils.TypeUtils;

public class InputGroupDate extends Item
{
    final private String namePrefix;
    final private int baseYear;
    final private int years;

    public InputGroupDate(String namePrefix,int baseYear,int years,Integer yearValue,Integer monthValue,Integer dayValue)
    {
        input_group().d(Display.flex);
        this.namePrefix=namePrefix;
        this.baseYear=baseYear;
        this.years=years;

        set(yearValue,monthValue,dayValue);
    }
    public InputGroupDate(String namePrefix,int baseYear,int years)
    {
        this(namePrefix,baseYear,years,null,null,null);
    }
    
    public InputGroupDate set(Integer yearValue,Integer monthValue,Integer dayValue)
    {
        clearInners();
        SelectYear selectYear=new SelectYear(this.baseYear,years,yearValue);
        SelectMonth selectMonth=new SelectMonth().set(monthValue);
        SelectDay selectDay=new SelectDay(dayValue);

        selectYear.name(namePrefix+"-year").form_select().style("width:30%;");
        selectMonth.name(namePrefix+"-month").form_select().style("width:40%;");
        selectDay.name(namePrefix+"-day").form_select().style("width:30%;");

        addInner(selectYear);
        addInner(selectMonth);
        addInner(selectDay);
        
        return this;
    }    
}
