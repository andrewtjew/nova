package org.nova.html.bootstrap.ext.input;

import java.time.LocalDate;

import org.nova.html.bootstrap.InputText;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.TextAlign;
import org.nova.localization.DateFormat;
import org.nova.utils.TypeUtils;

public class InputGroupVerificationCode extends StyleComponent<InputGroupVerificationCode>
{
    final private InputText[] inputs;
   //
    public InputGroupVerificationCode(String namePrefix,int digits)
    {
        super("div",null);
        d(Display.flex);
       // form_control();

        this.inputs=new InputText[digits];
        for (int i=0;i<digits;i++)
        {
            InputText input=this.inputs[i]=new InputText();
            input.size(1).maxlength(1);
            addInner(input);
        }
    }
    
}
