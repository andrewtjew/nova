package org.nova.html.bootstrap.messaging;

import java.util.LinkedList;

public class Client
{
    final LinkedList<Message> messages;
    Message message;
    public Client()
    {
        this.messages=new LinkedList<Message>();
    }
    
    synchronized public void queue(Message message)
    {
        if (message.isHighPriority())
        {
            if (this.messages.get(0).isHighPriority())
            {
                for (int i=1;i<this.messages.size();i++)
                {
                    if (this.messages.get(i).isHighPriority()==false)
                    {
                        this.messages.add(i,message);
                        return;
                    }
                }
                this.messages.add(message);
            }
            this.messages.addFirst(message);
        }
        else
        {
            this.messages.add(message);
        }
    }
    
    synchronized public Message getMessage()
    {
        if (this.messages.size()>0)
        {
            return this.messages.remove();
        }
        return null;
    }
    synchronized public void retireMessage()
    {
        this.message=null;
    }
}
