package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.localization.AMPM;
import org.nova.localization.ClockFormat;

public class InputGroupHourMinute extends StyleComponent<InputGroupHourMinute>
{
    final private String namePrefix;
    final int minuteStep;
    final Integer secondStep;
    final ClockFormat clockFormat;
    
    public InputGroupHourMinute(String namePrefix,ClockFormat clockFormat,int minuteStep,Integer secondStep,Integer hourValue,Integer minuteValue)
    {
        super("div",null);
        input_group().d(Display.flex);
        this.minuteStep=minuteStep;
        this.secondStep=secondStep;
        this.namePrefix=namePrefix;
        this.clockFormat=clockFormat;

        set(clockFormat,hourValue,minuteValue);
    }
    public InputGroupHourMinute(String namePrefix,ClockFormat clockFormat,int minuteStep)
    {
        this(namePrefix,clockFormat,minuteStep,null,null,null);
    }
    
    public InputGroupHourMinute set(ClockFormat clockFormat,Integer hour,Integer minute)
    {
//        clearInners();
//        SelectHourAMPM selectHour=new SelectHourAMPM(hourValue);
//        SelectMinute selectMinute=new SelectMinute(this.minuteStep,minuteValue);
//        selectHour.name(namePrefix+"-hour").form_control();
//        selectMinute.name(namePrefix+"-minute").form_control();
//        addInner(selectHour);
//        addInner(selectMinute);
//
//        if (this.secondStep!=null)
//        {
//            SelectSecond selectSecond=new SelectSecond(this.secondStep,secondValue);
//            selectSecond.name(namePrefix+"-second").form_control();
//            addInner(selectSecond);
//        }
//        else
//        {
//            addInner(new InputHidden(namePrefix+"-second",0));
//        }
//        
//        return this;
        
        clearInners();
        SelectHour selectHour=returnAddInner(new SelectHour(this.clockFormat,hour));
        SelectMinute selectMinute=returnAddInner(new SelectMinute(this.minuteStep,minute));

        selectHour.name(namePrefix+"-hour").form_select();
        selectMinute.name(namePrefix+"-minute").form_select();
        if (this.clockFormat==ClockFormat.AM_PM)
        {
            
            SelectAMPM selectAMPM=returnAddInner(new SelectAMPM(hour==null?null:(hour<12?AMPM.AM:AMPM.PM)));
            selectAMPM.name(namePrefix+"-AMPM").form_select();

            selectHour.style("width:33%");
            selectMinute.style("width:33%");
            selectAMPM.style("width:34%");

        }
        
        
        return this;
        
    }    
}
