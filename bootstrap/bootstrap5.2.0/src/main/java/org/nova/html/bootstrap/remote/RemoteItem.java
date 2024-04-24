package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Spinner;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.classes.AlignSelf;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.TextAlign;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.remote.Remote;
import org.nova.html.tags.div;
import org.nova.html.tags.script;

public class RemoteItem extends Item
{
    public RemoteItem(String href,boolean showSpinner,String loadingMessage) throws Throwable
    {
        returnAddInner(new script()).addInner(new LiteralHtml(Remote.js_getRemote(href,id())));
        
        Item item=new Item().text(StyleColor.info).p(2).d(Display.flex).justify_content(Justify.center);
        if (showSpinner)
        {
            item.returnAddInner(new Spinner(SpinnerType.border,BreakPoint.md));
        }
        if (loadingMessage!=null)
        {
            Item messageItem=item.returnAddInner(new Item()).addInner(loadingMessage).fs(5);
            if (showSpinner)
            {
                messageItem.ms(2);
            }
        }
        if (showSpinner||(loadingMessage!=null))
        {
            this.addInner(item);
        }
    }
    public RemoteItem(String href,String loadingMessage) throws Throwable
    {
        this(href, false,loadingMessage);
    }
    public RemoteItem(String href,boolean spinner) throws Throwable
    {
        this(href, spinner,null);
    }
}
