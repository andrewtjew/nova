package org.nova.html.bootstrap.ext.input;

import java.time.LocalTime;

import org.nova.localization.ClockFormat;

public class FormInputGroupHourMinute extends FormInputComponent<InputGroupHourMinute>
{
    public FormInputGroupHourMinute(FormCol col, String labelText,String namePrefix,ClockFormat clockFormat,int minuteStep,LocalTime value,boolean required)
    {
        super(col, labelText, new InputGroupHourMinute(namePrefix,clockFormat,minuteStep,value!=null?value.getHour():null,value!=null?value.getMinute():null,value!=null?value.getSecond():null));
    }
    public FormInputGroupHourMinute(FormCol col, String labelText,String namePrefix,ClockFormat clockFormat,int minuteStep,LocalTime value)
    {
        this(col, labelText, namePrefix,clockFormat,minuteStep,value,false);
    }
    public FormInputGroupHourMinute(FormCol col, String labelText,String namePrefix,ClockFormat clockFormat,int minuteStep)
    {
        this(col, labelText, namePrefix,clockFormat,minuteStep,null);
    }
}
