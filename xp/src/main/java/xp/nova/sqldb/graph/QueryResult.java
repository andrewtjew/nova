package xp.nova.sqldb.graph;

import java.util.Map;
import org.nova.sqldb.Row;

public class QueryResult
{
    final Row row;
    final Map<String,GraphObjectDescriptor> map;
    
    QueryResult(Row row,Map<String,GraphObjectDescriptor> map) throws Exception
    {
        this.map=map;
        this.row=row;
    }
    
    public <OBJECT extends NodeObject> OBJECT getNodeObject(String namespace,Class<OBJECT> type) throws Throwable
    {
        String typeName=namespace!=null?namespace+"."+type.getSimpleName():type.getSimpleName();
        GraphObjectDescriptor descriptor=this.map.get(typeName);
        if (descriptor==null)
        {
            throw new Exception("Type not in query: type="+type.getCanonicalName()+", namespace="+namespace);
        }

        Long nodeId = row.getNullableBIGINT(typeName + "._nodeId");
        if (nodeId==null)
        {
            return null;
        }
        NodeObject node = (NodeObject) type.getDeclaredConstructor().newInstance();
        for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
        {
            columnAccessor.set(node, typeName, row);
        }
        return (OBJECT)node;
    }

    public Long getNodeId(String namespace,Class<? extends NodeObject> type) throws Throwable
    {
        String typeName=namespace!=null?namespace+"."+type.getSimpleName():type.getSimpleName();
        GraphObjectDescriptor descriptor=this.map.get(typeName);
        if (descriptor==null)
        {
            throw new Exception("Type not in query: type="+type.getCanonicalName()+", namespace="+namespace);
        }

        Long nodeId = row.getNullableBIGINT(typeName + "._nodeId");
        if (nodeId==null)
        {
            return null;
        }
        return nodeId;
    }
    public Long getNodeId(Class<? extends NodeObject> type) throws Throwable
    {
        return getNodeId(null,type);
    }
    

    public <OBJECT extends NodeObject> OBJECT getNodeObject(Class<OBJECT> type) throws Throwable
    {
        return this.getNodeObject(null,type);
    }
    
    static public <OBJECT extends NodeObject> OBJECT getNodeObject(String namespace,Class<OBJECT> type,QueryResult result) throws Throwable
    {
        if (result==null)
        {
            return null;
        }
        return result.getNodeObject(namespace,type);
    }

    static public <OBJECT extends NodeObject> OBJECT getNodeObject(Class<OBJECT> type,QueryResult result) throws Throwable
    {
        return QueryResult.getNodeObject(null,type,result);
    }
}

