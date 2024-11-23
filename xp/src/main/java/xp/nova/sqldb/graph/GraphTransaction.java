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
            _put(object,nodeId);
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

    private void _put(NodeObject object,long nodeId) throws Throwable
    {
        //OPTIMIZE: build insert or update based on state in DB. Current insert and update are both built, then one is not used.
        object._nodeId=nodeId;
        Class<? extends NodeObject> type=object.getClass();
        GraphObjectDescriptor descriptor=this.graph.getGraphObjectDescriptor(type);
        this.graph.invalidateCacheLines(parent, descriptor);
        
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
            if (Graph.DEBUG_QUERY)
            {
                StringBuilder sb=new StringBuilder(sql);
                if (insertParameters.length>0)
                {
                    sb.append("(");
                    for (int i=0;i<insertParameters.length;i++)
                    {
                        if (i==0)
                        {
                            sb.append('(');
                        }
                        else
                        {
                            sb.append(',');
                        }
                        sb.append(insertParameters[i]);
                    }
                    sb.append(")");
                }
                Debugging.log("Graph",sb.toString());
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
            if (Graph.DEBUG_QUERY)
            {
                StringBuilder sb=new StringBuilder(sql);
                if (updateParameters.length>0)
                {
                    sb.append("(");
                    for (int i=0;i<updateParameters.length;i++)
                    {
                        if (i==0)
                        {
                            sb.append('(');
                        }
                        else
                        {
                            sb.append(',');
                        }
                        sb.append(updateParameters[i]);
                    }
                    sb.append(")");
                }
                Debugging.log("Graph",sb.toString());
            }
            accessor.executeUpdate(parent, null, sql, updateParameters);
        }
        else
        {
            throw new Exception("size="+rowSet.size());
        }
    }

    private long _link(Class<? extends NodeObject> fromNodeType, long fromNodeId,Relation_ relation,Class<? extends NodeObject> toNodeType,long toNodeId) throws Throwable
    {
        long relationValue=relation.getValue();
        
        this.graph.invalidateCacheLines(parent, this.graph.getGraphObjectDescriptor(fromNodeType),this.graph.getGraphObjectDescriptor(toNodeType));
        
        deleteLink(fromNodeId,relation.getValue(),toNodeId);//We should just check if link exist and return. 
        long nodeId=Insert.table("_node").value("transactionId",this.transactionId).executeAndReturnLongKey(parent, this.accessor);
        Insert.table("_link").value("fromNodeType",fromNodeType.getSimpleName()).value("nodeId",nodeId).value("fromNodeId",fromNodeId).value("toNodeType",toNodeType.getSimpleName()).value("toNodeId", toNodeId)
                .value("relationValue", relationValue)
                .execute(parent, this.accessor);
        return nodeId;
    }
    public long link(NodeObject fromNode,Relation_ relation,Class<? extends NodeObject> toNodeType,long toNodeId) throws Throwable
    {
        return _link(fromNode.getClass(),fromNode.getNodeId(),relation,toNodeType,toNodeId);
    }
    public long link(NodeObject fromNode,Relation_ relation,NodeObject toNode) throws Throwable
    {
        return _link(fromNode.getClass(),fromNode.getNodeId(),relation,toNode.getClass(),toNode.getNodeId());
    }
    public long link(Class<? extends NodeObject> fromNodeType,long fromNodeId,Relation_ relation,NodeObject toNode) throws Throwable
    {
        return _link(fromNodeType,fromNodeId,relation,toNode.getClass(),toNode.getNodeId());
    }
    
    private int _deleteLinks(Direction direction,Class<? extends NodeObject> nodeType,long nodeId,Relation_ relation) throws Throwable
    {
        RowSet rowSet;
        if (direction==Direction.FROM)
        {
            rowSet=this.accessor.executeQuery(parent, null, "SELECT toNodeType FROM _link WHERE fromNodeId=? AND relationValue=?",nodeId,relation);
        }
        else
        {
            rowSet=this.accessor.executeQuery(parent, null, "SELECT fromNodeType FROM _link WHERE toNodeId=? AND relationValue=?",nodeId,relation);
        }
        GraphObjectDescriptor[] descriptors=new GraphObjectDescriptor[rowSet.size()+1]; 
        for (int i=0;i<rowSet.size();i++)
        {
            Row row=rowSet.getRow(i);
            String typeName=row.getVARCHAR(0);
            descriptors[i]=this.graph.getGraphObjectDescriptor(typeName);
        }
        descriptors[rowSet.size()]=this.graph.getGraphObjectDescriptor(nodeType);
        this.graph.invalidateCacheLines(parent, descriptors);
        
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
        return _deleteLinks(direction,nodeObject.getClass(),nodeObject.getNodeId(),relation);
    }

    static long toRelationValue(Relation_ relation,RelationObjectType_ objectType)
    {
        long value=relation.getValue();
        if (objectType!=null)
        {
            value=(value<<32)|objectType.getValue();
        }
        return value;
    }
    
    public int deleteLink(long fromNodeId,long relationValue,long toNodeId) throws Throwable
    {
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT nodeId FROM _link WHERE fromNodeId=? AND toNodeId=? AND relationValue=?",fromNodeId,toNodeId,relationValue);
        if (rowSet.size()==0)
        {
            return 0;
        }
        else if (rowSet.size()!=1)
        {
            throw new Exception();
        }
        this.graph.invalidateCacheLines(parent, fromNodeId);
        this.graph.invalidateCacheLines(parent, toNodeId);
        int deleted=this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE fromNodeId=? AND toNodeId=? AND relationValue=?",fromNodeId,toNodeId,relationValue);
        long nodeId=rowSet.getRow(0).getBIGINT(0);
        deleted+=deleteNode(nodeId);
        return deleted;
    }
    public int deleteLink(NodeObject fromNode,Relation_ relation,long toNodeId) throws Throwable
    {
        return deleteLink(fromNode.getNodeId(),relation.getValue(),toNodeId);
    }
    public int deleteLink(NodeObject fromNode,Relation_ relation,NodeObject toNode) throws Throwable
    {
        return deleteLink(fromNode.getNodeId(),relation.getValue(),toNode.getNodeId());
    }

    public int deleteNode(long nodeId) throws Throwable
    {
        int deleted=this.accessor.executeUpdate(this.parent,null,"DELETE FROM _node WHERE id=?",nodeId);
        if (deleted>0)
        {
            this.graph.invalidateCacheLines(parent, nodeId);
            RowSet rowSet=this.accessor.executeQuery(this.parent,null,"SELECT fromNodeId,relationValue,toNodeId FROM _link WHERE fromNodeId=? OR toNodeId=?",nodeId,nodeId);
            for (Row row:rowSet.rows())
            {
                long fromNodeId=row.getBIGINT("fromNodeId");
                long toNodeId=row.getBIGINT("toNodeId");
                long relationValue=row.getBIGINT("relationValue");
                deleted+=deleteLink(fromNodeId,toNodeId,relationValue);
            }
        }
        return deleted;
    }

    public int deleteNode(NodeObject node) throws Throwable
    {
        if (node!=null)
        {
            if (node._nodeId!=null)
            {
                return deleteNode(node._nodeId);
            }
        }
        return 0;
    }
    public int deleteNodes(NodeObject...nodes) throws Throwable
    {
        int deleted=0;
        for (NodeObject node:nodes)
        {
            deleted+=deleteNode(node);
        }
        return deleted;
    }
    
    public void commit() throws Throwable
    {
        this.transaction.commit();
    }
    public void rollback() throws Throwable
    {
        this.graph.clearCache();
        this.transaction.rollback();
    }
    
    public <NODE extends NodeObject> void putArray(NodeObject arrayObject,NODE[] elements) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        deleteArray(arrayObject,(Class<NODE>)elements.getClass().getComponentType());
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

    public void appendToArray(NodeObject arrayObject,NodeObject element) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        long elementId=createNode(element);
        int index=0;
        var row=Select.source("_array").columns("`index`").orderBy("`index` DESC").limit(1).where("nodeId=?", arrayNodeId).executeOne(parent, this.accessor);
        if (row!=null)
        {
            index=row.getINTEGER(0)+1;
        }
        Insert.table("_array").value("elementId",elementId).value("nodeId",arrayNodeId).value("`index`",index).execute(parent, this.accessor);
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
    
    
}
