package xp.nova.sqldb.graph;

import java.util.HashMap;
import java.util.Map;
import org.nova.metrics.LongValueMeter;
import org.nova.operations.OperatorVariable;


public class GraphPerformanceMonitor
{
    HashMap<String,QueryPerformance> slowQueries;

    @OperatorVariable(description="ms")
    long minimumDuration;
    boolean caching;
    
    public GraphPerformanceMonitor(long minimumDuration)
    {
        this.slowQueries=new HashMap<String, QueryPerformance>();
        setMimimumDuration(minimumDuration);
    }
    
    public void setMimimumDuration(long minimumDuration)
    {
        this.minimumDuration=minimumDuration;
    }
    
    public void updateSlowQuery(long duration,String catalog,QueryCacheKey queryKey)
    {
        if (duration<this.minimumDuration)
        {
            return;
        }
        synchronized(this)
        {
            String key='`'+catalog+"`."+queryKey.preparedQuery.sql;
            QueryPerformance queryPerformance=slowQueries.get(key);
            if (queryPerformance==null)
            {
                queryPerformance=new QueryPerformance(Thread.currentThread().getStackTrace(),new LongValueMeter(),queryKey);
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
    public void setCaching(boolean caching)
    {
        this.caching=caching;
    }
}
