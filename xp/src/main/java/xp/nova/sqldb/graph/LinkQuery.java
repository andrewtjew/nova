package xp.nova.sqldb.graph;

import java.util.ArrayList;

public class LinkQuery
{
    final Direction direction;
    final long relationValue;
    final Class<? extends Node> targetNodeType;
    final Node targetNode;

    Class<? extends Node>[] nodeTypes;
    Class<? extends Node>[] optionalNodeTypes;
    Class<? extends Node>[] linkTypes;
    Class<? extends Node>[] optionalLinkTypes;
    String nodeNamespace=null;
    String linkNamespace=null;
    

//    boolean selectLink;   
    
//    String expression;
    Object[] parameters;
    ArrayList<LinkQuery> linkQueries;

    public LinkQuery(Direction direction,Relation_ relation,Node targetNode)
    {
        this.direction=direction;
        this.targetNodeType=targetNode.getClass();
        this.targetNode=targetNode;
        this.relationValue=relation.getValue();
    }
    public LinkQuery(Direction direction,Relation_ relation,Class<? extends Node> targetNodeType)
    {
        this.direction=direction;
        this.targetNodeType=targetNodeType;
        this.relationValue=relation.getValue();
        this.targetNode=null;
    }
    public LinkQuery(Direction direction,Relation_ relation)
    {
        this(direction,relation,(Class<? extends Node>)null);
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
    
    @SafeVarargs
    final public LinkQuery select(Class<? extends Node>... nodeTypes)
    {
        this.nodeTypes = nodeTypes;
        return this;
    }

    @SafeVarargs
    final public LinkQuery selectOptional(Class<? extends Node>... nodeTypes)
    {
        this.optionalNodeTypes= nodeTypes;
        return this;
    }

    @SafeVarargs
    final public LinkQuery selectLink(Class<? extends Node>... nodeTypes)
    {
        this.linkTypes = nodeTypes;
        return this;
    }
    @SafeVarargs
    final public LinkQuery selectOptionalLink(Class<? extends Node>... nodeTypes)
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
