package xp.nova.sqldb.graph;

import java.util.ArrayList;
import java.util.HashMap;

public class Query
{
    Class<? extends Node>[] nodeTypes;
    Class<? extends Node>[] optionalNodeTypes;

    String expression;
    String orderBy;
    Integer limit;
    Integer offset;
    
    ArrayList<LinkQuery> linkQueries;

    public Query()
    {
    }
    public Query where(String expression)
    {
        this.preparedQuery=null;
        this.expression=expression;
        return this;
    }
    public Query orderBy(String orderBy)
    {
        return orderBy(orderBy,false);
    }
    public Query orderBy(Class<? extends Node> nodeType,boolean descending)
    {
        return orderBy(nodeType.getSimpleName()+"._nodeId",descending);
    }
    public Query orderBy(Class<? extends Node> nodeType)
    {
        return orderBy(nodeType,false);
    }
    public Query orderBy(String orderBy,boolean descending)
    {
        this.preparedQuery=null;
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
        this.preparedQuery=null;
        this.limit=limit;
        return this;
    }
    public Query limit(int limit,int offset)
    {
        this.preparedQuery=null;
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
    final public Query select(Class<? extends Node>... nodeTypes)
    {
        this.preparedQuery=null;
        this.nodeTypes = nodeTypes;
        return this;
    }

    @SafeVarargs
    final public Query selectOptional(Class<? extends Node>... nodeTypes)
    {
        this.preparedQuery=null;
        this.optionalNodeTypes= nodeTypes;
        return this;
    }

    public Query traverse(LinkQuery linkQuery)
    {
        this.preparedQuery=null;
        if (this.linkQueries == null)
        {
            this.linkQueries = new ArrayList<>();
        }
        this.linkQueries.add(linkQuery);
        return this;
    }

    PreparedQuery preparedQuery=null;
    
    public PreparedQuery build(Graph graph,boolean count) throws Throwable
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
        PrepareState state=new PrepareState(graph,preparedQuery.typeDescriptorMap,sources,select,preparedQuery.descriptors);

        String on=null;
        if (on==null)
        {
            if (this.expression==null)
            {
                preparedQuery.start=" WHERE `~node`.id=";
            }
            else
            {
                preparedQuery.start=" AND `~node`.id=";
            }
            on=" ON `~node`.id=";
            sources.append(" `~node`");
        }
        if (this.nodeTypes != null)
        {
            for (int i = 0; i < this.nodeTypes.length; i++)
            {
                Class<? extends Node> type = this.nodeTypes[i];
                GraphObjectDescriptor descriptor = graph.getGraphObjectDescriptor(type);
                if (descriptor==null)
                {
                    throw new Exception("Type not registered: type="+type.getCanonicalName());
                }
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
                Class<? extends Node> type = this.optionalNodeTypes[i];
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
        state.addLinkQueries(this.linkQueries, on);
        StringBuilder query = count?new StringBuilder("SELECT COUNT(*) FROM" + sources):new StringBuilder("SELECT " + select + " FROM" + sources);

        if (this.expression!=null)
        {
            query.append(" WHERE ("+this.expression+")");
        }
        preparedQuery.offset=this.offset;
        preparedQuery.limit=this.limit;
        preparedQuery.orderBy=this.orderBy;
        
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
