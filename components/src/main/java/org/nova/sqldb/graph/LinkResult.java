package org.nova.sqldb.graph;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.nova.sqldb.Row;
import org.nova.sqldb.Select;
import org.nova.sqldb.graph.Graph.ColumnAccessor;

public class LinkResult 
{
    final private long linkId;
    final private long fromNodeId;
    final private long toNodeId;
    final private Entity[] entities;
    private Map<String,Integer> map;
    
    LinkResult(long linkId,long fromNodeId,long toNodeId,Entity[] entities)
    {
        this.entities=entities;
        this.linkId=linkId;
        this.fromNodeId=fromNodeId;
        this.toNodeId=toNodeId;
    }
    
    void setMap(Map<String,Integer> map)
    {
        this.map=map;
    }
    
    @SuppressWarnings("unchecked")
    public <ENTITY extends Entity> ENTITY get(Class<ENTITY> type) throws Exception
    {
        Integer index=this.map.get(type.getSimpleName());
        if (index==null)
        {
            throw new Exception();
        }
        return (ENTITY) this.entities[index];
    }

    @SuppressWarnings("unchecked")
    public <ENTITY extends Entity> ENTITY get(int index) throws Exception
    {
        return (ENTITY) this.entities[index];
    }
    
    public long getLinkId()
    {
        return this.getLinkId();
    }
    
    public long getFromNodeId()
    {
        return this.fromNodeId;
    }
    public long getToNodeId()
    {
        return this.toNodeId;
    }
}
