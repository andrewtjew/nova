package org.nova.security;

import org.nova.http.server.Context;

public interface PathAndQueryAuthentication
{
    public String signPathAndQuery(String pathAndQuery) throws Throwable;
    public boolean isRequestAuthentic(Context context) throws Throwable;
}
