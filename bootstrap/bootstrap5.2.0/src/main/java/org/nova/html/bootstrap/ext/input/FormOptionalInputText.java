package org.nova.html.bootstrap.ext.input;

public class FormOptionalInputText extends FormInputComponent<OptionalInputText>
{
    public FormOptionalInputText(FormCol col, String labelText,String optionalLabel,String name,boolean checked,boolean reversed)
    {
        super(col, labelText, new OptionalInputText(optionalLabel, name, checked,reversed), null);
    }
}
