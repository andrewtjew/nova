package org.nova.localization;

public class LiteralStringHandleResolver extends StringHandleResolver
{

    @Override
    public String resolve(LanguageCode languageCode,Class<?> type,String handle,Object...parameters) throws Throwable
    {
        if (handle==null)
        {
            return "";
        }
        return handle;
    }

}
