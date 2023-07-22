package org.nova.testing;

public class ConsoleOutTesting extends Testing
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
