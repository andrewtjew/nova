//package org.nova.sqldb.graph;
//
//import java.sql.Timestamp;
//import java.util.HashMap;
//
//import org.nova.sqldb.Row;
//import org.nova.sqldb.Select;
//import org.nova.sqldb.graph.Graph.ColumnAccessor;
//
//public class NodeEventResult<OBJECT extends NodeObject>
//{
//    final OBJECT objects;
//    
//    final public long createdEventId;
//    final public Timestamp created;
//    final public long creatorId;
//    final public String source;
//    final public long nodeId;
//
//    NodeEventResult(long nodeId,long createdEventId,Timestamp created,long creatorId,String source,OBJECT[] objects)
//    {
//        this.nodeId=nodeId;
//        this.createdEventId=createdEventId;
//        this.created=created;
//        this.creatorId=creatorId;
//        this.source=source;
//        this.objects=objects;
//    }
//    public OBJECT[] objects()
//    {
//        return (OBJECT[]) objects;
//    }
//
//}
