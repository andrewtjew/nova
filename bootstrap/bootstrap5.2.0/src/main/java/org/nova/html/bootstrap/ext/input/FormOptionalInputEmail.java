package org.nova.html.bootstrap.ext.input;

public class FormOptionalInputEmail extends FormInputComponent<OptionalInputEmail>
{
    public FormOptionalInputEmail(FormCol col, String labelText,String optionalLabel,String name,boolean checked,boolean reversed)
    {
        super(col, labelText, new OptionalInputEmail(optionalLabel, name, checked,reversed), null);
    }
}
