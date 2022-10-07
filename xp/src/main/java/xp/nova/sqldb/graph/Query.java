package xp.nova.sqldb.graph;

import java.util.ArrayList;
import java.util.HashMap;

import org.nova.utils.TypeUtils;

public class Query
{
    Class<? extends NodeObject>[] nodeTypes;
    Class<? extends NodeObject>[] optionalNodeTypes;
    Class<? extends GraphObject> one;
    
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
        if (nodeTypes.length>0)
        {
            this.one=nodeTypes[0];
        }
        return this;
    }
    @SafeVarargs
    final public Query selectOptional(Class<? extends NodeObject>... nodeTypes)
    {
        this.optionalNodeTypes= nodeTypes;
        if (nodeTypes.length>0)
        {
            this.one=nodeTypes[0];
        }
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

    static class State
    {
        final Graph graph;
        final HashMap<String,Meta> map;
        final StringBuilder sources;
        final StringBuilder select;
        final ArrayList<Object> parameters;
        
        public Class<? extends GraphObject> one;
        
        public State(Graph graph,HashMap<String,Meta> map,StringBuilder sources,StringBuilder select,ArrayList<Object> parameters)
        {
            this.graph=graph;
            this.map=map;
            this.sources=sources;
            this.select=select;
            this.parameters=parameters;
        }
    }    
    
    private void addLinkQueries(State state,ArrayList<LinkQuery> linkQueries, String source, int aliasIndex) throws Throwable
    {
        if (linkQueries == null)
        {
            return;
        }
        for (LinkQuery linkQuery : linkQueries)
        {
            if (state.one==null)
            {
                state.one=linkQuery.one;
            }
            TypeUtils.addToList(state.parameters,linkQuery.parameters);
            String linkAlias = "_link" + aliasIndex;
            String nodeAlias = "_node" + aliasIndex;
            switch (linkQuery.direction)
            {
            case FROM:
                state.sources.append(
                        " LEFT JOIN _link AS " + linkAlias + source + linkAlias + ".fromNodeId");
                break;
            case TO:
                state.sources.append(
                        " LEFT JOIN _link AS " + linkAlias + source + linkAlias + ".toNodeId");
                break;
            default:
                break;
            }
            if (linkQuery.relation!=null)
            {
                String typeName=linkQuery.relation.getClass().getSimpleName();
                state.sources.append(" AND "+linkAlias+".type='"+typeName+"' AND "+linkAlias+".relation="+linkQuery.relation.getValue());
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
                    Meta meta = state.graph.getMeta(type);
                    state.map.put(namespace+meta.getTypeName(), meta);
                    String typeName = meta.getTypeName();
                    String table = meta.getTableName();
                    String alias = meta.getTableAlias();
                    state.sources.append(" JOIN " + table + "AS " + alias + on + alias + "._nodeId");
                    for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                    {
                        String fieldColumnName = namespace + columnAccessor.getColumnName(typeName);
                        String tableColumnName = columnAccessor.getColumnName(alias);
                        if (state.select.length()>0)
                        {
                            state.select.append(',');
                        }
                        state.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
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
                for (int i = 0; i < linkQuery.optionalNodeTypes.length; i++)
                {
                    Class<? extends NodeObject> type = linkQuery.optionalNodeTypes[i];
                    Meta meta = state.graph.getMeta(type);
                    state.map.put(namespace+meta.getTypeName(), meta);
                    String typeName = meta.getTypeName();
                    String table = meta.getTableName();
                    String alias = meta.getTableAlias();
                    state.sources.append(" LEFT JOIN " + table + "AS " + alias + on + alias + "._nodeId");
                    for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                    {
                        String fieldColumnName = namespace + columnAccessor.getColumnName(typeName);
                        String tableColumnName = columnAccessor.getColumnName(alias);
                        if (state.select.length()>0)
                        {
                            state.select.append(',');
                        }
                        state.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
                    }
                }
            }
            addLinkQueries(state,linkQuery.linkQueries, " ON "+nodeAlias+".nodeId=", aliasIndex++);
        }
    }

    static class PreparedQuery
    {
        Class<? extends GraphObject> one;
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

        String on=null;
        
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
                if (sources.length()==0)
                {
                    sources.append(" "+table);
                    if (this.expression==null)
                    {
                        preparedQuery.start=" WHERE "+table+"._nodeId=";
                    }
                    else
                    {
                        preparedQuery.start=" AND "+table+"._nodeId=";
                    }

                    on=" ON "+table+"._nodeId=";
                }
                else
                {
                    sources.append(" JOIN " + table + " AS " + alias + on + alias + "._nodeId");
                }
                for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                {
                    String fieldColumnName = columnAccessor.getColumnName(typeName);
                    String tableColumnName = columnAccessor.getColumnName(alias);
                    if (select.length()>0)
                    {
                        select.append(',');
                    }
                    select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
                }
            }
        }
        if (on==null)
        {
            if (this.expression==null)
            {
                preparedQuery.start=" WHERE _node.id=";
            }
            else
            {
                preparedQuery.start=" AND _node.id=";
            }
            on=" ON _node.id=";
            sources.append(" _node");
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
                sources.append(" LEFT JOIN " + table + " AS " + alias + on + alias + "._nodeId");
                for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                {
                    String fieldColumnName = columnAccessor.getColumnName(typeName);
                    String tableColumnName = columnAccessor.getColumnName(alias);
                    if (select.length()>0)
                    {
                        select.append(',');
                    }
                    select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
                }
            }
        }
        ArrayList<Object> list=new ArrayList<Object>();
        TypeUtils.addToList(list, this.parameters);
        State state=new State(graph,preparedQuery.map,sources,select,list);
        state.one=this.one;
        addLinkQueries(state,this.linkQueries, on, 0);
        StringBuilder query = new StringBuilder("SELECT " + select + " FROM" + sources);

        if (this.expression!=null)
        {
            query.append(" WHERE ("+this.expression+")");
        }
        if (list.size()>0)
        {
            preparedQuery.parameters=list.toArray(new Object[list.size()]);
        }
        preparedQuery.sql=query.toString();
        preparedQuery.one=state.one;
        this.preparedQuery=preparedQuery;
        return this.preparedQuery;
    }
    
    
}
