//package org.nova.html.remote;
//
//
//import org.nova.html.ext.HtmlUtils;
//import org.nova.html.ext.LiteralHtml;
//import org.nova.html.tags.div;
//import org.nova.html.tags.script;
//
////Use this to populate content by calling back to server.
//public class RemoteContentItem extends div
//{
//    public RemoteContentItem(String href,boolean showSpinner,String loadingMessage) throws Throwable
//    {
//        returnAddInner(new script()).addInner(new LiteralHtml(Remote.js_getRemote(href,id())));
//        
//        Item item=new Item().text(StyleColor.info).p(2).d(Display.flex).justify_content(Justify.center);
//        if (showSpinner)
//        {
//            item.returnAddInner(new Spinner(SpinnerType.border,BreakPoint.md));
//        }
//        if (loadingMessage!=null)
//        {
//            Item messageItem=item.returnAddInner(new Item()).addInner(loadingMessage).fs(5);
//            if (showSpinner)
//            {
//                messageItem.ms(2);
//            }
//        }
//        if (showSpinner||(loadingMessage!=null))
//        {
//            this.addInner(item);
//        }
//    }
//    public RemoteContentItem(String href,String loadingMessage) throws Throwable
//    {
//        this(href, false,loadingMessage);
//    }
//    public RemoteContentItem(String href,boolean spinner) throws Throwable
//    {
//        this(href, spinner,null);
//    }
//}
