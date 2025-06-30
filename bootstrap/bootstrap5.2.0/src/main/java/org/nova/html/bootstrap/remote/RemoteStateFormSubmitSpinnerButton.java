//package org.nova.html.bootstrap.remote;
//
//import org.nova.html.bootstrap.Item;
//import org.nova.html.bootstrap.SpinnerType;
//import org.nova.html.bootstrap.classes.StyleColor;
//import org.nova.html.bootstrap.ext.Icon;
//import org.nova.html.bootstrap.ext.SpinnerButton;
//import org.nova.html.bootstrap.ext.SpinnerButtonComponent;
//import org.nova.html.elements.Composer;
//import org.nova.html.ext.LiteralHtml;
//import org.nova.html.remote.RemoteForm;
//import org.nova.html.tags.script;
//
//public class RemoteStateFormSubmitSpinnerButton extends SpinnerButtonComponent<RemoteStateFormSubmitSpinnerButton>
//{
//    static class SpinnerLabel extends Item
//    {
//        public SpinnerLabel(String label)
//        {
//            addInner(label);
//        }
//    }
//    
//    public RemoteStateFormSubmitSpinnerButton(RemoteForm form,String label) throws Throwable
//    {
//        super(new SpinnerLabel(label),SpinnerType.border,null);
//        color(StyleColor.primary);
//        onclick("if (document.getElementById('"+form.id()+"').reportValidity()){"+form.js_post()+";}else{"+js_reset()+";}");
//    }
//
//    public RemoteStateFormSubmitSpinnerButton(String label) throws Throwable
//    {
//        super(new SpinnerLabel(label),SpinnerType.border,null);
//        color(StyleColor.primary);
//    }
//    
//    
//    @Override
//    public void compose(Composer composer) throws Throwable
//    {
//        returnAddInner(new script()).addInner(new LiteralHtml(js_reset()));
//        super.compose(composer);
//    }            
//        
//}