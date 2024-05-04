package org.nova.http.server;

public interface ObjectLookup
{
    public Object getObject(Context context) throws Throwable;
}
