package org.nova.service.deviceSession;

import org.nova.http.server.Context;
import org.nova.http.server.Response;
import org.nova.tracing.Trace;

public interface PageStateRequestHandling
{
    public AbnormalResult beginRequest(Trace parent,Context context) throws Throwable;
    public void endRequest(Trace parent,Context context,Response<?> response) throws Throwable;
}
