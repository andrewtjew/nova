package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Spinner;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.Styling;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.elements.GlobalTagElement;
import org.nova.html.remote.RemoteObjectBinding;
import org.nova.html.remote.RemoteObjectElement;

public class RemoteObjectItem extends RemoteObjectElement<RemoteObjectItem> implements Styling<RemoteObjectItem>
{
    public RemoteObjectItem(String id,RemoteObjectBinding binding) throws Throwable
    {
        super("div",id,binding);
    }
    public RemoteObjectItem(RemoteObjectBinding binding) throws Throwable
    {
        this(null,binding);
    }
    
    
    @Override
    public GlobalTagElement<?> getElement()
    {
        return this;
    }
    
    public RemoteObjectItem load(String href,Long interval,Long timeout,boolean showSpinner,String waitingMessage) throws Throwable
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
        return (RemoteObjectItem) super.load(href, interval, timeout);
    }    
    public RemoteObjectItem load(String href) throws Throwable
    {
        return load(href,null,null,true,null);
    }

}