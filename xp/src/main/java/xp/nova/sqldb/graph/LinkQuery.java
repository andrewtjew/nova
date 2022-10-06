package xp.nova.sqldb.graph;

import java.util.ArrayList;

public class LinkQuery
{
    Class<? extends NodeObject>[] nodeTypes;
    Class<? extends NodeObject>[] optionalNodeTypes;
    Class<? extends LinkObject>[] linkTypes;
    Class<? extends GraphObject> one;
    Direction direction;
    String namespace;
    Relation relation;
    
    String expression;
    Object[] parameters;
    ArrayList<LinkQuery> linkQueries;

    public LinkQuery(Direction direction,Relation relation)
    {
        this.direction=direction;
        this.relation=relation;
    }
    public LinkQuery namespace(String namespace)
    {
        this.namespace=namespace;
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
    
    @SafeVarargs
    final public LinkQuery select(Class<? extends NodeObject>... nodeTypes)
    {
        this.nodeTypes = nodeTypes;
        if (nodeTypes.length>0)
        {
            this.one=nodeTypes[0];
        }
        return this;
    }

    @SafeVarargs
    final public LinkQuery selectOptional(Class<? extends NodeObject>... nodeTypes)
    {
        this.optionalNodeTypes= nodeTypes;
        if (nodeTypes.length>0)
        {
            this.one=nodeTypes[0];
        }
        return this;
    }

    @SafeVarargs
    final public LinkQuery selectLinkObjects(Class<? extends LinkObject>... linkTypes)
    {
        this.linkTypes = linkTypes;
        if (nodeTypes.length>0)
        {
            this.one=nodeTypes[0];
        }
        return this;
    }

    public LinkQuery traverse(LinkQuery predictate)
    {
        if (this.linkQueries == null)
        {
            this.linkQueries = new ArrayList<>();
        }
        this.linkQueries.add(predictate);
        return this;
    }
    
}
