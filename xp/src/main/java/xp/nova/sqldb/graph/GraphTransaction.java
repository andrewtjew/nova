package xp.nova.sqldb.graph;

import java.sql.Timestamp;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Insert;
import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.sqldb.SqlUtils;
import org.nova.sqldb.Transaction;
import org.nova.testing.Testing;
import org.nova.tracing.Trace;

public class GraphTransaction implements AutoCloseable
{
    final private long creatorId;
    final private String source;
    final Trace parent;
    final private GraphAccessor graphAccessor;
    final private Accessor accessor;
    final private Transaction transaction;
    private Long eventId;
    final private Graph graph;
    final private boolean autoCloseGraphAccessor;

    GraphTransaction(Trace parent,GraphAccessor graphAccessor,String source,long creatorId,boolean autoCloseGraphAccessor) throws Throwable
    {
        this.autoCloseGraphAccessor=autoCloseGraphAccessor;
        this.source=source;
        this.graph=graphAccessor.graph;
        this.parent=parent;
        this.creatorId=creatorId;
        this.graphAccessor=graphAccessor;
        this.accessor=graphAccessor.accessor;
        this.transaction=this.accessor.beginTransaction("GraphTransaction"+":"+creatorId+":"+source);
    }
    
    public synchronized long getEventId() throws Throwable
    {
        if (this.eventId==null)
        {
            Timestamp created=SqlUtils.now();
            this.eventId=this.accessor.executeUpdateAndReturnGeneratedKeys(parent,null
                    ,"INSERT INTO _event (created,creatorId,source) VALUES(?,?,?)"
                    ,created,this.creatorId,this.source
                    ).getAsLong(0);
        }
        return this.eventId;
    }
    
    public GraphAccessor getGraphAccessor()
    {
        return this.graphAccessor;
    }
    @Override
    public void close() throws Exception
    {
        this.transaction.close();
        if (this.autoCloseGraphAccessor)
        {
            this.graphAccessor.close();
        }
    }

    public long createNode(NodeObject...objects) throws Throwable
    {
        long nodeId=Insert.table("_node").value("eventId",this.getEventId()).executeAndReturnLongKey(parent, this.accessor);
        put(nodeId,objects);
        return nodeId;
    }

    public void put(long nodeId,NodeObject...objects) throws Throwable
    {
        long eventId=this.getEventId();
        for (NodeObject object:objects)
        {
            put(object,nodeId,eventId);
        }
    }

    public void put(NodeObject...objects) throws Throwable
    {
        if (objects.length>0)
        {
            Long nodeId=objects[0]._nodeId;
            if (nodeId==null)
            {
                throw new Exception();
            }
            put(nodeId,objects);
        }
    }
    
    void put(NodeObject object,long nodeId,long eventId) throws Throwable
    {
        object._nodeId=nodeId;
        Class<? extends NodeObject> type=object.getClass();
        Meta meta=this.graph.getMeta(type);
        String table=meta.getTableName();
        ColumnAccessor[] columnAccessors=meta.getColumnAccessors();

        StringBuilder insert=new StringBuilder();
        StringBuilder update=new StringBuilder();
        StringBuilder values=new StringBuilder();

        Object[] parameters=new Object[columnAccessors.length*2+1];
        int insertIndex=0;
        int updateIndex=columnAccessors.length+1;
        parameters[insertIndex++]=nodeId;
        parameters[insertIndex++]=eventId;
        parameters[updateIndex++]=eventId;

        insert.append("_nodeId,_eventId");
        values.append("?,?");
        update.append("_eventId=?");
        
        for (ColumnAccessor columnAccessor:columnAccessors)
        {
            if (columnAccessor.isGraphfield())
            {
                continue;
            }
            String name=columnAccessor.getName();
            Object value=columnAccessor.get(object);
            insert.append(',');
            insert.append('`'+name+'`');
            values.append(",?");
            update.append(",`"+name+"`=?");
            parameters[insertIndex++]=value;
            parameters[updateIndex++]=value;
        }
        
        String sql="INSERT INTO "+table+"("+insert+") VALUES ("+values+") ON DUPLICATE KEY UPDATE "+update;
        if (Graph.TEST)
        {
            Testing.log(sql);
        }
        accessor.executeUpdate(parent,null,sql,parameters);
    }


    public void link(long fromNodeId,Relation relation,long toNodeId) throws Throwable
    {
        String typeName=relation.getClass().getSimpleName();
        int value=relation.getValue();
        if (deleteLink(fromNodeId,relation,toNodeId)>1)
        {
            throw new Exception();
        }
        Insert.table("_link").value("fromNodeId",fromNodeId).value("toNodeId", toNodeId).value("eventId",this.getEventId())
                .value("type", typeName).value("relation", value)
                .executeAndReturnLongKey(parent, this.accessor);
    }
//    public void link(long fromNodeId,long toNodeId) throws Throwable
//    {
//        if (this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND toNodeId=? AND type IS NULL AND relation=0",fromNodeId,toNodeId)>1)
//        {
//            throw new Exception();
//        }
//        Insert.table("_link").value("fromNodeId",fromNodeId).value("toNodeId", toNodeId).value("eventId",this.getEventId())
//                .value("relation", 0)
//                .executeAndReturnLongKey(parent, this.accessor);
//    }
    
    public int deleteLinks(Direction direction,long nodeId) throws Throwable
    {
        if (direction==Direction.FROM)
        {
            return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=?",nodeId);
        }
        else
        {
            return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE toNodeId=?",nodeId);
        }
    }

//    public int deleteLinks(Direction direction,long nodeId,Class<? extends NodeObject> type) throws Throwable
//    {
//        String on=direction==Direction.FROM?" ON _link.fromNodeId=":" ON _link.toNodeId=";
//        Meta meta=this.graph.getMeta(type);
//        String table=meta.getTableName();
//        
//        RowSet rowSet=this.accessor.executeQuery(parent, null,
//                "SELECT _link.id FROM _link JOIN "+table+on+table+"._nodeId");
//        int deleted=0;
//        for (Row row:rowSet.rows())
//        {
//            deleted+=this.accessor.executeUpdate(this.parent,null
//                ,"DELETE FROM _link where id="+row.getBIGINT(0));
//        }
//        return deleted;
//    }

    public int deleteLinks(long nodeId,Direction direction,Relation relation,Class<? extends NodeObject> type) throws Throwable
    {
        String relationTypeName=relation.getClass().getSimpleName();
        String on=direction==Direction.FROM?" ON _link.fromNodeId=":" ON _link.toNodeId=";
        Meta meta=this.graph.getMeta(type);
        String table=meta.getTableName();
        
        RowSet rowSet=this.accessor.executeQuery(parent, null,
                "SELECT _link.id FROM _link JOIN "+table+on+table+"._nodeId WHERE _link.type=? AND _link.relation=?",relationTypeName,relation.getValue());
        int deleted=0;
        for (Row row:rowSet.rows())
        {
            deleted+=this.accessor.executeUpdate(this.parent,null
                ,"DELETE FROM _link where id="+row.getBIGINT(0));
        }
        return deleted;
    }

    public int deleteLinks(long fromNodeId,Relation relation) throws Throwable
    {
        String typeName=relation.getClass().getSimpleName();
        return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND type=? and relation=?",fromNodeId,typeName,relation.getValue());
    }

    public int deleteLink(long fromNodeId,Relation relation,long toNodeId) throws Throwable
    {
        String typeName=relation.getClass().getSimpleName();
        return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND toNodeId=? AND type=? and relation=?",fromNodeId,toNodeId,typeName,relation.getValue());
    }

    public int deleteLinks(long fromNodeId,long toNodeId) throws Throwable
    {
        return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND toNodeId=?",fromNodeId,toNodeId);
    }

    public void deleteNode(long nodeId) throws Throwable
    {
        int deleted=this.accessor.executeUpdate(this.parent,null,"DELETE FROM _node WHERE id=?",nodeId);
        this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=?",nodeId);
        this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE toNodeId=?",nodeId);
    }
    
    public void commit() throws Throwable
    {
        this.transaction.commit();
    }
    public void rollback() throws Throwable
    {
        this.transaction.rollback();
    }
    
    
}
