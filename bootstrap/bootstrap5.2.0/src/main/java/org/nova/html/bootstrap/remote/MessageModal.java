//package org.nova.html.bootstrap.remote;
//
//import org.apache.commons.text.StringEscapeUtils;
//import org.nova.html.bootstrap.Button;
//import org.nova.html.bootstrap.ButtonComponent;
//import org.nova.html.bootstrap.classes.Display;
//import org.nova.html.bootstrap.classes.Justify;
//import org.nova.html.bootstrap.classes.StyleColor;
//import org.nova.html.bootstrap.ext.ModalDocument;
//import org.nova.html.ext.HtmlUtils;
//
//public class MessageModal extends ModalDocument
//{
//    final private ButtonComponent<?> proceedButton;
//    public MessageModal(String title,String message,ButtonComponent<?> proceedButton) throws Throwable
//    {
//        this.proceedButton=proceedButton;
//        this.id();
//        this.proceedButton.id();
//        this.header().id();
//        this.body().id();
//        
//        this.header().addInner(title);
//        this.body().addInner(message);
//        this.footer().d(Display.flex).justify_content(Justify.end);
//        this.footer().addInner(proceedButton);
//
//    }
//
//    public MessageModal(String title,String message,String proceed) throws Throwable
//    {
//        this(
//                title,message
//                ,new Button(proceed).color(StyleColor.success).w(25).dismissModal().close()
//                );
//    }
//    public MessageModal(String title,String message) throws Throwable
//    {
//        this(title,message,"Proceed");
//    }
//    public MessageModal() throws Throwable
//    {
//        this(null,null,"Proceed");
//    }
//    public MessageModal(ButtonComponent<?> proceedButton) throws Throwable
//    {
//        this(null,null,proceedButton);
//    }
//    public String js_show(String header,String body)
//    {
//        body=StringEscapeUtils.escapeHtml4(body);
//        header=StringEscapeUtils.escapeHtml4(header);
//        return HtmlUtils.js_call("nova.ui.modal.proceed", this.id(),this.header().id(),header,this.body().id(),body,this.proceedButton.id(),null);
//     }
//}
