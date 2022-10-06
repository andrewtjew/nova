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
    
    static final private boolean TEST=true;
    
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
    public QueryResultSet execute(Trace parent,Object[] parameters,String orderBy,Long startNodeId,Query query) throws Throwable
    {
        PreparedQuery preparedQuery=query.build(this.graph);
        if (query.parameters!=null)
        {
            if (parameters.length!=0)
            {
                throw new Exception();
            }
            parameters=query.parameters;
        }
        else
        {
            translateParameters(parameters);
        }
        String sql;
        if (startNodeId!=null)
        {
            sql=preparedQuery.sql+preparedQuery.start+startNodeId;
        }
        else
        {
            sql=preparedQuery.sql;
        }
        RowSet rowSet;
        if (TEST)
        {
            System.out.println(sql);
        }
        if (parameters != null)
        {
            rowSet = accessor.executeQuery(parent, null,parameters, sql);
        }
        else
        {
            rowSet = accessor.executeQuery(parent, null, sql);
        }
        
        return new QueryResultSet(rowSet,preparedQuery.one,preparedQuery.map);
    }
    public QueryResultSet execute(Trace parent,String orderBy,Long startNodeId,Query query,Object...parameters) throws Throwable
    {
        return execute(parent,parameters,orderBy,startNodeId,query);
    }
    public QueryResultSet execute(Trace parent,long startNodeId,Query query,Object...parameters) throws Throwable
    {
        return execute(parent,parameters,null,startNodeId,query);
    }
    public QueryResultSet execute(Trace parent,Query query,Object...parameters) throws Throwable
    {
        return execute(parent,parameters,null,null,query);
    }
    
}
