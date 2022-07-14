package org.nova.sqldb.graph;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.nova.sqldb.Row;
import org.nova.sqldb.Select;
import org.nova.sqldb.graph.Graph.ColumnAccessor;

public class NodeResult
{
    final private long nodeId;
    final private NodeObject[] objects;
    private Map<String,Integer> map;
    
    NodeResult(long nodeId,NodeObject[] objects)
    {
        this.objects=objects;
        this.nodeId=nodeId;
    }
    
    void setMap(Map<String,Integer> map)
    {
        this.map=map;
    }
    
    @SuppressWarnings("unchecked")
    public <ENTITY extends NodeAttribute> ENTITY get(Class<ENTITY> type) throws Exception
    {
        Integer index=this.map.get(type.getSimpleName());
        if (index==null)
        {
            throw new Exception();
        }
        return (ENTITY) this.objects[index];
    }

    @SuppressWarnings("unchecked")
    public <ENTITY extends NodeObject> ENTITY get(int index) throws Exception
    {
        return (ENTITY) this.objects[index];
    }

    
    public long getNodeId()
    {
        return this.nodeId;
    }
    
    public Node openNode(GraphAccess access) throws Throwable
    {
        return access.openNode(this.nodeId);
    }
    
}
