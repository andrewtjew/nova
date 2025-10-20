//package org.nova.html.bootstrap.ext.input;
//
//import java.time.LocalDate;
//
//public class FormInputGroupDateRange2 extends FormInputComponent<InputGroupDateRange>
//{
//    public FormInputGroupDateRange2(FormCol col, String labelText,String namePrefix,int baseYear,int years,LocalDate start,LocalDate end,boolean required)
//    {
//        super(col, labelText, new InputGroupDateRange(namePrefix,baseYear,years
//                ,start!=null?start.getYear():null,start!=null?start.getMonth().getValue():null,start!=null?start.getDayOfMonth():null
//                        ,end!=null?end.getYear():null,end!=null?end.getMonth().getValue():null,end!=null?end.getDayOfMonth():null
//                        ));
//    }
//    public FormInputGroupDateRange2(FormCol col, String labelText,String namePrefix,int baseYear,int years,LocalDate start,LocalDate end)
//    {
//        this(col, labelText, namePrefix,baseYear,years,start,end,false);
//    }
//    public FormInputGroupDateRange2(FormCol col, String labelText,String namePrefix,int baseYear,int years)
//    {
//        this(col, labelText, namePrefix,baseYear,years,null,null);
//    }
////    public FormMultiSelectYearMonthDay(String labelText,String namePrefix,int baseYear,int years,LocalDate value,boolean required)
////    {
////        this(null,labelText,namePrefix,baseYear,years,value,required);
////    }
//}
