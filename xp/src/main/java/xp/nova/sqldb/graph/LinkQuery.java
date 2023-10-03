package xp.nova.sqldb.graph;

import java.util.ArrayList;

public class LinkQuery
{
    Class<? extends NodeObject>[] nodeTypes;
    Class<? extends NodeObject>[] optionalNodeTypes;
    Class<? extends NodeObject>[] linkTypes;
    Direction direction;
    String nodeNamespace="";
    String linkNamespace="";
    Relation_ relation;
    boolean optional;
    
    String expression;
    Object[] parameters;
    ArrayList<LinkQuery> linkQueries;

    public LinkQuery(Direction direction,Relation_ relation)
    {
        this.direction=direction;
        this.relation=relation;
        this.optional=false;
    }
    public LinkQuery nodeNamespace(String namespace)
    {
        this.nodeNamespace=namespace;
        return this;
    }    
    public LinkQuery linkNamespace(String namespace)
    {
        this.linkNamespace=namespace;
        return this;
    }    
    public LinkQuery optional()
    {
        this.optional=true;
        return this;
    }    
    
    public LinkQuery where(String expression)
    {
        this.expression=expression;
        return this;
    }

    public LinkQuery where(String expression, Object... parameters)
    {
        this.parameters=parameters;
        this.expression=expression;
        return this;
    }
    
//    @SafeVarargs
//    final public LinkQuery select2(Class<? extends GraphObject>... nodeTypes)
//    {
//        this.nodeTypes = nodeTypes;
//        return this;
//    }
    @SafeVarargs
    final public LinkQuery select(Class<? extends NodeObject>... nodeTypes)
    {
        this.nodeTypes = nodeTypes;
        return this;
    }

    @SafeVarargs
    final public LinkQuery selectOptional(Class<? extends NodeObject>... nodeTypes)
    {
        this.optionalNodeTypes= nodeTypes;
        return this;
    }

    @SafeVarargs
    final public LinkQuery selectLink(Class<? extends NodeObject>... nodeTypes)
    {
        this.linkTypes = nodeTypes;
        return this;
    }

    public LinkQuery traverse(LinkQuery linkQuery)
    {
        if (this.linkQueries == null)
        {
            this.linkQueries = new ArrayList<>();
        }
        this.linkQueries.add(linkQuery);
        return this;
    }
    
}
