/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.html.bootstrap.ext.DTP;

import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.deprecated.ObjectBuilder;
import org.nova.html.elements.Composer;
import org.nova.html.enums.link_rel;
import org.nova.html.ext.Head;
import org.nova.html.ext.InlineHtml;
import org.nova.html.tags.link;
import org.nova.html.tags.script;

//From http://www.daterangepicker.com/
public class DateTimeRangePicker extends StyleComponent<DateTimeRangePicker>
{
    private final String name;
    private DateTimeRangePickerOptions options; 
    private DateTimeRangePickerEvents events;
    
    public DateTimeRangePicker(Head head,String name,String value,int size,DateTimeRangePickerOptions options,DateTimeRangePickerEvents events,String cssFilePath)
    {
        super("input",null);
        attr("type","text");
        attr("value",value);
        attr("size",size);
        attr("name",name);
        this.name=name; 
        this.options=options;
        this.events=events;

        link link=new link().rel(link_rel.stylesheet).type("text/css").href(cssFilePath);
        script script=new script().src("/resources/html/datetimepicker/js/daterangepicker.js");
        script momentScript=new script().src("/resources/html/datetimepicker/js/moment.min.js");
        if (head!=null)
        {
            head.add(this.getClass().getCanonicalName(),link);
            head.add(this.getClass().getCanonicalName()+"_moment",momentScript);
            head.add(this.getClass().getCanonicalName(),script);
        }
        else
        {
            addInner(script);
            addInner(link); 
        }
    }
    

    public DateTimeRangePicker(Head head,String name,String value,int size,DateTimeRangePickerOptions options,DateTimeRangePickerEvents events)
    {
        this(head,name,value,size,options,events,"/resources/html/datetimepicker/css/daterangepicker.css");
    }
    
    public DateTimeRangePicker(Head head,String name,String value,int size,DateTimeRangePickerOptions options)
    {
        this(head,name,value,size,options,null);
    }

    public DateTimeRangePickerOptions getOptions()
    {
        return this.options;
    }
    public void setOptions(DateTimeRangePickerOptions options)
    {
        this.options=options;
    }
    public DateTimeRangePickerEvents getEvents()
    {
        return this.events;
    }
    public void setEvents(DateTimeRangePickerEvents events)
    {
        this.events=events;
    }
    private String buildEvent(String event,String function)
    {
        return "'"+event+".daterangepicker',"+function;
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        super.compose(composer);
        StringBuilder sb=new StringBuilder();
      
        String head="$('#"+id()+"')";
        if (this.options!=null)
        {
            ObjectBuilder ob=new ObjectBuilder();
            ob.addIfNotNull("showDropdowns",this.options.showDropdowns);
            ob.addIfNotNull("showWeekNumbers",this.options.showWeekNumbers);
            ob.addIfNotNull("showISOWeekNumbers",this.options.showISOWeekNumbers);
            ob.addIfNotNull("singleDatePicker",this.options.singleDatePicker);
            ob.addIfNotNull("timePicker",this.options.timePicker);
            ob.addIfNotNull("timePicker24Hour",this.options.timePicker24Hour);
            ob.addIfNotNull("timePickerSeconds",this.options.timePickerSeconds);
            ob.addIfNotNull("timePickerIncrement",this.options.timePickerIncrement);
            ob.addIfNotNull("autoApply",this.options.autoApply);
            ob.addIfNotNull("linkedCalendars",this.options.linkedCalendars);
            ob.addIfNotNull("autoUpdateInput",this.options.autoUpdateInput);
            ob.addIfNotNull("alwaysShowCalendars",this.options.alwaysShowCalendars);
            ob.addIfNotNull("showCustomRangeLabel",this.options.showCustomRangeLabel);
            ob.addIfNotNull("buttonClasses",this.options.buttonClasses);
            ob.addIfNotNull("applyClass",this.options.applyClass);
            ob.addIfNotNull("cancelClass",this.options.cancelClass);
            ob.addIfNotNull("startDate",this.options.startDate);
            ob.addIfNotNull("endDate",this.options.endDate);
            ob.addIfNotNull("minDate",this.options.minDate);
            ob.addIfNotNull("maxDate",this.options.maxDate);
            ob.addIfNotNull("opens",this.options.opens);
            ob.addIfNotNull("drops",this.options.drops);
            if (this.options.ranges!=null)
            {
                ob.begin("ranges");
                for (DateTimeLabelRange range:this.options.ranges)
                {
                    ob.addRawString(range.label, "[\""+range.from+"\",\""+range.to+"\"]");
                }
                ob.end();
            }          
            ob.addIfNotNull("locale", this.options.locale);
            
            
//            String name="$('input[name=\""+this.name+"\"]')";
//            sb.append("$(function() {");
//            sb.append(name+".daterangepicker("+ob+");");
//            sb.append("});");
            sb.append(head).append(".daterangepicker(").append(ob).append(");");
        }
        if (this.events!=null)
        {
            sb.append(head).append(".on(").append(buildEvent("apply.daterangepicker",this.events.apply)).append(");");
            sb.append(head).append(".on(").append(buildEvent("cancel.daterangepicker",this.events.cancel)).append(");");
            sb.append(head).append(".on(").append(buildEvent("hide.daterangepicker",this.events.hide)).append(");");
            sb.append(head).append(".on(").append(buildEvent("show.daterangepicker",this.events.show)).append(");");
            sb.append(head).append(".on(").append(buildEvent("hideCalendar.daterangepicker",this.events.hideCalendar)).append(");");
            sb.append(head).append(".on(").append(buildEvent("showCalendar.daterangepicker",this.events.showCalendar)).append(");");
        }
            
        if (sb.length()>0)
        {
//            script script=new script().addInner(sb.toString());
            script script=new script().addInner(new InlineHtml(sb.toString()));
            composer.getStringBuilder().append(script.getHtml());
        }
    }

}
