package org.nova.html.properties;

class LengthProperty extends Property
{
    protected LengthProperty(String label,Length_ size)
    {
        super(label+":"+size.toString()+";");
    }
}
