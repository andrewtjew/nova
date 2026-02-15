package xp.nova.sqldb.graph;

import java.sql.Timestamp;

import org.nova.debug.Debug;
import org.nova.debug.Debugging;
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
    private boolean closed=false;

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
        if (this.closed==false)
        {
            this.graph.clearCache();
            this.closed=true;
        }
        this.transaction.close();
        if (this.autoCloseGraphAccessor)
        {
            this.graphAccessor.close();
        }
    }

    public long createNode(NodeObject...objects) throws Throwable
    {
        long nodeId=Insert.table("`@node`").value("transactionId",this.transactionId).executeAndReturnLongKey(parent, this.accessor);
        update(nodeId,objects);
        return nodeId;
    }

    public long createNode(Node node) throws Throwable
    {
        return createNode(node.nodeObjects);
    }

    public void update(long nodeId,NodeObject...nodeObjects) throws Throwable
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
            update(nodeId,nodeObjects);
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
            if (descriptor.isVersioningDisabled()==false)
            {
                versionRow(parent,descriptor,accessor,rowSet.getColumnNames(),rowSet.getRow(0));
            }
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
            
            String insertSql="INSERT INTO `@deletedlink` (`nodeId`,`fromNodeId`,`toNodeId`,`relation`,`fromNodeType`,`toNodeType`,`deleted`) VALUES (?,?,?,?,?,?,?)";
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
        String relationKey=Relation_.getKey(relation);
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT nodeId FROM `@link` WHERE fromNodeId=? AND toNodeId=? AND relation=?",fromNodeId,toNodeId,relationKey);
        if (rowSet.size()==1)
        {
            return rowSet.getRow(0).getBIGINT(0);
        }
        
        this.graph.invalidateCacheLines(parent, fromNodeId);
        this.graph.invalidateCacheLines(parent, toNodeId);
        long nodeId=Insert.table("`@node`").value("transactionId",this.transactionId).executeAndReturnLongKey(parent, this.accessor);
        Insert.table("`@link`").value("fromNodeType",fromNodeType.getSimpleName()).value("nodeId",nodeId).value("fromNodeId",fromNodeId).value("toNodeType",toNodeType.getSimpleName()).value("toNodeId", toNodeId)
                .value("relation", relationKey)
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
            RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT toNodeId FROM `@link` WHERE fromNodeId=? AND relation=?",nodeId,relation);
            for (Row row:rowSet.rows())
            {
                deleted+=_deleteLink(nodeId,Relation_.getKey(relation),row.getBIGINT(0));
            }
        }
        else
        {
            RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT fromNodeId FROM `@link` WHERE toNodeId=? AND relation=?",nodeId,relation);
            for (Row row:rowSet.rows())
            {
                deleted+=_deleteLink(row.getBIGINT(0),Relation_.getKey(relation),nodeId);
            }
        }
        return deleted;
    }

    private int _deleteLink(long fromNodeId,String relationKey,long toNodeId) throws Throwable
    {
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT * FROM `@link` WHERE fromNodeId=? AND toNodeId=? AND relation=?",fromNodeId,toNodeId,relationKey);
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
        return _deleteLink(fromNode.getNodeId(),Relation_.getKey(relation),toNodeId);
    }
    public int deleteLink(NodeObject fromNode,Relation_ relation,NodeObject toNode) throws Throwable
    {
        return _deleteLink(fromNode.getNodeId(),Relation_.getKey(relation),toNode.getNodeId());
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
//                if (descriptor==null)
//                {
//                    throw new Exception("Type not registered. typeName="+typeName);
//                }
                String selectSql="SELECT * FROM "+descriptor.getTableName()+" WHERE _nodeId=?";
                RowSet rowSet=this.accessor.executeQuery(parent, null, selectSql,nodeId);
                versionRow(parent, descriptor, accessor, rowSet.getColumnNames(),rowSet.getRow(0));
            }
            Timestamp now=SqlUtils.now();
            this.accessor.executeQuery(this.parent,null,"INSERT INTO `@deletednode` (id,deleted) VALUES(?,?)",nodeId,now);
            
            RowSet rowSet=this.accessor.executeQuery(this.parent,null,"SELECT fromNodeId,relation,toNodeId FROM `@link` WHERE fromNodeId=? OR toNodeId=?",nodeId,nodeId);
            for (Row row:rowSet.rows())
            {
                long fromNodeId=row.getBIGINT("fromNodeId");
                long toNodeId=row.getBIGINT("toNodeId");
                String relationKey=row.getVARCHAR("relation");
                deleted+=_deleteLink(fromNodeId,relationKey,toNodeId);
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
    
    public void commit() throws Throwable
    {
        this.transaction.commit();
        this.closed=true;
    }
    public void rollback() throws Throwable
    {
        this.graph.clearCache();
        this.transaction.rollback();
        this.closed=true;
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
    
    public long createArray(NodeObject arrayObject,Relation_ relation,Node...elements) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        deleteArray(arrayObject,relation);
        return _putArray(arrayNodeId,relation,0,elements,false);
    }
    public long createArray(NodeObject arrayObject,Relation_ relation,NodeObject...elements) throws Throwable
    {
        return createArray(arrayObject,relation,toNodes(elements));
    }
    public long createArray(NodeObject arrayObject,Node...elements) throws Throwable
    {
        return createArray(arrayObject,null,elements);
    }
    public <ELEMENT extends NodeObject> long createArray(NodeObject arrayObject,ELEMENT...elements) throws Throwable
    {
        return createArray(arrayObject,null,elements);
    }
    
    private long _putArray(long arrayNodeId,Relation_ relation,int index,Node[] nodes,boolean insert) throws Throwable
    {
        String relationKey=Relation_.getKey(relation);
        long nodeId;
        RowSet rowSet=accessor.executeQuery(parent, null, "SELECT `nodeId` FROM `@arraylink` WHERE arrayNodeId=? AND relation=?",arrayNodeId,relationKey);
        if (rowSet.size()==0)
        {
            nodeId=accessor.executeUpdateAndReturnGeneratedKeys(parent, null, "INSERT INTO `@arraylink` (arrayNodeId,relation) VALUES (?,?)", arrayNodeId,relationKey).getAsLong(0);
        }
        else if (rowSet.size()==1)
        {
            nodeId=rowSet.getRow(0).getBIGINT(0);
        }
        else
        {
            throw new Exception("rows="+rowSet.size());
        }
        
        if (insert)
        {
            this.accessor.executeUpdate(parent, null, "UPDATE `@array` SET `index`=`index`+? WHERE nodeId=? AND `index`>=?",nodes.length,nodeId,index);
        }
        
        for (int i=0;i<nodes.length;i++)
        {
            Node node=nodes[i];
            if (node!=null)
            {
                Long elementId=node.nodeObjects[0].getNodeId();
                if (elementId==null)
                {
                    elementId=createNode(node);
                }
                accessor.executeUpdate(parent, null, "INSERT INTO `@array` (nodeId,elementId,`index`) VALUES (?,?,?)", nodeId,elementId,i+index);                
            }
            else
            {
                accessor.executeUpdate(parent, null, "INSERT INTO `@array` (nodeId,`index`) VALUES (?,?)", nodeId,i+index);                
            }
        }
        
        
        this.graph.invalidateCacheLines(parent, arrayNodeId);
        return nodeId;
    }

//    public void updateArrayElement(NodeObject arrayObject,Relation_ relation,int index,NodeObject...nodeObjects) throws Throwable
//    {
//        Long arrayNodeId=arrayObject._nodeId;
//        if (arrayNodeId==null)
//        {
//            throw new Exception();
//        }
//        String relationKey=relation.getKey();
//        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT `elementId`,`nodeId` FROM `@arraylink` JOIN `@arraylink.nodeId` ON `@array.nodeId` ON WHERE `arrayNodeId`=? AND relation=? AND `index`=?",arrayNodeId,relationKey,index);
//        if (rowSet.size()!=1)
//        {
//            throw new Exception("Not found: index="+index+", relation="+relationKey);
//        }
//        Long elementId=rowSet.getRow(0).getNullableBIGINT(0);
//        if (elementId!=null)
//        {
//            this.update(elementId, nodeObjects);
//        }
//        else
//        {
//            elementId=this.createNode(nodeObjects);
//            this.accessor.executeUpdate(parent, null, "UPDATE `@array` SET `elementId`=? WHERE `nodeId`=? AND `index`=?", elementId,arrayNodeId,index);
//        }
//        this.graph.invalidateCacheLines(parent, arrayNodeId);
//    }
    private boolean _removeArrayElements(NodeObject arrayObject,Relation_ relation,int index,int count,boolean delete) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        String relationKey=Relation_.getKey(relation);
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT elementId,`@arraylink`.nodeId FROM `@arraylink` JOIN `@array` ON `@arraylink`.nodeId=`@array`.nodeId WHERE arrayNodeId=? AND relation=? AND `index`>=?",arrayNodeId,relationKey,index);
        if (rowSet.size()==0)
        {
            return false;
        }
        if (delete)
        {
            for (int i=0;i<rowSet.size();i++)
            {
                Row row=rowSet.getRow(i);
                Long elementId=row.getNullableBIGINT(0);
                if (elementId!=null)
                {
                    _deleteNode(elementId);
                }
            }
        }
        long nodeId=rowSet.getRow(0).getBIGINT(1);
        if ((index==0)&&(count>=rowSet.size()))
        {
            this.accessor.executeUpdate(this.parent,null,"DELETE FROM `@arraylink` WHERE nodeId=?",nodeId);
            this.accessor.executeUpdate(this.parent,null,"DELETE FROM `@array` WHERE nodeId=?",nodeId);
        }
        else if (count==1) //To eliminate any range search for the common case.
        {
            this.accessor.executeUpdate(this.parent,null,"DELETE FROM `@array` WHERE nodeId=? AND `index`=?",nodeId,index);
            this.accessor.executeUpdate(parent, null, "UPDATE `@array` SET `index`=`index`-1 WHERE nodeId=? AND `index`>?",nodeId,index);
        }
        else 
        {
            this.accessor.executeUpdate(this.parent,null,"DELETE FROM `@array` WHERE nodeId=? AND `index`>=? AND `index`<?",nodeId,index,index+count);
            this.accessor.executeUpdate(parent, null, "UPDATE `@array` SET `index`=`index`-? WHERE nodeId=? AND `index`>=?",count,nodeId,index+count);
        }
        this.graph.invalidateCacheLines(parent, arrayNodeId);
        return true;
    }

    public boolean removeArrayElement(NodeObject arrayObject,Relation_ relation,int index) throws Throwable
    {
        return _removeArrayElements(arrayObject, relation, index, 1,false);
    }
    public boolean removeArrayElement(NodeObject arrayObject,int index) throws Throwable
    {
        return removeArrayElement(arrayObject,null,index);
    }
    public boolean removeArrayElements(NodeObject arrayObject,Relation_ relation,int index,int count) throws Throwable
    {
        return _removeArrayElements(arrayObject, relation, index, count,false);
    }
    public boolean removeArrayElements(NodeObject arrayObject,int index,int count) throws Throwable
    {
        return removeArrayElements(arrayObject,null,index,count);
    }

    public boolean deleteArrayElement(NodeObject arrayObject,Relation_ relation,int index) throws Throwable
    {
        return _removeArrayElements(arrayObject, relation, index, 1, true);
    }
    public boolean deleteArrayElement(NodeObject arrayObject,int index) throws Throwable
    {
        return deleteArrayElement(arrayObject,null,index);
    }

    public boolean deleteArrayElements(NodeObject arrayObject,Relation_ relation,int index,int count) throws Throwable
    {
        return _removeArrayElements(arrayObject, relation, index, count, true);
    }
    public boolean deleteArrayElements(NodeObject arrayObject,int index,int count) throws Throwable
    {
        return deleteArrayElements(arrayObject,null,index,count);
    }

    //----
    private boolean _removeArrayElements(NodeObject arrayObject,Relation_ relation,boolean delete,NodeObject...nodeObjects) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        String relationKey=Relation_.getKey(relation);
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT `@arraylink`.nodeId FROM `@arraylink` WHERE arrayNodeId=? AND relation=?",arrayNodeId,relationKey);
        if (rowSet.size()!=1)
        {
            if (rowSet.size()>1)
            {
                throw new Exception("arrayNodeId="+arrayNodeId+", relationKey="+relationKey);
            }
            return false;
        }
        long nodeId=rowSet.getRow(0).getBIGINT(0);
        for (int i=0;i<nodeObjects.length;i++)
        {
            NodeObject nodeObject=nodeObjects[i];
            Long elementId=nodeObject.getNodeId();
            rowSet=this.accessor.executeQuery(parent, null, "SELECT `index` FROM `@array` WHERE nodeId=? AND elementId=?",nodeId,elementId);
            if (rowSet.size()!=1)
            {
                throw new Exception("arrayNodeId="+arrayNodeId+", relationKey="+relationKey+", elementId="+nodeObject.getNodeId());
            }
            if (delete)
            {
                _deleteNode(elementId);
            }
            int index=rowSet.getRow(0).getINTEGER(0);
            this.accessor.executeUpdate(this.parent,null,"DELETE FROM `@array` WHERE nodeId=? AND `index`=?",nodeId,index);
            int updated=this.accessor.executeUpdate(parent, null, "UPDATE `@array` SET `index`=`index`-1 WHERE nodeId=? AND `index`>?",nodeId,index);
            if ((index==0)&&(updated==0))
            {
                if (i!=nodeObjects.length-1)
                {
                    throw new Exception();//should be impossible.
                }
                this.accessor.executeUpdate(this.parent,null,"DELETE FROM `@arraylink` WHERE nodeId=?",nodeId);
            }
        }
        this.graph.invalidateCacheLines(parent, arrayNodeId);
        return true;
    }
    public boolean removeArrayElements(NodeObject arrayObject,Relation_ relation,NodeObject...nodeObjects) throws Throwable
    {
        return _removeArrayElements(arrayObject, relation, false, nodeObjects);
    }
    public boolean removeArrayElements(NodeObject arrayObject,NodeObject...nodeObjects) throws Throwable
    {
        return removeArrayElements(arrayObject, null, nodeObjects);
    }
    public boolean deleteArrayElements(NodeObject arrayObject,Relation_ relation,NodeObject...nodeObjects) throws Throwable
    {
        return _removeArrayElements(arrayObject, relation, true, nodeObjects);
    }
    public boolean deleteArrayElements(NodeObject arrayObject,NodeObject...nodeObjects) throws Throwable
    {
        return deleteArrayElements(arrayObject, null, nodeObjects);
    }
    //--
    
    public boolean exchangeArrayElements(NodeObject arrayObject,Relation_ relation,int indexA,int indexB) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        String relationKey=Relation_.getKey(relation);
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT elementId,`@arraylink`.nodeId,`index` FROM `@arraylink` JOIN `@array` ON `@arraylink`.nodeId=`@array`.nodeId WHERE arrayNodeId=? AND relation=? AND `index` IN (?,?)",arrayNodeId,relationKey,indexA,indexB);
        if (rowSet.size()!=2)
        {
            return false;
        }
        Row rowA=rowSet.getRow(0);
        Row rowB=rowSet.getRow(1);
        Long elementIdA=rowA.getNullableBIGINT(0);
        Long elementIdB=rowB.getNullableBIGINT(0);
        long nodeIdA=rowA.getBIGINT(1);
        long nodeIdB=rowB.getBIGINT(1);
        indexA=rowA.getINTEGER(2);
        indexB=rowB.getINTEGER(2);
        
        this.accessor.executeUpdate(parent, null, "UPDATE `@array` SET elementId=? WHERE nodeId=? AND `index`=?", elementIdB,nodeIdA,indexA);
        this.accessor.executeUpdate(parent, null, "UPDATE `@array` SET elementId=? WHERE nodeId=? AND `index`=?", elementIdA,nodeIdB,indexB);
        this.graph.invalidateCacheLines(parent, arrayNodeId);
        return true;
    }
    public boolean exchangeArrayElements(NodeObject arrayObject,int indexA,int indexB) throws Throwable
    {
        return exchangeArrayElements(arrayObject, null, indexA, indexB);
    }
    public boolean exchangeArrayElements(NodeObject arrayObject,Relation_ relation,NodeObject objectA,NodeObject objectB) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        long elementIdA=objectA.getNodeId();
        long elementIdB=objectB.getNodeId();
        String relationKey=Relation_.getKey(relation);
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT `@arraylink`.nodeId,`index` FROM `@arraylink` JOIN `@array` ON `@arraylink`.nodeId=`@array`.nodeId WHERE arrayNodeId=? AND relation=? AND `elementId` IN (?,?)",arrayNodeId,relationKey,elementIdA,elementIdB);
        if (rowSet.size()!=2)
        {
            return false;
        }
        Row rowA=rowSet.getRow(0);
        Row rowB=rowSet.getRow(1);
        long nodeIdA=rowA.getBIGINT(0);
        long nodeIdB=rowB.getBIGINT(0);
        int indexA=rowA.getINTEGER(1);
        int indexB=rowB.getINTEGER(1);
        
        this.accessor.executeUpdate(parent, null, "UPDATE `@array` SET elementId=? WHERE nodeId=? AND `index`=?", elementIdB,nodeIdA,indexA);
        this.accessor.executeUpdate(parent, null, "UPDATE `@array` SET elementId=? WHERE nodeId=? AND `index`=?", elementIdA,nodeIdB,indexB);
        this.graph.invalidateCacheLines(parent, arrayNodeId);
        return true;
    }
    public boolean exchangeArrayElements(NodeObject arrayObject,NodeObject objectA,NodeObject objectB) throws Throwable
    {
        return exchangeArrayElements(arrayObject, null, objectA, objectB);
    }
    
    public long appendToArray(NodeObject arrayObject,Relation_ relation,Node...nodes) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        int index=0;
        String relationKey=Relation_.getKey(relation);
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT `index` FROM `@arraylink` JOIN `@array` ON `@arraylink`.nodeId=`@array`.nodeId WHERE arrayNodeId=? AND relation=? ORDER BY `index` DESC LIMIT 1",arrayNodeId,relationKey);
        if (rowSet.size()==1)
        {
            index=rowSet.getRow(0).getINTEGER(0)+1;
        }
        return _putArray(arrayNodeId,relation,index,nodes,false);
    }
    public long appendToArray(NodeObject arrayObject,Relation_ relation,NodeObject...elements) throws Throwable
    {
        return appendToArray(arrayObject,relation,toNodes(elements));
    }    
    public <ELEMENT extends NodeObject> long appendToArray(NodeObject arrayObject,ELEMENT...elements) throws Throwable
    {
        return appendToArray(arrayObject,null,elements);
    }    

    public long insertIntoArray(NodeObject arrayObject,Relation_ relation,int index,Node...nodes) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        return _putArray(arrayNodeId,relation,index,nodes,true);
    }
    public long insertIntoArray(NodeObject arrayObject,Relation_ relation,int index,NodeObject...nodeObjects) throws Throwable
    {
        return insertIntoArray(arrayObject,relation,index,toNodes(nodeObjects));
    }
    public long insertIntoArray(NodeObject arrayObject,int index,NodeObject...nodeObjects) throws Throwable
    {
        return insertIntoArray(arrayObject,null,index,nodeObjects);
    }

    private int _removeArray(NodeObject arrayObject,Relation_ relation,boolean delete) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        String relationKey=Relation_.getKey(relation);
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT elementId,`@arraylink`.nodeId FROM `@arraylink` JOIN `@array` ON `@arraylink`.nodeId=`@array`.nodeId WHERE arrayNodeId=? AND relation=?",arrayNodeId,relationKey);
        if (rowSet.size()==0)
        {
            return 0;
        }
        long nodeId=rowSet.getRow(0).getBIGINT(0);
        int deleted=0;
        if (delete)
        {
            for (Row row:rowSet.rows())
            {
                Long elementId=row.getNullableBIGINT("elementId");
                if (elementId!=null)
                {
                    deleted+=this._deleteNode(elementId);
                }
            }
        }
        deleted+=this.accessor.executeUpdate(this.parent,null,"DELETE FROM `@arraylink` WHERE nodeId=?",nodeId);
        deleted+=this.accessor.executeUpdate(this.parent,null,"DELETE FROM `@array` WHERE nodeId=?",nodeId);
        this.graph.invalidateCacheLines(parent, arrayNodeId);
        return deleted;
    }

    public boolean removeArray(NodeObject arrayObject,Relation_ relation) throws Throwable
    {
        return _removeArray(arrayObject, relation, false)>0;
    }
    public int deleteArray(NodeObject arrayObject,Relation_ relation) throws Throwable
    {
        return _removeArray(arrayObject, relation, true);
    }

    public boolean removeArray(NodeObject arrayObject) throws Throwable
    {
        return _removeArray(arrayObject, null, false)>0;
    }
    public int deleteArray(NodeObject arrayObject) throws Throwable
    {
        return _removeArray(arrayObject, null, true);
    }
}
