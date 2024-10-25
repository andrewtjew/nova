package xp.nova.sqldb.graph;

import org.nova.collections.ContentCache;
import org.nova.tracing.Trace;

import xp.nova.sqldb.graph.GraphAccessor;
import xp.nova.sqldb.graph.NodeObject;
import xp.nova.sqldb.graph.Query;
import xp.nova.sqldb.graph.QueryResultSet;

public class QueryCache extends ContentCache<QueryKey, QueryResultSet>
{
    public QueryCache() throws Throwable
    {
        super(0,0,0,0);
    }

    @Override
    protected ValueSize<QueryResultSet> load(Trace parent, QueryKey key) throws Throwable
    {
        return null;
    }
    
    @Override
    protected void onEvict(Trace parent, QueryKey key, QueryResultSet value) throws Throwable
    {
    }

}
