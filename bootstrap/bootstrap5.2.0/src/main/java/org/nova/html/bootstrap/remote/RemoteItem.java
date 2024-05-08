package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.elements.Element;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.tags.div;

public class RemoteItem extends div// extends StyleComponent<RemoteItem> 
{
    public RemoteItem()
    {
        id();
    }
    public RemoteItem clear()
    {
        this.clearInners();
        return this;
    }

    public RemoteItem setInner(Element element)
    {
        this.clearInners();
        addInner(element);
        return this;
    }
 
    public <RETURN extends Element> RETURN returnSetInner(RETURN element)
    {
        this.clearInners();
        addInner(element);
        return element;
    }    

}
