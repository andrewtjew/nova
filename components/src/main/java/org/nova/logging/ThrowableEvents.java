package org.nova.logging;

public class ThrowableEvents
{
    private ThrowableEvent first;
    private ThrowableEvent last; 
    
    public ThrowableEvents()
    {
    }
    
    public void log(Throwable throwable)
    {
        synchronized(this)
        {
            last=new ThrowableEvent(throwable);
            if (first==null)
            {
                first=last;
            }
        }
    }

    public ThrowableEvent getFirst()
    {
        synchronized(this)
        {
            return first;
        }
    }

    public ThrowableEvent getLast()
    {
        synchronized(this)
        {
            return last;
        }
    }

}