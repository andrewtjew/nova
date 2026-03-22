package org.nova.service.deviceSession;

import org.nova.http.client.SecurePathAndQuery;
import org.nova.security.QuerySecurity;

public class DeviceSessionPathAndQuery extends SecurePathAndQuery
{

    public DeviceSessionPathAndQuery(PageSession<?,?> session, String path) throws Throwable
    {
        super(session.getDeviceSession(), path);
    }

}
