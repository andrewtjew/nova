package org.nova.sqldb.graph;

public class NodeObject extends GraphObject
{
    @GraphField
    Long _nodeId;
    public Long getNodeId()
    {
        return this._nodeId;
    }    
}
