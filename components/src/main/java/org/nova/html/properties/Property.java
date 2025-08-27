package org.nova.html.properties;

public class Property
{
    protected String property;
    
    public Property(String property)
    {
        this.property=property;
    }
    public Property()
    {
    }
    @Override
    final public String toString()
    {
        return property;
    }    
}
