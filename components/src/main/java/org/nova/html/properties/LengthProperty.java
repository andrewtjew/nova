package org.nova.html.properties;

public class LengthProperty extends Property
{
    public LengthProperty(String label,Length size)
    {
        super(label+":"+size.toString()+";");
    }
}
