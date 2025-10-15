package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.Display;

public class InputGroupYearMonth extends StyleComponent<InputGroupYearMonth>
{
    final private String namePrefix;
    final private int baseYear;
    final private int years;

    public InputGroupYearMonth(String namePrefix,int baseYear,int years,Integer yearValue,Integer monthValue)
    {
        super("div",null);
        input_group().d(Display.flex);
        this.namePrefix=namePrefix;
        this.baseYear=baseYear;
        this.years=years;

        set(yearValue,monthValue);
    }
    public InputGroupYearMonth(String namePrefix,int baseYear,int years)
    {
        this(namePrefix,baseYear,years,null,null);
    }
    
    public InputGroupYearMonth set(Integer yearValue,Integer monthValue)
    {
        clearInners();
        SelectYear selectYear=new SelectYear(this.baseYear,years,yearValue);
        SelectMonth selectMonth=new SelectMonth().set(monthValue);

        selectYear.name(namePrefix+"-year").form_control();
        selectMonth.name(namePrefix+"-month").form_control();

        addInner(selectYear);
        addInner(selectMonth);
        
        return this;
    }    
}
