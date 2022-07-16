package org.nova.sqldb.graph;

public class Node
{
    final private long id;
    final GraphAccess access;

    Node(GraphAccess access,long id)
    {
        this.id=id;
        this.access=access;
    }

    public long getNodeId()
    {
        return this.id;
    }

    public void put(NodeObject...entities) throws Throwable
    {
        long eventId=this.access.getEventId();
        for (NodeObject entity:entities)
        {
            this.access.put(entity,this.id,eventId);
        }
    }
    
//    public <ENTITY extends NodeEntity> ENTITY getEntity(Class<ENTITY> type) throws Throwable
//    {
//        return new NodeQuery(this.access,this.id).getEntity(type);
//    }
//
//    public EntityResult getEntities(Class<? extends NodeEntity>...types) throws Throwable
//    {
//        return new NodeQuery(this.access,this.id).getNodeResult(types);
//    }
    
    public Link linkTo(long toNodeId) throws Throwable
    {
        return this.access.link(this.id,toNodeId);
    }
    public Link linkFrom(long fromNodeId) throws Throwable
    {
        return this.access.link(fromNodeId,this.id);
    }
    public int deleteLinks(Class<? extends NodeAttribute> toType) throws Throwable
    {
        return this.access.deleteLinks(this.id,toType);
    }
    
    
    
    
}
