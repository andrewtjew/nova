package org.nova.html.properties;

public class border_radius extends LengthProperty
{
    public border_radius(Length_ length)
    {
        super("border-radius",length);
    }
    public border_radius(double length,Unit_ unit)
    {
        this(new Length_(length,unit));
    }
    
    public border_radius(double length)
    {
        this(new Length_(length,null));
    }
}
