package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputFile;

public class FormInputFile extends FormInputComponent<InputFile>
{
    public FormInputFile(FormCol col, String labelText,String name,boolean required)
    {
        super(col, labelText, new InputFile(), null);
        input().name(name).required(required);
    }
    public FormInputFile(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,false);
    }
//    public FormInputFile(String labelText,String name,boolean required)
//    {
//        this(null,labelText,name,required);
//    }
//    public FormInputFile(String labelText,String name)
//    {
//        this(labelText, name,false);
//    }
}
