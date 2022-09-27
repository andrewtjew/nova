package org.nova.localization;

public class StringHandle
{
    final private String handle;
    final private Class<?> type;
    final private String locale;
    final private Object[] parameters;
    
    public StringHandle(String locale,Enum<?> handle,Object...parameters)
    {
        this.handle=handle.toString();
        this.type=handle.getClass();
        this.locale=locale;
        this.parameters=parameters;
    }
    public StringHandle(String locale,String handle,Object...parameters)
    {
        this.handle=handle;
        this.type=null;
        this.locale=locale;
        this.parameters=parameters;
    }
    
    public String resolve(StringHandleResolver resolver) throws Throwable
    {
        return resolver.resolve(locale,this.type,this.handle,this.parameters);
    }


}
