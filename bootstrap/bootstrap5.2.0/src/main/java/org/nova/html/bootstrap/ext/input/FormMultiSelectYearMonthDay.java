package org.nova.html.bootstrap.ext.input;

import java.time.LocalDate;

public class FormMultiSelectYearMonthDay extends FormInput<MultiSelectYearMonthDay>
{
    public FormMultiSelectYearMonthDay(FormCol col, String labelText,String namePrefix,int baseYear,int years,LocalDate value,boolean required)
    {
        super(col, labelText, new MultiSelectYearMonthDay(namePrefix,baseYear,years,value!=null?value.getYear():null,value!=null?value.getMonth().getValue():null,value!=null?value.getDayOfMonth():null));
    }
    public FormMultiSelectYearMonthDay(FormCol col, String labelText,String namePrefix,int baseYear,int years,LocalDate value)
    {
        this(col, labelText, namePrefix,baseYear,years,value,false);
    }
    public FormMultiSelectYearMonthDay(FormCol col, String labelText,String namePrefix,int baseYear,int years)
    {
        this(col, labelText, namePrefix,baseYear,years,null);
    }
//    public FormMultiSelectYearMonthDay(String labelText,String namePrefix,int baseYear,int years,LocalDate value,boolean required)
//    {
//        this(null,labelText,namePrefix,baseYear,years,value,required);
//    }
}
