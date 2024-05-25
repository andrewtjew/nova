package org.nova.html.bootstrap.ext.input;

import java.time.LocalDate;

public class FormInputGroupDate extends FormInput<InputGroupDate>
{
    public FormInputGroupDate(FormCol col, String labelText,String namePrefix,int baseYear,int years,LocalDate value,boolean required)
    {
        super(col, labelText, new InputGroupDate(namePrefix,baseYear,years,value!=null?value.getYear():null,value!=null?value.getMonth().getValue():null,value!=null?value.getDayOfMonth():null));
    }
    public FormInputGroupDate(FormCol col, String labelText,String namePrefix,int baseYear,int years,LocalDate value)
    {
        this(col, labelText, namePrefix,baseYear,years,value,false);
    }
    public FormInputGroupDate(FormCol col, String labelText,String namePrefix,int baseYear,int years)
    {
        this(col, labelText, namePrefix,baseYear,years,null);
    }
//    public FormMultiSelectYearMonthDay(String labelText,String namePrefix,int baseYear,int years,LocalDate value,boolean required)
//    {
//        this(null,labelText,namePrefix,baseYear,years,value,required);
//    }
}
