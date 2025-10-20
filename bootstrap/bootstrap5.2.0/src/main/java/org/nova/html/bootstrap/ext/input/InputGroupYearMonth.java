package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.Display;
import org.nova.localization.DateFormat;


public class InputGroupYearMonth extends StyleComponent<InputGroupYearMonth>
{
    final private DateFormat dateFormat;
    final private String namePrefix;
    final private int baseYear;
    final private int years;

    public InputGroupYearMonth(String namePrefix,DateFormat dateFormat,int baseYear,int years,Integer yearValue,Integer monthValue)
    {
        super("div",null);
        input_group().d(Display.flex);
        this.dateFormat=dateFormat;
        this.namePrefix=namePrefix;
        this.baseYear=baseYear;
        this.years=years;

        set(yearValue,monthValue);
    }
    public InputGroupYearMonth(String namePrefix,DateFormat dateFormat,int baseYear,int years)
    {
        this(namePrefix,dateFormat,baseYear,years,null,null);
    }
    
    public InputGroupYearMonth set(Integer yearValue,Integer monthValue)
    {
        clearInners();
        SelectYear selectYear=new SelectYear(this.baseYear,years,yearValue);
        SelectMonth selectMonth=new SelectMonth().set(monthValue);

        selectYear.name(namePrefix+"-year").form_select().style("width:40%");
        selectMonth.name(namePrefix+"-month").form_select().style("width:60%");

        switch (dateFormat)
        {
            case DAY_MONTH_YEAR:
            addInner(selectMonth);
            addInner(selectYear);
                break;
            case MONTH_DAY_YEAR:
            addInner(selectMonth);
            addInner(selectYear);
                break;
            case YEAR_MONTH_DAY:
            addInner(selectYear);
            addInner(selectMonth);
                break;
            default:
                break;
            
        }
        
        return this;
    }    

    public InputGroupYearMonth standardWidth()
    {
        this.style("width:13em;");
        return this;
    }    
}
