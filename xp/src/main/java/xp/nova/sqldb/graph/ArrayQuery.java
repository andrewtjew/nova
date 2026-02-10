package xp.nova.sqldb.graph;

import java.util.ArrayList;
import java.util.HashMap;

public class ArrayQuery
{
    Class<? extends NodeObject>[] nodeTypes;
    Class<? extends NodeObject>[] optionalNodeTypes;

    String expression;
 //   Object[] parameters;
    Integer limit;
    Integer offset;
    
    ArrayList<LinkQuery> linkQueries;

    public ArrayQuery()
    {
    }
    public ArrayQuery where(String expression)
    {
        this.preparedQuery=null;
        this.expression=expression;
        return this;
    }
    public ArrayQuery range(int startIndex,int length)
    {
        this.limit=length;
        this.offset=startIndex;
        return this;
    }
    public ArrayQuery start(int startIndex)
    {
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
        if ((this.expression==null)&&(this.offset==null))
        {
            preparedQuery.start=" WHERE `@arraylink`.arrayNodeId=";
        }
        else
        {
            preparedQuery.start=" AND `@arraylink`.arrayNodeId=";
        }
        on=" ON `@array`.elementId=";
        sources.append(" `@arraylink` JOIN `@array` ON `@array`.nodeId=`@arraylink`.nodeId ");
        
        if (this.nodeTypes != null)
        {
            for (int i = 0; i < this.nodeTypes.length; i++)
            {
                Class<? extends NodeObject> type = this.nodeTypes[i];
                GraphObjectDescriptor descriptor = graph.getGraphObjectDescriptor(type);
                if (descriptor==null)
                {
                    throw new Exception("Type not registered: type="+type);
                }
                preparedQuery.typeDescriptorMap.put(descriptor.getTypeName(), descriptor);
                String typeName = descriptor.getTypeName();
                state.descriptors.add(new NamespaceGraphObjectDescriptor(null,descriptor));
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
                state.descriptors.add(new NamespaceGraphObjectDescriptor(null,descriptor));
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
            if (this.offset!=null)
            {
                query.append(" AND `@array`.index>="+this.offset);
            }
        }
        else if (this.offset!=null)
        {
            query.append(" WHERE `@array`.index>="+this.offset);
            preparedQuery.limit=this.limit;
        }
        preparedQuery.orderBy="`@array`.index";
        preparedQuery.sql=query.toString();
        preparedQuery.array=true;
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
