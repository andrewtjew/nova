package xp.nova.sqldb.graph;

import java.sql.Timestamp;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Insert;
import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.sqldb.Select;
import org.nova.sqldb.SqlUtils;
import org.nova.sqldb.Transaction;
import org.nova.testing.Debugging;
import org.nova.tracing.Trace;

import xp.nova.sqldb.graph.Query.PreparedQuery;

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
                    ,"INSERT INTO _event (created,creatorId) VALUES(?,?)"
                    ,created,this.creatorId
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
        //OPTIMIZE: build insert or update based on state in DB. Current insert and update are both built, then one is not used.
        object._nodeId=nodeId;
        Class<? extends NodeObject> type=object.getClass();
        GraphObjectDescriptor meta=this.graph.register(type);
        String table=meta.getTableName();
        FieldDescriptor[] columnAccessors=meta.getColumnAccessors();

        StringBuilder insert=new StringBuilder();
        StringBuilder update=new StringBuilder();
        StringBuilder values=new StringBuilder();

        int length=columnAccessors.length;
        if (meta.getObjectType()==GraphObjectType.NODE)
        {
            length++; 
        }
        
        Object[] insertParameters=new Object[length];
        int insertIndex=0;
        insertParameters[insertIndex++]=nodeId;
        insertParameters[insertIndex++]=eventId;
        insert.append("_nodeId,_eventId");
        values.append("?,?");
        
        Object[] updateParameters=new Object[length];
        int updateIndex=0;
        update.append("_eventId=?");
        updateParameters[updateIndex++]=eventId;
        
        for (FieldDescriptor columnAccessor:columnAccessors)
        {
            if (columnAccessor.isInternal())
            {
                String name=columnAccessor.getName();
                System.out.println("column skipped="+name);
                continue;
            }
            String name=columnAccessor.getName();
            Object value=columnAccessor.get(object);
            insert.append(',');
            insert.append('`'+name+'`');
            values.append(",?");
            update.append(",`"+name+"`=?");
            insertParameters[insertIndex++]=value;
            updateParameters[updateIndex++]=value;
        }
        updateParameters[updateIndex++]=nodeId;

        String selectSql="SELECT * FROM "+table+" WHERE _nodeId=?";
        RowSet rowSet=accessor.executeQuery(parent, null, selectSql,nodeId);
        if (rowSet.size()==0)
        {
            String sql="INSERT INTO "+table+"("+insert+") VALUES ("+values+")";
            if (Graph.TEST)
            {
                Debugging.log(sql);
            }
            if (object instanceof IdentityNodeObject)
            {
            	((IdentityNodeObject)object)._id=accessor.executeUpdateAndReturnGeneratedKeys(parent, null, sql, insertParameters).getAsLong(0);
            }
            else if (object instanceof IdentityRelationNodeObject)
            {
                ((IdentityRelationNodeObject<?>)object)._id=accessor.executeUpdateAndReturnGeneratedKeys(parent, null, sql, insertParameters).getAsLong(0);
            }
            else
            {
            	accessor.executeUpdate(parent, null, sql, insertParameters);
            }
        }
        else if (rowSet.size()==1)
        {
            String sql="UPDATE "+table+" SET "+update+" WHERE _nodeId=?";
            accessor.executeUpdate(parent, null, sql, updateParameters);
            if (Graph.TEST)
            {
                Debugging.log(sql);
            }
        }
        else
        {
            throw new Exception("size="+rowSet.size());
        }
    }

    public long badlink(long fromNodeId,Relation_ relation,long toNodeId) throws Throwable
    {
        throw new Exception();
//        return link(fromNodeId, relation!=null?relation.getValue():null, toNodeId);
    }
    private long link(long fromNodeId,Integer relationValue,long toNodeId) throws Throwable
    {
        deleteLink(fromNodeId,relationValue,toNodeId);
        long nodeId=Insert.table("_node").value("eventId",this.getEventId()).executeAndReturnLongKey(parent, this.accessor);
        Insert.table("_link").value("nodeId",nodeId).value("fromNodeId",fromNodeId).value("toNodeId", toNodeId)
                .value("relationValue", relationValue)
                .execute(parent, this.accessor);
        return nodeId;
    }
    public <FROM extends RelationNodeObject<RELATION>,RELATION extends Relation_> long link(FROM fromNode,RELATION relation,NodeObject toNode) throws Throwable
    {
        return link(fromNode.getNodeId(),relation.getValue(),toNode.getNodeId());
    }
    public <FROM extends RelationNodeObject<RELATION>,RELATION extends Relation_> long link(Class<? extends RelationNodeObject<RELATION>> fromNodeType,long fromNodeId,RELATION relation,NodeObject toNode) throws Throwable
    {
        Row row=Select.source(fromNodeType.getSimpleName()).where("_nodeId=?", fromNodeId).columns("_nodeId").executeOne(parent, this.accessor);
        if (row==null)
        {
            throw new Exception();
        }
        return link(fromNodeId,relation.getValue(),toNode.getNodeId());
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


    public int deleteLinks(long fromNodeId,Relation_ relation) throws Throwable
    {
        throw new Exception();
//        String typeName=relation.getClass().getSimpleName();
//        return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND type=? and relation=?",fromNodeId,typeName,relation.getValue());
    }

    public int deleteLink(long fromNodeId,Relation_ relation,long toNodeId) throws Throwable
    {
        throw new Exception();
//        String typeName=relation.getClass().getSimpleName();
//        return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND toNodeId=? AND type=? and relation=?",fromNodeId,toNodeId,typeName,relation.getValue());
    }

    private int deleteLink(long fromNodeId,Integer relationValue,long toNodeId) throws Throwable
    {
        return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND toNodeId=? AND relationValue=?",fromNodeId,toNodeId,relationValue);
    }

    public int deleteLinks(long fromNodeId,long toNodeId) throws Throwable
    {
        return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND toNodeId=?",fromNodeId,toNodeId);
    }

    public int delete(QueryResultSet set,Class<? extends NodeObject> type) throws Throwable
    {
        //TODO: SQL can be optimized
        int deleted=0;
        for (QueryResult result:set.results)
        {
            NodeObject node=result.get(type);
            if (node!=null)
            {
                long nodeId=node.getNodeId();
                deleted+=deleteNode(nodeId)?1:0;
            }
        }
        System.out.println("deleted:"+deleted);
        return deleted;
    }
    
    public boolean deleteNode(long nodeId) throws Throwable
    {
        int deleted=this.accessor.executeUpdate(this.parent,null,"DELETE FROM _node WHERE id=?",nodeId);
        this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=?",nodeId);
        this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE toNodeId=?",nodeId);
        //The object rows are not deleted. A pruning process should go and delete all nodes.
        
        return deleted>0;
        
    }
    public boolean deleteNode(NodeObject node) throws Throwable
    {
        if (node!=null)
        {
            if (node._nodeId==null)
            {
                throw new Exception("Not a graph node");
            }
            return deleteNode(node.getNodeId());
        }
        return false;
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
