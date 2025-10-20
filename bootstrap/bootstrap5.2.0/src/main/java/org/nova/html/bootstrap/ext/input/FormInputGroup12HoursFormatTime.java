package org.nova.html.bootstrap.ext.input;

import java.time.LocalTime;

public class FormInputGroup12HoursFormatTime extends FormInputComponent<InputGroup12HoursFormatTime>
{
    public FormInputGroup12HoursFormatTime(FormCol col, String labelText,String namePrefix,int minuteStep,Integer secondStep,LocalTime value,boolean required)
    {
        super(col, labelText, new InputGroup12HoursFormatTime(namePrefix,minuteStep,secondStep,value!=null?value.getHour():null,value!=null?value.getMinute():null,value!=null?value.getSecond():null));
    }
    public FormInputGroup12HoursFormatTime(FormCol col, String labelText,String namePrefix,int minuteStep,Integer secondStep,LocalTime value)
    {
        this(col, labelText, namePrefix,minuteStep,secondStep,value,false);
    }
    public FormInputGroup12HoursFormatTime(FormCol col, String labelText,String namePrefix,int minuteStep,Integer secondStep)
    {
        this(col, labelText, namePrefix,minuteStep,secondStep,null);
    }
}
