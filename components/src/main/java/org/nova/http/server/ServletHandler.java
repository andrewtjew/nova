package org.nova.http.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nova.tracing.Trace;

public abstract class ServletHandler
{
    public abstract void handle(Trace parent,HttpServletRequest request, HttpServletResponse response) throws Throwable;
    public boolean isLog()
    {
        return true;
    }
    public boolean isLogRequestHeaders()
    {
        return true;
    }
    public boolean isLogResponseHeaders()
    {
        return true;
    }
    public boolean isLogLastRequestsInMemory()
    {
        return true;
    }

}
