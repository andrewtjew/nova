package org.nova.html.properties;

public class ColorProperty extends Property
{
    public ColorProperty(String label,Color color)
    {
        this.string=label+":"+color.toString()+";";
    }
}
