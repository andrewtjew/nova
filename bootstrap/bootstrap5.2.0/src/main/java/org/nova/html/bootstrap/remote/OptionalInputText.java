package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.InputText;

public class OptionalInputText extends OptionalInput<InputText>
{

    public OptionalInputText(String checkboxText, String namePrefix, boolean checked, boolean reversed)
    {
        super(checkboxText, namePrefix, new InputText(), checked, reversed);
    }

}
