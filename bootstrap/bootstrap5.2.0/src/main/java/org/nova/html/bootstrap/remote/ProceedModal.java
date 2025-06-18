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
import org.nova.html.remote.RemoteForm;
import org.nova.html.remoting.Inputs;
import org.nova.html.tags.script;


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
        this.proceedButton.id(id()+"-proceed");
        this.header().id(id()+"-header");
        this.body().id(id()+"-body");
        
        this.header().addInner(title);
        if (message!=null)
        {
            this.body().addInner(message);
        }
        this.footer().d(Display.flex).justify_content(Justify.between);
        this.footer().addInner(proceedButton).addInner(cancelButton);
        cancelButton.onclick(this.js_hide());
        cancelButton.id(id()+"-cancel");
    }

    public ProceedModal(String id,String title,String message,String cancel,String proceed) throws Throwable
    {
        this(id,
                title,message
                ,new Button(cancel).color(StyleColor.warning).w(25)
                ,new SpinnerButton(proceed).color(StyleColor.primary).w(25)
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
    public ProceedModal(String title) throws Throwable
    {
        this(null,title,null,"Cancel","Proceed");
    }
    public ProceedModal() throws Throwable
    {
        this(null,null,"Cancel","Proceed");
    }
    public void onProceed(String script)
    {
        proceedButton.onclick(script);
    }
    public void onProceed(RemoteForm form) throws Throwable
    {
        proceedButton.onclick(form.js_post());
    }
    public void onProceed(RemoteForm form,String action) throws Throwable
    {
        proceedButton.onclick(form.js_post(action));
    }
    
//    public void onProceed(Inputs inputs,String action) throws Throwable
//    {
//        proceedButton.onclick(inputs.js_post(action));
//    }
//    public void onProceed(Inputs inputs) throws Throwable
//    {
//        proceedButton.onclick(inputs.js_post());
//    }
//    public String js_showAndOnProceed(String script) throws Throwable
//    {
//        return HtmlUtils.js_call("nova.ui.modal.proceed", this.id(),null,null,null,null,this.proceedButton.id(),script);
//    }
    public String js_showAndOnProceed(String header,String body,String script)
    {
        body=StringEscapeUtils.escapeHtml4(body);
        header=StringEscapeUtils.escapeHtml4(header); 
        return HtmlUtils.js_call("nova.ui.modal.proceed", id(),header,body,script);
    }

}
