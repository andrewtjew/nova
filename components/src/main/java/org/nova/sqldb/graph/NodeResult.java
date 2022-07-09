package org.nova.sqldb.graph;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.nova.sqldb.Row;
import org.nova.sqldb.Select;
import org.nova.sqldb.graph.Graph.ColumnAccessor;

public class NodeResult extends EntityResult
{
    final private long nodeId;
    
    NodeResult(long nodeId,Entity[] entities)
    {
        super(entities);
        this.nodeId=nodeId;
    }
    
    public long getNodeId()
    {
        return this.nodeId;
    }
}
