package org.nova.security;

import org.nova.http.server.Context;

public interface PathAndQuerySecurity
{
    public String securePathAndQuery(String pathAndQuery) throws Throwable;
    public boolean isRequestSecure(Context context) throws Throwable;
}
