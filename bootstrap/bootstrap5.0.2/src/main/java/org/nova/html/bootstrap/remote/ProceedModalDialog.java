package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.ButtonComponent;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.ModalDialog;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.ext.ModalDocument;
import org.nova.html.bootstrap.ext.ModalOption;
import org.nova.html.bootstrap.ext.SpinnerButton;
import org.nova.html.bootstrap.localization.UIHandle;
import org.nova.html.remote.Inputs;
import org.nova.localization.LanguageCode;
import org.nova.localization.StringHandle;

public class ProceedModalDialog extends ModalDocument
{
    public ProceedModalDialog(Inputs inputs,String action,String title,String message,ButtonComponent<?> cancelButton,ButtonComponent<?> proceedButton) throws Throwable
    {
        this.header().returnAddInner(new Item()).fs(3).addInner(title);
        this.body().addInner(message);
        this.footer().d(Display.flex).justify_content(Justify.between);
        this.footer().addInner(cancelButton).addInner(proceedButton);
        proceedButton.onclick(inputs.js_post(action));
        cancelButton.onclick(this.js_option(ModalOption.hide));
    }

    public ProceedModalDialog(Inputs inputs,String action,String title,String message,String cancel,String proceed) throws Throwable
    {
        this(inputs,action
                ,title,message
                ,new Button(cancel).color(StyleColor.warning).w(25)
                ,new SpinnerButton(proceed).color(StyleColor.success).w(25)
                );
    }

    public ProceedModalDialog(Inputs inputs,String action,String title,String message) throws Throwable
    {
        this(inputs,action,title,message,"Cancel","Proceed");
    }
    
    public String js_show()
    {
        return this.js_option(ModalOption.show);
    }

}
