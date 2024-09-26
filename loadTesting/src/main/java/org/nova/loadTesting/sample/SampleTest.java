package org.nova.loadTesting.sample;

import java.util.Random;

import org.nova.frameworks.ServerApplicationRunner;
import org.nova.html.ext.Text;
import org.nova.loadTesting.Actor;
import org.nova.loadTesting.Actor.Act;
import org.nova.loadTesting.LoadTestService;
import org.nova.loadTesting.Settings;
import org.nova.loadTesting.State;
import org.nova.tracing.Trace;


public class SampleTest
{
    static public class SampleActorState extends State
    {
        public SampleActorState(int id)
        {
            super(id);
        }
        
    }
    
    static class Start implements Actor.Act<SampleActorState>
    {
        @Override
        public Act<SampleActorState> run(Trace parent, SampleActorState state)
        {
            return new LongBurn();
        }
    }
    
    static int BURN_BASE=1000000;
    
    static class LongBurn implements Actor.Act<SampleActorState>
    {
        @Override
        public Act<SampleActorState> run(Trace parent, SampleActorState state)
        {
            Random random=new Random();
            for (int i=0;i<BURN_BASE*5;i++)
            {
                random.nextInt();
            }
            return new ShortBurn();
        }
    }

    static class ShortBurn implements Actor.Act<SampleActorState>
    {
        @Override
        public Act<SampleActorState> run(Trace parent, SampleActorState state)
        {
            Random random=new Random();
            for (int i=0;i<BURN_BASE;i++)
            {
                random.nextInt();
            }
            return new Sleep();
        }
    }
    
    static class Sleep implements Actor.Act<SampleActorState>
    {
        @Override
        public Act<SampleActorState> run(Trace parent, SampleActorState state) throws Throwable
        {
            Thread.sleep(100);
            return new LongBurn();
        }
    }
    
    public static void main(String[] args) throws Throwable
    {
        var settings=new Settings();
        LoadTestService.run(args,settings, SampleActorState.class, new Start(),200);
        Thread.sleep(1000);
        System.out.println("Done");
    }
}
