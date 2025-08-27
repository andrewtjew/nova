package xp.nova.sqldb.graph;

import org.nova.collections.ContentCache;
import org.nova.tracing.Trace;

public class CountCache extends ContentCache<QueryCacheKey, Long>
{
    final private Graph graph;
    public CountCache(Graph graph) throws Throwable
    {
        super(0,0,0,0);
        this.graph=graph;
    }

    @Override
    protected ValueSize<Long> load(Trace parent, QueryCacheKey key) throws Throwable
    {
        return null;
    }
    
    @Override
    protected void onEvict(Trace parent, QueryCacheKey key, Long value) throws Throwable
    {
        this.graph.evictCacheSets(parent,key,null);
    }

}
