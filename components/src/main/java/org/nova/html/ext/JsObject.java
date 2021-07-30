package org.nova.html.ext;

public class JsObject
{
    private final String name;
    public JsObject(String name)
    {
        this.name=name;
    }
    @Override 
    public String toString()
    {
        return this.name;
    }
}