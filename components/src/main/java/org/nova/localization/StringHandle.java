package org.nova.localization;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;

public class StringHandle
{
    final private String handle;
    final private Class<?> type;
    final private LanguageCode languageCode;
    final private Object[] parameters;
    
    public StringHandle(LanguageCode languageCode,Enum<?> handle,Object...parameters)
    {
        this.handle=handle.toString();
        this.type=handle.getClass();
        this.languageCode=languageCode;
        this.parameters=parameters;
    }
    public StringHandle(LanguageCode languageCode,String handle,Object...parameters)
    {
        this.handle=handle;
        this.type=null;
        this.languageCode=languageCode;
        this.parameters=parameters;
    }
    
    public String resolve(StringHandleResolver resolver) throws Throwable
    {
        return resolver.resolve(this.languageCode,this.type,this.handle,this.parameters);
    }


}
