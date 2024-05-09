package org.nova.html.remote;


import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.ext.Spinner;
import org.nova.html.remote.Remote;
import org.nova.html.tags.div;
import org.nova.html.tags.script;

//Use this to populate content by calling back to server.
public class RemoteContent extends div
{
    public RemoteContent(String href) throws Throwable
    {
        id();
        if (href!=null)
        {
            returnAddInner(new script()).addInner(new LiteralHtml(Remote.js_getRemote(href,id())));
        }
        /*
        if ((showSpinner||waitingMessage!=null))
        {
            div item=new div().style("display:flex;justify:center;");
            if (showSpinner)
            {
    //            item.returnAddInner(new Spinner(SpinnerType.border,BreakPoint.md));
                item.returnAddInner(new Spinner());
            }
            if (waitingMessage!=null)
            {
                div messageItem=item.returnAddInner(new div()).addInner(waitingMessage);
                if (showSpinner)
                {
                    messageItem.style("margin-right:1em;");
                }
            }
            if (showSpinner||(waitingMessage!=null))
            {
                this.addInner(item);
            }
        }
        */
    }
}
