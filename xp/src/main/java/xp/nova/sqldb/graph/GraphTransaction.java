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
            if (object==null)
            {
                continue;
            }
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

//    public void put(ArrayElement[] elements) throws Throwable
//    {
//        Long arrayNodeId=arrayObject._nodeId;
//        if (arrayNodeId==null)
//        {
//            throw new Exception();
//        }
//        for (int i=0;i<elements.length;i++)
//        {
//            ArrayElement object=elements[i];
//            object._index=i;
//            object._arrayNodeId=arrayNodeId;
//            put(object);
//        }
//    }
    public <ELEMENT extends NodeObject> void createArray(NodeObject arrayObject,ELEMENT[] elements) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        deleteArray(arrayObject,(Class<ELEMENT>)elements.getClass().getComponentType());
        for (int i=0;i<elements.length;i++)
        {
            NodeObject element=elements[i];
            if (element!=null)
            {
                long elementId=createNode(element);
                Insert.table("_array").value("elementId",elementId).value("arrayId",arrayNodeId).value("`index`",i).execute(parent, this.accessor);
            }
        }
    }
//    public <ELEMENT extends NodeObject> void createArrayElement(NodeObject arrayObject,ELEMENT element,int index) throws Throwable
//    {
//        Long arrayNodeId=arrayObject._nodeId;
//        if (arrayNodeId==null)
//        {
//            throw new Exception();
//        }
//        if (element!=null)
//        {
//            long elementId=createNode(element);
//            Insert.table("_array").value("arrayId",arrayNodeId).value("elementId",elementId).value("index",index).execute(parent, this.accessor);
//        }
//    }
    public int deleteArray(NodeObject arrayObject,Class<? extends NodeObject> elementType) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        String elementTypeName=elementType.getSimpleName();
        String deleteSql="DELETE _node,_array FROM _node JOIN _array ON _node.id=_array.elementId JOIN "+elementTypeName+" ON _node.id="+elementTypeName+"._nodeId";
        int deleted=this.accessor.executeUpdate(this.parent,null,deleteSql);
        return deleted;
    }
    
    void put(NodeObject object,long nodeId,long eventId) throws Throwable
    {
        //OPTIMIZE: build insert or update based on state in DB. Current insert and update are both built, then one is not used.
        object._nodeId=nodeId;
        Class<? extends NodeObject> type=object.getClass();
        GraphObjectDescriptor descriptor=this.graph.register(type);
        String table=descriptor.getTableName();
        FieldDescriptor[] columnAccessors=descriptor.getFieldDescriptors();

        StringBuilder insert=new StringBuilder();
        StringBuilder update=new StringBuilder();
        StringBuilder values=new StringBuilder();

        int length=columnAccessors.length;
        if (descriptor.getObjectType()==GraphObjectType.NODE)
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
            if (Graph.DEBUG)
            {
                Debugging.log("Graph",sql);
            }
            if (object instanceof IdentityNodeObject)
            {
            	((IdentityNodeObject)object)._id=accessor.executeUpdateAndReturnGeneratedKeys(parent, null, sql, insertParameters).getAsLong(0);
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
            if (Graph.DEBUG)
            {
                Debugging.log("Graph",sql);
            }
        }
        else
        {
            throw new Exception("size="+rowSet.size());
        }
    }

    private long link(Class<? extends NodeObject> fromNodeType, long fromNodeId,Relation_ relation,RelationObjectType_ objectType,Class<? extends NodeObject> toNodeType,long toNodeId) throws Throwable
    {
        long relationValue=toRelationValue(relation, objectType);
        deleteLink(fromNodeId,relation,objectType,toNodeId);//We should just check if link exist and return. 
        long nodeId=Insert.table("_node").value("eventId",this.getEventId()).executeAndReturnLongKey(parent, this.accessor);
        Insert.table("_link").value("fromNodeType",fromNodeType.getSimpleName()).value("nodeId",nodeId).value("fromNodeId",fromNodeId).value("toNodeType",toNodeType.getSimpleName()).value("toNodeId", toNodeId)
                .value("relationValue", relationValue)
                .execute(parent, this.accessor);
        return nodeId;
    }
    private long link(Class<? extends NodeObject> fromNodeType, long fromNodeId,Relation_ relation,Class<? extends NodeObject> toNodeType,long toNodeId) throws Throwable
    {
        return link(fromNodeType,fromNodeId,relation,null,toNodeType,toNodeId);
    }
    public long link(NodeObject fromNode,Relation_ relation,Class<? extends NodeObject> toNodeType,long toNodeId) throws Throwable
    {
        return link(fromNode.getClass(),fromNode.getNodeId(),relation,toNodeType,toNodeId);
    }
    public long link(NodeObject fromNode,Relation_ relation,NodeObject toNode) throws Throwable
    {
        return link(fromNode.getClass(),fromNode.getNodeId(),relation,toNode.getClass(),toNode.getNodeId());
    }
    public long link(Class<? extends NodeObject> fromNodeType,long fromNodeId,Relation_ relation,NodeObject toNode) throws Throwable
    {
        return link(fromNodeType,fromNodeId,relation,toNode.getClass(),toNode.getNodeId());
    }
//--
    public long link(NodeObject fromNode,Relation_ relation,RelationObjectType_ relationObjectType,Class<? extends NodeObject> toNodeType,long toNodeId) throws Throwable
    {
        return link(fromNode.getClass(),fromNode.getNodeId(),relation,relationObjectType,toNodeType,toNodeId);
    }
    public long link(NodeObject fromNode,Relation_ relation,RelationObjectType_ relationObjectType,NodeObject toNode) throws Throwable
    {
        return link(fromNode.getClass(),fromNode.getNodeId(),relation,relationObjectType,toNode.getClass(),toNode.getNodeId());
    }
    public long link(Class<? extends NodeObject> fromNodeType,long fromNodeId,Relation_ relation,RelationObjectType_ relationObjectType,NodeObject toNode) throws Throwable
    {
        return link(fromNodeType,fromNodeId,relation,relationObjectType,toNode.getClass(),toNode.getNodeId());
    }
    
    
    
    private int deleteLinks(Direction direction,long nodeId,Relation_ relation,RelationObjectType_ objectType) throws Throwable
    {
        long relationValue=GraphTransaction.toRelationValue(relation, objectType);
        if (direction==Direction.FROM)
        {
            return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND relationValue=?",nodeId,relationValue);
        }
        else
        {
            return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE toNodeId=? AND relationValue=?",nodeId,relationValue);
        }
    }
    public int deleteLinks(Direction direction,NodeObject nodeObject,Relation_ relation,RelationObjectType_ objectType) throws Throwable
    {
        return deleteLinks(direction,nodeObject.getNodeId(),relation,objectType);
    }

//    public int deleteLinks(long fromNodeId,Relation_ relation) throws Throwable
//    {
//        throw new Exception();
////        String typeName=relation.getClass().getSimpleName();
////        return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND type=? and relation=?",fromNodeId,typeName,relation.getValue());
//    }

//    public int deleteLink(long fromNodeId,Relation_ relation,long toNodeId) throws Throwable
//    {
//        throw new Exception();
////        String typeName=relation.getClass().getSimpleName();
////        return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND toNodeId=? AND type=? and relation=?",fromNodeId,toNodeId,typeName,relation.getValue());
//    }
    
    static long toRelationValue(Relation_ relation,RelationObjectType_ objectType)
    {
        long value=relation.getValue();
        if (objectType!=null)
        {
            value=(value<<32)|objectType.getValue();
        }
        return value;
    }
    
    private int deleteLink(long fromNodeId,long relationValue,long toNodeId) throws Throwable
    {
        return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND toNodeId=? AND relationValue=?",fromNodeId,toNodeId,relationValue);
    }
    public int deleteLink(long fromNodeId,Relation_ relation,RelationObjectType_ objectType,long toNodeId) throws Throwable
    {
        return deleteLink(fromNodeId,toRelationValue(relation, objectType),toNodeId);
    }
    public int deleteLink(NodeObject fromNode,Relation_ relation,RelationObjectType_ objectType,long toNodeId) throws Throwable
    {
        return deleteLink(fromNode.getNodeId(),relation,objectType,toNodeId);
    }
    public int deleteLink(NodeObject fromNode,Relation_ relation,long toNodeId) throws Throwable
    {
        return deleteLink(fromNode,relation,null,toNodeId);
    }
    public int deleteLink(NodeObject fromNode,Relation_ relation,RelationObjectType_ objectType,NodeObject toNode) throws Throwable
    {
        return deleteLink(fromNode.getNodeId(),relation,objectType,toNode.getNodeId());
    }
    public int deleteLink(NodeObject fromNode,Relation_ relation,NodeObject toNode) throws Throwable
    {
        return deleteLink(fromNode,relation,null,toNode.getNodeId());
    }

    private int deleteNodes(Class<? extends NodeObject> deleteNodeType,QueryResultSet set) throws Throwable
    {
        //TODO: SQL can be optimized
        int deleted=0;
        for (QueryResult result:set.results)
        {
            NodeObject node=result.get(deleteNodeType);
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
        //The node objects are not deleted. A pruning process should go and delete all nodes.
        //Alternative is to record meta data to store the node objects when put or createNode is called.
        
        return deleted>0;
        
    }
    public boolean deleteNode(NodeObject node) throws Throwable
    {
        if (node!=null)
        {
            if (node._nodeId!=null)
            {
                return deleteNode(node.getNodeId());
            }
        }
        return false;
    }
//    public int deleteNodes(NodeObject[] nodes) throws Throwable
//    {
//        int total=0;
//        for (NodeObject node:nodes)
//        {
//            total+=deleteNode(node)?1:0;
//        }
//        return total;
//    }
    public int deleteNodes(NodeObject...nodes) throws Throwable
    {
        int total=0;
        for (NodeObject node:nodes)
        {
            total+=deleteNode(node)?1:0;
        }
        return total;
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
