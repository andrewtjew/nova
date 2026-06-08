package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputFile;

public class FormInputFile extends FormInputComponent<InputFile>
{
    public FormInputFile(FormCol col, String labelText,String name,boolean required)
    {
        super(col, labelText, new InputFile());
        input().name(name).required(required);
    }
    public FormInputFile(FormCol col, String labelText,String name)
    {
        this(col, labelText, name,false);
    }
}
