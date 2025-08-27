package org.nova.html.properties;

public class color_ extends ColorProperty
{

    public color_(Color color)
    {
        super("color", color);
    }
    public color_(String value)
    {
        super("color", new Color(value));
    }

}
