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
import org.nova.sqldb.graph.Graph.EntityMeta;
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

    public Node createNode(NodeEntity entity,NodeAttribute...attributes) throws Throwable
    {
        long nodeId=Insert.table("s_node").value("createdEventId",this.getEventId()).executeAndReturnLongKey(parent, this.accessor);
        Node node=new Node(this,nodeId);
        node.put(entity);
        node.put(attributes);
        return node;
    }

    public Node openNode(long nodeId) throws Throwable
    {
        RowSet rowSet=this.accessor.executeQuery(parent, null
                ,"SELECT id FROM s_node WHERE id=?"
                ,nodeId);
        int size=rowSet.size();
        if (size==0)
        {
            return null;
        }
        return new Node(this,nodeId);
    }
    
    void put(NodeObject object,long nodeId,long eventId) throws Throwable
    {
        Class<? extends NodeObject> type=object.getClass();
        EntityMeta meta=this.graph.getEntityMeta(type);
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
    <ENTITY extends NodeAttribute> ENTITY get(long nodeId,Class<? extends NodeAttribute> type) throws Throwable
    {
        EntityMeta meta=this.graph.getEntityMeta(type);
        String table=meta.getTableName();

        Row row=Select.source(table).executeOne(parent, this.accessor, "_nodeId_=?",nodeId);
        if (row==null)
        {
            return null;
        }
        ENTITY entity=(ENTITY) type.newInstance();
        for (ColumnAccessor columnAccessor:meta.getColumnAccessors())
        {
            columnAccessor.set(entity, null, row);
        }        
        return entity;
    }

    public long getCount(Class<? extends NodeAttribute> type,String where,Object...parameters) throws Throwable
    {
        EntityMeta meta=this.graph.getEntityMeta(type);
        String table=meta.getTableName();
        return this.accessor.executeQuery(parent,null,"SELECT count(*) FROM "+table+" WHERE "+where,parameters).getRow(0).getBIGINT(0);
    }
    
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
    
    public Event getEntityEvent(long nodeId,Class<? extends NodeAttribute> type) throws Throwable
    {
        String tableName=this.graph.getEntityMeta(type).getTableName();
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

    public Link link(long fromNodeId,long toNodeId) throws Throwable
    {
        try
        {
            long id=Insert.table("s_link").value("fromNodeId",fromNodeId).value("toNodeId", toNodeId).value("createdEventId",this.getEventId()).executeAndReturnLongKey(parent, this.accessor);
            return new Link(this,id,fromNodeId,toNodeId);
        }
        catch (Throwable t)
        {
            return openLink(fromNodeId,toNodeId);
        }
    }
    
    int deleteLinks(long fromNodeId,Class<? extends NodeAttribute> toType) throws Throwable
    {
        EntityMeta meta=this.graph.getEntityMeta(toType);
        String table=meta.getTableName();
        RowSet rowSet=this.accessor.executeQuery(parent, null
                ,"SELECT s_link.id FROM s_link JOIN "+table+" ON "+table+"._nodeId=s_link.toNodeId WHERE fromNodeId=?"
                ,fromNodeId);
        Object[][] parameters=new Object[rowSet.size()][];
        for (int i=0;i<parameters.length;i++)
        {
            parameters[i]=new Object[1];
            parameters[i][0]=rowSet.getRow(i).getBIGINT(0);
        }
        
        int[] results=this.accessor.executeBatchUpdate(this.parent,null,parameters,"DELETE FROM s_link WHERE id=?");
        int total=0;
        for (int i=0;i<results.length;i++)
        {
            total+=results[i];
        }
        return total;
        
    }
    
    public Link openLink(long linkId) throws Throwable
    {
        RowSet rowSet=this.accessor.executeQuery(parent, null
                ,"SELECT fromNodeId,toNodeId FROM s_link WHERE id=?"
                ,linkId);
        int size=rowSet.size();
        if (size==0)
        {
            return null;
        }
        else if (size>1)
        {
            throw new Exception();
        }
        Row row=rowSet.getRow(0);
        return new Link(this,linkId,row.getBIGINT(0),row.getBIGINT(1));
    }
    public Link openLink(long fromNodeId,long toNodeId) throws Throwable
    {
        RowSet rowSet=this.accessor.executeQuery(parent, null
                ,"SELECT id FROM s_link WHERE fromNodeId=? AND toNodeId=?"
                ,fromNodeId,toNodeId);
        int size=rowSet.size();
        if (size==0)
        {
            return null;
        }
        else if (size>1)
        {
            throw new Exception();
        }
        Row row=rowSet.getRow(0);
        return new Link(this,row.getBIGINT(0),fromNodeId,toNodeId);
    }
}
