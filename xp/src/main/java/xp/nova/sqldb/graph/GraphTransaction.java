package xp.nova.sqldb.graph;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;

import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Delete;
import org.nova.sqldb.Insert;
import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.sqldb.Select;
import org.nova.sqldb.SqlUtils;
import org.nova.sqldb.Transaction;
import org.nova.sqldb.Update;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

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
            insert.append(name);
            values.append(",?");
            update.append(','+name+"=?");
            parameters[insertIndex++]=value;
            parameters[updateIndex++]=value;
        }
        
        String sql="INSERT INTO "+table+"("+insert+") VALUES ("+values+") ON DUPLICATE KEY UPDATE "+update;
        accessor.executeUpdate(parent,null,sql,parameters);
    }


    public void link(long fromNodeId,long toNodeId,Relation relation) throws Throwable
    {
        String typeName=relation.getClass().getSimpleName();
        int value=relation.getValue();
        if (deleteLink(fromNodeId,toNodeId,relation)>1)
        {
            throw new Exception();
        }
        Insert.table("_link").value("fromNodeId",fromNodeId).value("toNodeId", toNodeId).value("eventId",this.getEventId())
                .value("type", typeName).value("relation", value)
                .executeAndReturnLongKey(parent, this.accessor);
    }
    public void link(long fromNodeId,long toNodeId) throws Throwable
    {
        if (this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND toNodeId=? AND type IS NULL AND relation=0",fromNodeId,toNodeId)>1)
        {
            throw new Exception();
        }
        Insert.table("_link").value("fromNodeId",fromNodeId).value("toNodeId", toNodeId).value("eventId",this.getEventId())
                .value("relation", 0)
                .executeAndReturnLongKey(parent, this.accessor);
    }
    
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

    public int deleteLinks(Direction direction,long nodeId,Class<? extends NodeObject> type) throws Throwable
    {
        String on=direction==Direction.FROM?" ON _link.fromNodeId=":" ON _link.toNodeId=";
        Meta meta=this.graph.getMeta(type);
        String table=meta.getTableName();
        
        RowSet rowSet=this.accessor.executeQuery(parent, null,
                "SELECT _link.id FROM _link JOIN "+table+on+table+"._nodeId");
        int deleted=0;
        for (Row row:rowSet.rows())
        {
            deleted+=this.accessor.executeUpdate(this.parent,null
                ,"DELETE FROM _link where id="+row.getBIGINT(0));
        }
        return deleted;
    }

    public int deleteLinks(Class<? extends Relation> relationType,Direction direction,long nodeId,Class<? extends NodeObject> type) throws Throwable
    {
        String relationTypeName=relationType.getSimpleName();
        String on=direction==Direction.FROM?" ON _link.fromNodeId=":" ON _link.toNodeId=";
        Meta meta=this.graph.getMeta(type);
        String table=meta.getTableName();
        
        RowSet rowSet=this.accessor.executeQuery(parent, null,
                "SELECT _link.id FROM _link JOIN "+table+on+table+"._nodeId WHERE _link.type=?",relationTypeName);
        int deleted=0;
        for (Row row:rowSet.rows())
        {
            deleted+=this.accessor.executeUpdate(this.parent,null
                ,"DELETE FROM _link where id="+row.getBIGINT(0));
        }
        return deleted;
    }

    public int deleteLinks(Class<? extends Relation> type,Direction direction,long nodeId) throws Throwable
    {
        String typeName=type.getSimpleName();
        if (direction==Direction.FROM)
        {
            return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND type=?",nodeId,typeName);
        }
        return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE toNodeId=? AND type=?",nodeId,typeName);
    }
    
    public int deleteLink(long fromNodeId,long toNodeId,Relation relation) throws Throwable
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
        this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=?",nodeId);
        this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE toNodeId=?",nodeId);
        this.accessor.executeUpdate(this.parent,null,"DELETE FROM _node WHERE id=?",nodeId);
    }
    
    public void commit() throws Throwable
    {
        if (this.eventId!=null)
        {
            this.transaction.commit();
        }
    }
    public void rollback() throws Throwable
    {
        if (this.eventId!=null)
        {
            this.transaction.rollback();
        }
    }
    
//    public Link openLink(long linkId) throws Throwable
//    {
//        RowSet rowSet=this.accessor.executeQuery(parent, null
//                ,"SELECT fromNodeId,toNodeId FROM _link WHERE id=?"
//                ,linkId);
//        int size=rowSet.size();
//        if (size==0)
//        {
//            return null;
//        }
//        else if (size>1)
//        {
//            throw new Exception();
//        }
//        Row row=rowSet.getRow(0);
//        return new Link(this,linkId,row.getBIGINT(0),row.getBIGINT(1));
//    }
//    public Link openLink(long fromNodeId,long toNodeId) throws Throwable
//    {
//        RowSet rowSet=this.accessor.executeQuery(parent, null
//                ,"SELECT id FROM _link WHERE fromNodeId=? AND toNodeId=?"
//                ,fromNodeId,toNodeId);
//        int size=rowSet.size();
//        if (size==0)
//        {
//            return null;
//        }
//        else if (size>1)
//        {
//            throw new Exception();
//        }
//        Row row=rowSet.getRow(0);
//        return new Link(this,row.getBIGINT(0),fromNodeId,toNodeId);
//    }
    
    
}
