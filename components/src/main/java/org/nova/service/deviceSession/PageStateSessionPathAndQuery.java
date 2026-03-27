package org.nova.service.deviceSession;

import org.nova.http.client.SecurePathAndQuery;
import org.nova.security.PathAndQueryAuthentication;

public class PageStateSessionPathAndQuery extends SecurePathAndQuery
{
    public PageStateSessionPathAndQuery(Session session, String path) throws Throwable
    {
        super(session.getDeviceSession(), path);
    }

}
