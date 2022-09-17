package xp.nova.sqldb.graph;

import java.util.Map;

import org.nova.sqldb.Row;

import xp.nova.sqldb.graph.Graph.ColumnAccessor;
import xp.nova.sqldb.graph.Graph.Meta;

public class QueryResult
{
    final private Row row;
    final Map<String,Meta> map;
    
    QueryResult(Row row,Map<String,Meta> map)
    {
        this.map=map;
        this.row=row;
    }
    
    public <OBJECT extends GraphObject> OBJECT get(String namespace,Class<OBJECT> type) throws Throwable
    {
        String typeName=namespace!=null?namespace+"."+type.getSimpleName():type.getSimpleName();
        Meta meta=this.map.get(typeName);
        if (meta==null)
        {
            return null;
        }

        Long nodeId = row.getNullableBIGINT(typeName + "._nodeId");
        if (nodeId==null)
        {
            return null;
        }
        NodeObject nodeObject = (NodeObject) type.newInstance();
        for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
        {
            columnAccessor.set(nodeObject, typeName, row);
        }
        return (OBJECT)nodeObject;
    }

    public <OBJECT extends GraphObject> OBJECT get(Class<OBJECT> type) throws Throwable
    {
        return get(null,type);
    }
}

