package org.nova.html.bootstrap.remote;

import org.apache.commons.text.StringEscapeUtils;
import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.ButtonComponent;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.ext.ModalDocument;
import org.nova.html.bootstrap.ext.SpinnerButton;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.remote.Inputs;

public class ProceedModal extends ModalDocument
{
    final private ButtonComponent<?> proceedButton;
//    final private ButtonComponent<?> cancelButton;
    public ProceedModal(String title,String message,ButtonComponent<?> cancelButton,ButtonComponent<?> proceedButton) throws Throwable
    {
        this.proceedButton=proceedButton;
  //      this.cancelButton=cancelButton;
        this.id();
        this.proceedButton.id();
        this.header().id();
        this.body().id();
        
        this.header().addInner(title);
        this.body().addInner(message);
        this.footer().d(Display.flex).justify_content(Justify.between);
        this.footer().addInner(cancelButton).addInner(proceedButton);
        cancelButton.onclick(this.js_hide());

    }

    public ProceedModal(String title,String message,String cancel,String proceed) throws Throwable
    {
        this(
                title,message
                ,new Button(cancel).color(StyleColor.warning).w(25)
                ,new SpinnerButton(proceed).color(StyleColor.success).w(25)
                );
    }
    public ProceedModal(String title,String message) throws Throwable
    {
        this(title,message,"Cancel","Proceed");
    }
    public ProceedModal() throws Throwable
    {
        this(null,null,"Cancel","Proceed");
    }
    public ProceedModal(ButtonComponent<?> cancelButton,ButtonComponent<?> proceedButton) throws Throwable
    {
        this(null,null,cancelButton,proceedButton);
    }
    public void onProceed(Inputs inputs,String action) throws Throwable
    {
        proceedButton.onclick(inputs.js_post(action));
    }
    public String js_showAndOnProceed(String code) throws Throwable
    {
        return HtmlUtils.js_call("nova.ui.modal.proceed", this.id(),null,null,null,null,this.proceedButton.id(),code);
    }
    public String js_showAndOnProceed(String header,String body,String code)
    {
        body=StringEscapeUtils.escapeHtml4(body);
//        body=StringEscapeUtils.escapeHtml4(body);
        header=StringEscapeUtils.escapeHtml4(header); 
//        header=StringEscapeUtils.escapeHtml4(header);
        return HtmlUtils.js_call("nova.ui.modal.proceed", this.id(),this.header().id(),header,this.body().id(),body,this.proceedButton.id(),code);
    }

}
