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
    final private NodeAttribute[] entities;
    private Map<String,Integer> map;
    
    NodeResult(long nodeId,NodeAttribute[] entities)
    {
        this.entities=entities;
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
        return (ENTITY) this.entities[index];
    }

    @SuppressWarnings("unchecked")
    public <ENTITY extends NodeObject> ENTITY get(int index) throws Exception
    {
        return (ENTITY) this.entities[index];
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
