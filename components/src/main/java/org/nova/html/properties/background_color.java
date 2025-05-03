package org.nova.html.properties;

public class background_color extends ColorProperty
{

    public background_color(Color color)
    {
        super("background-color", color);
    }
    public background_color(String value)
    {
        super("background-color", new Color(value));
    }

}
