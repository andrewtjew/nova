package org.nova.html.bootstrap.messaging;

import java.util.LinkedList;

public class Receiver
{
    final LinkedList<Message> pageMessages;
    final LinkedList<Message> popupMessages;
    final LinkedList<Message> footerMessages;
    //Message message;
    public Receiver()
    {
        this.pageMessages=new LinkedList<Message>();
        this.popupMessages=new LinkedList<Message>();
        this.footerMessages=new LinkedList<Message>();
    }
    
    private LinkedList<Message> getMessages(Presentation presentation)
    {
        switch (presentation)
        {
        case FOOTER:
            return this.footerMessages;
        case BAR:
            return this.pageMessages;
        case MODAL:
            return this.popupMessages;
        default:
            break;
        }
        return null;
    }
    
    synchronized public void queue(Message message)
    {
        LinkedList<Message> messages=getMessages(message.getPresentation());
        if (message.isHighPriority())
        {
            messages.addFirst(message);
        }
        else
        {
            messages.add(message);
        }
    }
    
    synchronized public Message getMessage(Presentation presentation)
    {
        LinkedList<Message> messages=getMessages(presentation);
        if (messages.size()>0)
        {
            return messages.remove();
        }
        return null;
    }
}
