package xp.nova.sqldb.graph;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.nova.sqldb.Row;
import org.nova.sqldb.Select;

import xp.nova.sqldb.graph.Graph.ColumnAccessor;

public class LinkNodeResult 
{
    final long linkId;
    final long fromNodeId;
    final long toNodeId;
    final GraphObject[] objects;
    Map<String,Integer> map;
    
    LinkNodeResult(long linkId,long fromNodeId,long toNodeId,GraphObject[] objects)
    {
        this.objects=objects;
        this.linkId=linkId;
        this.fromNodeId=fromNodeId;
        this.toNodeId=toNodeId;
    }
    
    void setMap(Map<String,Integer> map)
    {
        this.map=map;
    }
    
    @SuppressWarnings("unchecked")
    public <OBJECT extends NodeObject> OBJECT get(Class<OBJECT> type) throws Exception
    {
        Integer index=this.map.get(type.getSimpleName());
        if (index==null)
        {
            return null;
        }
        return (OBJECT) this.objects[index];
    }

    @SuppressWarnings("unchecked")
    public <ENTITY extends NodeObject> ENTITY get(int index) throws Exception
    {
        return (ENTITY) this.objects[index];
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
