package org.nova.html.properties;

public class color extends ColorProperty
{

    public color(Color_ color)
    {
        super("color", color);
    }
    public color(String value)
    {
        super("color", new Color_(value));
    }

}
