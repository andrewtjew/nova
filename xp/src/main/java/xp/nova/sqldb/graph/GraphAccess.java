package xp.nova.sqldb.graph;

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
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

import xp.nova.sqldb.graph.Graph.ColumnAccessor;
import xp.nova.sqldb.graph.Graph.Meta;

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
                    ,"INSERT INTO _event (created,creatorId,source) VALUES(?,?,?)"
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

    public Node createNode(NodeObject...objects) throws Throwable
    {
        long nodeId=Insert.table("_node").value("eventId",this.getEventId()).executeAndReturnLongKey(parent, this.accessor);
        Node node=new Node(this,nodeId);
        node.put(objects);
        return node;
    }

    public Node openNode(long nodeId) throws Throwable
    {
        RowSet rowSet=this.accessor.executeQuery(parent, null
                ,"SELECT id FROM _node WHERE id=?"
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
        Meta meta=this.graph.getMeta(type);
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

        insert.append("_nodeId,_eventId");
        values.append("?,?");
        update.append("_eventId=?");
        
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

    public long getCount(Class<? extends NodeObject> type,String where,Object...parameters) throws Throwable
    {
        Meta meta=this.graph.getMeta(type);
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
    
    public Event getNodeObjectEvent(long nodeId,Class<? extends NodeObject> type) throws Throwable
    {
        String tableName=this.graph.getMeta(type).getTableName();
        RowSet rowSet=this.accessor.executeQuery(parent,null
                ,"SELECT id,created,creatorId,source FROM "+tableName+" JOIN _event ON _event.id="+tableName+"._eventId WHERE "+tableName+"._nodeId_=?",nodeId);
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
            long id=Insert.table("_link").value("fromNodeId",fromNodeId).value("toNodeId", toNodeId).value("eventId",this.getEventId()).executeAndReturnLongKey(parent, this.accessor);
            return new Link(this,id,fromNodeId,toNodeId);
        }
        catch (Throwable t)
        {
            return openLink(fromNodeId,toNodeId);
        }
    }
    
    int deleteLinks(long fromNodeId,Class<? extends NodeObject> toType) throws Throwable
    {
        Meta meta=this.graph.getMeta(toType);
        String table=meta.getTableName();
        RowSet rowSet=this.accessor.executeQuery(parent, null
                ,"SELECT _link.id FROM _link JOIN "+table+" ON "+table+"._nodeId=_link.toNodeId WHERE fromNodeId=?"
                ,fromNodeId);
        Object[][] parameters=new Object[rowSet.size()][];
        for (int i=0;i<parameters.length;i++)
        {
            parameters[i]=new Object[1];
            parameters[i][0]=rowSet.getRow(i).getBIGINT(0);
        }
        
        int[] results=this.accessor.executeBatchUpdate(this.parent,null,parameters,"DELETE FROM _link WHERE id=?");
        int total=0;
        for (int i=0;i<results.length;i++)
        {
            total+=results[i];
        }
        return total;
        
    }
    boolean deleteNode(long nodeId) throws Throwable
    {
        this.accessor.executeUpdate(this.parent,null,"DELETE FROM _link WHERE toNodeId=?",nodeId);
        int deleted=this.accessor.executeUpdate(this.parent,null,"DELETE FROM _node WHERE id=?",nodeId);
        return deleted>0;
    }
    
    public Link openLink(long linkId) throws Throwable
    {
        RowSet rowSet=this.accessor.executeQuery(parent, null
                ,"SELECT fromNodeId,toNodeId FROM _link WHERE id=?"
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
                ,"SELECT id FROM _link WHERE fromNodeId=? AND toNodeId=?"
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
    
    final RowSet getLinkNodes(long[] fromNodeIds,long[] toNodeIds,Class<? extends NodeObject>[] requiredObjectTypes,Class<? extends NodeObject>[] optionalObjectTypes,String orderBy,String expression,Object[] parameters) throws Throwable
    {
        Trace parent=this.parent;
        StringBuilder select = new StringBuilder();
        StringBuilder join = new StringBuilder();

        Graph graph = this.graph;
        String on=fromNodeIds!=null?" ON _link.toNodeId=":" ON _link.fromNodeId=";
        for (Class<? extends GraphObject> type : requiredObjectTypes)
        {
            if (type==null)
            {
                throw new Exception();
            }
            Meta meta=graph.getMeta(type);
            String typeName = meta.getTypeName();
            String table = meta.getTableName();
            String alias= meta.getTableAlias();
            switch (meta.getObjectType())
            {
            case LINK_OBJECT:
                join.append(" JOIN " + table + "AS "+alias+on + alias+ "._linkId");
                break;
            case NODE_OBJECT:
                join.append(" JOIN " + table + "AS "+alias+on+alias+ "._nodeId");
                break;
            default:
                throw new Exception();
            }

            for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
            {
                String fieldColumnName = columnAccessor.getColumnName(typeName);
                String tableColumnName = columnAccessor.getColumnName(alias);
                select.append(','+tableColumnName + " AS '" + fieldColumnName + '\'');
            }
        }
        for (Class<? extends GraphObject> type : optionalObjectTypes)
        {
            Meta meta=graph.getMeta(type);
            String typeName = meta.getTypeName();
            String table = meta.getTableName();
            String alias= meta.getTableAlias();
            switch (meta.getObjectType())
            {
            case LINK_OBJECT:
                join.append(" LEFT JOIN " + table + "AS "+alias+on + alias+ "._linkId");
                break;
            case NODE_OBJECT:
                join.append(" LEFT JOIN " + table + "AS "+alias+on+alias+ "._nodeId");
                break;
            default:
                throw new Exception();
            }
            for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
            {
                String fieldColumnName = columnAccessor.getColumnName(typeName);
                String tableColumnName = columnAccessor.getColumnName(alias);
                select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
            }
        }
        StringBuilder query = new StringBuilder("SELECT _link.id AS '_link.id',_link.fromNodeId AS '_link.fromNodeId',_link.toNodeId AS '_link.toNodeId'" + select + "FROM _link" + join);
        
        query.append(" WHERE ");
        if (fromNodeIds!=null)
        {
            if (fromNodeIds.length==1)
            {
                query.append("_link.fromNodeId="+fromNodeIds[0]);
            }
            else
            {
                query.append("_link.fromNodeId IN ("+Utils.combine(fromNodeIds,",")+")");
            }
        }
        if (toNodeIds!=null)
        {
            if (toNodeIds.length==1)
            {
                query.append("_link.toNodeId="+toNodeIds[0]);
            }
            else
            {
                query.append("_link.toNodeId IN ("+Utils.combine(toNodeIds,",")+")");
            }
        }
        if (expression!=null)
        {
            query.append(" AND "+expression);
        }
        if (orderBy != null)
        {
            query.append(" ORDER BY ");
            query.append(orderBy);
        }
        System.out.println(query);
        RowSet rowSet;
        if (parameters!=null)
        {
            rowSet = getAccessor().executeQuery(parent, null,
                    query.toString(), parameters);
        }
        else
        {
            rowSet = getAccessor().executeQuery(parent, null,
                    query.toString());
        }
        System.out.println(query);
        return rowSet;
    }
    
}
