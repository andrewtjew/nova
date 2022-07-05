package org.nova.sqldb.graph;

import java.sql.Timestamp;

public class NodeObject extends GraphObject
{
    long _createdEventId=Long.MIN_VALUE;
    long _nodeId=Long.MIN_VALUE;
    Long _id=null;
    
    public long getCreatedEventId()
    {
        return this._createdEventId;
    }
    public long getNodeId()
    {
        return this._nodeId;
    }
    public long getId()
    {
        return this._id;
    }
}
