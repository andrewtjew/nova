package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputEmail;

public class OptionalInputEmail extends OptionalInput<InputEmail>
{

    public OptionalInputEmail(String checkboxText, String namePrefix, boolean checked, boolean reversed)
    {
        super(checkboxText, namePrefix, new InputEmail(), checked, reversed);
    }

}
