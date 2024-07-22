package xp.nova.sqldb.graph;

import java.util.Map;
import java.util.Map.Entry;

import org.nova.sqldb.Row;

public class QueryResult
{
    final private Row row;
    final Map<String,GraphObjectDescriptor> map;
    
    QueryResult(Row row,Map<String,GraphObjectDescriptor> map) throws Exception
    {
        this.map=map;
        this.row=row;
    }
    
    public <OBJECT extends NodeObject> OBJECT get(String namespace,Class<OBJECT> type) throws Throwable
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
        NodeObject nodeObject = (NodeObject) type.newInstance();
        for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
        {
            columnAccessor.set(nodeObject, typeName, row);
        }
        return (OBJECT)nodeObject;
    }
    

    public <OBJECT extends NodeObject> OBJECT get(Class<OBJECT> type) throws Throwable
    {
        return this.get(null,type);
    }
    
    static public <OBJECT extends NodeObject> OBJECT get(String namespace,Class<OBJECT> type,QueryResult result) throws Throwable
    {
        if (result==null)
        {
            return null;
        }
        return result.get(namespace,type);
    }

    static public <OBJECT extends NodeObject> OBJECT get(Class<OBJECT> type,QueryResult result) throws Throwable
    {
        return QueryResult.get(null,type,result);
    }
}

