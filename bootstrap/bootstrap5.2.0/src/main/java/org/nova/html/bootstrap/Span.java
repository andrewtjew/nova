package org.nova.html.bootstrap;

public class Span extends StyleComponent<Span>
{
    public Span(String text)
    {
        super("span",null);
        addInner(text);
    }
    public Span()
    {
        super("span",null);
    }
}
