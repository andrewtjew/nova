package xp.nova.sqldb.graph;

import java.sql.Timestamp;

public class NodeObject extends GraphObject
{
    @GraphField
    protected Long _nodeId;
    public Long getNodeId()
    {
        return this._nodeId;
    }        
}
