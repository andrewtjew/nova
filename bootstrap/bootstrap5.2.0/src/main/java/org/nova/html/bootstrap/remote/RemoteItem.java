package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.Item;
import org.nova.html.remote.RemoteResponse;

public class RemoteItem extends Item
{
    public RemoteItem()
    {
        id();
    }
    public RemoteResponse respond(RemoteResponse response)
    {
        response.outerHtml(this);
        return response;
    }
    

}
