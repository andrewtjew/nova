package xp.nova.sqldb.graph;

public class NodeObject extends GraphObject
{
    @GraphField
    protected Long _nodeId;
    public Long getNodeId()
    {
        return this._nodeId;
    }        
}
