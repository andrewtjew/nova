package org.nova.sqldb.graph;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;

import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Insert;
import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.sqldb.Select;
import org.nova.sqldb.SqlUtils;
import org.nova.sqldb.Transaction;
import org.nova.sqldb.Update;
import org.nova.sqldb.graph.Graph.ColumnAccessor;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;

public class GraphAccess implements AutoCloseable
{
    final private Long creatorId;
    final private String source;
    final Graph graph;
    final Trace parent;
    final private Accessor accessor;
    final private Transaction transaction;
    private Long eventId;
    
    GraphAccess(Trace parent,Graph graph,String source,Long creatorId,boolean beginTransaction) throws Throwable
    {
        this.graph=graph;
        this.source=source;
        this.parent=parent;
        this.accessor=this.graph.getConnector().openAccessor(parent);
        this.creatorId=creatorId;
        if (beginTransaction)
        {
            this.transaction=this.accessor.beginTransaction("GraphAccess");
        }
        else
        {
            this.transaction=null;
        }
    }
    
    public synchronized long getEventId() throws Throwable
    {
        if (this.eventId==null)
        {
            if (this.creatorId==null)
            {
                throw new Exception();
            }
            Timestamp created=SqlUtils.now();
            this.eventId=this.accessor.executeUpdateAndReturnGeneratedKeys(parent,null
                    ,"INSERT INTO s_event (created,creatorId,source) VALUES(?,?,?)"
                    ,created,this.creatorId,this.source
                    ).getAsLong(0);
        }
        return this.eventId;
    }
    
    @Override
    public void close() throws Exception
    {
        if (this.transaction!=null)
        {
            this.transaction.close();
        }
        if (this.accessor!=null)
        {
            this.accessor.close();
        }
    }

    public Node createNode(Entity...entities) throws Throwable
    {
        long nodeId=Insert.table("s_node").value("createdEventId",this.getEventId()).executeAndReturnLongKey(parent, this.accessor);
        Node node=new Node(this,nodeId);
        node.put(entities);
        return node;
    }

    public Node openNode(long nodeId) throws Throwable
    {
        RowSet rowSet=this.accessor.executeQuery(parent, null
                ,"SELECT count(*) FROM s_node WHERE id=?"
                ,nodeId);
        if (rowSet.getRow(0).getBIGINT(0)==0)
        {
            return null;
        }
        return new Node(this,nodeId);
    }
    
    void put(Entity object,long nodeId,long eventId) throws Throwable
    {
        Class<? extends Entity> type=object.getClass();
        String table=this.graph.getTableName(type.getSimpleName());

        ColumnAccessor[] columnAccessors=this.graph.getColumnAccessors(type);
        StringBuilder insert=new StringBuilder();
        StringBuilder update=new StringBuilder();
        StringBuilder values=new StringBuilder();

        Object[] parameters=new Object[columnAccessors.length*2+1];
        int insertIndex=0;
        int updateIndex=columnAccessors.length+1;
        parameters[insertIndex++]=nodeId;
        parameters[insertIndex++]=eventId;
        parameters[updateIndex++]=eventId;

        insert.append("_nodeId,_createdEventId");
        values.append("?,?");
        update.append("_createdEventId=?");
        
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

    @SuppressWarnings("unchecked")
    <ENTITY extends Entity> ENTITY get(long nodeId,Class<? extends Entity> type) throws Throwable
    {
        String typeName=type.getSimpleName();
        String table=graph.getTableName(typeName);
        
        Row row=Select.source(table).executeOne(parent, this.accessor, "_nodeId_=?",nodeId);
        if (row==null)
        {
            return null;
        }
        ColumnAccessor[] columnAccessors=graph.getColumnAccessors(type);
        ENTITY entity=(ENTITY) type.newInstance();
        for (ColumnAccessor columnAccessor:columnAccessors)
        {
            columnAccessor.set(entity, null, row);
        }        
        return entity;
    }
    
    
    public long getCount(Class<? extends Entity> type,String where,Object...parameters) throws Throwable
    {
        String table=this.graph.getTableName(type.getSimpleName());
        return this.accessor.executeQuery(parent,null,"SELECT count(*) FROM "+table+" WHERE "+where,parameters).getRow(0).getBIGINT(0);
    }
    
//    public void linkOne(long fromNodeId,long toNodeId,Class<? extends Entity> toType) throws Throwable
//    {
//        Event event=this.getEvent();
//
//        String table=this.graph.getTableName(toType.getSimpleName());
//        Transaction transaction=null;
//        if (this.atomic==false)
//        {
//            transaction=this.accessor.beginTransaction(this.trace.getCategory());
//        }
//        try
//        {
//            RowSet rowSet=accessor.executeQuery(this.trace, null
//                    ,"SELECT _link.id FROM _link JOIN "+table+" ON _link.toNodeId="+table+"._nodeId AND "+table+"._retiredEventId IS NULL WHERE _link.retiredEventId IS NULL");
//            if (rowSet.size()>1)
//            {
//                throw new Exception();
//            }
//            if (rowSet.size()==1)
//            {
//                long id=rowSet.getRow(0).getBIGINT(0);
//                int updated=accessor.executeUpdate(this.trace,null
//                        ,"UPDATE _link SET retiredEventId=? WHERE id=?"
//                        ,event.id,id);
//                System.out.println(updated);
//            }
//            this.accessor.executeUpdate(this.trace, null, "INSERT INTO _link (createdEventId,fromNodeId,toNodeId) VALUES(?,?,?)",event.id,fromNodeId,toNodeId);
//        }
//        catch (Throwable t)
//        {
//            if (transaction!=null)
//            {
//                transaction.rollback();
//                transaction=null;
//            }
//            throw t;
//        }
//        finally
//        {
//            if (transaction!=null)
//            {
//                transaction.commit();
//            }
//        }
//    }
    public final Accessor getAccessor() throws Exception
    {
        if (this.accessor==null)
        {
            throw new Exception();
        }
        return this.accessor;
    }

    public Transaction beginTransaction() throws Throwable
    {
        if (this.transaction!=null)
        {
            return this.transaction;
        }
        return this.accessor.beginTransaction("Graph");
    }
    
    public Event getEntityEvent(long nodeId,Class<? extends Entity> type) throws Throwable
    {
        String tableName=this.graph.getTableName(type);
        RowSet rowSet=this.accessor.executeQuery(parent,null
                ,"SELECT id,created,creatorId,source FROM "+tableName+" JOIN s_event ON s_event.id="+tableName+"._createdEventId_ WHERE "+tableName+"._nodeId_=?",nodeId);
        if (rowSet.size()==0)
        {
            return null;
        }
        Row row=rowSet.getRow(0);
        return new Event(row.getBIGINT(0),row.getTIMESTAMP(1),row.getBIGINT(2),row.getVARCHAR(3));
    }

    public void commit() throws Exception
    {
        if (this.transaction!=null)
        {
            this.transaction.commit();
        }
        else
        {
            throw new Exception();
        }
    }
}
