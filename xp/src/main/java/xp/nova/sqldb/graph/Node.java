package xp.nova.sqldb.graph;

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

    public void put(NodeObject...objects) throws Throwable
    {
        long eventId=this.access.getEventId();
        for (NodeObject entity:objects)
        {
            this.access.put(entity,this.id,eventId);
        }
    }
    public Link linkTo(long toNodeId) throws Throwable
    {
        return this.access.link(this.id,toNodeId);
    }
    public Link linkTo(Node node) throws Throwable
    {
        return this.access.link(this.id,node.getNodeId());
    }
    public Link linkFrom(long fromNodeId) throws Throwable
    {
        return this.access.link(fromNodeId,this.id);
    }
    public int deleteLinks(Class<? extends NodeObject> toType) throws Throwable
    {
        return this.access.deleteLinks(this.id,toType);
    }
    public boolean delete() throws Throwable
    {
        return this.access.deleteNode(this.id);
    }
    
    
    
    
}