package org.nova.html.remote;

import org.nova.http.client.PathAndQuery;

public class RemoteStatePathAndQuery extends PathAndQuery
{

    public RemoteStatePathAndQuery(RemoteStateContent<?> content,String path) throws Throwable
    {
        super(path);
        addQuery(content.getRemoteStateBinding().getKey(),content.id());
    }

}