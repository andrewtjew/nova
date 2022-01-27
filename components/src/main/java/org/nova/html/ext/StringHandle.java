package org.nova.html.ext;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;

public class StringHandle extends Element
{
    public static StringHandleResolver RESOLVER=new StringHandleEnumResolver();
    
    final private Enum<?> handle;
    final private Enum<?> namespace;
    final private Object[] parameters;
    
    public StringHandle(Enum<?> namespace,Enum<?> handle,Object...parameters)
    {
        this.handle=handle;
        this.namespace=namespace;
        this.parameters=parameters;
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        composer.getStringBuilder().append(RESOLVER.resolve(this.namespace,this.handle,this.parameters));
    }


}
