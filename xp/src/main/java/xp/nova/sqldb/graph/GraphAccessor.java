package xp.nova.sqldb.graph;

import java.lang.reflect.Array;

import org.nova.sqldb.Accessor;
import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.testing.Debugging;
import org.nova.testing.LogLevel;
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
    
    public long getCount(Trace parent,Class<? extends NodeObject> type,String where,Object...parameters) throws Throwable
    {
        GraphObjectDescriptor descriptor=this.graph.getGraphObjectDescriptorMap().get(type.getSimpleName());
        String table=descriptor.getTableName();
        return accessor.executeQuery(parent,null,"SELECT count(*) FROM "+table+" WHERE "+where,parameters).getRow(0).getBIGINT(0);
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
    
    
    private void debugPrint(QueryResultSet set)
    {
        if (set==null)
        {
            Debugging.log("null set");
            return;
        }
        for (int i=0;i<set.results.length;i++)
        {
            var result=set.results[i];
            StringBuilder sb=new StringBuilder();
            sb.append(i);
            int seperator='[';
            for (Object item:result.row.getObjects())
            {
                sb.append(seperator+(item==null?"null":item.toString()));
                seperator=',';
            }
            sb.append(']');
            Debugging.log(sb.toString());
        }
        
    }
    
    public QueryResultSet execute(Trace parent,Object[] parameters,Long startNodeId,Query query) throws Throwable
    {
        PreparedQuery preparedQuery=query.build(this.graph);
        translateParameters(parameters);
        QueryKey key=new QueryKey(startNodeId, preparedQuery, parameters);
        var valueSize=this.graph.getFromCache(key);
        if (valueSize!=null)
        {
            if (Debugging.ENABLE && Graph.DEBUG)
            {
                QueryResultSet queryResultSet=execute(parent, preparedQuery,parameters,startNodeId,query);
                QueryResultSet cachedResultSet=valueSize.value();
                if (queryResultSet.equals(cachedResultSet)==false)
                {
                    debugPrint(queryResultSet);
                    debugPrint(cachedResultSet);
                    throw new Exception();
                }
                if (Graph.DEBUG_CACHING)
                {
                    Debugging.log("Graph","cache hit");
                }
            }
            
            return valueSize.value();
        }
        try (Trace trace=new Trace(parent,"GraphAccessor.execute"))
        {
            QueryResultSet queryResultSet=execute(trace, preparedQuery,parameters,startNodeId,query);
            if (Debugging.ENABLE && Graph.DEBUG_CACHING)
            {
                Debugging.log("Graph","cache miss",LogLevel.WARNING);
            }
            trace.close();
            long duration=trace.getDurationMs();
            this.graph.updateCache(parent, key, queryResultSet,duration);
            return queryResultSet;
        }
    }        
        
    public QueryResultSet execute(Trace parent,PreparedQuery preparedQuery,Object[] parameters,Long startNodeId,Query query) throws Throwable
    {
        StringBuilder sb=new StringBuilder(preparedQuery.sql);
        if (startNodeId!=null)
        {
            sb.append(preparedQuery.start+startNodeId);
        }
        String sql=sb.toString();
        RowSet rowSet;
        if (Debugging.ENABLE && Graph.DEBUG_QUERY)
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
            Debugging.log("GraphAccessor",sql);
        }
        if (parameters != null)
        {
            rowSet = accessor.executeQuery(parent, null,parameters, sql);
        }
        else
        {
            rowSet = accessor.executeQuery(parent, null, sql);
        }
        if (Debugging.ENABLE && Graph.DEBUG_QUERY)
        {
            Debugging.log("GraphAccessor",rowSet.size());
        }
        
        return new QueryResultSet(rowSet,preparedQuery.typeDescriptorMap);
    }
    public QueryResultSet execute(Trace parent,long startNodeId,Query query,Object...parameters) throws Throwable
    {
        return execute(parent,parameters,startNodeId,query);
    }
    public QueryResultSet execute(Trace parent,NodeObject startNodeObject,Query query,Object...parameters) throws Throwable
    {
        return execute(parent,parameters,startNodeObject.getNodeId(),query);
    }
    public QueryResultSet execute(Trace parent,Query query,Object...parameters) throws Throwable
    {
        return execute(parent,parameters,null,query);
    }
    
    public Accessor getAccessor()
    {
        return this.accessor;
    }

    public QueryResultSet execute(Trace parent,PreparedQuery preparedQuery,Object[] parameters,Long startNodeId,ArrayQuery query) throws Throwable
    {
        StringBuilder sb=new StringBuilder(preparedQuery.sql);
        if (startNodeId!=null)
        {
            sb.append(preparedQuery.start+startNodeId);
        }
        String sql=sb.toString();
        RowSet rowSet;
        if (Debugging.ENABLE && Graph.DEBUG_QUERY)
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
            Debugging.log("GraphAccessor",sql);
        }
        if (parameters != null)
        {
            rowSet = accessor.executeQuery(parent, null,parameters, sql);
        }
        else
        {
            rowSet = accessor.executeQuery(parent, null, sql);
        }
        if (Debugging.ENABLE && Graph.DEBUG_QUERY)
        {
            Debugging.log("GraphAccessor",rowSet.size());
        }
        
        return new QueryResultSet(rowSet,preparedQuery.typeDescriptorMap);
    }
    
    
    public <ELEMENT extends NodeObject> ELEMENT[] getArray(Trace parent,NodeObject arrayObject,Class<? extends NodeObject> elementType) throws Throwable
    {
        Long arrayNodeId=arrayObject.getNodeId();
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        String typeName=elementType.getSimpleName();
        String sql="SELECT "+typeName+".*,_array.index as _index FROM _array JOIN "+typeName+" ON _array.elementId="+typeName+"._nodeId WHERE _array.nodeId=?";
        RowSet rowSet=this.accessor.executeQuery(parent, null, sql,arrayNodeId);
        if (rowSet.size()==0)
        {
            return null;
        }
        int largestArrayIndex=0;
        for (int i=0;i<rowSet.size();i++)
        {
            Row row=rowSet.getRow(i);
            int index=row.getINTEGER("_index");
            if (index>largestArrayIndex)
            {
                largestArrayIndex=index;
            }
        }
        GraphObjectDescriptor descriptor=this.graph.getGraphObjectDescriptor(elementType);
        Object array=Array.newInstance(elementType,largestArrayIndex+1);
        for (int i=0;i<rowSet.size();i++)
        {
            Row row=rowSet.getRow(i);
            int index=row.getINTEGER("_index");
            NodeObject element = (NodeObject)elementType.getDeclaredConstructor().newInstance();
            for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
            {
                columnAccessor.set(element, null, row);
            }
            Array.set(array, index, element);
        }
        return (ELEMENT[]) array;
    }

    public <ELEMENT extends NodeObject> ELEMENT getArrayElement(Trace parent,NodeObject arrayObject,Class<? extends NodeObject> elementType,int index) throws Throwable
    {
        Long arrayNodeId=arrayObject.getNodeId();
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        String typeName=elementType.getSimpleName();
        String sql="SELECT "+typeName+".*,_array.index as _index FROM _array JOIN "+typeName+" ON _array.elementId="+typeName+"._nodeId WHERE _array.nodeId=? AND _array.index=?";
        RowSet rowSet=this.accessor.executeQuery(parent, null, sql,arrayNodeId,index);
        if (rowSet.size()==0)
        {
            return null;
        }
        GraphObjectDescriptor descriptor=this.graph.getGraphObjectDescriptor(elementType);
        Row row=rowSet.getRow(0);
        NodeObject element = (NodeObject)elementType.getDeclaredConstructor().newInstance();
        for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
        {
            columnAccessor.set(element, null, row);
        }
        return (ELEMENT)element;
    }


    
}
