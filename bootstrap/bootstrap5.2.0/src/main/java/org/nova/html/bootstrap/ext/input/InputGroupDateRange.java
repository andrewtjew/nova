package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.TextAlign;
import org.nova.utils.TypeUtils;

public class InputGroupDateRange extends Item
{
    final private SelectYear selectYearStart;
    final private SelectMonth selectMonthStart;
    final private SelectDay selectDayStart;

    final private SelectYear selectYearEnd;
    final private SelectMonth selectMonthEnd;
    final private SelectDay selectDayEnd;

    public InputGroupDateRange(String namePrefix,int baseYear,int years,Integer yearStartValue,Integer monthStartValue,Integer dayStartValue,Integer yearEndValue,Integer monthEndValue,Integer dayEndValue)
    {
        input_group().d(Display.flex);

        namePrefix=TypeUtils.isNullOrEmpty(namePrefix)?"":namePrefix+"-";
        
        this.selectYearStart=returnAddInner(new SelectYear(baseYear,years,yearStartValue));
        this.selectMonthStart=returnAddInner(new SelectMonth().set(monthStartValue));
        this.selectDayStart=returnAddInner(new SelectDay(dayStartValue));

        this.selectYearStart.name(namePrefix+"yearStart").form_select().style("width:15%;");
        this.selectMonthStart.name(namePrefix+"monthStart").form_select().style("width:20%;");
        this.selectDayStart.name(namePrefix+"dayStart").form_select().style("width:13%;");

        returnAddInner(new Span()).text(TextAlign.center).addInner(" - ").mt(2).style("width:4%;");

        this.selectYearEnd=returnAddInner(new SelectYear(baseYear,years,yearEndValue));
        this.selectMonthEnd=returnAddInner(new SelectMonth().set(monthEndValue));
        this.selectDayEnd=returnAddInner(new SelectDay(dayEndValue));

        this.selectYearEnd.name(namePrefix+"yearEnd").form_select().style("width:15%;");
        this.selectMonthEnd.name(namePrefix+"monthEnd").form_select().style("width:20%;");
        this.selectDayEnd.name(namePrefix+"dayEnd").form_select().style("width:13%;");
    }
    public InputGroupDateRange(String namePrefix,int baseYear,int years)
    {
        this(namePrefix,baseYear,years,null,null,null,null,null,null);
    }
    public InputGroupDateRange(int baseYear,int years)
    {
        this(null,baseYear,years);
    }
    public InputGroupDateRange disabled(boolean value)
    {
        this.selectYearStart.disabled(value);
        this.selectMonthStart.disabled(value);
        this.selectDayStart.disabled(value);
        return this;
    }
    
    public InputGroupDateRange set(Integer yearStartValue,Integer monthStartValue,Integer dayStartValue,Integer yearEndValue,Integer monthEndValue,Integer dayEndValue)
    {
        this.selectYearStart.set(yearStartValue);
        this.selectMonthStart.set(monthStartValue);
        this.selectDayStart.set(dayStartValue);
        
        this.selectYearEnd.set(yearEndValue);
        this.selectMonthEnd.set(monthEndValue);
        this.selectDayEnd.set(dayEndValue);
        
        return this;
    }    
}
