package xp.nova.sqldb.graph;

import java.util.ArrayList;
import java.util.HashMap;

import org.nova.sqldb.Accessor;
import org.nova.sqldb.RowSet;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;

import xp.nova.sqldb.graph.Graph.ColumnAccessor;
import xp.nova.sqldb.graph.Graph.Meta;

public class Query
{
    private Class<? extends NodeObject>[] nodeTypes;
    private Class<? extends LinkObject>[] linkTypes;

    final private Direction direction;
    final private Long nodeId;

    private Object[] parameters;
    private String whereExpression;
    private String orderby;
    private ArrayList<QueryPath> followPaths;

    public Query(long nodeId, Direction direction)
    {
        this.direction = direction;
        this.nodeId = nodeId;
    }

    public Query(long nodeId)
    {
        this(nodeId, null);
    }

    public Query()
    {
        this.nodeId = null;
        this.direction = null;
    }

    @SuppressWarnings("unchecked")
    public Query selectNodes(Class<? extends NodeObject>... nodeTypes)
    {
        this.nodeTypes = nodeTypes;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Query selectLinks(Class<? extends LinkObject>... linkTypes)
    {
        this.linkTypes = linkTypes;
        return this;
    }

    public Query follow(QueryPath path)
    {
        if (this.followPaths == null)
        {
            this.followPaths = new ArrayList<>();
        }
        this.followPaths.add(path);
        return this;
    }

    public Query where(String expression, Object... parameters)
    {
        return this;
    }

    public Query orderBy(String orderBy)
    {
        this.orderby = orderBy;
        return this;
    }

    private void addPath(ArrayList<QueryPath> paths, String pathSource, int aliasIndex) throws Throwable
    {
        if (paths == null)
        {
            return;
        }
        for (QueryPath path : paths)
        {
            String linkAlias = "_link" + aliasIndex;
            String nodeAlias = "_node" + aliasIndex;
            switch (path.direction)
            {
            case FROM:
                sources.append(
                        " LEFT JOIN _link AS " + linkAlias + " ON " + pathSource + ".id=" + linkAlias + ".fromNodeId");
                break;
            case TO:
                sources.append(
                        " LEFT JOIN _link AS " + linkAlias + " ON " + pathSource + ".id=" + linkAlias + ".toNodeId");
                break;
            default:
                break;
            }
            if (path.nodeTypes != null)
            {
                String on = null;
                switch (path.direction)
                {
                case FROM:
                    on = " ON " + linkAlias + ".toNodeId=";
                    break;
                case TO:
                    on = " ON " + linkAlias + ".fromNodeId=";
                    break;
                default:
                    break;
                }
                String namespace = path.namespace != null ? path.namespace + "." : "";
                for (int i = 0; i < path.nodeTypes.length; i++)
                {
                    Class<? extends NodeObject> type = path.nodeTypes[i];
                    Meta meta = graph.getMeta(type);
                    this.map.put(namespace+meta.getTypeName(), meta);
                    String typeName = meta.getTypeName();
                    String table = meta.getTableName();
                    String alias = meta.getTableAlias();
                    if (i > 0)
                    {
                        sources.append(" LEFT");
                    }
                    sources.append(" JOIN " + table + "AS " + alias + on + alias + "._nodeId");

                    for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                    {
                        String fieldColumnName = namespace + columnAccessor.getColumnName(typeName);
                        String tableColumnName = columnAccessor.getColumnName(alias);
                        select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
                    }
                }
                totalResultObjects += this.nodeTypes.length;
            }
            addPath(path.followPaths, nodeAlias, aliasIndex++);
        }
    }

    private Graph graph;
    private StringBuilder sources;
    private StringBuilder select;
    private int totalResultObjects;
    
    private HashMap<String,Meta> map;
    
    
    public QueryResult[] execute(Trace parent, Graph graph, Accessor accessor) throws Throwable
    {
        this.graph = graph;
        this.map=new HashMap<String, Meta>();
        this.select = new StringBuilder();
        this.sources = new StringBuilder();
        StringBuilder where = new StringBuilder();
        if (this.direction != null)
        {
            switch (this.direction)
            {
            case FROM:
                sources.append(" _link JOIN _node ON _link.toNodeId=_node.id ");
                where.append(" _link.fromNodeId=" + this.nodeId);
                break;
            case TO:
                sources.append(" _link JOIN _node ON _link.fromNodeId=_node.id ");
                where.append(" _link.toNodeId=" + this.nodeId);
                break;
            default:
                break;

            }
        }
        else
        {
            sources.append(" _node");
            if (this.nodeId != null)
            {
                where.append(" _node.id=" + this.nodeId);
            }
        }
        select.append(" _node.id AS _nodeId");
        int totalResultObjects = 0;
        if (this.nodeTypes != null)
        {
            for (int i = 0; i < this.nodeTypes.length; i++)
            {
                Class<? extends NodeObject> type = this.nodeTypes[i];
                Meta meta = graph.getMeta(type);
                this.map.put(meta.getTypeName(), meta);
                String typeName = meta.getTypeName();
                String table = meta.getTableName();
                String alias = meta.getTableAlias();
                if (i > 0)
                {
                    sources.append(" LEFT");
                }
                sources.append(" JOIN " + table + "AS " + alias + " ON _node.id=" + alias + "._nodeId");
                for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                {
                    String fieldColumnName = columnAccessor.getColumnName(typeName);
                    String tableColumnName = columnAccessor.getColumnName(alias);
                    select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
                }
            }
            totalResultObjects += this.nodeTypes.length;
        }
        addPath(this.followPaths, "_node", 0);
        if (this.whereExpression != null)
        {
            if (where.length() > 0)
            {
                where.append(" AND");
            }
            where.append(" (" + this.whereExpression + ")");
        }
        StringBuilder query = new StringBuilder("SELECT" + select + " FROM" + sources);

        if (where.length() > 0)
        {
            query.append(" WHERE" + where);
        }
        if (this.orderby != null)
        {
            query.append(" ORDER BY ");
            query.append(orderby);
        }
        System.out.println(query);
        RowSet rowSet;
        if (parameters != null)
        {
            rowSet = accessor.executeQuery(parent, null, query.toString(), parameters);
        }
        else
        {
            rowSet = accessor.executeQuery(parent, null, query.toString());
        }
        QueryResult[] results=new QueryResult[rowSet.size()];
        for (int i=0;i<results.length;i++)
        {
            results[i]=new QueryResult(rowSet.getRow(i), this.map);
        }
        return results;
    }

    public QueryResult[] execute(Trace parent, Graph graph) throws Throwable
    {
        try (Accessor accessor=graph.getConnector().openAccessor(parent))
        {
            return execute(parent,graph,accessor);
        }
    }

    public QueryResult executeOne(Trace parent, Graph graph,Accessor accessor) throws Throwable
    {
        QueryResult[] results=execute(parent,graph,accessor);
        if (results.length==0)
        {
            return null;
        }
        if (results.length>1)
        {
            throw new Exception();
        }
        return results[0];
    }
    public QueryResult executeOne(Trace parent, Graph graph) throws Throwable
    {
        try (Accessor accessor=graph.getConnector().openAccessor(parent))
        {
            return executeOne(parent,graph,accessor);
        }
    }

    public long getCount(Trace parent,Graph graph,Accessor accessor,Class<? extends NodeObject> type,String where,Object...parameters) throws Throwable
    {
        Meta meta=this.graph.getMeta(type);
        String table=meta.getTableName();
        return accessor.executeQuery(parent,null,"SELECT count(*) FROM "+table+" WHERE "+where,parameters).getRow(0).getBIGINT(0);
    }
    
    
}
