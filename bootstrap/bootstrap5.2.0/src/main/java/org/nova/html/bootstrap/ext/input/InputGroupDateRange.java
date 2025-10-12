package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.AlignItems;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.TextAlign;
import org.nova.net.printing.Align;
import org.nova.utils.TypeUtils;

public class InputGroupDateRange extends StyleComponent<InputGroupDateRange>
{
    final private SelectYear selectYearStart;
    final private SelectMonth selectMonthStart;
    final private SelectDay selectDayStart;

    final private SelectYear selectYearEnd;
    final private SelectMonth selectMonthEnd;
    final private SelectDay selectDayEnd;

    public InputGroupDateRange(String namePrefix,int baseYear,int years,Integer yearStartValue,Integer monthStartValue,Integer dayStartValue,Integer yearEndValue,Integer monthEndValue,Integer dayEndValue)
    {
        super("div",null);
        input_group().d(Display.flex).flex(Flex.wrap);

        namePrefix=TypeUtils.isNullOrEmpty(namePrefix)?"":namePrefix+"-";
        
        Item start=returnAddInner(new Item()).d(Display.flex).align_items(AlignItems.center).style("width:50%;min-width:19em;");
        start.returnAddInner(new Label()).addInner("From:").style("width:4em;").text(TextAlign.end).pe(2);
        this.selectYearStart=start.returnAddInner(new SelectYear(baseYear,years,yearStartValue));
        this.selectMonthStart=start.returnAddInner(new SelectMonth().set(monthStartValue));
        this.selectDayStart=start.returnAddInner(new SelectDay(dayStartValue));

        this.selectYearStart.name(namePrefix+"yearStart").form_select().style("width:30%;");
        this.selectMonthStart.name(namePrefix+"monthStart").form_select().style("width:45%;");
        this.selectDayStart.name(namePrefix+"dayStart").form_select().style("width:25%;");

        Item end=returnAddInner(new Item()).d(Display.flex).align_items(AlignItems.center).style("width:50%;min-width:19em;");
        end.returnAddInner(new Label()).addInner("To:").style("width:4em;").text(TextAlign.end).pe(2);
        this.selectYearEnd=end.returnAddInner(new SelectYear(baseYear,years,yearEndValue));
        this.selectMonthEnd=end.returnAddInner(new SelectMonth().set(monthEndValue));
        this.selectDayEnd=end.returnAddInner(new SelectDay(dayEndValue));

        this.selectYearEnd.name(namePrefix+"yearEnd").form_select().style("width:30%;");
        this.selectMonthEnd.name(namePrefix+"monthEnd").form_select().style("width:45%;");
        this.selectDayEnd.name(namePrefix+"dayEnd").form_select().style("width:25%;");
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
