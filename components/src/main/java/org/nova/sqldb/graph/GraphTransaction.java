package org.nova.sqldb.graph;

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
import org.nova.sqldb.graph.Graph.ColumnAccessor;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;

public class GraphTransaction implements AutoCloseable
{
    final private Transaction transaction;
    final private Accessor accessor;
    final private Trace trace;
    final private long creatorId;
    final private Graph graph;
    final private boolean atomic;
    private Long eventId;
    
    GraphTransaction(Trace parent,Graph graph,String category,long creatorId,boolean atomic) throws Throwable
    {
        this.trace=new Trace(parent, category);
        this.graph=graph;
        this.accessor=this.graph.getConnector().openAccessor(parent);
        this.creatorId=creatorId;
        this.atomic=atomic;
        if (atomic)
        {
            this.transaction=this.accessor.beginTransaction(category);
        }
        else
        {
            this.transaction=null;
        }
    }
    
    public long getEventId() throws Throwable
    {
        if (this.eventId==null)
        {
            this.eventId=this.accessor.executeUpdateAndReturnGeneratedKeys(this.trace,"getEventId"
                    ,"INSERT INTO _event (creatorId,created,source) VALUES(?,?,?)"
                    ,this.creatorId,SqlUtils.now(),this.trace.getCategory()
                    ).getAsLong(0);
        }
        return this.eventId;
    }
    
    public void commit() throws Exception
    {
        if (this.atomic==false)
        {
            throw new Exception("Not atomic");
        }
        this.transaction.commit();
    }
    
    @Override
    public void close() throws Exception
    {
        if (this.transaction!=null)
        {
            this.transaction.close();
        }
        this.accessor.close();
        this.trace.close();
    }
    
    public Node createNode() throws Throwable
    {
        long eventId=this.getEventId();
        long nodeId=this.accessor.executeUpdateAndReturnGeneratedKeys(trace,"createNode"
                ,"INSERT INTO _node (createdEventId) VALUES(?)"
                ,eventId).getLong(0);
        return new Node(this,nodeId);
    }

    public void put(NodeObject object) throws Throwable
    {
        if (object._id==null)
        {
            throw new Exception("Unknown object");
        }
        put(object,object._nodeId);
    }

    public void put(NodeObject object,NodeObject nodeObject) throws Throwable
    {
        if (nodeObject._id==null)
        {
            throw new Exception("Unknown object");
        }
        put(object,nodeObject._nodeId);
    }
    public void put(NodeObject object,long nodeId) throws Throwable
    {
        put(object,nodeId,this.getEventId());
    }

    public Node createNodeAndPut(NodeObject...objects) throws Throwable
    {
        Node node=createNode();
        for (NodeObject object:objects)
        {
            node.put(object);
        }
        return node;
    }
    
    public <OBJECT extends NodeObject> OBJECT get(Class<? extends NodeObject> type,long nodeId) throws Throwable
    {
        String table=type.getSimpleName();
        Row row=Select.source(table).executeOne(this.trace, accessor, "_nodeId=? AND _retiredEventId IS NULL",nodeId);
        if (row==null)
        {
            return null;
        }
        ColumnAccessor[] columnAccessors=this.graph.getColumnAccessors(type);
        NodeObject object=type.newInstance();
        for (ColumnAccessor columnAccessor:columnAccessors)
        {
            columnAccessor.set(object, null, row);
        }        
        return (OBJECT)object;
    }

    public <OBJECT extends NodeObject> OBJECT buildObject(Class<OBJECT> type,Row row) throws Throwable
    {
        String table=type.getSimpleName();
        Long id=row.getNullableBIGINT(table+"._id");
        if (id==null)
        {
            return null;
        }
        ColumnAccessor[] columnAccessors=this.graph.getColumnAccessors(type);
        OBJECT object=type.newInstance();
        for (ColumnAccessor columnAccessor:columnAccessors)
        {
            columnAccessor.set(object, table, row);
        }        
        return object;
    }
    
    String buildSql(Class<?>[] types,boolean includeNodeEvent,String where,Long nodeId) throws Exception
    {
        StringBuilder select=new StringBuilder();
        StringBuilder join=new StringBuilder();

        if (includeNodeEvent)
        {
            join.append("JOIN _event ON _event.id=_node.createdEventId");
            select.append(",_event.id AS '_event.id',_event.created AS '_event.created',_event.creatorId AS '_event.creatorId',_event.source AS '_event.source'");
        }
        
        for (Class<?> type:types)
        {
            String typeName=type.getSimpleName();
            ColumnAccessor[] columnAccessors=this.graph.getColumnAccessors(type);
            for (ColumnAccessor columnAccessor:columnAccessors)
            {
                select.append(',');
                String columnName=columnAccessor.getSelectColumnName(typeName);
                select.append(columnName+" AS '"+columnName+'\'');
            }
            join.append(" LEFT JOIN "+typeName+" ON _node.id="+typeName+"._nodeId AND "+typeName+"._retiredEventId IS NULL");
        }
        
        
        if (nodeId!=null)
        {
            if (where!=null)
            {
                where="_node.id="+nodeId+" AND "+where;
            }
            else
            {
                where="_node.id="+nodeId;
            }
        }
        String sql="SELECT _node.id"+select.toString()+" FROM _node "+join.toString();
        if (where==null)
        {
            return sql;
        }
        return sql+" WHERE "+where;
    }
    
    NodeResult build(Row row,boolean includeNodeEvent,Class<? extends NodeObject>[] types) throws Throwable
    {
        long nodeId=row.getBIGINT("id");
        long createdEventId=0;
        Timestamp created=null;
        long creatorId=0;
        String source=null;
        if (includeNodeEvent)
        {
            createdEventId=row.getBIGINT("_event.id");
            created=row.getTIMESTAMP("_event.created");
            creatorId=row.getBIGINT("_event.creatorId");
            source=row.getVARCHAR("_event.source");
        }

        NodeResult result=new NodeResult(nodeId,createdEventId,created,creatorId,source);
        for (Class<? extends NodeObject> type:types)
        {
            String typeName=type.getSimpleName();
            Long id=row.getNullableBIGINT(typeName+"._id");
            if (id!=null)
            {
                ColumnAccessor[] columnAccessors=this.graph.getColumnAccessors(type);
                NodeObject object=type.newInstance();
                for (ColumnAccessor columnAccessor:columnAccessors)
                {
                    columnAccessor.set(object, typeName, row);
                }        
                result.put(typeName,object);
            }
        }
        return result;
    }
    
    public NodeResult get(long nodeId,boolean includeNodeEvent,Class<? extends NodeObject>...types) throws Throwable
    {
        String sql=buildSql(types, includeNodeEvent, null, nodeId);
        System.out.println(sql);
        RowSet rowSet=this.accessor.executeQuery(trace, "get-select", sql);
        if (rowSet.rows()==null)
        {
            return null;
        }
        return build(rowSet.getRow(0),includeNodeEvent,types);
    }
    public NodeResult get(long nodeId,Class<? extends NodeObject>...types) throws Throwable
    {
        return get(nodeId,false,types);
    }
    
    
    public <OBJECT extends NodeObject> OBJECT get(Class<NodeObject> type,NodeObject nodeObject) throws Throwable
    {
        if (nodeObject._id==null)
        {
            throw new Exception("Unknown object");
        }
        return get(type,nodeObject._nodeId);
    }

    public <OBJECT extends NodeObject> OBJECT get(Class<OBJECT> type,String where,Object...parameters) throws Throwable
    {
        String table=type.getSimpleName();
        Row row=Select.source(table).executeOne(this.trace, accessor, "_retiredEventId IS NULL AND "+where,parameters);
        if (row==null)
        {
            return null;
        }
        ColumnAccessor[] columnAccessors=this.graph.getColumnAccessors(type);
        OBJECT object=type.newInstance();
        for (ColumnAccessor columnAccessor:columnAccessors)
        {
            columnAccessor.set(object, null, row);
        }        
        return object;
    }

    public Long getNodeId(Class<? extends NodeObject> type,String where,Object...parameters) throws Throwable
    {
        String table=type.getSimpleName();
        Row row=Select.source(table).columns("_nodeId").executeOne(this.trace, accessor, "_retiredEventId IS NULL AND "+where,parameters);
        if (row==null)
        {
            return null;
        }
        return row.getBIGINT(0);
    }
    public Node getNode(Class<? extends NodeObject> type,String where,Object...parameters) throws Throwable
    {
        Long id=getNodeId(type,where,parameters);
        if (id==null)
        {
            return null;
        }
        return new Node(this,id);
    }
    
    void put(NodeObject object,long nodeId,long eventId) throws Throwable
    {
        Class<?> type=object.getClass();
        String typeName=type.getSimpleName();
        
        ColumnAccessor[] columnAccessors=this.graph.getColumnAccessors(type);
        Insert insert=Insert.table(typeName);
        insert.value("_nodeId", nodeId);
        insert.value("_createdEventId", eventId);
        
        ColumnAccessor idAccessor=null;
        for (ColumnAccessor columnAccessor:columnAccessors)
        {
            if (columnAccessor.isGraphField())
            {
                switch (columnAccessor.getName())
                {
                case "_createdEventId":
                    columnAccessor.set(object, eventId);
                break;

                case "_nodeId":
                    columnAccessor.set(object, nodeId);
                    break;
                
                case "_id":
                    idAccessor=columnAccessor;
                break;
                }
                
                continue;
            }
            insert.value(columnAccessor.getName(), columnAccessor.get(object));
        }
        
        Transaction transaction=null;
        if (this.atomic==false)
        {
            transaction=this.accessor.beginTransaction(this.trace.getCategory());
        }
        try
        {
            if (object._id!=null)
            {
                if (accessor.executeUpdate(this.trace, "node.put", "UPDATE "+typeName+" SET _retiredEventId=? WHERE _id=? AND _retiredEvent IS NULL",eventId,object._id)==1)
                {
                    insert.execute(this.trace, accessor);
                    this.graph.evict(typeName, nodeId);
                }
            }
            else
            {
                accessor.executeUpdate(this.trace, "node.put", "UPDATE "+typeName+" SET _retiredEventId=? WHERE _nodeId=?",eventId,nodeId);
                long id=insert.executeAndReturnLongKey(this.trace, accessor);
                idAccessor.set(object, id);
                this.graph.evict(typeName, nodeId);
            }
        }
        catch (Throwable t)
        {
            if (transaction!=null)
            {
                transaction.rollback();
                transaction=null;
            }
            throw t;
        }
        finally
        {
            if (transaction!=null)
            {
                transaction.commit();
            }
        }
    }
    
    public Event getEvent(long eventId) throws Throwable
    {
        Row row=Select.source("event").executeOne(this.trace, this.accessor, "SELECT * FROM _event WHERE id=?",eventId);
        if (row==null)
        {
            return null;
        }
        Event event=new Event();
        event.id=eventId;
        event.creatorId=row.getBIGINT("creatorId");
        event.created=row.getTIMESTAMP("created");
        event.source=row.getVARCHAR("source");
        return event;
    }
    
    
    public final Accessor getAccessor()
    {
        return this.accessor;
    }
//    final Trace getTrace()
//    {
//        return this.trace;
//    }
//    final Graph getGraph()
//    {
//        return this.graph;
//    }
}
