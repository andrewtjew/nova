package xp.nova.sqldb.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.nova.collections.RingBuffer;
import org.nova.metrics.LongValueMeter;
import org.nova.operations.OperatorVariable;


public class PerformanceMonitor
{
    static public record QueryCatalogKey(PreparedQuery preparedQuery,String catalog)
    {
        @Override
        public boolean equals(Object obj) 
        {
          if (this == obj) 
          {
            return true;
          }
          if (obj == null || getClass() != obj.getClass()) 
          {
            return false;
          }
          QueryCatalogKey other = (QueryCatalogKey) obj;
          return preparedQuery.equals(other.preparedQuery) && catalog.equals(other.catalog);
        }
        public int hashCode() 
        {
            return preparedQuery.hashCode()+catalog.hashCode();
        }
    }
    

    HashMap<QueryCatalogKey,StackAndMeter> slowQueries;

    @OperatorVariable(description="ms")
    long minimumDuration;
    
    public PerformanceMonitor(long minimumDuration)
    {
        this.slowQueries=new HashMap<QueryCatalogKey, StackAndMeter>();
        setMimimumDuration(minimumDuration);
    }
    
    public void setMimimumDuration(long minimumDuration)
    {
        this.minimumDuration=minimumDuration;
    }
    
    public void updateSlowQuery(long duration,PreparedQuery query,String catalog)
    {
        if (duration<this.minimumDuration)
        {
            return;
        }
        synchronized(this)
        {
            QueryCatalogKey key=new QueryCatalogKey(query,catalog);
            StackAndMeter stackAndMeter=slowQueries.get(key);
            if (stackAndMeter==null)
            {
                stackAndMeter=new StackAndMeter(Thread.currentThread().getStackTrace(),new LongValueMeter());
                this.slowQueries.put(key, stackAndMeter);
            }
            stackAndMeter.meter().update(duration);
        }
    }
    public void clear()
    {
        synchronized(this)
        {
            this.slowQueries.clear();
        }
    }
    public Map<QueryCatalogKey,StackAndMeter> getSnapshot()
    {
        synchronized (this)
        {
            return (HashMap<QueryCatalogKey,StackAndMeter>)this.slowQueries.clone();
        }
    }
}
