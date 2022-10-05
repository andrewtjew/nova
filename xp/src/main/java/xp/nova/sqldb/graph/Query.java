package xp.nova.sqldb.graph;

import java.util.ArrayList;
import java.util.HashMap;

public class Query
{
    private Class<? extends NodeObject>[] nodeTypes;
    private Class<? extends NodeObject>[] optionalNodeTypes;
    
    String expression;
    Object[] parameters;
    
    private ArrayList<LinkQuery> linkQueries;

    public Query()
    {
    }
    public Query where(String expression)
    {
        this.expression=expression;
        return this;
    }

    public Query where(String expression, Object... parameters)
    {
        GraphAccessor.translateParameters(parameters);
        this.parameters=parameters;
        this.expression=expression;
        return this;
    }
    
    @SafeVarargs
    final public Query select(Class<? extends NodeObject>... nodeTypes)
    {
        this.nodeTypes = nodeTypes;
        return this;
    }
    @SafeVarargs
    final public Query selectOptional(Class<? extends NodeObject>... nodeTypes)
    {
        this.optionalNodeTypes= nodeTypes;
        return this;
    }

    public Query traverse(LinkQuery linkQuery)
    {
        if (this.linkQueries == null)
        {
            this.linkQueries = new ArrayList<>();
        }
        this.linkQueries.add(linkQuery);
        return this;
    }

  
    private void addLinkQueries(Graph graph,HashMap<String,Meta> map,StringBuilder sources,StringBuilder select,ArrayList<Object> parameters,ArrayList<LinkQuery> linkQueries, String source, int aliasIndex) throws Throwable
    {
        if (linkQueries == null)
        {
            return;
        }
        for (LinkQuery linkQuery : linkQueries)
        {
            String linkAlias = "_link" + aliasIndex;
            String nodeAlias = "_node" + aliasIndex;
            switch (linkQuery.direction)
            {
            case FROM:
                sources.append(
                        " LEFT JOIN _link AS " + linkAlias + " ON " + source + ".id=" + linkAlias + ".fromNodeId");
                break;
            case TO:
                sources.append(
                        " LEFT JOIN _link AS " + linkAlias + " ON " + source + ".id=" + linkAlias + ".toNodeId");
                break;
            default:
                break;
            }
            if (linkQuery.relation!=null)
            {
                String typeName=linkQuery.relation.getClass().getSimpleName();
                sources.append(" AND "+linkAlias+".type='"+typeName+"' AND "+linkAlias+".relation="+linkQuery.relation.getValue());
            }

            if (linkQuery.nodeTypes != null)
            {
                String on = null;
                switch (linkQuery.direction)
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
                String namespace = linkQuery.namespace != null ? linkQuery.namespace + "." : "";
                for (int i = 0; i < linkQuery.nodeTypes.length; i++)
                {
                    Class<? extends NodeObject> type = linkQuery.nodeTypes[i];
                    Meta meta = graph.getMeta(type);
                    map.put(namespace+meta.getTypeName(), meta);
                    String typeName = meta.getTypeName();
                    String table = meta.getTableName();
                    String alias = meta.getTableAlias();
                    sources.append(" JOIN " + table + "AS " + alias + on + alias + "._nodeId");
                    for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                    {
                        String fieldColumnName = namespace + columnAccessor.getColumnName(typeName);
                        String tableColumnName = columnAccessor.getColumnName(alias);
                        select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
                    }
                }
            }
            if (linkQuery.optionalNodeTypes != null)
            {
                String on = null;
                switch (linkQuery.direction)
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
                String namespace = linkQuery.namespace != null ? linkQuery.namespace + "." : "";
                for (int i = 0; i < linkQuery.nodeTypes.length; i++)
                {
                    Class<? extends NodeObject> type = linkQuery.nodeTypes[i];
                    Meta meta = graph.getMeta(type);
                    map.put(namespace+meta.getTypeName(), meta);
                    String typeName = meta.getTypeName();
                    String table = meta.getTableName();
                    String alias = meta.getTableAlias();
                    sources.append(" LEFT JOIN " + table + "AS " + alias + on + alias + "._nodeId");
                    for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                    {
                        String fieldColumnName = namespace + columnAccessor.getColumnName(typeName);
                        String tableColumnName = columnAccessor.getColumnName(alias);
                        select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
                    }
                }
            }
            addLinkQueries(graph,map,sources,select,parameters,linkQuery.linkQueries, nodeAlias, aliasIndex++);
        }
    }

    static class PreparedQuery
    {
        String sql;
        String start;
        Object[] parameters;
        HashMap<String,Meta> map;
    }
    
    PreparedQuery preparedQuery=null;
    
    public PreparedQuery build(Graph graph) throws Throwable
    {
        if (this.preparedQuery!=null)
        {
            return this.preparedQuery;
        }
        PreparedQuery preparedQuery=new PreparedQuery();
        preparedQuery.map=new HashMap<String, Meta>();
        StringBuilder select = new StringBuilder();
        StringBuilder sources = new StringBuilder();

        if (this.nodeTypes != null)
        {
            for (int i = 0; i < this.nodeTypes.length; i++)
            {
                Class<? extends NodeObject> type = this.nodeTypes[i];
                Meta meta = graph.getMeta(type);
                preparedQuery.map.put(meta.getTypeName(), meta);
                String typeName = meta.getTypeName();
                String table = meta.getTableName();
                String alias = meta.getTableAlias();
                sources.append("JOIN " + table + "AS " + alias + " ON _node.id=" + alias + "._nodeId");
                for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                {
                    String fieldColumnName = columnAccessor.getColumnName(typeName);
                    String tableColumnName = columnAccessor.getColumnName(alias);
                    select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
                }
            }
        }
        if (this.optionalNodeTypes != null)
        {
            for (int i = 0; i < this.optionalNodeTypes.length; i++)
            {
                Class<? extends NodeObject> type = this.optionalNodeTypes[i];
                Meta meta = graph.getMeta(type);
                preparedQuery.map.put(meta.getTypeName(), meta);
                String typeName = meta.getTypeName();
                String table = meta.getTableName();
                String alias = meta.getTableAlias();
                sources.append("LEFT JOIN " + table + "AS " + alias + " ON _node.id=" + alias + "._nodeId");
                for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                {
                    String fieldColumnName = columnAccessor.getColumnName(typeName);
                    String tableColumnName = columnAccessor.getColumnName(alias);
                    select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
                }
            }
        }
        addLinkQueries(graph,preparedQuery.map,sources,select,null,this.linkQueries, "_node", 0);
        StringBuilder query = new StringBuilder("SELECT" + select + " FROM" + sources);
        
        if (this.expression!=null)
        {
            query.append(" WHERE ("+expression+")");
            preparedQuery.start=" AND _node.id=";
        }
        else
        {
            preparedQuery.start=" WHERE _node.id=";
        }

        preparedQuery.sql=query.toString();
        this.preparedQuery=preparedQuery;
        return this.preparedQuery;
    }
    
    
}
