package org.nova.html.bootstrap.messaging;

import org.nova.html.elements.Element;
import org.nova.html.ext.Text;

public class Message
{
    final private Presentation presentation;
    final private Element content;
    final private boolean highPriority;
    
    public Message(boolean highPriority,Presentation presentation,Element content)
    {
        this.presentation=presentation;
        this.highPriority=highPriority;
        this.content=content;
    }
    public Message(Presentation presentation,Element content)
    {
        this(false,presentation,content);
    }    
    public Message(Presentation presentation,String text)
    {
        this(presentation,new Text(text));
    }    
    public boolean isHighPriority()
    {
        return this.highPriority;
    }

    public Presentation getPresentation()
    {
        return presentation;
    }

    public Element getContent()
    {
        return this.content;
    }

    
}
