package xp.nova.sqldb.graph;

import java.util.ArrayList;

public class LinkQuery
{
    final Direction direction;
    final long relationValue;
    
    Class<? extends NodeObject>[] nodeTypes;
    Class<? extends NodeObject>[] optionalNodeTypes;
    Class<? extends NodeObject>[] linkTypes;
    Class<? extends NodeObject>[] optionalLinkTypes;
    String nodeNamespace=null;
    String linkNamespace=null;

//    boolean selectLink;
    
//    String expression;
    Object[] parameters;
    ArrayList<LinkQuery> linkQueries;
    
    public LinkQuery(Direction direction,Relation_ relation,RelationObjectType_ objectType)
    {
        this.direction=direction;
        this.relationValue=GraphTransaction.toRelationValue(relation, objectType);
    }
    public LinkQuery(Direction direction,Relation_ relation)
    {
        this(direction,relation,null);
    }
//    public LinkQuery(Direction direction) throws Exception
//    {
//        this.direction=direction;
//        this.relation=null;
//    }
    
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
    @SafeVarargs
    final public LinkQuery selectOptionalLink(Class<? extends NodeObject>... nodeTypes)
    {
        this.optionalLinkTypes = nodeTypes;
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
