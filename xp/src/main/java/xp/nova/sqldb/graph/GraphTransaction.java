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
    final Trace parent;
    final private GraphAccessor graphAccessor;
    final private Accessor accessor;
    final private Transaction transaction;
    private Long transactionId;
    final private Graph graph;
    final private boolean autoCloseGraphAccessor;

    GraphTransaction(Trace parent,GraphAccessor graphAccessor,String source,long creatorId,boolean autoCloseGraphAccessor) throws Throwable
    {
        this.autoCloseGraphAccessor=autoCloseGraphAccessor;
        this.graph=graphAccessor.graph;
        this.parent=parent;
        this.graphAccessor=graphAccessor;
        this.accessor=graphAccessor.accessor;
        this.transaction=this.accessor.beginTransaction("GraphTransaction"+":"+creatorId+":"+source);

        Timestamp created=SqlUtils.now();
        this.transactionId=this.accessor.executeUpdateAndReturnGeneratedKeys(parent,null
                ,"INSERT INTO _transaction (created,creatorId,source) VALUES(?,?,?)"
                ,created,creatorId,source
                ).getAsLong(0);
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
        long nodeId=Insert.table("_node").value("transactionId",this.transactionId).executeAndReturnLongKey(parent, this.accessor);
        put(nodeId,objects);
        return nodeId;
    }

    public void put(long nodeId,NodeObject...objects) throws Throwable
    {
        for (NodeObject object:objects)
        {
            if (object==null)
            {
                continue;
            }
            put(object,nodeId);
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
                Insert.table("_array").value("elementId",elementId).value("nodeId",arrayNodeId).value("`index`",i).execute(parent, this.accessor);
            }
        }
    }
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
    
    void put(NodeObject object,long nodeId) throws Throwable
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
        insertParameters[insertIndex++]=this.transactionId;
        insert.append("_nodeId,_transactionId");
        values.append("?,?");
        
        Object[] updateParameters=new Object[length];
        int updateIndex=0;
        update.append("_transactionId=?");
        updateParameters[updateIndex++]=this.transactionId;
        
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

    private long link(Class<? extends NodeObject> fromNodeType, long fromNodeId,Relation_ relation,Class<? extends NodeObject> toNodeType,long toNodeId) throws Throwable
    {
        long relationValue=relation.getValue();
        deleteLink(fromNodeId,relation,toNodeId);//We should just check if link exist and return. 
        long nodeId=Insert.table("_node").value("transactionId",this.transactionId).executeAndReturnLongKey(parent, this.accessor);
        Insert.table("_link").value("fromNodeType",fromNodeType.getSimpleName()).value("nodeId",nodeId).value("fromNodeId",fromNodeId).value("toNodeType",toNodeType.getSimpleName()).value("toNodeId", toNodeId)
                .value("relationValue", relationValue)
                .execute(parent, this.accessor);
        return nodeId;
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
    
    
    
    private int deleteLinks(Direction direction,long nodeId,Relation_ relation) throws Throwable
    {
        long relationValue=relation.getValue();
        if (direction==Direction.FROM)
        {
            return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND relationValue=?",nodeId,relationValue);
        }
        else
        {
            return this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE toNodeId=? AND relationValue=?",nodeId,relationValue);
        }
    }
    public int deleteLinks(Direction direction,NodeObject nodeObject,Relation_ relation) throws Throwable
    {
        return deleteLinks(direction,nodeObject.getNodeId(),relation);
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
    public int deleteLink(long fromNodeId,Relation_ relation,long toNodeId) throws Throwable
    {
        return deleteLink(fromNodeId,relation.getValue(),toNodeId);
    }
    public int deleteLink(NodeObject fromNode,Relation_ relation,long toNodeId) throws Throwable
    {
        return deleteLink(fromNode.getNodeId(),relation,toNodeId);
    }
    public int deleteLink(NodeObject fromNode,Relation_ relation,NodeObject toNode) throws Throwable
    {
        return deleteLink(fromNode.getNodeId(),relation,toNode.getNodeId());
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
        this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? OR toNodeId=?",nodeId,nodeId);
        this.accessor.executeUpdate(this.parent,null,"INSERT INTO _delete (nodeId,transactionId) VALUES(?,?)",nodeId,this.transactionId);
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
