package xp.nova.sqldb.graph;

import org.nova.sqldb.Accessor;
import org.nova.sqldb.RowSet;
import org.nova.tracing.Trace;

import xp.nova.sqldb.graph.Query.PreparedQuery;

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
    
    public GraphTransaction beginTransaction(Trace parent,String source,long creatorId,boolean autoCloseGraphAccesoor) throws Throwable
    {
        return new GraphTransaction(parent, this, source, creatorId,autoCloseGraphAccesoor);
    }
    public GraphTransaction beginTransaction(Trace parent,String source,Long creatorId) throws Throwable
    {
        return beginTransaction(parent,source,creatorId,false);
    }
    
    public long getCount(Trace parent,Class<? extends NodeObject> type,String where,Object...parameters) throws Throwable
    {
        Meta meta=this.graph.getMeta(type);
        String table=meta.getTableName();
        return accessor.executeQuery(parent,null,"SELECT count(*) FROM "+table+" WHERE "+where,parameters).getRow(0).getBIGINT(0);
    }
    
    static final private boolean TEST=false;
    
    static void translateParameters(Object[] parameters)
    {
        for (Object parameter:parameters)
        {
            if (parameters!=null)
            {
                if (parameter instanceof ShortEnummerable)
                {
                    parameter=((ShortEnummerable)parameter).getValue();
                }
                else if (parameter instanceof IntegerEnummerable)
                {
                    parameter=((IntegerEnummerable)parameter).getValue();
                }
            }
        }
    }
    public QueryResultSet execute(Trace parent,String orderBy,Long startNodeId,Query query,Object...parameters) throws Throwable
    {
        PreparedQuery preparedQuery=query.build(this.graph);
        if (parameters.length>0)
        {
            if (preparedQuery.parameters!=null)
            {
                throw new Exception();
            }
        }
        else
        {
            parameters=preparedQuery.parameters;
        }
        RowSet rowSet;
        if (TEST)
        {
            System.out.println(preparedQuery.sql);
        }
        if (parameters != null)
        {
            rowSet = accessor.executeQuery(parent, null, preparedQuery.sql, parameters);
        }
        else
        {
            rowSet = accessor.executeQuery(parent, null, preparedQuery.sql);
        }
        
        return new QueryResultSet(rowSet, preparedQuery.map);
    }
    public QueryResultSet execute(Trace parent,long startNodeId,Query query,Object...parameters) throws Throwable
    {
        return execute(parent,null,startNodeId,query,parameters);
    }
    public QueryResultSet execute(Trace parent,Query query,Object...parameters) throws Throwable
    {
        return execute(parent,null,null,query,parameters);
    }
    
}
