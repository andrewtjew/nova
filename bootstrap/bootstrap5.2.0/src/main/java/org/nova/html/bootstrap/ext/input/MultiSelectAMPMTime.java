package org.nova.html.bootstrap.ext.input;

import java.time.LocalDate;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Select;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.tags.option;
import org.nova.localization.CountryCode;
import org.nova.localization.CurrencyCode;
import org.nova.utils.TypeUtils;

public class MultiSelectAMPMTime extends Item
{
    final private String namePrefix;
    final int minuteStep;
    final Integer secondStep;

    public MultiSelectAMPMTime(String namePrefix,int minuteStep,Integer secondStep,Integer hourValue,Integer minuteValue,Integer secondValue)
    {
        input_group().d(Display.flex);
        this.minuteStep=minuteStep;
        this.secondStep=secondStep;
        this.namePrefix=namePrefix;

        set(hourValue,minuteValue,secondValue);
    }
    public MultiSelectAMPMTime(String namePrefix,int minuteStep,int secondStep)
    {
        this(namePrefix,minuteStep,secondStep,null,null,null);
    }
    
    public MultiSelectAMPMTime set(Integer hourValue,Integer minuteValue,Integer secondValue)
    {
        clearInners();
        SelectHourAMPM selectHour=new SelectHourAMPM(hourValue);
        SelectMinute selectMinute=new SelectMinute(this.secondStep,minuteValue);
        selectHour.name(namePrefix+"-year").form_control();
        selectMinute.name(namePrefix+"-month").form_control();
        addInner(selectHour);
        addInner(selectMinute);

        if (this.secondStep!=null)
        {
            SelectSecond selectSecond=new SelectSecond(this.secondStep,secondValue);
            selectSecond.name(namePrefix+"-day").form_control();
            addInner(selectSecond);
        }
        
        return this;
    }    
}
