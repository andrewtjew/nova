package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.elements.Element;
import org.nova.html.remote.RemoteResponse;

public class RemoteItem extends StyleComponent<RemoteItem> 
{
    public RemoteItem()
    {
        super("div",null);
        id();
    }
    public RemoteItem clear()
    {
        this.clearIneners();
        return this;
    }

 
    

}
