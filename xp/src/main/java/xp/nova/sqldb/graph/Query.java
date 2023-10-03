package xp.nova.sqldb.graph;

import java.util.ArrayList;
import java.util.HashMap;

import org.nova.utils.TypeUtils;

public class Query
{
    Class<? extends NodeObject>[] nodeTypes;
    Class<? extends NodeObject>[] optionalNodeTypes;

    String expression;
    Object[] parameters;
    String orderBy;
    
    private ArrayList<LinkQuery> linkQueries;

    public Query()
    {
    }
    public Query where(String expression)
    {
        this.expression=expression;
        return this;
    }
    public Query orderBy(String orderBy)
    {
        this.orderBy=orderBy;
        return this;
    }
    public Query orderBy(String orderBy,boolean descending)
    {
        if (descending)
        {
            this.orderBy=orderBy+" DESC";
        }
        else
        {
            this.orderBy=orderBy;
        }
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
        this.nodeTypes=nodeTypes;
        return this;
    }
    @SafeVarargs
    final public Query selectOptional(Class<? extends NodeObject>... optionalNodeTypes)
    {
        this.optionalNodeTypes=optionalNodeTypes;
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
        final HashMap<String,GraphObjectDescriptor> map;
        final StringBuilder sources;
        final StringBuilder select;
        final ArrayList<Object> parameters;
        private int aliasIndex=0;
        
        public State(Graph graph,HashMap<String,GraphObjectDescriptor> map,StringBuilder sources,StringBuilder select,ArrayList<Object> parameters)
        {
            this.graph=graph;
            this.map=map;
            this.sources=sources;
            this.select=select;
            this.parameters=parameters;
        }
        public int getNextAliasIndex()
        {
            return this.aliasIndex++;
        }
    }    
    
    private void addLinkQueries(State state,ArrayList<LinkQuery> linkQueries, String source) throws Throwable
    {
        if (linkQueries == null)
        {
            return;
        }
        for (LinkQuery linkQuery : linkQueries)
        {
            TypeUtils.addToList(state.parameters,linkQuery.parameters);
            int aliasIndex=state.getNextAliasIndex();
            String linkAlias = "_link" + aliasIndex;
            String nodeAlias=null;
            String optional=linkQuery.optional?" LEFT":" ";
            switch (linkQuery.direction)
            {
            case FROM:
                nodeAlias = " ON _link" + aliasIndex+".toNodeId=";
                state.sources.append(
                        optional+" JOIN _link AS " + linkAlias + source + linkAlias + ".fromNodeId");
                break;
            case TO:
                nodeAlias = " ON _link" + aliasIndex+".fromNodeId=";
                state.sources.append(
                        optional+" JOIN _link AS " + linkAlias + source + linkAlias + ".toNodeId");
                break;
            default:
                break;
            }
//            if (linkQuery.relation!=null)
            {
                int relationValue=linkQuery.relation.getValue();
                if (relationValue<0)
                {
                    state.sources.append(" AND "+linkAlias+".relation="+relationValue);
                }
                else
                {
                    String typeName=linkQuery.relation.getClass().getSimpleName();
                    state.sources.append(" AND "+linkAlias+".type='"+typeName+"' AND "+linkAlias+".relation="+relationValue);
                }
            }
//            state.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
            
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
                    
                for (int i = 0; i < linkQuery.nodeTypes.length; i++)
                {
                    Class<? extends NodeObject> type = linkQuery.nodeTypes[i];
                    GraphObjectDescriptor descriptor = state.graph.register(type);
                    state.map.put(linkQuery.nodeNamespace+descriptor.getTypeName(), descriptor);
                    String typeName = descriptor.getTypeName();
                    String table = descriptor.getTableName();

                    String as=" ";
                    String alias=table;
                    if (linkQuery.nodeNamespace!=null)
                    {
                        alias="`"+linkQuery.nodeNamespace+"_"+typeName+"`";
                        as=" AS "+alias+" ";
                    }

                    state.sources.append(" JOIN " + table + as + on + alias + "._nodeId");
                    for (FieldDescriptor columnAccessor : descriptor.getColumnAccessors())
                    {
                        String fieldColumnName = linkQuery.nodeNamespace + columnAccessor.getColumnName(typeName);
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
                for (int i = 0; i < linkQuery.optionalNodeTypes.length; i++)
                {
                    Class<? extends NodeObject> type = linkQuery.optionalNodeTypes[i];
                    GraphObjectDescriptor descriptor = state.graph.register(type);
                    state.map.put(descriptor.getNamespaceTypeName(linkQuery.nodeNamespace), descriptor);
                    String typeName = descriptor.getTypeName();
                    String table = descriptor.getTableName();
                    String alias = descriptor.getTableAlias(linkQuery.nodeNamespace);
                    state.sources.append(" LEFT JOIN " + table + "AS " + alias + on + alias + "._nodeId");
                    for (FieldDescriptor columnAccessor : descriptor.getColumnAccessors())
                    {
                        String fieldColumnName = linkQuery.nodeNamespace + columnAccessor.getColumnName(typeName);
                        String tableColumnName = columnAccessor.getColumnName(alias);
                        if (state.select.length()>0)
                        {
                            state.select.append(',');
                        }
                        state.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
                    }
                }
            }
            
            if (linkQuery.linkTypes!= null)
            {
                String on = " ON " + linkAlias + ".nodeId=";
                for (int i = 0; i < linkQuery.linkTypes.length; i++)
                {
                    Class<? extends NodeObject> type = linkQuery.optionalNodeTypes[i];
                    GraphObjectDescriptor descriptor = state.graph.register(type);
                    state.map.put(descriptor.getNamespaceTypeName(linkQuery.nodeNamespace), descriptor);
                    String typeName = descriptor.getTypeName();
                    String table = descriptor.getTableName();
                    String alias = descriptor.getTableAlias(linkQuery.nodeNamespace);
                    state.sources.append(" LEFT JOIN " + table + "AS " + alias + on + alias + "._nodeId");
                    for (FieldDescriptor columnAccessor : descriptor.getColumnAccessors())
                    {
                        String fieldColumnName = linkQuery.linkNamespace + columnAccessor.getColumnName(typeName);
                        String tableColumnName = columnAccessor.getColumnName(alias);
                        if (state.select.length()>0)
                        {
                            state.select.append(',');
                        }
                        state.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
                    }
                }
            }
            addLinkQueries(state,linkQuery.linkQueries, nodeAlias);
        }
    }

    static class PreparedQuery
    {
//        Class<? extends GraphObject> one;
        String sql;
        String start;
        Object[] parameters;
        HashMap<String,GraphObjectDescriptor> map;
        String orderBy;
        String countSql;
    }
    
    PreparedQuery preparedQuery=null;
    
    public PreparedQuery build(Graph graph) throws Throwable
    {
        if (this.preparedQuery!=null)
        {
            return this.preparedQuery;
        }
        PreparedQuery preparedQuery=new PreparedQuery();
        preparedQuery.map=new HashMap<String, GraphObjectDescriptor>();
        StringBuilder select = new StringBuilder();
        StringBuilder sources = new StringBuilder();

        if (this.expression==null)
        {
            preparedQuery.start=" WHERE _node.id=";
        }
        else
        {
            preparedQuery.start=" AND _node.id=";
        }
        String on=" ON _node.id=";
        sources.append(" _node");
        
        if (this.nodeTypes != null)
        {
            for (int i = 0; i < this.nodeTypes.length; i++)
            {
                Class<? extends NodeObject> type = this.nodeTypes[i];
                GraphObjectDescriptor descriptor = graph.register(type);
                preparedQuery.map.put(descriptor.getTypeName(), descriptor);
                String typeName = descriptor.getTypeName();
                String table = descriptor.getTableName();
                sources.append(" JOIN " + table + " " + on + table + "._nodeId");
                for (FieldDescriptor columnAccessor : descriptor.getColumnAccessors())
                {
                    String fieldColumnName = columnAccessor.getColumnName(typeName);
                    if (select.length()>0)
                    {
                        select.append(',');
                    }
                    select.append(fieldColumnName + " AS '" + fieldColumnName + '\'');
                }
            }
        }
        if (this.optionalNodeTypes != null)
        {
            for (int i = 0; i < this.optionalNodeTypes.length; i++)
            {
                Class<? extends NodeObject> type = this.optionalNodeTypes[i];
                GraphObjectDescriptor descriptor = graph.register(type);
                preparedQuery.map.put(descriptor.getTypeName(), descriptor);
                String typeName = descriptor.getTypeName();
                String table = descriptor.getTableName();
                sources.append(" LEFT JOIN " + table + " " + on + table + "._nodeId");
                for (FieldDescriptor columnAccessor : descriptor.getColumnAccessors())
                {
                    String fieldColumnName = columnAccessor.getColumnName(typeName);
                    if (select.length()>0)
                    {
                        select.append(',');
                    }
                    select.append(fieldColumnName + " AS '" + fieldColumnName + '\'');
                }
            }
        }
        ArrayList<Object> list=new ArrayList<Object>();
        TypeUtils.addToList(list, this.parameters);
        State state=new State(graph,preparedQuery.map,sources,select,list);
        addLinkQueries(state,this.linkQueries, on);
        StringBuilder query = new StringBuilder("SELECT " + select + " FROM" + sources);
        preparedQuery.countSql="SELECT count(*) FROM" + sources;

        if (this.expression!=null)
        {
            query.append(" WHERE ("+this.expression+")");
        }
        if (list.size()>0)
        {
            preparedQuery.parameters=list.toArray(new Object[list.size()]);
        }
        preparedQuery.sql=query.toString();
        if (this.orderBy!=null)
        {
            preparedQuery.orderBy=" ORDER BY "+this.orderBy;
        }
        this.preparedQuery=preparedQuery;
        return this.preparedQuery;
    }
    
    
}
