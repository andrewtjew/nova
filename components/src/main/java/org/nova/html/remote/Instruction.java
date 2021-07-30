package org.nova.html.remote;

public class Instruction
{
    public boolean trace;
    public Command command;
    public String[] parameters;
    
    Instruction(boolean trace,Command command,String...parameters)
    {
        this.trace=trace;
        this.command=command;
        this.parameters=parameters;
    }
}
