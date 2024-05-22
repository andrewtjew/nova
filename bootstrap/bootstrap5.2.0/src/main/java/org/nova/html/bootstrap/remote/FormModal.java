//package org.nova.html.bootstrap.remote;
//
//import org.apache.commons.text.StringEscapeUtils;
//import org.nova.html.bootstrap.Button;
//import org.nova.html.bootstrap.ButtonComponent;
//import org.nova.html.bootstrap.classes.Display;
//import org.nova.html.bootstrap.classes.Justify;
//import org.nova.html.bootstrap.classes.StyleColor;
//import org.nova.html.bootstrap.ext.ModalDocument;
//import org.nova.html.bootstrap.ext.SpinnerButton;
//import org.nova.html.ext.HtmlUtils;
//import org.nova.html.remoting.Inputs;
//
//public class FormModal extends ModalDocument
//{
//    final private ButtonComponent<?> proceedButton;
//
//    public FormModal(String id,String title,ButtonComponent<?> cancelButton,ButtonComponent<?> proceedButton) throws Throwable
//    {
//        this.proceedButton=proceedButton;
//        if (id!=null)
//        {
//            this.id(id);
//        }
//        else
//        {
//            this.id();
//        }
//        this.proceedButton.id();
//        this.header().id();
//        this.body().id();
//        
//        this.header().addInner(title);
//        this.footer().d(Display.flex).justify_content(Justify.end);
//        this.footer().addInner(cancelButton);
//        this.footer().addInner(proceedButton);
//        cancelButton.onclick(this.js_hide());
//    }
//
//    public FormModal(String id,String title,String cancel,String proceed) throws Throwable
//    {
//        this(id,
//                title
//                ,new Button(cancel).color(StyleColor.secondary)
//                ,new SpinnerButton(proceed).color(StyleColor.primary)
//                );
//    }
//    public FormModal(String title,String cancel,String proceed) throws Throwable
//    {
//        this(null,title,cancel,proceed);
//    }
//    public FormModal(String id,String title) throws Throwable
//    {
//        this(id,title,"Cancel","Proceed");
//    }
//    public FormModal(String title) throws Throwable
//    {
//        this(null,title);
//    }
//    public FormModal() throws Throwable
//    {
//        this(null);
//    }
//    public void onSubmit(Inputs inputs,String action) throws Throwable
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
//    public String js_showAndOnProceed(String header,String body,String script)
//    {
//        body=StringEscapeUtils.escapeHtml4(body);
//        header=StringEscapeUtils.escapeHtml4(header); 
//        return HtmlUtils.js_call("nova.ui.modal.proceed", this.id(),this.header().id(),header,this.body().id(),body,this.proceedButton.id(),script);
//    }
//
//}
