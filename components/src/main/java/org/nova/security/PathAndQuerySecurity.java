package org.nova.security;

import org.nova.http.server.Context;
import org.nova.tracing.Trace;

public interface PathAndQuerySecurity
{
    public String securePathAndQuery(String pathAndQuery) throws Throwable;
    public AbnormalResponse verifyRequest(Trace parent,Context context) throws Throwable;
}
