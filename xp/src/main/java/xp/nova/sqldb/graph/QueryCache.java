//package xp.nova.sqldb.graph;
//
//import org.nova.collections.ContentCache;
//import org.nova.tracing.Trace;
//
//import xp.nova.sqldb.graph.GraphAccessor;
//import xp.nova.sqldb.graph.NodeObject;
//import xp.nova.sqldb.graph.Query;
//import xp.nova.sqldb.graph.QueryResultSet;
//
//public class QueryCache extends ContentCache<QueryKey, QueryResultSet>
//{
//    final private Graph graph;
//
//    public QueryCache(Graph graph) throws Throwable
//    {
//        super(0,0,0,0);
//        this.graph=graph;
//    }
//
//    public QueryResultSet get(Trace parent,Query query,Object...parameters) throws Throwable
//    {
//        return get(parent,new QueryKey(null,query,parameters));
//    }    
//    
//    public QueryResultSet get(Trace parent,NodeObject nodeObject,Query query,Object...parameters) throws Throwable
//    {
//        return get(parent,nodeObject.getNodeId(),query,parameters);
//    }    
//    public QueryResultSet get(Trace parent,long nodeId,Query query,Object...parameters) throws Throwable
//    {
//        return get(parent,new QueryKey(nodeId,query,parameters));
//    }    
//
//    @Override
//    public ValueSize<QueryResultSet> getFromCache(QueryKey key) throws Throwable
//    {
//        key.query.build(this.graph);
//        return super.getFromCache(key);
//    }    
//
//    @Override
//    protected ValueSize<QueryResultSet> load(Trace parent, QueryKey key) throws Throwable
//    {
//        try (GraphAccessor accessor=this.graph.openGraphAccessor(parent,this.grap))
//        {
//            if (key.nodeId!=null)
//            {
//                QueryResultSet result=accessor.execute(parent, key.nodeId,key.query,key.parameters);
//                return new ValueSize<QueryResultSet>(result);
//            }
//            QueryResultSet result=accessor.execute(parent, key.query,key.parameters);
//            return new ValueSize<QueryResultSet>(result);
//        }
//    }
//    
//    @Override
//    protected void onEvict(Trace parent, QueryKey key, QueryResultSet value) throws Throwable
//    {
//    }
//
//}
