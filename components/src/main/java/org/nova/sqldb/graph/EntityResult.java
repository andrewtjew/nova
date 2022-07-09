package org.nova.sqldb.graph;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.nova.sqldb.Row;
import org.nova.sqldb.Select;
import org.nova.sqldb.graph.Graph.ColumnAccessor;

public class EntityResult 
{
    final private Entity[] entities;
    private Map<String,Integer> map;
    
    EntityResult(Entity[] entities)
    {
        this.entities=entities;
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

}
