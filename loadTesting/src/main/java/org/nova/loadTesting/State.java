package org.nova.loadTesting;

public class State
{
    final private int id;
    
    protected State(int id)
    {
        this.id=id;
    }
    public int getId()
    {
        return this.id;
    }
}