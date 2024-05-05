package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.Item;
import org.nova.html.elements.Element;
import org.nova.html.remote.RemoteResponse;

public class RemoteItem extends Item
{
    public RemoteItem()
    {
        id();
    }
    public RemoteResponse respond(Element element,RemoteResponse response)
    {
        this.clearIneners();
        this.addInner(element);
        response.outerHtml(this);
        return response;
    }
    
 
    

}
