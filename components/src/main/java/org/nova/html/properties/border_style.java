package org.nova.html.properties;

public class border_style extends Property
{
    final private BorderStyle borderStyle;
    public border_style(BorderStyle borderStyle)
    {
        this.borderStyle=borderStyle;
    }
    @Override
    public String toString()
    {
        return "border-style:"+borderStyle.toString()+";";
    }
}
