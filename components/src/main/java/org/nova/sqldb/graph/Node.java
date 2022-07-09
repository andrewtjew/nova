package org.nova.sqldb.graph;

public class Node
{
    final private long id;
    final GraphAccess graphEvent;

    Node(GraphAccess graphEvent,long id)
    {
        this.id=id;
        this.graphEvent=graphEvent;
    }

    public long getNodeId()
    {
        return this.id;
    }

    public void put(Entity...entities) throws Throwable
    {
        long eventId=this.graphEvent.getEventId();
        for (Entity entity:entities)
        {
            this.graphEvent.put(entity,this.id,eventId);
        }
    }
    
    public <ENTITY extends Entity> ENTITY getEntity(Class<ENTITY> type) throws Throwable
    {
        return new NodeQuery(this.graphEvent,this.id).getEntity(type);
    }

    public EntityResult getEntities(Class<? extends Entity>...types) throws Throwable
    {
        return new NodeQuery(this.graphEvent,this.id).getNodeResult(types);
    }
    
//    public void link(Node toNode) throws Throwable
//    {
//        throw new Exception();
//    }
    public void link(long toNodeId) throws Throwable
    {
        throw new Exception();
    }

    
    
}
