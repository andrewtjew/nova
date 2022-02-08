package org.nova.localization;

public abstract class StringHandleResolver
{
    public abstract String resolve(LanguageCode languageCode,Class<?> type,String handle,Object...parameters) throws Throwable;
    
}
