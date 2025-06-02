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
    
    public <OBJECT extends Node> OBJECT getObject(String namespace,Class<OBJECT> type) throws Throwable
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
        Node node = (Node) type.getDeclaredConstructor().newInstance();
        for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
        {
            columnAccessor.set(node, typeName, row);
        }
        return (OBJECT)node;
    }
    

    public <OBJECT extends Node> OBJECT getObject(Class<OBJECT> type) throws Throwable
    {
        return this.getObject(null,type);
    }
    
    static public <OBJECT extends Node> OBJECT getObject(String namespace,Class<OBJECT> type,QueryResult result) throws Throwable
    {
        if (result==null)
        {
            return null;
        }
        return result.getObject(namespace,type);
    }

    static public <OBJECT extends Node> OBJECT getObject(Class<OBJECT> type,QueryResult result) throws Throwable
    {
        return QueryResult.getObject(null,type,result);
    }
}

