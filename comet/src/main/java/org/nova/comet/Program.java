package org.nova.comet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.nova.concurrent.TimeBase;
import org.nova.concurrent.TimerRunnable;
import org.nova.concurrent.TimerTask;
import org.nova.configuration.Configuration;
import org.nova.frameworks.CoreEnvironment;
import org.nova.frameworks.ServerApplication;
import org.nova.frameworks.ServerApplicationRunner;
import org.nova.http.server.HttpServer;
import org.nova.http.server.HttpTransport;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.scripts.Script;
import org.nova.services.SessionServerApplication;
import org.nova.tracing.Trace;

public class Program
{
    
    public static void main(String[] args) throws Throwable
    {
        if (args.length==0)
        {
            return;
        }
        String name="org.nova.scripts."+args[0];
        
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
            System.out.println("script ended");
        }
    }
    
}
