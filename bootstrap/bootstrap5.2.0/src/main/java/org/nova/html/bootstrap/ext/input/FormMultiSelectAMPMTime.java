package org.nova.html.bootstrap.ext.input;

import java.time.LocalDate;
import java.time.LocalTime;

public class FormMultiSelectAMPMTime extends FormInput<MultiSelectAMPMTime>
{
    public FormMultiSelectAMPMTime(Integer columns, String labelText,String namePrefix,int minuteStep,Integer secondStep,LocalTime value,boolean required)
    {
        super(columns, labelText, new MultiSelectAMPMTime(namePrefix,minuteStep,secondStep,value!=null?value.getHour():null,value!=null?value.getMinute():null,value!=null?value.getSecond():null));
    }
    public FormMultiSelectAMPMTime(Integer columns, String labelText,String namePrefix,int minuteStep,Integer secondStep,LocalTime value)
    {
        this(columns, labelText, namePrefix,minuteStep,secondStep,value,false);
    }
    public FormMultiSelectAMPMTime(Integer columns, String labelText,String namePrefix,int minuteStep,Integer secondStep)
    {
        this(columns, labelText, namePrefix,minuteStep,secondStep,null);
    }
    public FormMultiSelectAMPMTime(String labelText,String namePrefix,int minuteStep,Integer secondStep,LocalTime value,boolean required)
    {
        this(null,labelText,namePrefix,minuteStep,secondStep,value,required);
    }
}
