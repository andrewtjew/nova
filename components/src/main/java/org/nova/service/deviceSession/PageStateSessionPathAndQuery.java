package org.nova.service.deviceSession;

import org.nova.http.client.SecurePathAndQuery;

public class PageStateSessionPathAndQuery extends SecurePathAndQuery
{
    public PageStateSessionPathAndQuery(Session session, String path) throws Throwable
    {
        super(session.getDeviceSession(), path);
    }

}
