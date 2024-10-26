package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.ext.InputHidden;

public class InputGroup12HoursFormatTime extends Item
{
    final private String namePrefix;
    final int minuteStep;
    final Integer secondStep;

    public InputGroup12HoursFormatTime(String namePrefix,int minuteStep,Integer secondStep,Integer hourValue,Integer minuteValue,Integer secondValue)
    {
        input_group().d(Display.flex);
        this.minuteStep=minuteStep;
        this.secondStep=secondStep;
        this.namePrefix=namePrefix;

        set(hourValue,minuteValue,secondValue);
    }
    public InputGroup12HoursFormatTime(String namePrefix,int minuteStep,int secondStep)
    {
        this(namePrefix,minuteStep,secondStep,null,null,null);
    }
    
    public InputGroup12HoursFormatTime set(Integer hourValue,Integer minuteValue,Integer secondValue)
    {
        clearInners();
        SelectHourAMPM selectHour=new SelectHourAMPM(hourValue);
        SelectMinute selectMinute=new SelectMinute(this.minuteStep,minuteValue);
        selectHour.name(namePrefix+"-hour").form_control();
        selectMinute.name(namePrefix+"-minute").form_control();
        addInner(selectHour);
        addInner(selectMinute);

        if (this.secondStep!=null)
        {
            SelectSecond selectSecond=new SelectSecond(this.secondStep,secondValue);
            selectSecond.name(namePrefix+"-second").form_control();
            addInner(selectSecond);
        }
        else
        {
            addInner(new InputHidden(namePrefix+"-second",0));
        }
        
        return this;
    }    
}
