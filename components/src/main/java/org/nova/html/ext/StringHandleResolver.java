package org.nova.html.ext;

public abstract class StringHandleResolver
{
    public abstract String resolve(Enum<?> namespace,Enum<?> handle,Object...parameters) throws Throwable;
    
}
