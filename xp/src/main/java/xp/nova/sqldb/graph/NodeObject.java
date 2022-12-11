package xp.nova.sqldb.graph;

public class NodeObject extends GraphObject
{
    @InternalField
    protected Long _nodeId;
    public Long getNodeId()
    {
        return this._nodeId;
    }        
}
