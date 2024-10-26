package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Spinner;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.Styling;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.elements.TagElement;
import org.nova.html.remote.RemoteStateBinding;
import org.nova.html.remote.RemoteStateElement;

public class RemoteStateItem extends RemoteStateElement<RemoteStateItem> implements Styling<RemoteStateItem>
{
    public RemoteStateItem(String id,RemoteStateBinding binding) throws Throwable
    {
        super("div",id,binding);
    }
    public RemoteStateItem(RemoteStateBinding binding) throws Throwable
    {
        this(null,binding);
    }
    
    @Override
    public TagElement<?> getElement()
    {
        return this;
    }
    
    public RemoteStateItem load(String href,Long interval,Long timeout,boolean showSpinner,String waitingMessage) throws Throwable
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
        return (RemoteStateItem) super.load(href, interval, timeout);
    }    
    public RemoteStateItem load(String href) throws Throwable
    {
        return load(href,null,null,true,null);
    }
//    public String js_postStatic(PathAndQuery pathAndQuery) throws Throwable
//    {
//        return Remote.js_postStatic(pathAndQuery.addQuery(this.getRemoteStateBinding().getStateKey(), this.id()).toString());
//        
//    }
}