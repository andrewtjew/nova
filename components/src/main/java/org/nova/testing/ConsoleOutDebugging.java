package org.nova.testing;

public class ConsoleOutDebugging extends Debugging
{
    public ConsoleOutDebugging()
    {
    }
    
    
    @Override
    public void _log(LogLevel logLevel,String category,Object object)
    {
        switch (logLevel)
        {
            case ERROR:
            case WARNING:
            System.err.println(category+":"+object);
            break;

            default:
            System.out.println(category+":"+object);
            break;
            
        }
    }

}
