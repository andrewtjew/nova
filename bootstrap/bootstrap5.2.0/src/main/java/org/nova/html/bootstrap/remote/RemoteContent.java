package org.nova.html.bootstrap.remote;


import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Spinner;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.remote.Remote;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.tags.div;
import org.nova.html.tags.script;

//Use this to populate content by calling back to server.
public class RemoteContent extends Item
{
    public RemoteContent(String id) throws Throwable
    {
        if (id==null)
        {
            id();
        }
        else
        {
            id(id);
        }
    }
    public RemoteContent() throws Throwable
    {
        this(null);
    }        
    public RemoteContent href(String href,boolean showSpinner,String waitingMessage) throws Throwable
    {
        returnAddInner(new script()).addInner(new LiteralHtml(Remote.js_getRemote(id(),href)));
        if ((showSpinner||waitingMessage!=null))
        {
            Item item=new Item();
            item.d(Display.flex).justify_content(Justify.center);
            if (showSpinner)
            {
                item.returnAddInner(new Spinner(SpinnerType.border,BreakPoint.md));
            }
            if (waitingMessage!=null)
            {
                Item messageItem=item.returnAddInner(new Item()).addInner(waitingMessage);
                if (showSpinner)
                {
                    messageItem.me(1);
                }
            }
            if (showSpinner||(waitingMessage!=null))
            {
                this.addInner(item);
            }
        }
        return this;
    }    

    public RemoteResponse respond(RemoteResponse response) throws Throwable
    {
        response.outerHtml(this);
        return response;
    }
}
