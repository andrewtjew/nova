package xp.nova.sqldb.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nova.html.tags.pre;
import org.nova.utils.TypeUtils;

import xp.nova.sqldb.graph.Query.State;

public class Query
{
    Class<? extends NodeObject>[] nodeTypes;
    Class<? extends NodeObject>[] optionalNodeTypes;

    String expression;
 //   Object[] parameters;
    String orderBy;
    Integer limit;
    Integer offset;
    
    ArrayList<LinkQuery> linkQueries;

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
    public Query orderBy(Class<? extends NodeObject> nodeType,boolean descending)
    {
        return orderBy(nodeType.getSimpleName()+"._nodeId",descending);
    }
    public Query orderBy(Class<? extends NodeObject> nodeType)
    {
        return orderBy(nodeType,false);
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
    public Query limit(int limit,int offset)
    {
        this.limit=limit;
        this.offset=offset;
        return this;
    }

//    public Query where(String expression, Object... parameters)
//    {
//        GraphAccessor.translateParameters(parameters);
//        this.parameters=parameters;
//        this.expression=expression;
//        return this;
//    }
    
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
        final List<NamespaceGraphObjectDescriptor> descriptors;
        int aliasIndex=0;
        
        
        public State(Graph graph,HashMap<String,GraphObjectDescriptor> map,StringBuilder sources,StringBuilder select,List<NamespaceGraphObjectDescriptor> descriptors)
        {
            this.graph=graph;
            this.map=map;
            this.sources=sources;
            this.select=select;
            this.descriptors=descriptors;
        }
        void addLinkQueries(ArrayList<LinkQuery> linkQueries, String source) throws Throwable
        {
            if (linkQueries == null)
            {
                return;
            }
            for (LinkQuery linkQuery : linkQueries)
            {
//                TypeUtils.addToList(state.parameters,linkQuery.parameters);
                String linkAlias = "_link" + this.aliasIndex;
                String nodeAlias=null;
                String nodeNamespace = linkQuery.nodeNamespace != null ? linkQuery.nodeNamespace + "." : "";
                String linkNamespace = linkQuery.linkNamespace != null ? linkQuery.linkNamespace + "." : "";

//                Class<? extends NodeObject> fromType=null;
                if (linkQuery.nodeTypes==null)
                {
                    this.sources.append(" LEFT JOIN");
                }
                else
                {
                    this.sources.append(" JOIN");
                }
      //          Class<? extends NodeObject> relationFromType=state.graph.getRelationNodeType(linkQuery.relation);
                switch (linkQuery.direction)
                {
                case FROM:
                    nodeAlias = " ON _link" + this.aliasIndex+".toNodeId=";
                    this.sources.append(" _link AS " + linkAlias + source + linkAlias + ".fromNodeId");
                    this.sources.append(" AND "+linkAlias+".relationValue="+linkQuery.relationValue);
                    if (linkQuery.nodeTypes!=null)
                    {
                        this.sources.append(" AND "+linkAlias+".toNodeType='"+linkQuery.nodeTypes[0].getSimpleName()+"'");
                    }
                    else if ((linkQuery.optionalNodeTypes!=null)&&(linkQuery.optionalNodeTypes.length==1))
                    {
                        this.sources.append(" AND "+linkAlias+".toNodeType='"+linkQuery.optionalNodeTypes[0].getSimpleName()+"'");
                    }
                    else if (linkQuery.targetNodeType!=null)
                    {
                        this.sources.append(" AND "+linkAlias+".toNodeType='"+linkQuery.targetNodeType.getSimpleName()+"'");
                    }
                    else
                    {
                        throw new Exception("Cannot infer targetNodeType");
                    }
                
                    
                    break;
                case TO:
                    nodeAlias = " ON _link" + this.aliasIndex+".fromNodeId=";
                    this.sources.append(" _link AS " + linkAlias + source + linkAlias + ".toNodeId");
                    this.sources.append(" AND "+linkAlias+".relationValue="+linkQuery.relationValue);
                    if (linkQuery.nodeTypes!=null)
                    {
                        this.sources.append(" AND "+linkAlias+".fromNodeType='"+linkQuery.nodeTypes[0].getSimpleName()+"'");
                    }
                    else if ((linkQuery.optionalNodeTypes!=null)&&(linkQuery.optionalNodeTypes.length==1))
                    {
                        this.sources.append(" AND "+linkAlias+".fromNodeType='"+linkQuery.optionalNodeTypes[0].getSimpleName()+"'");
                    }
                    else if (linkQuery.targetNodeType!=null)
                    {
                        this.sources.append(" AND "+linkAlias+".fromNodeType='"+linkQuery.targetNodeType.getSimpleName()+"'");
                    }
                    else
                    {
                        throw new Exception("Cannot infer targetNodeType. Specify targetNodeType in LinkQuery constructor.");
                    }
                    
                    break;
                default:
                    break;
                }

                if (linkQuery.linkTypes != null)
                {
                    String on = " ON " + linkAlias + ".nodeId=";
                    for (int i = 0; i < linkQuery.linkTypes.length; i++)
                    {
                        Class<? extends NodeObject> type = linkQuery.linkTypes[i];
                        GraphObjectDescriptor descriptor = this.graph.getGraphObjectDescriptor(type);
                        this.descriptors.add(new NamespaceGraphObjectDescriptor(linkNamespace,descriptor));
                        this.map.put(nodeNamespace+descriptor.getTypeName(), descriptor);
                        String typeName = descriptor.getTypeName();
                        String table = descriptor.getTableName();

                        String as=" ";
                        String alias=table;
                        if (linkQuery.linkNamespace!=null)
                        {
                            alias="`"+linkQuery.linkNamespace+"_"+typeName+"`";
                            as=" AS "+alias+" ";
                        }

                        this.sources.append(" JOIN " + table + as + on + alias + "._nodeId");
                        for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
                        {
                            String fieldColumnName = linkNamespace + columnAccessor.getColumnName(typeName);
                            String tableColumnName = columnAccessor.getColumnName(alias);
                            if (this.select.length()>0)
                            {
                                this.select.append(',');
                            }
                            this.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
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
                    GraphObjectDescriptor descriptor = this.graph.getGraphObjectDescriptor(type);
                    this.descriptors.add(new NamespaceGraphObjectDescriptor(nodeNamespace,descriptor));
                    
                    String typeName = descriptor.getTypeName();
                    String table = descriptor.getTableName();

                    String as=" ";
                    String alias=table;
                    if (linkQuery.linkNamespace!=null)
                    {
                        alias="`"+linkQuery.linkNamespace+"_"+typeName+"`";
                        as=" AS "+alias+" ";
                    }
                    this.sources.append(" JOIN " + table + as + on + alias + "._nodeId AND "+alias+"._nodeId="+linkQuery.targetNode.getNodeId());
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
                        GraphObjectDescriptor descriptor = this.graph.getGraphObjectDescriptor(type);
                        this.map.put(nodeNamespace+descriptor.getTypeName(), descriptor);
                        String typeName = descriptor.getTypeName();
                        String table = descriptor.getTableName();
                        this.descriptors.add(new NamespaceGraphObjectDescriptor(nodeNamespace,descriptor));

                        String as=" ";
                        String alias=table;
                        if (linkQuery.linkNamespace!=null)
                        {
                            alias="`"+linkQuery.linkNamespace+"_"+typeName+"`";
                            as=" AS "+alias+" ";
                        }

                        this.sources.append(" JOIN " + table + as + on + alias + "._nodeId");
                        for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
                        {
                            String fieldColumnName = nodeNamespace + columnAccessor.getColumnName(typeName);
                            String tableColumnName = columnAccessor.getColumnName(alias);
                            if (this.select.length()>0)
                            {
                                this.select.append(',');
                            }
                            this.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
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
                        GraphObjectDescriptor descriptor = this.graph.getGraphObjectDescriptor(type);
                        this.map.put(descriptor.getNamespaceTypeName(linkQuery.nodeNamespace), descriptor);
                        String typeName = descriptor.getTypeName();
                        String table = descriptor.getTableName();
                        this.descriptors.add(new NamespaceGraphObjectDescriptor(nodeNamespace,descriptor));
                        String alias = descriptor.getTableAlias(linkQuery.nodeNamespace);
                        this.sources.append(" LEFT JOIN " + table + "AS " + alias + on + alias + "._nodeId");
                        for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
                        {
                            String fieldColumnName = nodeNamespace + columnAccessor.getColumnName(typeName);
                            String tableColumnName = columnAccessor.getColumnName(alias);
                            if (this.select.length()>0)
                            {
                                this.select.append(',');
                            }
                            this.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
                        }
                    }
                }
                this.aliasIndex++;
                addLinkQueries(linkQuery.linkQueries, nodeAlias);
            }
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
        preparedQuery.descriptors=new ArrayList<NamespaceGraphObjectDescriptor>();
        StringBuilder select = new StringBuilder();
        StringBuilder sources = new StringBuilder();
        State state=new State(graph,preparedQuery.typeDescriptorMap,sources,select,preparedQuery.descriptors);

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
                GraphObjectDescriptor descriptor = graph.getGraphObjectDescriptor(type);
                preparedQuery.typeDescriptorMap.put(descriptor.getTypeName(), descriptor);
                state.descriptors.add(new NamespaceGraphObjectDescriptor(null,descriptor));
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
                GraphObjectDescriptor descriptor = graph.getGraphObjectDescriptor(type);
                state.descriptors.add(new NamespaceGraphObjectDescriptor(null,descriptor));
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
//        State state=new State(graph,preparedQuery.typeDescriptorMap,sources,select);
        state.addLinkQueries(this.linkQueries, on);
        StringBuilder query = new StringBuilder("SELECT " + select + " FROM" + sources);

        if (this.expression!=null)
        {
            query.append(" WHERE ("+this.expression+")");
        }
        preparedQuery.offset=this.offset;
        preparedQuery.limit=this.limit;
        preparedQuery.orderBy=this.orderBy;
        
//        if (this.orderBy!=null)
//        {
//            query.append(" ORDER BY "+this.orderBy);
//        }
//        if (this.limit!=null)
//        {
//            if (this.offset!=null)
//            {
//                query.append(" LIMIT "+this.limit+" OFFSET "+this.offset);
//            }
//            else
//            {
//                query.append(" LIMIT "+this.limit);
//            }
//        }
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
        if (!(o instanceof Query))
        {
            return false;
        }
        
        Query other = (Query) o;
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
