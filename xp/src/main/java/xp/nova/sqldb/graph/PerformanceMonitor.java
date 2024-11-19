package xp.nova.sqldb.graph;

import java.util.HashMap;
import java.util.Map;
import org.nova.metrics.LongValueMeter;
import org.nova.operations.OperatorVariable;


public class PerformanceMonitor
{
    HashMap<String,QueryPerformance> slowQueries;

    @OperatorVariable(description="ms")
    long minimumDuration;
    
    public PerformanceMonitor(long minimumDuration)
    {
        this.slowQueries=new HashMap<String, QueryPerformance>();
        setMimimumDuration(minimumDuration);
    }
    
    public void setMimimumDuration(long minimumDuration)
    {
        this.minimumDuration=minimumDuration;
    }
    
    public void updateSlowQuery(long duration,QueryResultSet resultSet,String catalog)
    {
        if (duration<this.minimumDuration)
        {
            return;
        }
        synchronized(this)
        {
            String key='`'+catalog+"`."+resultSet.sql;
            QueryPerformance queryPerformance=slowQueries.get(key);
            if (queryPerformance==null)
            {
                queryPerformance=new QueryPerformance(Thread.currentThread().getStackTrace(),new LongValueMeter(),resultSet);
                this.slowQueries.put(key, queryPerformance);
            }
            queryPerformance.meter().update(duration);
        }
    }
    public void clear()
    {
        synchronized(this)
        {
            this.slowQueries.clear();
        }
    }
    public Map<String,QueryPerformance> getSnapshot()
    {
        synchronized (this)
        {
            return (HashMap<String,QueryPerformance>)this.slowQueries.clone();
        }
    }
}
