package org.nova.html.bootstrap.ext.input;

import java.time.LocalDate;

public class FormMultiSelectYearMonthDay extends FormInput<MultiSelectYearMonthDay>
{
    public FormMultiSelectYearMonthDay(Integer columns, String labelText,String namePrefix,int baseYear,int years,LocalDate value,boolean required)
    {
        super(columns, labelText, new MultiSelectYearMonthDay(namePrefix,baseYear,years,value!=null?value.getYear():null,value!=null?value.getMonth().getValue():null,value!=null?value.getDayOfMonth():null));
    }
    public FormMultiSelectYearMonthDay(Integer columns, String labelText,String namePrefix,int baseYear,int years,LocalDate value)
    {
        this(columns, labelText, namePrefix,baseYear,years,value,false);
    }
    public FormMultiSelectYearMonthDay(Integer columns, String labelText,String namePrefix,int baseYear,int years)
    {
        this(columns, labelText, namePrefix,baseYear,years,null);
    }
    public FormMultiSelectYearMonthDay(String labelText,String namePrefix,int baseYear,int years,LocalDate value,boolean required)
    {
        this(null,labelText,namePrefix,baseYear,years,value,required);
    }
}
