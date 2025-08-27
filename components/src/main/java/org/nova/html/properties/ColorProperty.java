package org.nova.html.properties;

public class ColorProperty extends Property
{
    public ColorProperty(String label,Color color)
    {
        this.property=label+":"+color.toString()+";";
    }
}
