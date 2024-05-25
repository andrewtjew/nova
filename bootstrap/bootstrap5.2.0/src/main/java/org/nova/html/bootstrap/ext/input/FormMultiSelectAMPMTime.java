package org.nova.html.bootstrap.ext.input;

import java.time.LocalDate;
import java.time.LocalTime;

public class FormMultiSelectAMPMTime extends FormInput<MultiSelectAMPMTime>
{
    public FormMultiSelectAMPMTime(FormCol col, String labelText,String namePrefix,int minuteStep,Integer secondStep,LocalTime value,boolean required)
    {
        super(col, labelText, new MultiSelectAMPMTime(namePrefix,minuteStep,secondStep,value!=null?value.getHour():null,value!=null?value.getMinute():null,value!=null?value.getSecond():null));
    }
    public FormMultiSelectAMPMTime(FormCol col, String labelText,String namePrefix,int minuteStep,Integer secondStep,LocalTime value)
    {
        this(col, labelText, namePrefix,minuteStep,secondStep,value,false);
    }
    public FormMultiSelectAMPMTime(FormCol col, String labelText,String namePrefix,int minuteStep,Integer secondStep)
    {
        this(col, labelText, namePrefix,minuteStep,secondStep,null);
    }
}
