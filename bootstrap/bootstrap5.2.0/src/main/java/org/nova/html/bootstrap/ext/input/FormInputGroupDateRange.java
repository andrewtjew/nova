package org.nova.html.bootstrap.ext.input;

import java.time.LocalDate;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.AlignItems;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.TextAlign;
import org.nova.html.elements.Composer;
import org.nova.localization.DateFormat;
import org.nova.utils.TypeUtils;

public class FormInputGroupDateRange extends StyleComponent<FormInputGroupDateRange>
{
    private InputGroupDate start;
    private InputGroupDate end;
    final private Item validationMessage;
    
    public FormInputGroupDateRange(FormCol formCol, String startLabelText,String endLabelText,String namePrefix,DateFormat dateFormat,int baseYear,int years,LocalDate start,LocalDate end,boolean required)
    {
        super("div");
        id();
        if (formCol!=null)
        {
            if (formCol.breakPoint != null)
            {
                if (formCol.columns>0)
                {
                    addClass("col", formCol.breakPoint, formCol.columns);
                }
                else if (formCol.auto)
                {
                    addClass("col", formCol.breakPoint, "auto");
                }
                else
                {
                    addClass("col", formCol.breakPoint);
                }
            }
            else 
            {
                if (formCol.columns>0)
                {
                    addClass("col", formCol.columns);
                }
                else if (formCol.auto)
                {
                    addClass("col", "auto");
                }
                else
                {
                    addClass("col");
                }
            }
        }
        
        Item container=returnAddInner(new Item()).w(100).d(Display.flex).p(0).m(0);
        Item left=container.returnAddInner(new Item()).style("width:48%;");
        Item center=container.returnAddInner(new Item()).style("width:4%;").align_items(AlignItems.end).d(Display.flex);
        Item right=container.returnAddInner(new Item()).style("width:48%;");

        left.returnAddInner(new Item()).returnAddInner(new Label(startLabelText));
        center.returnAddInner(new Item()).addInner("-").text(TextAlign.center).w(100).pb(2);
        right.returnAddInner(new Item()).returnAddInner(new Label(endLabelText));

        namePrefix=TypeUtils.isNullOrEmpty(namePrefix)?"":namePrefix+"-";

        this.start=left.returnAddInner(new InputGroupDate(namePrefix+"start",dateFormat,baseYear, years));
        this.end=right.returnAddInner(new InputGroupDate(namePrefix+"end",dateFormat,baseYear, years));
//        this.end.form_control();

        d(Display.block);
        this.validationMessage=returnAddInner(new Item());
        this.validationMessage.w(100).px(1).id();
        mb(3);
        
    }
    public FormInputGroupDateRange(FormCol col, String startLabelText,String endLabelText,String namePrefix,DateFormat dateFormat,int baseYear,int years,LocalDate start,LocalDate end)
    {
        this(col, startLabelText, endLabelText,namePrefix,dateFormat,baseYear,years,start,end,false);
    }
    public FormInputGroupDateRange(FormCol col, String startLabelText,String endLabelText,String namePrefix,DateFormat dateFormat,int baseYear,int years)
    {
        this(col, startLabelText, endLabelText,namePrefix,dateFormat,baseYear,years,null,null);
    }
//    public FormMultiSelectYearMonthDay(String labelText,String namePrefix,int baseYear,int years,LocalDate value,boolean required)
//    {
//        this(null,labelText,namePrefix,baseYear,years,value,required);
//    }
    public void setValidationErrorMessage(String message)
    {
        this.validationMessage.clearInners();
        this.validationMessage.returnAddInner(new Item()).addInner(message).w(100).bg(StyleColor.warning).px(2);
    }
    @Override
    public void compose(Composer composer) throws Throwable
    {
        super.compose(composer);
        this.validationMessage.clearInners();
    }            
    public InputGroupDate startInput()
    {
        return this.start;
    }
    public InputGroupDate endInput()
    {
        return this.end;
    }
}
