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
        this.clearInners();
        return this;
    }

    public RemoteItem setInner(Element element)
    {
        clear();
        addInner(element);
        return this;
    }
 
    public <RETURN extends Element> RETURN returnSetInner(RETURN element)
    {
        clear();
        addInner(element);
        return element;
    }    

}
