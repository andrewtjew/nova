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
    
    public GraphTransaction beginTransaction(Trace parent,String source,long creatorId,boolean autoCloseGraphAccessor) throws Throwable
    {
        return new GraphTransaction(parent, this, source, creatorId,autoCloseGraphAccessor);
    }
    public GraphTransaction beginTransaction(Trace parent,String source,Long creatorId) throws Throwable
    {
        return beginTransaction(parent,source,creatorId,false);
    }
    
    public long getCount(Trace parent,Class<? extends NodeObject> type,String where,Object...parameters) throws Throwable
    {
//        GraphObjectDescriptor descriptor=this.graph.register(type);
        GraphObjectDescriptor descriptor=this.graph.getGraphObjectDescriptorMap().get(type.getSimpleName());
        String table=descriptor.getTableName();
        return accessor.executeQuery(parent,null,"SELECT count(*) FROM "+table+" WHERE "+where,parameters).getRow(0).getBIGINT(0);
    }
    
    static final private boolean TEST=true;
    
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
        StringBuilder sb=new StringBuilder(preparedQuery.sql);
        if (startNodeId!=null)
        {
            sb.append(preparedQuery.start+startNodeId);
        }
        if (preparedQuery.orderBy!=null)
        {
            sb.append(preparedQuery.orderBy);
        }
        String sql=sb.toString();
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
    public Accessor getAccessor()
    {
        return this.accessor;
    }
    public long getCount(Trace parent,Object[] parameters,Long startNodeId,Query query) throws Throwable
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
        StringBuilder sb=new StringBuilder(preparedQuery.countSql);
        if (startNodeId!=null)
        {
            sb.append(preparedQuery.start+startNodeId);
        }
        String sql=sb.toString();
        RowSet rowSet;
        if (TEST)
        {
            System.out.println("GraphAcessor.getCount:sql");
        }
        if (parameters != null)
        {
            rowSet = accessor.executeQuery(parent, null,parameters, sql);
        }
        else
        {
            rowSet = accessor.executeQuery(parent, null, sql);
        }
        return rowSet.getRow(0).getBIGINT(0);
    }
    public long getCount(Trace parent,long startNodeId,Query query,Object...parameters) throws Throwable
    {
        return getCount(parent,parameters,startNodeId,query);
    }
    public long getCount(Trace parent,Query query,Object...parameters) throws Throwable
    {
        return getCount(parent,parameters,null,query);
    }
    
    
    
}
