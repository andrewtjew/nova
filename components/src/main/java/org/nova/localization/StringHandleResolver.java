package org.nova.localization;

public abstract class StringHandleResolver
{
    public abstract String resolve(String locale,Class<?> type,String handle,Object...parameters) throws Throwable;
    
}
