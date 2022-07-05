package org.nova.sqldb.graph;

import java.sql.Timestamp;
import java.util.HashMap;

import org.nova.sqldb.Row;
import org.nova.sqldb.Select;
import org.nova.sqldb.graph.Graph.ColumnAccessor;

public class NodeResult2 
{
    final private HashMap<String, NodeObject> objects;
    
    final public long createdEventId;
    final public Timestamp created;
    final public long creatorId;
    final public String source;
    final public long nodeId;

    NodeResult2(long nodeId,long createdEventId,Timestamp created,long creatorId,String source)
    {
        this.nodeId=nodeId;
        this.createdEventId=createdEventId;
        this.created=created;
        this.creatorId=creatorId;
        this.source=source;
        this.objects=new HashMap<String, NodeObject>();
    }
    void put(String typeName,NodeObject object)
    {
        this.objects.put(typeName,object);
    }
    public <OBJECT extends NodeObject> OBJECT get(Class<? extends NodeObject> type) throws Throwable
    {
        String typeName=type.getSimpleName();
        return (OBJECT)objects.get(typeName);
    }

}
