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
                ,"INSERT INTO `~transaction` (created,creatorId,source) VALUES(?,?,?)"
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

    public long create(Node...objects) throws Throwable
    {
        long nodeId=Insert.table("`~node`").value("transactionId",this.transactionId).executeAndReturnLongKey(parent, this.accessor);
        update(nodeId,objects);
        return nodeId;
    }

    public void update(long nodeId,Node...objects) throws Throwable
    {
        for (Node object:objects)
        {
            if (object==null)
            {
                continue;
            }
            if ((object._nodeId!=null)&&(object._nodeId!=nodeId))
            {
                throw new Exception("object "+object.getClass().getSimpleName()+" belongs to another node. objectNodeId="+nodeId+", nodeId="+nodeId);
            }
            _put(object,nodeId);
        }
    }

    public void update(Node...objects) throws Throwable
    {
        if (objects.length>0)
        {
            Long nodeId=objects[0]._nodeId;
            if (nodeId==null)
            {
                throw new Exception();
            }
            update(nodeId,objects);
        }
    }

    private void _put(Node object,long nodeId) throws Throwable
    {
        //OPTIMIZE: the sql statements can be pre-calculated. 
        object._nodeId=nodeId;
        Class<? extends Node> type=object.getClass();
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

            int length=columnAccessors.length;
            if (descriptor.getObjectType()==GraphObjectType.NODE)
            {
                length++; 
            }
            
            Object[] insertValues=new Object[length];
            int insertIndex=0;
            insertValues[insertIndex++]=nodeId;
            insertValues[insertIndex++]=this.transactionId;
            insertColumnNames.append("_nodeId,`~transactionId`");
            insertValuePlaceholders.append("?,?");
            
            for (FieldDescriptor columnAccessor:columnAccessors)
            {
                if (columnAccessor.isInternal())
                {
                    continue;
                }
                String name=columnAccessor.getName();
                Object value=columnAccessor.get(object);
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
                        if (i==0)
                        {
                            sb.append('(');
                        }
                        else
                        {
                            sb.append(',');
                        }
                        sb.append(insertValues[i]);
                    }
                    sb.append(")");
                }
                Debugging.log(Graph.DEBUG_CATEGORY,sb.toString());
            }
            if (object instanceof IdentityNode)
            {
                ((IdentityNode)object)._id=accessor.executeUpdateAndReturnGeneratedKeys(parent, null, sql, insertValues).getAsLong(0);
            }
            else
            {
                accessor.executeUpdate(parent, null, sql, insertValues);
            }
            Insert.table("`~nodetype`").value("id", nodeId).value("type", descriptor.getTypeName()).execute(parent, accessor);
        }
        else if (rowSet.size()==1)
        {
            FieldDescriptor[] columnAccessors=descriptor.getFieldDescriptors();
            StringBuilder update=new StringBuilder();
            int length=columnAccessors.length;
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
                Object value=columnAccessor.get(object);
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
            Object[] insertValues=new Object[row.getColumns()];
            for (int i=0;i<rowSet.getColumns();i++)
            {
                insertValues[i]=row.getObjects()[i];
            }
            String insertSql="INSERT INTO `~deletedlink` (`nodeId`,`fromNodeId`,`toNodeId`,`relationValue`,`fromNodeType`,`toNodeType`) VALUES (?,?,?,?,?,?)";
            accessor.executeUpdate(parent, null, insertSql, insertValues);
        }
    }

    private void invalidateNodeTables(Trace parent,long nodeId) throws Throwable
    {
        if (this.graph.performanceMonitor.caching==false)
        {
            return;
        }
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT type FROM `~nodetype` WHERE id=?",nodeId);
        for (Row row:rowSet.rows())
        {
            GraphObjectDescriptor descriptor=this.graph.getGraphObjectDescriptor(row.getVARCHAR(0));
            this.graph.invalidateCacheLines(parent, descriptor);
        }
    }
    
    
    private long _link(Class<? extends Node> fromNodeType, long fromNodeId,Relation_ relation,Class<? extends Node> toNodeType,long toNodeId) throws Throwable
    {
        long relationValue=relation.getValue();
        
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT nodeId FROM `~link` WHERE fromNodeId=? AND toNodeId=? AND relationValue=?",fromNodeId,toNodeId,relationValue);
        if (rowSet.size()==1)
        {
            return rowSet.getRow(0).getBIGINT(0);
        }
        this.graph.invalidateCacheLines(parent, fromNodeId);
        this.graph.invalidateCacheLines(parent, toNodeId);
        long nodeId=Insert.table("`~node`").value("transactionId",this.transactionId).executeAndReturnLongKey(parent, this.accessor);
        Insert.table("`~link`").value("fromNodeType",fromNodeType.getSimpleName()).value("nodeId",nodeId).value("fromNodeId",fromNodeId).value("toNodeType",toNodeType.getSimpleName()).value("toNodeId", toNodeId)
                .value("relationValue", relationValue)
                .execute(parent, this.accessor);
        return nodeId;
    }
    public long link(Node fromNode,Relation_ relation,Class<? extends Node> toNodeType,long toNodeId) throws Throwable
    {
        return _link(fromNode.getClass(),fromNode.getNodeId(),relation,toNodeType,toNodeId);
    }
    public long link(Node fromNode,Relation_ relation,Node toNode) throws Throwable
    {
        return _link(fromNode.getClass(),fromNode.getNodeId(),relation,toNode.getClass(),toNode.getNodeId());
    }
    public long link(Class<? extends Node> fromNodeType,long fromNodeId,Relation_ relation,Node toNode) throws Throwable
    {
        return _link(fromNodeType,fromNodeId,relation,toNode.getClass(),toNode.getNodeId());
    }
    
    public int deleteLinks(Direction direction,Node node,Relation_ relation) throws Throwable
    {
        //Can be optimized to reduce SELECT in _deleteLink, but this function is likely not used often.
        int deleted=0;
        long nodeId=node.getNodeId();
        if (direction==Direction.FROM)
        {
            RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT toNodeId FROM `~link` WHERE fromNodeId=? AND relationValue=?",nodeId,relation);
            for (Row row:rowSet.rows())
            {
                deleted+=_deleteLink(nodeId,relation.getValue(),row.getBIGINT(0));
            }
        }
        else
        {
            RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT fromNodeId FROM `~link` WHERE toNodeId=? AND relationValue=?",nodeId,relation);
            for (Row row:rowSet.rows())
            {
                deleted+=_deleteLink(row.getBIGINT(0),relation.getValue(),nodeId);
            }
        }
        return deleted;
    }

//    static long toRelationValue(Relation_ relation,RelationObjectType_ objectType)
//    {
//        long value=relation.getValue();
//        if (objectType!=null)
//        {
//            value=(value<<32)|objectType.getValue();
//        }
//        return value;
//    }
//    
    private int _deleteLink(long fromNodeId,long relationValue,long toNodeId) throws Throwable
    {
        RowSet rowSet=this.accessor.executeQuery(parent, null, "SELECT * FROM `~link` WHERE fromNodeId=? AND toNodeId=? AND relationValue=?",fromNodeId,toNodeId,relationValue);
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
        int deleted=this.accessor.executeUpdate(this.parent,null,"DELETE FROM `~link` WHERE nodeId=?",linkNodeId);
        deleted+=_deleteNode(linkNodeId);
        return deleted;
    }
    public int deleteLink(Node fromNode,Relation_ relation,long toNodeId) throws Throwable
    {
        return _deleteLink(fromNode.getNodeId(),relation.getValue(),toNodeId);
    }
    public int deleteLink(Node fromNode,Relation_ relation,Node toNode) throws Throwable
    {
        return _deleteLink(fromNode.getNodeId(),relation.getValue(),toNode.getNodeId());
    }

    private int _deleteNode(long nodeId) throws Throwable
    {
        int deleted=this.accessor.executeUpdate(this.parent,null,"DELETE FROM `~node` WHERE id=?",nodeId);
        if (deleted>0)
        {
            this.graph.invalidateCacheLines(parent, nodeId);
            RowSet types=this.accessor.executeQuery(parent, null, "SELECT type FROM `~nodetype` WHERE id=?",nodeId);
            for (Row row:types.rows())
            {
                String typeName=row.getVARCHAR(0);
                var descriptor=this.graph.getGraphObjectDescriptor(typeName);
                String selectSql="SELECT * FROM "+descriptor.getTableName()+" WHERE _nodeId=?";
                RowSet rowSet=this.accessor.executeQuery(parent, null, selectSql,nodeId);
                versionRow(parent, descriptor, accessor, rowSet.getColumnNames(),rowSet.getRow(0));
            }
            Timestamp now=SqlUtils.now();
            this.accessor.executeQuery(this.parent,null,"INSERT INTO `~deletednode` (id,deleted) VALUES(?,?)",nodeId,now);
            
            RowSet rowSet=this.accessor.executeQuery(this.parent,null,"SELECT fromNodeId,relationValue,toNodeId FROM `~link` WHERE fromNodeId=? OR toNodeId=?",nodeId,nodeId);
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

    public int deleteNode(Node node) throws Throwable
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
    public int deleteNodes(Node...nodes) throws Throwable
    {
        int deleted=0;
        for (Node node:nodes)
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
    
    public <NODE extends Node> void putArray(Node arrayObject,NODE[] elements) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        deleteArray(arrayObject,(Class<NODE>)elements.getClass().getComponentType());
        for (int i=0;i<elements.length;i++)
        {
            Node element=elements[i];
            if (element!=null)
            {
                long elementId=create(element);
                Insert.table("~array").value("elementId",elementId).value("nodeId",arrayNodeId).value("`index`",i).execute(parent, this.accessor);
            }
        }
    }

    public <NODE extends Node> void appendToArray(NODE[] elements,Node arrayObject) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        int base=0;
        var row=Select.source("`~array`").columns("`index`").orderBy("`index` DESC").limit(1).where("nodeId=?", arrayNodeId).executeOne(parent, this.accessor);
        if (row!=null)
        {
            base=row.getINTEGER(0)+1;
        }
        for (int i=0;i<elements.length;i++)
        {
            Node element=elements[i];
            if (element!=null)
            {
                long elementId=create(element);
                Insert.table("`~array`").value("elementId",elementId).value("nodeId",arrayNodeId).value("`index`",i+base).execute(parent, this.accessor);
            }
        }
    }
    public <NODE extends Node> void appendToArray(Node arrayObject,NODE...elements) throws Throwable
    {
        appendToArray(elements,arrayObject);
    }
    public int deleteArray(Node arrayObject,Class<? extends Node> elementType) throws Throwable
    {
        Long arrayNodeId=arrayObject._nodeId;
        if (arrayNodeId==null)
        {
            throw new Exception();
        }
        String elementTypeName=elementType.getSimpleName();
        String deleteSql="DELETE `~node`,`~array` FROM `~node` JOIN `~array` ON `~node`.id=`~array`.elementId JOIN "+elementTypeName+" ON `~node`.id="+elementTypeName+".`~nodeId`";
        int deleted=this.accessor.executeUpdate(this.parent,null,deleteSql);
        return deleted;
    }
    
    
}
