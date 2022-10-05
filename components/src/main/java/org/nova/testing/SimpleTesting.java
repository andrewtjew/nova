package org.nova.testing;

public class SimpleTesting extends Testing
{
    public SimpleTesting()
    {
    }
    
    
    @Override
    public void _log(LogLevel logLevel,Object object)
    {
        System.out.println(object);
    }

}
