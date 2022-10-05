package org.nova.html.bootstrap.messaging;

import org.nova.html.elements.Element;

public class Message
{
    final private Level level;
    final private Presentation displayMethod;
    final private String title;
    final private String content;
    final private Priority priority;
    final Long id;
    
    public Message(Long id,Priority priority,Level level,Presentation presentation,String title,Element content)
    {
        this.id=id;
        this.level=level;
        this.displayMethod=presentation;
        this.title=title;
        this.content="test";
        this.priority=priority;
    }
    
    public Priority getPriority()
    {
        return this.priority;
    }
    public Level getLevel()
    {
        return level;
    }

    public Presentation getPresentation()
    {
        return displayMethod;
    }

    public String getTitle()
    {
        return title;
    }

    public String getContent()
    {
        return content;
    }
    
    
}
