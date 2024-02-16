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
    Integer limit;
    
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
        return orderBy(orderBy,false);
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
    public Query limit(int limit)
    {
        this.limit=limit;
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

    static class State
    {
        final Graph graph;
        final HashMap<String,GraphObjectDescriptor> map;
        final StringBuilder sources;
        final StringBuilder select;
        final ArrayList<Object> parameters;
//        Class<? extends NodeObject>[] nodeTypes;
        int aliasIndex=0;
        Class<? extends RelationNodeObject<?>> startType=null;
        
        
        public State(Graph graph,HashMap<String,GraphObjectDescriptor> map,StringBuilder sources,StringBuilder select,ArrayList<Object> parameters)
        {
            this.graph=graph;
            this.map=map;
            this.sources=sources;
            this.select=select;
            this.parameters=parameters;
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
            String linkAlias = "_link" + state.aliasIndex;
            String nodeAlias=null;
            String nodeNamespace = linkQuery.nodeNamespace != null ? linkQuery.nodeNamespace + "." : "";
            String linkNamespace = linkQuery.linkNamespace != null ? linkQuery.linkNamespace + "." : "";

            Class<? extends RelationNodeObject<?>> fromType=null;
            if (linkQuery.nodeTypes==null)
            {
                state.sources.append(" LEFT JOIN");
            }
            else
            {
                state.sources.append(" JOIN");
            }
            switch (linkQuery.direction)
            {
            case FROM:
                nodeAlias = " ON _link" + state.aliasIndex+".toNodeId=";
                state.sources.append(" _link AS " + linkAlias + source + linkAlias + ".fromNodeId");
                if (linkQuery.relation!=null)
                {
                    if (fromType==null)
                    {
                        fromType=state.graph.getRelationNodeType(linkQuery.relation);
                        if (state.aliasIndex==0)
                        {
                            state.startType=fromType;
                        }
                    }
                    if (fromType!=state.graph.getRelationNodeType(linkQuery.relation))
                    {
                        throw new Exception("Not all link queries have the same from type");
                    }
                    int relationValue=linkQuery.relation.getValue();
                    state.sources.append(" AND "+linkAlias+".relationValue="+relationValue);
                }
                
                break;
            case TO:
                nodeAlias = " ON _link" + state.aliasIndex+".fromNodeId=";
                state.sources.append(" _link AS " + linkAlias + source + linkAlias + ".toNodeId");
                if (linkQuery.relation!=null)
                {
                    int relationValue=linkQuery.relation.getValue();
                    state.sources.append(" AND "+linkAlias+".relationValue="+relationValue);
                }
                
                break;
            default:
                break;
            }

//            if (linkQuery.selectLink)
//            {
//                String on = " ON " + linkAlias + ".nodeId=";
//                Class<? extends NodeObject> type = LinkObject.class;
//                GraphObjectDescriptor descriptor = state.graph.register(type);
//                state.map.put(nodeNamespace+descriptor.getTypeName(), descriptor);
//                String typeName = descriptor.getTypeName();
//                String table = descriptor.getTableName();
//
//                String as=" ";
//                String alias=table;
//                if (linkQuery.linkNamespace!=null)
//                {
//                    alias="`"+linkQuery.linkNamespace+"_"+typeName+"`";
//                    as=" AS "+alias+" ";
//                }
//
//                for (FieldDescriptor columnAccessor : descriptor.getColumnAccessors())
//                {
//                    String fieldColumnName = linkNamespace + columnAccessor.getColumnName(typeName);
//                    String tableColumnName = columnAccessor.getColumnName(linkAlias);
//                    if (state.select.length()>0)
//                    {
//                        state.select.append(',');
//                    }
//                    state.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
//                }
//            }
            if (linkQuery.linkTypes != null)
            {
                String on = " ON " + linkAlias + ".nodeId=";
                for (int i = 0; i < linkQuery.nodeTypes.length; i++)
                {
                    Class<? extends NodeObject> type = linkQuery.nodeTypes[i];
                    GraphObjectDescriptor descriptor = state.graph.register(type);
                    state.map.put(nodeNamespace+descriptor.getTypeName(), descriptor);
                    String typeName = descriptor.getTypeName();
                    String table = descriptor.getTableName();

                    String as=" ";
                    String alias=table;
                    if (linkQuery.linkNamespace!=null)
                    {
                        alias="`"+linkQuery.linkNamespace+"_"+typeName+"`";
                        as=" AS "+alias+" ";
                    }

                    state.sources.append(" JOIN " + table + as + on + alias + "._nodeId");
                    for (FieldDescriptor columnAccessor : descriptor.getColumnAccessors())
                    {
                        String fieldColumnName = linkNamespace + columnAccessor.getColumnName(typeName);
                        String tableColumnName = columnAccessor.getColumnName(alias);
                        if (state.select.length()>0)
                        {
                            state.select.append(',');
                        }
                        state.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
                    }
                }
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
                for (int i = 0; i < linkQuery.nodeTypes.length; i++)
                {
                    Class<? extends NodeObject> type = linkQuery.nodeTypes[i];
                    GraphObjectDescriptor descriptor = state.graph.register(type);
                    state.map.put(nodeNamespace+descriptor.getTypeName(), descriptor);
                    String typeName = descriptor.getTypeName();
                    String table = descriptor.getTableName();

                    String as=" ";
                    String alias=table;
                    if (linkQuery.linkNamespace!=null)
                    {
                        alias="`"+linkQuery.linkNamespace+"_"+typeName+"`";
                        as=" AS "+alias+" ";
                    }

                    state.sources.append(" JOIN " + table + as + on + alias + "._nodeId");
                    for (FieldDescriptor columnAccessor : descriptor.getColumnAccessors())
                    {
                        String fieldColumnName = nodeNamespace + columnAccessor.getColumnName(typeName);
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
                        String fieldColumnName = nodeNamespace + columnAccessor.getColumnName(typeName);
                        String tableColumnName = columnAccessor.getColumnName(alias);
                        if (state.select.length()>0)
                        {
                            state.select.append(',');
                        }
                        state.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
                    }
                }
            }
            state.aliasIndex++;
            addLinkQueries(state,linkQuery.linkQueries, nodeAlias);
        }
    }

    static class PreparedQuery
    {
        String sql;
        String start;
        Object[] parameters;
        HashMap<String,GraphObjectDescriptor> typeDescriptorMap;
        String orderBy;
        String countSql;
        String limit;
        Class<? extends RelationNodeObject<?>> startType;
    }
    
    PreparedQuery preparedQuery=null;
    
    public PreparedQuery build(Graph graph) throws Throwable
    {
        if (this.preparedQuery!=null)
        {
            return this.preparedQuery;
        }
        PreparedQuery preparedQuery=new PreparedQuery();
        preparedQuery.typeDescriptorMap=new HashMap<String, GraphObjectDescriptor>();
        StringBuilder select = new StringBuilder();
        StringBuilder sources = new StringBuilder();

        String on=null;
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
        
        if (this.nodeTypes != null)
        {
            for (int i = 0; i < this.nodeTypes.length; i++)
            {
                Class<? extends NodeObject> type = this.nodeTypes[i];
                GraphObjectDescriptor descriptor = graph.register(type);
                preparedQuery.typeDescriptorMap.put(descriptor.getTypeName(), descriptor);
                String typeName = descriptor.getTypeName();
                String table = descriptor.getTableName();
                {
                    sources.append(" JOIN " + table + " AS " + table + on + table + "._nodeId");
                }
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
                preparedQuery.typeDescriptorMap.put(descriptor.getTypeName(), descriptor);
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
        State state=new State(graph,preparedQuery.typeDescriptorMap,sources,select,list);
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
        if (this.limit!=null)
        {
            preparedQuery.limit=" LIMIT "+this.limit;
        }
        preparedQuery.startType=state.startType;
        this.preparedQuery=preparedQuery;
        return this.preparedQuery;
    }
    
    
}
