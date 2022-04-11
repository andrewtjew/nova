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

public class DateTimeRangePickerOptions
{
    static public enum OpenPosition
    {
        right,
        left,
        center,
    }
    
    static public enum DropPosition
    {
        up,
        down,
    }
    
    public Boolean showDropdowns;
    public Boolean showWeekNumbers;
    public Boolean showISOWeekNumbers;
    public Boolean singleDatePicker;
    public Boolean timePicker;
    public Boolean timePicker24Hour;
    public Boolean timePickerSeconds;
    public Boolean autoApply;
    public Boolean linkedCalendars;
    public Boolean autoUpdateInput;
    public Boolean alwaysShowCalendars;
    public Boolean showCustomRangeLabel;
    public Integer timePickerIncrement;
    public Integer maxSpan;
    public OpenPosition opens;
    public DropPosition drops;

    public String buttonClasses;
    public String applyClass;
    public String cancelClass;

    public String minDate;
    public String maxDate;
    public String startDate;
    public String endDate;
    public DateTimeRangeLocale locale;
    public DateTimeLabelRange[] ranges;
}
