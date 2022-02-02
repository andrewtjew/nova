package org.nova.html.localization;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;

public class StringHandle extends Element
{
    static StringHandleResolver RESOLVER=new StringHandleEnumResolver();
    
    final private String handle;
    final private Class<?> type;
    final private LanguageCode languageCode;
    final private Object[] parameters;
    
    public static void setResolver(StringHandleResolver resolver)
    {
        RESOLVER=resolver;
    }
    
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
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        composer.getStringBuilder().append(RESOLVER.resolve(this.languageCode,this.type,this.handle,this.parameters));
    }


}
