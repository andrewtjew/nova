package org.nova.html.ext;

public class StringHandleEnumResolver extends StringHandleResolver
{

    @Override
    public String resolve(Enum<?> namespace,Enum<?> handle,Object...parameters) throws Throwable
    {
        if (handle==null)
        {
            return "";
        }
        return "[|"+namespace.toString()+"."+handle.toString()+"|]";
    }

}
