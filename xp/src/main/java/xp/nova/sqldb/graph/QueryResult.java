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
    
//    public Long getNodeId() throws Exception
//    {
//        for (String typeName:this.map.keySet())
//        {
//            Long nodeId = row.getNullableBIGINT(typeName + "._nodeId");
//            return nodeId;
//        }
//        return null;
//    }
    
    public <OBJECT extends NodeObject> OBJECT get(String namespace,Class<OBJECT> type) throws Throwable
    {
        String typeName=namespace!=null?namespace+"."+type.getSimpleName():type.getSimpleName();
        GraphObjectDescriptor descriptor=this.map.get(typeName);
        if (descriptor==null)
        {
            throw new Exception();
        }

        Long nodeId = row.getNullableBIGINT(typeName + "._nodeId");
        if (nodeId==null)
        {
            return null;
        }
        NodeObject nodeObject = (NodeObject) type.newInstance();
        for (FieldDescriptor columnAccessor : descriptor.getColumnAccessors())
        {
            columnAccessor.set(nodeObject, typeName, row);
        }
        return (OBJECT)nodeObject;
    }
    

    public <OBJECT extends NodeObject> OBJECT get(Class<OBJECT> type) throws Throwable
    {
        return get(null,type);
    }
}

