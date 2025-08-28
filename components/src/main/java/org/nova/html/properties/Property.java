package org.nova.html.properties;

public class Property
{
    protected String property;
    
    public Property(String property)
    {
        this.property=property;
    }
    @Override
    final public String toString()
    {
        return property;
    }    
}
