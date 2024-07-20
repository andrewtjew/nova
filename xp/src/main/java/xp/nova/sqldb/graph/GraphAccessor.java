package xp.nova.sqldb.graph;

import java.lang.reflect.Array;

import org.apache.commons.lang3.NotImplementedException;
import org.nova.html.tags.pre;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.testing.Debugging;
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
    public QueryResultSet execute(Trace parent,Object[] parameters,Long startNodeId,Query query) throws Throwable
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
        if (preparedQuery.limit!=null)
        {
            sb.append(preparedQuery.limit);
        }
        String sql=sb.toString();
        RowSet rowSet;
        if (Graph.DEBUG)
        {
            Debugging.log("Graph",sql);
        }
        if (parameters != null)
        {
            rowSet = accessor.executeQuery(parent, null,parameters, sql);
        }
        else
        {
            rowSet = accessor.executeQuery(parent, null, sql);
        }
        
        return new QueryResultSet(rowSet,preparedQuery.typeDescriptorMap);
    }
    public QueryResultSet execute(Trace parent,long startNodeId,Query query,Object...parameters) throws Throwable
    {
        return execute(parent,parameters,startNodeId,query);
    }
    public QueryResultSet execute(Trace parent,NodeObject startNodeObject,Query query,Object...parameters) throws Throwable
    {
        PreparedQuery preparedQuery=query.build(this.graph);
//        if (preparedQuery.startType!=startNodeObject.getClass())
//        {
//            throw new Exception("Expected="+preparedQuery.startType.getName()+", actual="+startNodeObject.getClass().getName());
//        }
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
    
    public <ELEMENT extends NodeObject> ELEMENT[] getArrayElements(Trace parent,NodeObject arrayObject,Class<? extends NodeObject> elementType) throws Throwable
    {
        Long arrayNodeId=arrayObject.getNodeId();
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        String typeName=elementType.getSimpleName();
        String sql="SELECT "+typeName+".*,_array.index as _index FROM _array JOIN "+typeName+" ON _array.elementId="+typeName+"._nodeId WHERE _array.nodeId=?";
        RowSet rowSet=this.accessor.executeQuery(parent, null, sql,arrayNodeId);
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
        GraphObjectDescriptor descriptor=this.graph.register(elementType);
        Object array=Array.newInstance(elementType,largestArrayIndex+1);
        for (int i=0;i<rowSet.size();i++)
        {
            Row row=rowSet.getRow(i);
            int index=row.getINTEGER("_index");
            NodeObject element = (NodeObject)elementType.newInstance();
            for (FieldDescriptor columnAccessor : descriptor.getColumnAccessors())
            {
                columnAccessor.set(element, typeName, row);
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
        else if (rowSet.size()>1)
        {
            throw new NotImplementedException();
        }
        GraphObjectDescriptor descriptor=this.graph.register(elementType);
        Row row=rowSet.getRow(0);
        NodeObject element = (NodeObject)elementType.newInstance();
        for (FieldDescriptor columnAccessor : descriptor.getColumnAccessors())
        {
            columnAccessor.set(element, typeName, row);
        }
        return (ELEMENT)element;
    }


    
}
