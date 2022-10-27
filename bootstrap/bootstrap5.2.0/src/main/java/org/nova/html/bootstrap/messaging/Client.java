package org.nova.html.bootstrap.messaging;

import java.util.LinkedList;

public class Client
{
    LinkedList<Message> high;
    LinkedList<Message> normal;
    LinkedList<Message> low;
    Message message;
    public Client()
    {
        this.low=new LinkedList<Message>();
        this.normal=new LinkedList<Message>();
        this.high=new LinkedList<Message>();
    }
    
    synchronized public void queue(Message message)
    {
        switch (message.getPriority())
        {
            case HIGH:
                high.add(message);
            case LOW:
                low.add(message);
                break;
            case NORMAL:
                normal.add(message);
                break;
            default:
                break;
            
        }
    }
    
    synchronized public Message getNextMessage()
    {
        if (this.message!=null)
        {
            return this.message;
        }

        if (this.high.size()>0)
        {
            message=this.high.remove();
        }
        if (this.normal.size()>0)
        {
            message=this.normal.remove();
        }
        if (this.low.size()>0)
        {
            message=this.low.remove();
        }
        return message;
    }
    synchronized public void retireMessage()
    {
        this.message=null;
    }
}
