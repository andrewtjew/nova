package xp.nova.sqldb.graph;

import java.sql.Timestamp;

import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.debug.LogLevel;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Insert;
import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.sqldb.Select;
import org.nova.sqldb.SqlUtils;
import org.nova.sqldb.Transaction;
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
                ,"INSERT INTO `@transaction` (created,creatorId,source) VALUES(?,?,?)"
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
        long nodeId=Insert.table("`@node`").value("transactionId",this.transactionId).executeAndReturnLongKey(parent, this.accessor);
        updateNode(nodeId,objects);
        return nodeId;
    }

    public long createNode(Node node) throws Throwable
    {
        return createNode(node.nodeObjects);
    }

    public void updateNode(long nodeId,NodeObject...nodeObjects) throws Throwable
    {
        for (NodeObject nodeObject:nodeObjects)
        {
            if (nodeObject==null)
            {
                continue;
            }
            if ((nodeObject._nodeId!=null)&&(nodeObject._nodeId!=nodeId))
            {
                throw new Exception("object "+nodeObject.getClass().getSimpleName()+" belongs to another node. objectNodeId="+nodeId+", nodeId="+nodeId);
            }
            _put(nodeId,nodeObject);
        }
    }

    public void update(NodeObject...nodeObjects) throws Throwable
    {
        if (nodeObjects.length>0)
        {
            Long nodeId=nodeObjects[0]._nodeId;
            if (nodeId==null)
            {
                throw new Exception();
            }
            updateNode(nodeId,nodeObjects);
        }
    }

    private void _put(long nodeId, NodeObject nodeObject) throws Throwable
    {
        //OPTIMIZE: the sql statements can be pre-calculated. 
        nodeObject._nodeId=nodeId;
        nodeObject._transactionId=this.transactionId;
        Class<? extends NodeObject> type=nodeObject.getClass();
        GraphObjectDescriptor descriptor=this.graph.register(type);
        if (descriptor==null)
        {
            for (String name:this.graph.getTypes())
            {
                System.out.println("Type="+name);
            }
            throw new Exception("Type not registered:"+type.getSimpleName());
        }
        this.graph.invalidateCacheLines(parent, descriptor);
        
        String selectSql="SELECT * FROM "+descriptor.getTableName()+" WHERE _nodeId=?";
        RowSet rowSet=accessor.executeQuery(parent, null, selectSql,nodeId);
        if (rowSet.size()==0)
        {
            FieldDescriptor[] columnAccessors=descriptor.getFieldDescriptors();

            StringBuilder insertColumnNames=new StringBuilder();
            StringBuilder insertValuePlaceholders=new StringBuilder();

            int length=columnAccessors.length-1;
            if (descriptor.getObjectType()==GraphObjectType.NODE)
            {
                length++; 
            }
            
            Object[] insertValues=new Object[length];
            int insertIndex=0;
            insertValues[insertIndex++]=nodeId;
            insertValues[insertIndex++]=this.transactionId;
            insertColumnNames.append("_nodeId,`_transactionId`");
            insertValuePlaceholders.append("?,?");
            
            for (FieldDescriptor columnAccessor:columnAccessors)
            {
                if (columnAccessor.isInternal())
                {
                    continue;
                }
                String name=columnAccessor.getName();
                Object value=columnAccessor.get(nodeObject);
                insertColumnNames.append(',');
                insertColumnNames.append('`'+name+'`');
                insertValuePlaceholders.append(",?");
                insertValues[insertIndex++]=value;
            }

            String sql="INSERT INTO "+descriptor.getTableName()+"("+insertColumnNames+") VALUES ("+insertValuePlaceholders+")";
            if (Debug.ENABLE&&Graph.DEBUG&&Graph.DEBUG_QUERY)
            {
                StringBuilder sb=new StringBuilder(sql);
                if (insertValues.length>0)
                {
                    sb.append("(");
                    for (int i=0;i<insertValues.length;i++)
                    {
                        if (i>0)
                        {
                            sb.append(',');
                        }
                        sb.append(insertValues[i]);
                    }
                    sb.append(")");
                }
                Debugging.log(Graph.DEBUG_CATEGORY,sb.toString());
            }
            if (nodeObject instanceof IdentityNodeObject)
            {
                ((IdentityNodeObject)nodeObject)._id=accessor.executeUpdateAndReturnGeneratedKeys(parent, null, sql, insertValues).getAsLong(0);
            }
            else
            {
                accessor.executeUpdate(parent, null, sql, insertValues);
            }
            Insert.table("`@nodetype`").value("id", nodeId).value("type", descriptor.getTypeName()).execute(parent, accessor);
        }
        else if (rowSet.size()==1)
        {
            FieldDescriptor[] columnAccessors=descriptor.getFieldDescriptors();
            StringBuilder update=new StringBuilder();
            int length=columnAccessors.length-1;
            if (descriptor.getObjectType()==GraphObjectType.NODE)
            {
                length++; 
            }
            Object[] updateValues=new Object[length];
            int updateValueIndex=0;
            update.append("_transactionId=?");
            updateValues[updateValueIndex++]=this.transactionId;

            
            for (FieldDescriptor columnAccessor:columnAccessors)
            {
                if (columnAccessor.isInternal())
                {
                    String name=columnAccessor.getName();
                    System.out.println("column skipped="+name);
                    continue;
                }
                String name=columnAccessor.getName();
                Object value=columnAccessor.get(nodeObject);
                update.append(",`"+name+"`=?");
                updateValues[updateValueIndex++]=value;
            }
            updateValues[updateValueIndex++]=nodeId;

            String updateSql="UPDATE "+descriptor.getTableName()+" SET "+update+" WHERE _nodeId=?";
            if (Graph.DEBUG_QUERY)
            {
                StringBuilder sb=new StringBuilder(updateSql);
                if (updateValues.length>0)
                {
                    sb.append("(");
                    for (int i=0;i<updateValues.length;i++)
                    {
                        if (i==0)
                        {
                            sb.append('(');
                        }
                        else
                        {
                            sb.append(',');
                        }
                        sb.append(updateValues[i]);
                    }
                    sb.append(")");
                }
                Debugging.log(Graph.DEBUG_CATEGORY,sb.toString());
            }
            versionRow(parent,descriptor,accessor,rowSet.getColumnNames(),rowSet.getRow(0));
            accessor.executeUpdate(parent, null, updateSql, updateValues);
            
        }
        else
        {
            throw new Exception("size="+rowSet.size());
        }
    }

    private void versionRow(Trace parent,GraphObjectDescriptor descriptor,Accessor accessor,String[] columnNames,Row row) throws Throwable
    {
        StringBuilder insertColumnNames=new StringBuilder();
        StringBuilder insertValuePlaceholders=new StringBuilder();
        Object[] insertValues=new Object[row.getColumns()];
        for (int i=0;i<columnNames.length;i++)
        {
            if (i>0)
            {
                insertColumnNames.append(',');
                insertValuePlaceholders.append(',');
            }
            insertColumnNames.append('`'+columnNames[i]+'`');
            insertValuePlaceholders.append('?');
            insertValues[i]=row.getObjects()[i];
        }
        String insertSql="INSERT INTO "+descriptor.getVersionedTableName()+"("+insertColumnNames+") VALUES ("+insertValuePlaceholders+")";
        accessor.executeUpdate(parent, null, insertSql, insertValues);
    }
    private void versionLinks(Trace parent,Accessor accessor,RowSet rowSet) throws Throwable
    {
        for (Row row:rowSet.rows())
        {
            Object[] insertValues=new Object[row.getColumns()+1];
            for (int i=0;i<rowSet.getColumns();i++)
            {
                insertValues[i]=row.getObjects()[i];
            }
            insertValues[rowSet.getColumns()]=SqlUtils.now();
            
            String insertSql="INSERT INTO `@deletedlink` (`nodeId`,`fromNodeId`,`toNodeId`,`relationValue`,`fromNodeType`,`toNodeType`,`deleted`) VALUES (?,?,?,?,?,?,?)";
            accessor.executeUpdate(parent, null, insertSql, insertValues);
        }
    }

//    private void invalidateNodeTables(Trace parent,long nodeId) throws Throwable
//    {
//        if (this.graph.performanceMonitor.caching==false)
//        {
//            return;
//        }
//        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT type FROM `@nodetype` WHERE id=?",nodeId);
//        for (Row row:rowSet.rows())
//        {
//            GraphObjectDescriptor descriptor=this.graph.getGraphObjectDescriptor(row.getVARCHAR(0));
//            this.graph.invalidateCacheLines(parent, descriptor);
//        }
//    }
    
    
    public long createLink(NodeObject fromNode,Relation_ relation,Class<? extends NodeObject> toNodeType,long toNodeId) throws Throwable
    {
        return _link(fromNode.getClass(),fromNode.getNodeId(),relation,toNodeType,toNodeId);
    }
    public long createLink(NodeObject fromNode,Relation_ relation,NodeObject toNode) throws Throwable
    {
        return _link(fromNode.getClass(),fromNode.getNodeId(),relation,toNode.getClass(),toNode.getNodeId());
    }
    public long createLink(Class<? extends NodeObject> fromNodeType,long fromNodeId,Relation_ relation,NodeObject toNode) throws Throwable
    {
        return _link(fromNodeType,fromNodeId,relation,toNode.getClass(),toNode.getNodeId());
    }

    private long _link(Class<? extends NodeObject> fromNodeType, long fromNodeId,Relation_ relation,Class<? extends NodeObject> toNodeType,long toNodeId) throws Throwable
    {
        long relationValue=relation.getValue();
        
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT nodeId FROM `@link` WHERE fromNodeId=? AND toNodeId=? AND relationValue=?",fromNodeId,toNodeId,relationValue);
        if (rowSet.size()==1)
        {
            return rowSet.getRow(0).getBIGINT(0);
        }
        this.graph.invalidateCacheLines(parent, fromNodeId);
        this.graph.invalidateCacheLines(parent, toNodeId);
        long nodeId=Insert.table("`@node`").value("transactionId",this.transactionId).executeAndReturnLongKey(parent, this.accessor);
        Insert.table("`@link`").value("fromNodeType",fromNodeType.getSimpleName()).value("nodeId",nodeId).value("fromNodeId",fromNodeId).value("toNodeType",toNodeType.getSimpleName()).value("toNodeId", toNodeId)
                .value("relationValue", relationValue)
                .execute(parent, this.accessor);
        return nodeId;
    }
    
    public int deleteNodeLinks(NodeObject node,Direction direction,Relation_ relation) throws Throwable
    {
        //Can be optimized to reduce SELECT in _deleteLink, but this function is likely not used often.
        int deleted=0;
        long nodeId=node.getNodeId();
        if (direction==Direction.FROM)
        {
            RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT toNodeId FROM `@link` WHERE fromNodeId=? AND relationValue=?",nodeId,relation);
            for (Row row:rowSet.rows())
            {
                deleted+=_deleteLink(nodeId,relation.getValue(),row.getBIGINT(0));
            }
        }
        else
        {
            RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT fromNodeId FROM `@link` WHERE toNodeId=? AND relationValue=?",nodeId,relation);
            for (Row row:rowSet.rows())
            {
                deleted+=_deleteLink(row.getBIGINT(0),relation.getValue(),nodeId);
            }
        }
        return deleted;
    }

    private int _deleteLink(long fromNodeId,long relationValue,long toNodeId) throws Throwable
    {
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT * FROM `@link` WHERE fromNodeId=? AND toNodeId=? AND relationValue=?",fromNodeId,toNodeId,relationValue);
        if (rowSet.size()==0)
        {
            return 0;
        }
        else if (rowSet.size()!=1)
        {
            throw new Exception();
        }
        versionLinks(parent,this.accessor,rowSet);
        long linkNodeId=rowSet.getRow(0).getBIGINT("nodeId");
        this.graph.invalidateCacheLines(parent, fromNodeId);
        this.graph.invalidateCacheLines(parent, toNodeId);
        int deleted=this.accessor.executeUpdate(this.parent,null,"DELETE FROM `@link` WHERE nodeId=?",linkNodeId);
        deleted+=_deleteNode(linkNodeId);
        return deleted;
    }
    public int deleteLink(NodeObject fromNode,Relation_ relation,long toNodeId) throws Throwable
    {
        return _deleteLink(fromNode.getNodeId(),relation.getValue(),toNodeId);
    }
    public int deleteLink(NodeObject fromNode,Relation_ relation,NodeObject toNode) throws Throwable
    {
        return _deleteLink(fromNode.getNodeId(),relation.getValue(),toNode.getNodeId());
    }

    private int _deleteNode(long nodeId) throws Throwable
    {
        int deleted=this.accessor.executeUpdate(this.parent,null,"DELETE FROM `@node` WHERE id=?",nodeId);
        if (deleted>0)
        {
            this.graph.invalidateCacheLines(parent, nodeId);
            RowSet types=this.accessor.executeQuery(parent, null, "SELECT type FROM `@nodetype` WHERE id=?",nodeId);
            for (Row row:types.rows())
            {
                String typeName=row.getVARCHAR(0);
                var descriptor=this.graph.getGraphObjectDescriptor(typeName);
                String selectSql="SELECT * FROM "+descriptor.getTableName()+" WHERE _nodeId=?";
                RowSet rowSet=this.accessor.executeQuery(parent, null, selectSql,nodeId);
                versionRow(parent, descriptor, accessor, rowSet.getColumnNames(),rowSet.getRow(0));
            }
            Timestamp now=SqlUtils.now();
            this.accessor.executeQuery(this.parent,null,"INSERT INTO `@deletednode` (id,deleted) VALUES(?,?)",nodeId,now);
            
            RowSet rowSet=this.accessor.executeQuery(this.parent,null,"SELECT fromNodeId,relationValue,toNodeId FROM `@link` WHERE fromNodeId=? OR toNodeId=?",nodeId,nodeId);
            for (Row row:rowSet.rows())
            {
                long fromNodeId=row.getBIGINT("fromNodeId");
                long toNodeId=row.getBIGINT("toNodeId");
                long relationValue=row.getBIGINT("relationValue");
                deleted+=_deleteLink(fromNodeId,relationValue,toNodeId);
            }
        }
        if (Debug.ENABLE && Graph.DEBUG && Graph.DEBUG_CACHING)
        {
            Debugging.log(Graph.DEBUG_CATEGORY,"deleteNode:nodeId="+nodeId+", deleted="+deleted);
        }
        return deleted;
    }

    public int deleteNode(long nodeId) throws Throwable
    {
        return _deleteNode(nodeId);
    }

    public int deleteNode(NodeObject node) throws Throwable
    {
        if (node._nodeId==null)
        {
            throw new Exception();
        }
        return deleteNode(node._nodeId);
    }
//    public int deleteNodes(NodeObject...nodes) throws Throwable
//    {
//        int deleted=0;
//        for (NodeObject node:nodes)
//        {
//            deleted+=deleteNode(node);
//        }
//        return deleted;
//    }
    
    public void commit() throws Throwable
    {
        this.transaction.commit();
    }
    public void rollback() throws Throwable
    {
        this.graph.clearCache();
        this.transaction.rollback();
    }

    private Node[] toNodes(NodeObject...nodeObjects)
    {
        Node[] nodes=new Node[nodeObjects.length];
        for (int i=0;i<nodes.length;i++)
        {
            NodeObject element=nodeObjects[i];
            if (element!=null)
            {
                nodes[i]=new Node(element);
            }
        }
        return nodes;
    }
    
    public void createArray(NodeObject arrayObject,NodeObject...elements) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        deleteArray(arrayObject);
        _createArray(arrayNodeId,0,toNodes(elements));
    }

    public void createArray(NodeObject arrayObject,Node...elements) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        deleteArray(arrayObject);
        _createArray(arrayNodeId,0,elements);
    }
    public void updateArrayElement(NodeObject arrayObject,int index,NodeObject...nodeObjects) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT elementId FROM `@array` WHERE nodeId=? AND index=?",arrayNodeId,index);
        if (rowSet.size()!=1)
        {
            throw new Exception("Invalid index: index="+index);
        }
        Long elementId=rowSet.getRow(0).getNullableBIGINT(0);
        if (elementId!=null)
        {
            this.updateNode(elementId, nodeObjects);
        }
        else
        {
            elementId=this.createNode(nodeObjects);
            this.accessor.executeUpdate(parent, null, "UPDATE `@array' SET elementId=? WHERE nodeId=? AND index=?", elementId,arrayNodeId,index);
        }
        this.graph.invalidateCacheLines(parent, arrayNodeId);
    }
    public void appendToArray(NodeObject arrayObject,NodeObject...elements) throws Throwable
    {
        appendToArray(arrayObject,toNodes(elements));
    }    
    public void appendToArray(NodeObject arrayObject,Node...elements) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        int base=0;
        var row=Select.source("`@array`").columns("`index`").orderBy("`index` DESC").limit(1).where("nodeId=?", arrayNodeId).executeOne(parent, this.accessor);
        if (row!=null)
        {
            base=row.getINTEGER(0)+1;
        }
        _createArray(arrayNodeId,base,elements);
    }
    private void _createArray(long arrayNodeId,int base,Node[] elements) throws Throwable
    {
        for (int i=0;i<elements.length;i++)
        {
            Node node=elements[i];
            if (node!=null)
            {
                long elementId=createNode(node);
                Insert.table("`@array`").value("elementId",elementId).value("nodeId",arrayNodeId).value("`index`",i+base).execute(parent, this.accessor);
            }
            else
            {
                Insert.table("`@array`").value("nodeId",arrayNodeId).value("`index`",i+base).execute(parent, this.accessor);
            }
        }
        this.graph.invalidateCacheLines(parent, arrayNodeId);
    }
    
    public int deleteArray(NodeObject arrayObject) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        int deleted=0;
        RowSet rowSet=accessor.executeQuery(parent, null, "SELECT `elementId`,`index` FROM `@array` WHERE nodeId=?",arrayNodeId);
        if (rowSet.size()==0)
        {
            return deleted;
        }
        long version=accessor.executeUpdateAndReturnGeneratedKeys(parent, null, "INSERT INTO `@deletedarray` (`deleted`,`nodeId`) VALUES (?,?)", SqlUtils.now(),arrayNodeId).getAsLong(0);
        for (Row row:rowSet.rows())
        {
            Long elementId=row.getNullableBIGINT("elementId");
            if (elementId!=null)
            {
                deleted+=this._deleteNode(elementId);
            }
            int index=row.getINTEGER("index");
            accessor.executeUpdate(parent, null, "INSERT INTO `@deletedelement` (`version`,`elementId`,`index`) VALUES (?,?,?)", version,elementId,index);
        }
        this.accessor.executeUpdate(this.parent,null,"DELETE FROM `@array` WHERE nodeId=?",arrayNodeId);
        this.graph.invalidateCacheLines(parent, arrayNodeId);
        return deleted;
    }
    
    
}
