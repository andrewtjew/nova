package xp.nova.sqldb.graph;

import org.nova.collections.ContentCache;
import org.nova.tracing.Trace;

public class QueryResultSetCache extends ContentCache<QueryCacheKey, QueryResultSet>
{
    final private Graph graph;
    public QueryResultSetCache(Graph graph) throws Throwable
    {
        super(0,0,0,0);
        this.graph=graph;
    }

    @Override
    protected ValueSize<QueryResultSet> load(Trace parent, QueryCacheKey key) throws Throwable
    {
        return null;
    }
    
    @Override
    protected void onEvict(Trace parent, QueryCacheKey key, QueryResultSet value) throws Throwable
    {
        this.graph.evictCacheSets(parent,key,value);
    }

}
