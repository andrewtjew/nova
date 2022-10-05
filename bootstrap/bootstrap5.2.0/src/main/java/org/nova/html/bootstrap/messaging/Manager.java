package org.nova.html.bootstrap.messaging;

public class Manager
{
    long id;
    synchronized public long save(Message message)
    {
        return id++;
    }
    
    
    
}
