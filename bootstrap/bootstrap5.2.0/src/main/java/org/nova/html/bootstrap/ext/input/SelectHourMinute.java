package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Select;
import org.nova.html.tags.option;
import org.nova.localization.ClockFormat;
import org.nova.utils.TypeUtils;

public class SelectHourMinute extends Select
{
    public final ClockFormat clockFormat;
    public final int minuteStep;
    public SelectHourMinute(ClockFormat clockFormat,int minuteStep,Integer hour,Integer minute)
    {
        this.clockFormat=clockFormat;
        this.minuteStep=minuteStep;
        set(hour,minute);
    }
    public SelectHourMinute(ClockFormat clockFormat,int minuteStep)
    {
        this(clockFormat,minuteStep,null,null);
    }
    
    public SelectHourMinute set(Integer hourValue,Integer minuteValue)
    {
        clearInners();
        Integer selected=null;
        if ((hourValue!=null)&&(minuteValue!=null))
        {
            selected=hourValue*60+minuteValue;
        }
        switch (this.clockFormat)
        {
            case AM_PM:
            {
                for (int hours=0;hours<24;hours++)
                {
                    for (int minutes=0;minutes<60;minutes+=this.minuteStep)
                    {
                        int value=hours*60+minutes;
                        option option=returnAddInner(new option()).value(value);
                        if (TypeUtils.equals(value,selected))
                        {
                            option.selected(true);
                            selected=null;
                        }
                        if (hours==0)
                        {
                            option.addInner("12:"+String.format("%02d", minutes)+" AM");
                        }
                        else if (hours==12)
                        {
                            option.addInner("12:"+String.format("%02d", minutes)+" PM");
                        }
                        else if (hours<12)
                        {
                            option.addInner(hours+":"+String.format("%02d", minutes)+" AM");
                        }
                        else
                        {
                            option.addInner((hours-12)+":"+String.format("%02d", minutes)+" PM");
                        }
                    }
                }
                int value=24*60;
                option option=returnAddInner(new option()).value(value);
                option.selected(TypeUtils.equals(value,selected));
                option.addInner("12:00 AM");
            }
            break;

            case HOURS_24:
            for (int hours=0;hours<24;hours++)
            {
                for (int minutes=0;minutes<60;minutes+=minuteStep)
                {
                    int value=hours*60+minutes;
                    option option=returnAddInner(new option()).value(value);
                    if (TypeUtils.equals(value,selected))
                    {
                        option.selected(true);
                        selected=null;
                    }

                    option.addInner(hours+":"+String.format("%02d", minutes));
                }
            }
            int value=24*60;
            option option=returnAddInner(new option()).value(value);
            option.selected(TypeUtils.equals(value,selected));
            option.addInner("0:00");
            break;
            default:
                break;
        }
        return this;
    }
    
    public SelectHourMinute standardWidth()
    {
        this.style("width:8em;");
        return this;
    }    
    
  }
