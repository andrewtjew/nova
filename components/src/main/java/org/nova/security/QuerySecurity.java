package org.nova.security;

public interface QuerySecurity
{
    public String getSecurityQueryKey();
    public String signQuery(String query) throws Throwable;
}
