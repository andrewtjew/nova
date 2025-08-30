package org.nova.html.bootstrap.ext.input;

import java.time.LocalDate;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.Display;
import org.nova.utils.TypeUtils;

public class InputGroupDate extends StyleComponent<InputGroupDate>
{
    final private SelectYear selectYear;
    final private SelectMonth selectMonth;
    final private SelectDay selectDay;

    public InputGroupDate(String namePrefix,int baseYear,int years,Integer yearValue,Integer monthValue,Integer dayValue)
    {
        super("div",null);
        input_group().d(Display.flex);

        namePrefix=TypeUtils.isNullOrEmpty(namePrefix)?"":namePrefix+"-";
        
        this.selectYear=returnAddInner(new SelectYear(baseYear,years,yearValue));
        this.selectMonth=returnAddInner(new SelectMonth().set(monthValue));
        this.selectDay=returnAddInner(new SelectDay(dayValue));

        this.selectYear.name(namePrefix+"year").form_select().style("width:30%;");
        this.selectMonth.name(namePrefix+"month").form_select().style("width:40%;");
        this.selectDay.name(namePrefix+"day").form_select().style("width:30%;");
    }
    public InputGroupDate(String namePrefix,int baseYear,int years)
    {
        this(namePrefix,baseYear,years,null,null,null);
    }
    public InputGroupDate(int baseYear,int years)
    {
        this(null,baseYear,years);
    }
    public InputGroupDate disabled(boolean value)
    {
        this.selectYear.disabled(value);
        this.selectMonth.disabled(value);
        this.selectDay.disabled(value);
        return this;
    }
    
    public InputGroupDate set(Integer yearValue,Integer monthValue,Integer dayValue)
    {
        this.selectYear.set(yearValue);
        this.selectMonth.set(monthValue);
        this.selectDay.set(dayValue);
        
        return this;
    }    
    public InputGroupDate set(LocalDate date)
    {
        if (date!=null)
        {
            this.selectYear.set(date.getYear());
            this.selectMonth.set(date.getMonthValue());
            this.selectDay.set(date.getDayOfMonth());
        }
        else
        {
            this.selectYear.set(null);
            this.selectMonth.set((Integer)null);
            this.selectDay.set(null);
        }
        
        return this;
    }    
}
