package org.nova.html.localization;

public class StringHandleEnumResolver extends StringHandleResolver
{

    @Override
    public String resolve(LanguageCode languageCode,Class<?> type,String handle,Object...parameters) throws Throwable
    {
        if (handle==null)
        {
            return "";
        }
        String typeName=type!=null?"."+type.getName()+":":":";
        return "[|"+languageCode.toString()+typeName+handle.toString()+"|]";
    }

}
