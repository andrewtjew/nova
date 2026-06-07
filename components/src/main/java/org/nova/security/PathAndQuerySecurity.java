package org.nova.security;

import org.nova.http.server.Context;

public interface PathAndQuerySecurity
{
    public String securePathAndQuery(String pathAndQuery) throws Throwable;
    public boolean verifyRequest(Context context) throws Throwable;
}
