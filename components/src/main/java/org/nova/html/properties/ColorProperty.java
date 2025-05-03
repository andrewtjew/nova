package org.nova.html.properties;

public class ColorProperty extends Property
{
    final private Color color;
    final String label;
    public ColorProperty(String label,Color color)
    {
        this.label=label;
        this.color=color;
    }
    @Override
    public String toString()
    {
        return this.label+":"+this.color.toString()+";";
    }
}
