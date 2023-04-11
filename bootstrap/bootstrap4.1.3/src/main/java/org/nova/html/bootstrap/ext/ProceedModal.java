package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.ButtonComponent;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.remoting.ModalOption;

public class ProceedModal extends ModalDocument
{
    final private ButtonComponent<?> proceedButton;
    public ProceedModal(String id,String title,String message,ButtonComponent<?> cancelButton,ButtonComponent<?> proceedButton) throws Throwable
    {
        this.proceedButton=proceedButton;
        if (id!=null)
        {
            this.id(id);
        }
        else
        {
            this.id();
        }
        this.proceedButton.id();
        this.header().id();
        this.body().id();
        
        this.header().addInner(title);
        this.body().addInner(message);
        this.footer().d(Display.flex).justify_content(Justify.between);
        this.footer().addInner(proceedButton).addInner(cancelButton);
        cancelButton.onclick(this.js_option(ModalOption.hide));

    }

    public ProceedModal(String id,String title,String message,String cancel,String proceed) throws Throwable
    {
        this(id,
                title,message
                ,new Button(cancel).color(StyleColor.warning).w(25)
                ,new Button(proceed).color(StyleColor.success).w(25)
                );
    }
    public ProceedModal(String title,String message,String cancel,String proceed) throws Throwable
    {
        this(null,title,message,cancel,proceed);
    }
    public ProceedModal(String id,String title,String message) throws Throwable
    {
        this(id,title,message,"Cancel","Proceed");
    }
    public ProceedModal(String title,String message) throws Throwable
    {
        this(null,title,message,"Cancel","Proceed");
    }
    public ProceedModal() throws Throwable
    {
        this(null,null,"Cancel","Proceed");
    }
    public void onProceed(String script)
    {
        proceedButton.onclick(script);
    }

}
