package org.nova.sqldb.graph;

public class Link
{
    final private long id;
    final private long fromNodeId;
    final private long toNodeId;
    final private GraphAccess access;

    protected Link(GraphAccess access,long id,long fromNodeId,long toNodeId)
    {
        this.access=access;
        this.id=id;
        this.fromNodeId=fromNodeId;
        this.toNodeId=toNodeId;
    }

    public long getLinkId()
    {
        return this.id;
    }
    public long getFromNodeId()
    {
        return this.fromNodeId;
    }
    public long getToNodeId()
    {
        return this.toNodeId;
    }
    
    public void delete()
    {
    }
}
