package org.nova.sqldb.graph;

import java.sql.Timestamp;

public class NodeEntity extends NodeObject
{
    @GraphField
    Long _nodeId;
    public Long getNodeId()
    {
        return this._nodeId;
    }
}
