package org.nova.security;

import org.nova.http.server.Context;

public interface QuerySecurity
{
    public String getSecurityQueryKey();
    public String signQuery(String query) throws Throwable;
}
