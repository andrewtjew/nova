package org.nova.html.properties;

public class LengthProperty extends Property
{
    public LengthProperty(String label,Length size)
    {
        this.string=label+":"+size.toString()+";";
    }
}
