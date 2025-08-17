package xp.nova.sqldb.graph;

import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.debug.LogLevel;
import org.nova.json.ObjectMapper;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.RowSet;
import org.nova.tracing.Trace;

public class GraphAccessor implements AutoCloseable
{
    final Accessor accessor;
    final Graph graph;
   
    GraphAccessor(Graph graph,Accessor accessor)
    {
        this.accessor=accessor;
        this.graph=graph;
    }

    @Override
    public void close() throws Exception
    {
        this.accessor.close();
    }
    
    public GraphTransaction beginTransaction(Trace parent,String source,long creatorId,boolean autoCloseGraphAccessor) throws Throwable
    {
        return new GraphTransaction(parent, this, source, creatorId,autoCloseGraphAccessor);
    }
    public GraphTransaction beginTransaction(Trace parent,String source,Long creatorId) throws Throwable
    {
        return beginTransaction(parent,source,creatorId,false);
    }
    
    static void translateParameters(Object[] parameters)
    {
        for (int i=0;i<parameters.length;i++)
        {
            Object parameter=parameters[i];
            if (parameter!=null)
            {
                if (parameter instanceof ShortEnummerable)
                {
                    parameters[i]=((ShortEnummerable)parameter).getValue();
                }
                else if (parameter instanceof IntegerEnummerable)
                {
                    parameters[i]=((IntegerEnummerable)parameter).getValue();
                }
            }
        }
    }
    public Accessor getAccessor()
    {
        return this.accessor;
    }
    
    
    private void debugPrint(QueryResultSet set) throws Throwable
    {
        if (set==null)
        {
            Debugging.log(Graph.DEBUG_CATEGORY,"null set");
            return;
        }
        for (int i=0;i<set.results.length;i++)
        {
            var result=set.results[i];
            StringBuilder sb=new StringBuilder();
            sb.append(i+":");
            sb.append(ObjectMapper.writeObjectToString(result.row.getObjects()));
            Debugging.log(Graph.DEBUG_CATEGORY,sb.toString());
        }
        
    }

    public QueryResultSet execute(Trace parent,Object[] parameters,long startNodeId,ArrayQuery query) throws Throwable
    {
        PreparedQuery preparedQuery=query.build(this.graph,false);
        translateParameters(parameters);
        return execute(parent,preparedQuery,parameters,startNodeId);
    }
    public QueryResultSet execute(Trace parent,long startNodeId,ArrayQuery query,Object...parameters) throws Throwable
    {
        return execute(parent,parameters,startNodeId,query);
    }
    public QueryResultSet execute(Trace parent,Node startNode,ArrayQuery query,Object...parameters) throws Throwable
    {
        return execute(parent,parameters,startNode.getNodeId(),query);
    }
    
    public QueryResultSet execute(Trace parent,Object[] parameters,Long startNodeId,Query query) throws Throwable
    {
        PreparedQuery preparedQuery=query.build(this.graph,false);
        translateParameters(parameters);
        return execute(parent,preparedQuery,parameters,startNodeId);
    }
    private QueryResultSet execute(Trace parent,PreparedQuery preparedQuery,Object[] parameters,Long startNodeId) throws Throwable
    {
        QueryKey key=new QueryKey(startNodeId, preparedQuery, parameters);
        if (this.graph.performanceMonitor.caching)
        {
            var valueSize=this.graph.getFromCache(key);
            if (valueSize!=null)
            {
                if (Debug.ENABLE && Graph.DEBUG && Graph.DEBUG_VERIFY_CACHING)
                {
                    QueryResultSet queryResultSet=_execute(parent, preparedQuery,parameters,startNodeId);
                    QueryResultSet cachedResultSet=valueSize.value();
                    if (queryResultSet.equals(cachedResultSet)==false)
                    {
                        debugPrint(queryResultSet);
                        debugPrint(cachedResultSet);
                        throw new Exception();
                    }
                }
                return valueSize.value();
            }
        }
        try (Trace trace=new Trace(parent,"GraphAccessor.execute"))
        {
            QueryResultSet queryResultSet=_execute(trace, preparedQuery,parameters,startNodeId);
            if (Debug.ENABLE && Graph.DEBUG && Graph.DEBUG_CACHING)
            {
                if (this.graph.performanceMonitor.caching)
                {
                    Debugging.log(Graph.DEBUG_CATEGORY,"cache miss:excecute");
                }
            }
            trace.close();
            long duration=trace.getDurationMs();
            this.graph.updateQueryResultSetCache(parent, key, queryResultSet,duration);
            return queryResultSet;
        }
    }        
        
    private QueryResultSet _execute(Trace parent,PreparedQuery preparedQuery,Object[] parameters,Long startNodeId) throws Throwable
    {
        StringBuilder sb=new StringBuilder(preparedQuery.sql);
        if (startNodeId!=null)
        {
            sb.append(preparedQuery.start+startNodeId);
        }
        if (preparedQuery.orderBy!=null)
        {
            sb.append(" ORDER BY "+preparedQuery.orderBy);
        }
        if (preparedQuery.limit!=null)
        {
            sb.append(" LIMIT "+preparedQuery.limit);
            if (preparedQuery.offset!=null)
            {
                sb.append(" OFFSET "+preparedQuery.offset);
            }
        }
        String sql=sb.toString();
        RowSet rowSet;
        if (Debug.ENABLE && Graph.DEBUG && Graph.DEBUG_QUERY)
        {
            StringBuilder debug=new StringBuilder(sql);
            if ((parameters!=null)&&(parameters.length>0))
            {
                debug.append("(");
                for (int i=0;i<parameters.length;i++)
                {
                    if (i==0)
                    {
                        debug.append('(');
                    }
                    else
                    {
                        debug.append(',');
                    }
                    debug.append(parameters[i]);
                }
                debug.append(")");
            }
            Debugging.log(Graph.DEBUG_CATEGORY,"query="+sql);
        }
        if (parameters != null)
        {
            rowSet = accessor.executeQuery(parent, null,parameters, sql);
        }
        else
        {
            rowSet = accessor.executeQuery(parent, null, sql);
        }
        if (Debug.ENABLE && Graph.DEBUG && Graph.DEBUG_QUERY)
        {
            Debugging.log(Graph.DEBUG_CATEGORY,"rows="+rowSet.size());
        }
        return new QueryResultSet(rowSet,preparedQuery,sql,startNodeId,parameters);
    }
    public QueryResultSet execute(Trace parent,long startNodeId,Query query,Object...parameters) throws Throwable
    {
        return execute(parent,parameters,startNodeId,query);
    }
    public QueryResultSet execute(Trace parent,Node startNode,Query query,Object...parameters) throws Throwable
    {
        return execute(parent,parameters,startNode.getNodeId(),query);
    }
    public QueryResultSet execute(Trace parent,Query query,Object...parameters) throws Throwable
    {
        return execute(parent,parameters,null,query);
    }
    
    public long count(Trace parent,Object[] parameters,long startNodeId,ArrayQuery query) throws Throwable
    {
        PreparedQuery preparedQuery=query.build(this.graph,true);
        translateParameters(parameters);
        return count(parent,preparedQuery,parameters,startNodeId);
    }
    public long count(Trace parent,long startNodeId,ArrayQuery query,Object...parameters) throws Throwable
    {
        return count(parent,parameters,startNodeId,query);
    }
    public long count(Trace parent,Node startNode,ArrayQuery query,Object...parameters) throws Throwable
    {
        return count(parent,parameters,startNode.getNodeId(),query);
    }
    
    
    public long count(Trace parent,Object[] parameters,Long startNodeId,Query query) throws Throwable
    {
        PreparedQuery preparedQuery=query.build(this.graph,true);
        translateParameters(parameters);
        return count(parent,preparedQuery,parameters,startNodeId);
    }
    
    private long count(Trace parent,PreparedQuery preparedQuery,Object[] parameters,Long startNodeId) throws Throwable
    {
        QueryKey key=new QueryKey(startNodeId, preparedQuery, parameters);
        if (this.graph.performanceMonitor.caching)
        {
            var valueSize=this.graph.getFromCountCache(key);
            if (valueSize!=null)
            {
                if (Debug.ENABLE && Graph.DEBUG && Graph.DEBUG_VERIFY_CACHING)
                {
                    long count=_executeCount(parent, preparedQuery,parameters,startNodeId);
                    if (count==valueSize.value()==false)
                    {
                        Debugging.log(Graph.DEBUG_CATEGORY,"count="+count+", cached="+valueSize.value());
                        throw new Exception();
                    }
                }
                
                return valueSize.value();
            }
        }
        try (Trace trace=new Trace(parent,"GraphAccessor.execute"))
        {
            long count=_executeCount(trace, preparedQuery,parameters,startNodeId);
            if (Debug.ENABLE && Graph.DEBUG && Graph.DEBUG_CACHING)
            {
                if (this.graph.performanceMonitor.caching)
                {
                    Debugging.log(Graph.DEBUG_CATEGORY,"cache miss:count");
                }
            }
            trace.close();
            long duration=trace.getDurationMs();
            this.graph.updateCountCache(parent, key, count,duration);
            return count;
        }
    }        

    private long _executeCount(Trace parent,PreparedQuery preparedQuery,Object[] parameters,Long startNodeId) throws Throwable
    {
        StringBuilder sb=new StringBuilder(preparedQuery.sql);
        if (startNodeId!=null)
        {
            sb.append(preparedQuery.start+startNodeId);
        }
        if (preparedQuery.orderBy!=null)
        {
            sb.append(" ORDER BY "+preparedQuery.orderBy);
        }
        if (preparedQuery.limit!=null)
        {
            sb.append(" LIMIT "+preparedQuery.limit);
            if (preparedQuery.offset!=null)
            {
                sb.append(" OFFSET "+preparedQuery.offset);
            }
        }
        String sql=sb.toString();
        RowSet rowSet;
        if (Debug.ENABLE && Graph.DEBUG && Graph.DEBUG_QUERY)
        {
            StringBuilder debug=new StringBuilder(sql);
            if ((parameters!=null)&&(parameters.length>0))
            {
                debug.append("(");
                for (int i=0;i<parameters.length;i++)
                {
                    if (i==0)
                    {
                        debug.append('(');
                    }
                    else
                    {
                        debug.append(',');
                    }
                    debug.append(parameters[i]);
                }
                debug.append(")");
            }
            Debugging.log(Graph.DEBUG_CATEGORY,"count.sql="+sql);
        }
        if (parameters != null)
        {
            rowSet = accessor.executeQuery(parent, null,parameters, sql);
        }
        else
        {
            rowSet = accessor.executeQuery(parent, null, sql);
        }
        if (Debug.ENABLE && Graph.DEBUG && Graph.DEBUG_QUERY)
        {
            Debugging.log(Graph.DEBUG_CATEGORY,"count.value="+rowSet.size());
        }
        return rowSet.getRow(0).getBIGINT(0);
    }
    public long count(Trace parent,long startNodeId,Query query,Object...parameters) throws Throwable
    {
        return count(parent,parameters,startNodeId,query);
    }
    public long count(Trace parent,Node startNode,Query query,Object...parameters) throws Throwable
    {
        return count(parent,parameters,startNode.getNodeId(),query);
    }
    public long count(Trace parent,Query query,Object...parameters) throws Throwable
    {
        return count(parent,parameters,null,query);
    }

    
}
