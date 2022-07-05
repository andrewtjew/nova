package org.nova.sqldb.graph;

import java.lang.reflect.Array;
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

public class GraphTransaction implements AutoCloseable
{
    final private Transaction transaction;
    final private Accessor accessor;
    final private Trace trace;
    final private Long creatorId;
    final private Graph graph;
    final private boolean atomic;
    private Event event;
    
    GraphTransaction(Trace parent,Graph graph,String category,Long creatorId,boolean atomic) throws Throwable
    {
        this.trace=new Trace(parent, category);
        this.graph=graph;
        this.accessor=this.graph.getConnector().openAccessor(parent);
        this.creatorId=creatorId;
        this.atomic=atomic;
        if (atomic)
        {
            if (category==null)
            {
                throw new Exception();
            }
            this.transaction=this.accessor.beginTransaction(category);
        }
        else
        {
            this.transaction=null;
        }
    }
    
    public synchronized Event getEvent() throws Throwable
    {
        if (this.event==null)
        {
            if (this.creatorId==null)
            {
                throw new Exception();
            }
            Timestamp created=SqlUtils.now();
            String source=this.trace.getCategory();
            long eventId=this.accessor.executeUpdateAndReturnGeneratedKeys(this.trace,"getEventId"
                    ,"INSERT INTO _event (created,creatorId,source) VALUES(?,?,?)"
                    ,created,this.creatorId,source
                    ).getAsLong(0);
            this.event=new Event(eventId,created,this.creatorId,source);
        }
        return this.event;
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
    
    public Node createNode(NodeObject...objects) throws Throwable
    {
        Event event=this.getEvent();
        long nodeId=this.accessor.executeUpdateAndReturnGeneratedKeys(trace,"createNode"
                ,"INSERT INTO _node (createdEventId) VALUES(?)"
                ,event.id).getLong(0);
        Node node=new Node(this,nodeId);
        for (NodeObject object:objects)
        {
            put(object,node.getNodeId(),event);
        }
        return node;
    }

    public void put(NodeObject object) throws Throwable
    {
        if (object._id==null)
        {
            throw new Exception("Unknown object");
        }
        put(object,object._nodeId);
    }
    public boolean update(NodeObject object) throws Throwable
    {
        if (object._id==null)
        {
            throw new Exception("Unknown object");
        }
        Class<? extends NodeObject> type=object.getClass();
        ColumnAccessor[] columnAccessors=this.graph.getColumnAccessors(type);
        Update update=Update.table(this.graph.getTableName(type));
        for (ColumnAccessor columnAccessor:columnAccessors)
        {
            if (columnAccessor.isGraphField()==false)
            {
                update.set(columnAccessor.getName(), columnAccessor.get(object));
            }
        }
        return update.execute(this.trace, this.accessor, "_id=?", object._id)>0;
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
        put(object,nodeId,getEvent());
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
        String table=this.graph.getTableName(type);
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
        String table=this.graph.getTableName(type);
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
    
    @SuppressWarnings("unchecked")
    String buildNodeSql(Class<? extends GraphObject>[] types,boolean includeNodeEvent) throws Exception
    {
        StringBuilder select=new StringBuilder();
        StringBuilder join=new StringBuilder();

        if (includeNodeEvent)
        {
            join.append("JOIN _event ON _event.id=_node.createdEventId");
            select.append(",_event.id AS '_event.id',_event.created AS '_event.created',_event.creatorId AS '_event.creatorId',_event.source AS '_event.source'");
        }
        
        for (Class<? extends GraphObject> type:types)
        {
            Class<? extends NodeObject> nodeType;
            String table;
            if (TypeUtils.isDerivedFrom(type, LinkedNodeObject.class))
            {
                nodeType=this.graph.getLinkedName((Class<? extends LinkedNodeObject<?>>)type);
                table=this.graph.getTableName(nodeType);
                join.append(" LEFT JOIN _link ON _node.id=_link.fromNodeId LEFT JOIN "+table+" ON _link.toNodeId="+table+"._nodeId AND "+table+"._retiredEventId IS NULL");
            }
            else
            {
                nodeType=(Class<? extends NodeObject>) type;
                table=this.graph.getTableName(nodeType);
                join.append(" LEFT JOIN "+table+" ON _node.id="+table+"._nodeId AND "+table+"._retiredEventId IS NULL");
            }
            String typeName=nodeType.getSimpleName();
            ColumnAccessor[] columnAccessors=this.graph.getColumnAccessors(nodeType);
            for (ColumnAccessor columnAccessor:columnAccessors)
            {
                select.append(',');
                String fieldColumnName=columnAccessor.getColumnName(typeName);
                String tableColumnName=columnAccessor.getColumnName(table);
                select.append(tableColumnName+" AS '"+fieldColumnName+'\'');
            }
        }
        String sql="SELECT _node.id"+select.toString()+" FROM _node "+join.toString();
        System.out.println(sql);
        return sql;
    }

//    String buildNodeSql(Class<? extends NodeObject> type) throws Exception
//    {
//        StringBuilder select=new StringBuilder(",_event.id AS '_event.id',_event.created AS '_event.created',_event.creatorId AS '_event.creatorId',_event.source AS '_event.source'");
//        StringBuilder join=new StringBuilder("JOIN _event ON _event.id=_node.createdEventId");
//        
//        String table=this.graph.getTableName(type);
//        ColumnAccessor[] columnAccessors=this.graph.getColumnAccessors(type);
//        for (ColumnAccessor columnAccessor:columnAccessors)
//        {
//            select.append(',');
//            String columnName=columnAccessor.getSelectColumnName(table);
//            select.append(columnName+" AS '"+columnName+'\'');
//        }
//        join.append(" LEFT JOIN "+table+" ON _node.id="+table+"._nodeId AND "+table+"._retiredEventId IS NULL");
//        
//        return "SELECT _node.id"+select.toString()+" FROM _node "+join.toString();
//    }
//    
    @SuppressWarnings("unchecked")
    NodeResult build(Row row,Class<? extends GraphObject>[] types) throws Throwable
    {
        long nodeId=row.getBIGINT("id");
        NodeResult result=new NodeResult(nodeId);
        for (Class<? extends GraphObject> type:types)
        {
            Class<? extends NodeObject> nodeType;
            if (TypeUtils.isDerivedFrom(type, LinkedNodeObject.class))
            {
                nodeType=this.graph.getLinkedName((Class<? extends LinkedNodeObject<?>>)type);
            }
            else
            {
                nodeType=(Class<? extends NodeObject>)type;
            }
            
            String typeName=nodeType.getSimpleName();
            Long id=row.getNullableBIGINT(typeName+"._id");
            if (id!=null)
            {
                ColumnAccessor[] columnAccessors=this.graph.getColumnAccessors(nodeType);
                NodeObject object=(NodeObject) nodeType.newInstance();
                for (ColumnAccessor columnAccessor:columnAccessors)
                {
                    columnAccessor.set(object, typeName, row);
                }        
                result.put(typeName,object);
            }
        }
        return result;
    }
    
//    public NodeResult get(long nodeId,boolean includeNodeEvent,Class<? extends NodeObject>...types) throws Throwable
//    {
//        String sql=buildNodeSql(types, includeNodeEvent, nodeId);
//        System.out.println(sql);
//        RowSet rowSet=this.accessor.executeQuery(trace, "get-select", sql);
//        if (rowSet.rows()==null)
//        {
//            return null;
//        }
//        return build(rowSet.getRow(0),types);
//    }
    
    public NodeResult get(long nodeId,Class<? extends GraphObject>...types) throws Throwable
    {
        String sql=buildNodeSql(types, false)+" WHERE _node.id="+nodeId;
        RowSet rowSet=this.accessor.executeQuery(trace, "get-select", sql);
        if (rowSet.size()==0)
        {
            return null;
        }
        return build(rowSet.getRow(0),types);
    }

    
    public <OBJECT extends NodeObject> OBJECT get(Class<NodeObject> type,NodeObject nodeObject) throws Throwable
    {
        if (nodeObject._id==null)
        {
            throw new Exception("Unknown object");
        }
        return get(type,nodeObject._nodeId);
    }

    public <OBJECT extends NodeObject> OBJECT getOne(Class<OBJECT> type,String where,Object...parameters) throws Throwable
    {
        String table=this.graph.getTableName(type);
        
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

    public NodeResult[] get(Where where,Class<? extends NodeObject>...types) throws Throwable
    {
        String sql=buildNodeSql(types, false)+" WHERE "+where.getWhere();
        System.out.println(sql);
        RowSet rowSet=this.accessor.executeQuery(trace, "get", sql,where.getParameters());
        NodeResult[] results=new NodeResult[rowSet.size()];
        for (int i=0;i<rowSet.size();i++)
        {
            results[i]=build(rowSet.getRow(i),types);
        }
        return results;
    }
    
///
    public <OBJECT extends NodeObject> OBJECT[] get(Class<OBJECT> type,String where,Object...parameters) throws Throwable
    {
        String table=this.graph.getTableName(type);
        RowSet rowSet=this.accessor.executeQuery(this.trace, null
                ,"SELECT * FROM "+table+" WHERE _retiredEventId IS NULL AND "+where
                ,parameters);
        
        OBJECT[] array=(OBJECT[])Array.newInstance(type, rowSet.size());
        for (int i=0;i<rowSet.size();i++)
        {
            Row row=rowSet.getRow(i);
            ColumnAccessor[] columnAccessors=this.graph.getColumnAccessors(type);
            OBJECT object=type.newInstance();
            for (ColumnAccessor columnAccessor:columnAccessors)
            {
                columnAccessor.set(object, null, row);
            }        
            array[i]=object;
//            Array.set(array, i, object);
        }
        return array;
    }

    public long getCount(Class<? extends NodeObject> type,String where,Object...parameters) throws Throwable
    {
        String table=this.graph.getTableName(type);
        RowSet rowSet=this.accessor.executeQuery(this.trace, null
                ,"SELECT count(*) FROM "+table+" WHERE _retiredEventId IS NULL AND "+where
                ,parameters);
        return rowSet.getRow(0).getBIGINT(0);
    }

    public Long getNodeId(Class<? extends NodeObject> type,String where,Object...parameters) throws Throwable
    {
        String table=this.graph.getTableName(type);
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
    
    void put(NodeObject object,long nodeId,Event event) throws Throwable
    {
        Class<? extends NodeObject> type=object.getClass();
        String table=this.graph.getTableName(type);

        ColumnAccessor[] columnAccessors=this.graph.getColumnAccessors(type);
        Insert insert=Insert.table(table);
        insert.value("_nodeId", nodeId);
        insert.value("_createdEventId", event.id);
        
        ColumnAccessor idAccessor=null;
        for (ColumnAccessor columnAccessor:columnAccessors)
        {
            if (columnAccessor.isGraphField())
            {
                switch (columnAccessor.getName())
                {
                case "_createdEventId":
                    columnAccessor.set(object, event.id);
                break;

                case "_created":
                    columnAccessor.set(object, event.created);
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
            if (accessor.executeQuery(this.trace, null, "SELECT count(*) FROM _node WHERE id=?",nodeId).getRow(0).getBIGINT(0)!=1)
            {
                throw new Exception("Invalid nodeId="+nodeId);
            }
            if (object._id!=null)
            {
                if (accessor.executeUpdate(this.trace, "node.put"
                        , "UPDATE "+table+" SET _retiredEventId=?,_retired=? WHERE _id=? AND _retiredEvent IS NULL"
                        ,event.id,event.created,object._id)==1)
                {
                    insert.execute(this.trace, accessor);
                    this.graph.evict(table, nodeId);
                }
            }
            else
            {
                accessor.executeUpdate(this.trace, "node.put"
                        , "UPDATE "+table+" SET _retiredEventId=?,_retired=? WHERE _nodeId=?",event.id,event.created,nodeId);
                long id=insert.executeAndReturnLongKey(this.trace, accessor);
                idAccessor.set(object, id);
                this.graph.evict(table, nodeId);
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
        return new Event(eventId,row.getTIMESTAMP("created"),row.getBIGINT("creatorId"),row.getVARCHAR("source"));
    }
    
    public void linkOne(long fromNodeId,long toNodeId,Class<? extends NodeObject> toType) throws Throwable
    {
        Event event=this.getEvent();

        String table=this.graph.getTableName(toType);
        Transaction transaction=null;
        if (this.atomic==false)
        {
            transaction=this.accessor.beginTransaction(this.trace.getCategory());
        }
        try
        {
            RowSet rowSet=accessor.executeQuery(this.trace, null
                    ,"SELECT _link.id FROM _link JOIN "+table+" ON _link.toNodeId="+table+"._nodeId AND "+table+"._retiredEventId IS NULL WHERE _link.retiredEventId IS NULL");
            if (rowSet.size()>1)
            {
                throw new Exception();
            }
            if (rowSet.size()==1)
            {
                long id=rowSet.getRow(0).getBIGINT(0);
                int updated=accessor.executeUpdate(this.trace,null
                        ,"UPDATE _link SET retiredEventId=? WHERE id=?"
                        ,event.id,id);
                System.out.println(updated);
            }
            this.accessor.executeUpdate(this.trace, null, "INSERT INTO _link (createdEventId,fromNodeId,toNodeId) VALUES(?,?,?)",event.id,fromNodeId,toNodeId);
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
    public void linkOne(NodeObject fromNode,NodeObject toNode) throws Throwable
    {
        linkOne(fromNode._nodeId,toNode._nodeId,toNode.getClass());
    }
    public void linkOne(NodeObject fromNode,long toNodeId,Class<? extends NodeObject> toType) throws Throwable
    {
        linkOne(fromNode._nodeId,toNodeId,toType);
    }
    public void linkOne(Node fromNode,NodeObject toNode) throws Throwable
    {
        linkOne(fromNode.getNodeId(),toNode._nodeId,toNode.getClass());
    }
    public void linkOne(Node fromNode,long toNodeId,Class<? extends NodeObject> toType) throws Throwable
    {
        linkOne(fromNode.getNodeId(),toNodeId,toType);
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
