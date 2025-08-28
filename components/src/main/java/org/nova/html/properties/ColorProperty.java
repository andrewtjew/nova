package org.nova.html.properties;

public class ColorProperty extends Property
{
    protected ColorProperty(String label,Color_ color)
    {
        super(label+":"+color.toString()+";");
    }
}
