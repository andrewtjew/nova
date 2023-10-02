package org.nova.testing;

public class ConsoleOutTesting extends Debugging
{
    public ConsoleOutTesting()
    {
    }
    
    
    @Override
    public void _log(LogLevel logLevel,Object object)
    {
        System.out.println(object);
    }

}
