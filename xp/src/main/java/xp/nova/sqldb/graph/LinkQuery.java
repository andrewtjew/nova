package xp.nova.sqldb.graph;

import java.util.ArrayList;

public class LinkQuery
{
    /*
     * Links must have fromNodeType and toNodeType because the target of a link is specified by Relation and targetNodeType
     */
    final Direction direction;
    final long relationValue;
    final Class<? extends NodeObject> targetNodeType;
    final Long targetNodeId;

    Class<? extends NodeObject>[] nodeTypes;
    Class<? extends NodeObject>[] optionalNodeTypes;
    Class<? extends NodeObject>[] linkTypes;
    Class<? extends NodeObject>[] optionalLinkTypes;
    String nodeNamespace=null;
    String linkNamespace=null;
    Object[] parameters;
    ArrayList<LinkQuery> linkQueries;

    public LinkQuery(Direction direction,Relation_ relation,NodeObject targetNode)
    {
        this(direction,relation,targetNode.getClass(),targetNode.getNodeId());
    }
    public LinkQuery(Direction direction,Relation_ relation,Class<? extends NodeObject> targetNodeType)
    {
        this(direction,relation,targetNodeType,null);
    }
    public LinkQuery(Direction direction,Relation_ relation,Class<? extends NodeObject> targetNodeType,Long targetNodeId)
    {
        this.direction=direction;
        this.targetNodeType=targetNodeType;
        this.relationValue=relation.getValue();
        this.targetNodeId=targetNodeId;
    }    
//    public LinkQuery(Direction direction,Relation_ relation)
//    {
//        this(direction,relation,(Class<? extends NodeObject>)null);
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
