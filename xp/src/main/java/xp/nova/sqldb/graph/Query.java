package xp.nova.sqldb.graph;

import java.util.ArrayList;

import org.nova.sqldb.Accessor;
import org.nova.sqldb.RowSet;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;

import xp.nova.sqldb.graph.Graph.ColumnAccessor;
import xp.nova.sqldb.graph.Graph.Meta;

public class Query
{
    private Class<? extends NodeObject>[] nodeTypes;
    private Class<? extends NodeObject>[] toNodeTypes;
    private Class<? extends LinkObject>[] toLinkTypes;
    private Class<? extends LinkObject>[] withLinkTypes;
    private Long fromNodeId;
    private Long toNodeId;
    private Long nodeId;
    private Object[] parameters;
    private String whereExpression;
    private String orderby;
    
    @SuppressWarnings("unchecked")
    public Query selectNodes(Class<? extends NodeObject>...nodeTypes)
    {
        this.nodeTypes=nodeTypes;
        return this;
    }
    public Query selectToLinks(Class<? extends LinkObject>...linkTypes)
    {
        this.toLinkTypes=linkTypes;
        return this;
    }
    public Query selectWithLinks(Class<? extends LinkObject>...linkTypes)
    {
        this.withLinkTypes=linkTypes;
        return this;
    }
    public Query withNodeId(long nodeId)
    {
        this.nodeId=nodeId;
        return this;
    }
    public Query withLinksFrom(long fromNodeId)
    {
        this.fromNodeId=fromNodeId;
        return this;
    }
    public Query withLinksTo(long toNodeId)
    {
        this.toNodeId=toNodeId;
        return this;
    }
    public Query where(String expression,Object...parameters)
    {
        return this;
    }
    public Query orderBy(String orderBy)
    {
        this.orderby=orderBy;
        return this;
    }
    public RowSet execute(Trace parent,Graph graph,Accessor accessor) throws Throwable
    {
        StringBuilder select = new StringBuilder();
        StringBuilder sources = new StringBuilder();
        StringBuilder where=new StringBuilder();
        String on=null;
        if (this.fromNodeId!=null)
        {
            sources.append(" _link JOIN _node ON _link.toNodeId=_node.id ");
            where.append(" _link.fromNodeId="+this.fromNodeId);
        }
        else if (this.toNodeId!=null)
        {
            sources.append(" _link JOIN _node ON _link.fromNodeId=_node.id ");
            where.append(" _link.toNodeId="+this.toNodeId);
        }
        else if (this.nodeId!=null)
        {
            sources.append("_node ");
            where.append(" _node.id="+this.nodeId);
        }
        else
        {
            sources.append(" _node");
        }
        select.append(" _node.id AS _nodeId");
        if (this.nodeTypes!=null)
        {
            for (Class<? extends NodeObject> type : this.nodeTypes)
            {
                Meta meta=graph.getMeta(type);
                String typeName = meta.getTypeName();
                String table = meta.getTableName();
                String alias= meta.getTableAlias();
                sources.append(" JOIN " + table + "AS "+alias+" ON _node.id="+alias+ "._nodeId");
                for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                {
                    String fieldColumnName = columnAccessor.getColumnName(typeName);
                    String tableColumnName = columnAccessor.getColumnName(alias);
                    select.append(','+tableColumnName + " AS '" + fieldColumnName + '\'');
                }
            }
        }
        if (this.whereExpression!=null)
        {
            where.append(" AND ("+this.whereExpression+")");
        }
        StringBuilder query = new StringBuilder("SELECT"+select+ " FROM" + sources);
        if (this.whereExpression!=null)
        {
            query.append(" WHERE"+this.whereExpression);
        }
        if (this.orderby != null)
        {
            query.append(" ORDER BY ");
            query.append(orderby);
        }
        System.out.println(query);
        RowSet rowSet;
        if (parameters!=null)
        {
            rowSet = accessor.executeQuery(parent, null,
                    query.toString(), parameters);
        }
        else
        {
            rowSet = accessor.executeQuery(parent, null,
                    query.toString());
        }
        System.out.println(query);
        return rowSet;
    }
}
