package org.nova.html.properties;

class LengthProperty extends Property
{
    public LengthProperty(String label,Length size)
    {
        this.string=label+":"+size.toString()+";";
    }
}
