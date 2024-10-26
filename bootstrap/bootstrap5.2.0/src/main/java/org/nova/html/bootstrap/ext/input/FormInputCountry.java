package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputText;
import org.nova.html.tags.datalist;
import org.nova.html.tags.option;
import org.nova.localization.CountryCode;

public class FormInputCountry extends FormInput<InputText>
{
    public FormInputCountry(FormCol col, String labelText,String name,String value,boolean required)
    {
        super(col, labelText, new InputText(), null);
        input().name(name).required(required);
        if (value!=null)
        {
            input().value(value);
        }
        datalist list=returnAddInner(new datalist());
        for (CountryCode item:CountryCode.values())
        {
            list.returnAddInner(new option()).value(item.getValue().getDisplayName());
        }
        input().list(list);
    }
    public FormInputCountry(FormCol col, String labelText,String name,String value)
    {
        this(col, labelText, name,value,false);
    }
    public FormInputCountry(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,null);
    }

//    public FormInputCountry(String labelText,String name,String value,boolean required)
//    {
//        this(null,labelText,name,value,required);
//    }
//    public FormInputCountry(String labelText,String name,String value)
//    {
//        this(labelText, name,value,false);
//    }
//    public FormInputCountry(String labelText,String name)
//    {
//        this(labelText,name,null);
//    }
}
