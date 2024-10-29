package xp.nova.sqldb.graph;

import java.util.ArrayList;
import java.util.HashMap;

import org.nova.utils.TypeUtils;

public class ArrayQuery
{
    Class<? extends NodeObject>[] nodeTypes;
    Class<? extends NodeObject>[] optionalNodeTypes;

    String expression;
 //   Object[] parameters;
    String orderBy;
    Integer limit;
    Integer offset;
    
    ArrayList<LinkQuery> linkQueries;

    public ArrayQuery()
    {
    }
    public ArrayQuery where(String expression)
    {
        this.expression=expression;
        return this;
    }
    public ArrayQuery limit(int startIndex,int length)
    {
        this.limit=length;
        this.offset=startIndex;
        return this;
    }

    @SafeVarargs
    final public ArrayQuery select(Class<? extends NodeObject>... nodeTypes)
    {
        this.nodeTypes = nodeTypes;
        return this;
    }

    @SafeVarargs
    final public ArrayQuery selectOptional(Class<? extends NodeObject>... nodeTypes)
    {
        this.optionalNodeTypes= nodeTypes;
        return this;
    }

    public ArrayQuery traverse(LinkQuery linkQuery)
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
        int aliasIndex=0;
        
        
        public State(Graph graph,HashMap<String,GraphObjectDescriptor> map,StringBuilder sources,StringBuilder select)
        {
            this.graph=graph;
            this.map=map;
            this.sources=sources;
            this.select=select;
//            this.parameters=parameters;
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
//            TypeUtils.addToList(state.parameters,linkQuery.parameters);
            String linkAlias = "_link" + state.aliasIndex;
            String nodeAlias=null;
            String nodeNamespace = linkQuery.nodeNamespace != null ? linkQuery.nodeNamespace + "." : "";
            String linkNamespace = linkQuery.linkNamespace != null ? linkQuery.linkNamespace + "." : "";

//            Class<? extends NodeObject> fromType=null;
            if (linkQuery.nodeTypes==null)
            {
                state.sources.append(" LEFT JOIN");
            }
            else
            {
                state.sources.append(" JOIN");
            }
  //          Class<? extends NodeObject> relationFromType=state.graph.getRelationNodeType(linkQuery.relation);
            switch (linkQuery.direction)
            {
            case FROM:
                nodeAlias = " ON _link" + state.aliasIndex+".toNodeId=";
                state.sources.append(" _link AS " + linkAlias + source + linkAlias + ".fromNodeId");
                state.sources.append(" AND "+linkAlias+".relationValue="+linkQuery.relationValue);
                if (linkQuery.nodeTypes!=null)
                {
                    state.sources.append(" AND "+linkAlias+".toNodeType='"+linkQuery.nodeTypes[0].getSimpleName()+"'");
                }
                else if ((linkQuery.optionalNodeTypes!=null)&&(linkQuery.optionalNodeTypes.length==1))
                {
                    state.sources.append(" AND "+linkAlias+".toNodeType='"+linkQuery.optionalNodeTypes[0].getSimpleName()+"'");
                }
                else if (linkQuery.targetNodeType!=null)
                {
                    state.sources.append(" AND "+linkAlias+".toNodeType='"+linkQuery.targetNodeType.getSimpleName()+"'");
                }
                else
                {
                    throw new Exception("Cannot infer targetNodeType");
                }
            
                
                break;
            case TO:
                nodeAlias = " ON _link" + state.aliasIndex+".fromNodeId=";
                state.sources.append(" _link AS " + linkAlias + source + linkAlias + ".toNodeId");
                state.sources.append(" AND "+linkAlias+".relationValue="+linkQuery.relationValue);
                if (linkQuery.nodeTypes!=null)
                {
                    state.sources.append(" AND "+linkAlias+".fromNodeType='"+linkQuery.nodeTypes[0].getSimpleName()+"'");
                }
                else if ((linkQuery.optionalNodeTypes!=null)&&(linkQuery.optionalNodeTypes.length==1))
                {
                    state.sources.append(" AND "+linkAlias+".fromNodeType='"+linkQuery.optionalNodeTypes[0].getSimpleName()+"'");
                }
                else if (linkQuery.targetNodeType!=null)
                {
                    state.sources.append(" AND "+linkAlias+".fromNodeType='"+linkQuery.targetNodeType.getSimpleName()+"'");
                }
                else
                {
                    throw new Exception("Cannot infer targetNodeType. Specify targetNodeType in LinkQuery constructor.");
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
                for (int i = 0; i < linkQuery.linkTypes.length; i++)
                {
                    Class<? extends NodeObject> type = linkQuery.linkTypes[i];
                    GraphObjectDescriptor descriptor = state.graph.getGraphObjectDescriptor(type);
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
                    for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
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
            if ((linkQuery.targetNode!=null)&&(linkQuery.nodeTypes == null)&&(linkQuery.optionalNodeTypes==null))
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
                Class<? extends NodeObject> type = linkQuery.targetNodeType;
                GraphObjectDescriptor descriptor = state.graph.register(type);
                String typeName = descriptor.getTypeName();
                String table = descriptor.getTableName();

                String as=" ";
                String alias=table;
                if (linkQuery.linkNamespace!=null)
                {
                    alias="`"+linkQuery.linkNamespace+"_"+typeName+"`";
                    as=" AS "+alias+" ";
                }
                state.sources.append(" JOIN " + table + as + on + alias + "._nodeId AND "+alias+"._nodeId="+linkQuery.targetNode.getNodeId());
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
                    for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
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
                    for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
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
                preparedQuery.start=" WHERE nodeId=";
            }
            else
            {
                preparedQuery.start=" AND nodeId";
            }
            on=" ON _node.id=";
            sources.append(" _array");
        }
        
        if (this.nodeTypes != null)
        {
            for (int i = 0; i < this.nodeTypes.length; i++)
            {
                Class<? extends NodeObject> type = this.nodeTypes[i];
                GraphObjectDescriptor descriptor = graph.getGraphObjectDescriptor(type);
                preparedQuery.typeDescriptorMap.put(descriptor.getTypeName(), descriptor);
                String typeName = descriptor.getTypeName();
                String table = descriptor.getTableName();
                {
                    sources.append(" JOIN " + table + " AS " + table + on + table + "._nodeId");
                }
                for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
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
                for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
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
//        ArrayList<Object> list=new ArrayList<Object>();
//        TypeUtils.addToList(list, this.parameters);
        State state=new State(graph,preparedQuery.typeDescriptorMap,sources,select);
        addLinkQueries(state,this.linkQueries, on);
        StringBuilder query = new StringBuilder("SELECT " + select + " FROM" + sources);

        if (this.expression!=null)
        {
            query.append(" WHERE ("+this.expression+")");
        }
        if (this.orderBy!=null)
        {
            query.append(" ORDER BY "+this.orderBy);
        }
        if (this.limit!=null)
        {
            if (this.offset!=null)
            {
                query.append(" LIMIT "+this.limit+" OFFSET "+this.offset);
            }
            else
            {
                query.append(" LIMIT "+this.limit);
            }
        }
        preparedQuery.sql=query.toString();
        this.preparedQuery=preparedQuery;
        return this.preparedQuery;
    }
    @Override
    public final int hashCode()
    {
        if (this.preparedQuery==null)
        {
            throw new RuntimeException();
        }
            
        return this.preparedQuery.hashCode();
    }

    @Override
    public boolean equals(Object o) 
    {
        if (o == this)
        {
            return true;
        }
        if (!(o instanceof ArrayQuery))
        {
            return false;
        }
        
        ArrayQuery other = (ArrayQuery) o;
        if (other.preparedQuery==null)
        {
            throw new RuntimeException();
        }
        if (this.preparedQuery==null)
        {
            throw new RuntimeException();
        }
            
        return other.preparedQuery.equals(this.preparedQuery);
    }        
}
