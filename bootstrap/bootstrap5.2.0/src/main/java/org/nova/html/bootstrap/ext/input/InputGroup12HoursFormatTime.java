package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.ext.InputHidden;
import org.nova.utils.TypeUtils;

public class InputGroup12HoursFormatTime extends StyleComponent<InputGroup12HoursFormatTime>
{
    final SelectHourAMPM selectHour;
    final SelectMinute selectMinute;
    final SelectSecond selectSecond;

    public InputGroup12HoursFormatTime(String namePrefix,int minuteStep,Integer secondStep,Integer hourValue,Integer minuteValue,Integer secondValue)
    {
        super("div",null);
        input_group().d(Display.flex);

        namePrefix=TypeUtils.isNullOrEmpty(namePrefix)?"":namePrefix+"-";

        this.selectHour=returnAddInner(new SelectHourAMPM(hourValue));
        this.selectMinute=returnAddInner(new SelectMinute(minuteStep,minuteValue));
        selectHour.name(namePrefix+"hour").form_select();
        selectMinute.name(namePrefix+"minute").form_select();
        if (secondStep!=null)
        {
            this.selectSecond=returnAddInner(new SelectSecond(secondStep,secondValue));
            selectSecond.name(namePrefix+"second");
            addInner(selectSecond);
            selectHour.style("width:33.3%;");
            selectMinute.style("width:33.3%;");
            selectSecond.style("width:33.3%;");
        }
        else
        {
            this.selectSecond=null;
            addInner(new InputHidden(namePrefix+"second",0));
            selectHour.style("width:50%;");
            selectMinute.style("width:50%;");
        }
    }
    public InputGroup12HoursFormatTime(String namePrefix,int minuteStep,Integer secondStep)
    {
        this(namePrefix,minuteStep,secondStep,null,null,null);
    }
    public InputGroup12HoursFormatTime required(boolean required)
    {
        this.selectHour.required(required);
        this.selectMinute.required(required);
        if (this.selectSecond!=null)
        {
            this.selectSecond.required(required);
        }
        return this;
    }
    public InputGroup12HoursFormatTime required()
    {
        return required(true);
    }
        
    public InputGroup12HoursFormatTime set(Integer hourValue,Integer minuteValue,Integer secondValue)
    {
        this.selectHour.set(hourValue);
        this.selectMinute.set(minuteValue);
        if (this.selectSecond!=null)
        {
            this.selectSecond.set(secondValue);
        }
        return this;
    }    
}
