package org.nova.security;

import org.nova.http.server.Context;

import jakarta.servlet.http.HttpServletRequest;

public interface QuerySecurity
{
    public String signQuery(String query) throws Throwable;
    public boolean isQuerySecure(Context context) throws Throwable;
}
