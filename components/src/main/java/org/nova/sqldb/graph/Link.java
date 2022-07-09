package org.nova.sqldb.graph;

public class Link<OBJECT extends Entity>
{
    final private long nodeId;
    final private long fromNodeId;
    final private long toNodeId;
    final private OBJECT toNodeObject;

    protected Link(long nodeId,long fromNodeId,long toNodeId,OBJECT toNodeObject)
    {
        this.nodeId=nodeId;
        this.fromNodeId=fromNodeId;
        this.toNodeId=toNodeId;
        this.toNodeObject=toNodeObject;
    }

    public OBJECT getToNodeObject()
    {
        return this.toNodeObject;
    }
    public long getId()
    {
        return this.nodeId;
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
