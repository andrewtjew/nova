package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Spinner;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.Styling;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.elements.GlobalTagElement;
import org.nova.html.elements.TagElement;
import org.nova.html.remote.RemoteElement;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.http.server.annotations.ContentWriters;

@ContentWriters(RemoteResponseWriter.class)
public class RemoteItem extends RemoteElement<RemoteItem> implements Styling<RemoteItem>
{
    public RemoteItem() throws Throwable
    {
        super("div");
    }
    
    @Override
    public GlobalTagElement<?> getElement()
    {
        return this;
    }
    
    public RemoteItem load(String href,Long interval,Long timeout,boolean showSpinner,String waitingMessage) throws Throwable
    {
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
        return (RemoteItem) super.load(href, interval, timeout);
    }    
    public RemoteItem load(String href) throws Throwable
    {
        return load(href,null,null,true,null);
    }    

    public RemoteItem loadStatic(String href,Long interval,Long timeout,boolean showSpinner,String waitingMessage) throws Throwable
    {
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
        return (RemoteItem) super.loadStatic(href, interval, timeout);
    }    
    public RemoteItem loadStatic(String href) throws Throwable
    {
        return loadStatic(href,null,null,true,null);
    }    

    
}