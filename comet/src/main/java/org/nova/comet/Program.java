package org.nova.comet;

import org.nova.builders.Script;

public class Program
{
    
    public static void main(String[] args) throws Throwable
    {
        if (args.length==0)
        {
            return;
        }
        String name="org.nova.builders."+args[0];
        
        Class<?> type=Class.forName(name);
        Script script=(Script)type.getDeclaredConstructor().newInstance();
        
        String[] scriptArgs=new String[args.length-1];
        System.arraycopy(args, 1, scriptArgs, 0, scriptArgs.length);

        try
        {
            script.run(scriptArgs);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        finally
        {
            System.out.println("comet: script ended");
        }
    }
    
}
