package xp.nova.sqldb.graph;

public class NodeObject
{
    @Internal
    protected Long _nodeId;

    @Internal
    protected Long _transactionId;
    public Long getNodeId()
    {
        return this._nodeId;
    }        
}
