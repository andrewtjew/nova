package org.nova.loadTesting;

import org.nova.frameworks.ServerApplicationRunner;
import org.nova.html.ext.Text;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceRunnable;

public class Actor<STATE extends State> implements TraceRunnable
{
    static public interface Act<ACTOR extends State>
    {
        public Act<ACTOR> run(Trace parent,ACTOR state) throws Throwable;
    }
    static public <STATE extends State> void run(Settings settings,String[] args,Class<STATE> stateType,Actor.Act<STATE> runnable,int instances) throws Throwable
    {
        Text.SAFE_ESCAPE = true;
        new ServerApplicationRunner().run(args, (coreEnvironment, operatorServer) ->
        {
            LoadTestService service=new LoadTestService(coreEnvironment, operatorServer);

            @SuppressWarnings("unchecked")
            Actor<STATE>[] runners=(Actor<STATE>[])new Actor<?>[instances];
            
            var constructor=stateType.getDeclaredConstructor(int.class);
            for (int i=0;i<runners.length;i++)
            {
                STATE state=constructor.newInstance(i);
                runners[i]=new Actor<STATE>(i,settings,state,runnable);
            }
            Trace trace=new Trace(service.getTraceManager(),"root");
            var progress=service.getMultiTaskScheduler().schedule(trace, "loadTest", runners);
            
            return service;
        });
    }
    
    final private Settings settings;
    final private Act<STATE> start;
    final private STATE state;
    final int id;
    
    public Actor(int id,Settings settings,STATE state,Act<STATE> start)
    {
        this.id=id;
        this.state=state;
        this.start=start;
        this.settings=settings;
    }
    
    @Override
    public void run(Trace parent) throws Throwable
    {
        var act=this.start;
        try (Trace actorTrace=new Trace(parent,"Actor-"+this.id))
        {
            try
            {
                do 
                {
                    try (Trace actTrace=new Trace(actorTrace,"Act-"+act.getClass().getSimpleName()+"-"+this.id))
                    {
                        act=act.run(actorTrace, this.state);
                    }
                } 
                while (act!=null);
            }
            catch (Throwable t)
            {
                actorTrace.close(t);
            }
        }
    }
    
}
