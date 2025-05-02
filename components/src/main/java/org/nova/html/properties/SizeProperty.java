package org.nova.html.properties;

public class SizeProperty extends Property
{
    final private Size size;
    final String label;
    public SizeProperty(String label,Size size)
    {
        this.label=label;
        this.size=size;
    }
    @Override
    public String toString()
    {
        return this.label+":"+this.size.toString()+";";
    }
}
