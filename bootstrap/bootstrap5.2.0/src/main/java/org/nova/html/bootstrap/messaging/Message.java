package org.nova.html.bootstrap.messaging;

import org.nova.html.elements.Element;
import org.nova.html.ext.Text;

public class Message
{
    final private Level level;
    final private Presentation presentation;
    final private Element content;
    final private boolean highPriority;
    
    public Message(boolean highPriority,Long expire,Level level,Presentation presentation,Element content)
    {
        this.level=level;
        this.presentation=presentation;
        this.highPriority=highPriority;
        this.content=content;
    }
    public Message(Level level,Presentation presentation,Element content)
    {
        this(false,null,Level.INFO,presentation,content);
    }    
    public Message(Level level,Presentation presentation,String text)
    {
        this(level,presentation,new Text(text));
    }    
    public boolean isHighPriority()
    {
        return this.highPriority;
    }
    public Level getLevel()
    {
        return level;
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
