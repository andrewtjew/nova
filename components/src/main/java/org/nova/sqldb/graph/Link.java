package org.nova.sqldb.graph;

public class Link extends GraphObject
{
    final private long fromNodeId;
    final private long toNodeId;

    public Link(long fromNodeId,long toNodeId)
    {
        this.fromNodeId=fromNodeId;
        this.toNodeId=toNodeId;
    }
    
}
