package xp.nova.sqldb.graph;

import java.util.ArrayList;

public class Predicate
{
    final Direction direction;
    Class<? extends NodeObject>[] nodeTypes;
    Class<? extends LinkObject>[] linkTypes;
    ArrayList<Predicate> predicates;
    String namespace;
    Relation relation;
    public Predicate(Direction direction)
    {
        this.direction=direction;
    }
    public Predicate(Direction direction,Relation relation)
    {
        this.direction=direction;
        this.relation=relation;
    }
    public Predicate namespace(String namespace)
    {
    	this.namespace=namespace;
    	return this;
    }
    @SafeVarargs
    final public Predicate selectNodeObjects(Class<? extends NodeObject>...nodeTypes)
    {
        this.nodeTypes=nodeTypes;
        return this;
    }

    @SafeVarargs
    final public Predicate selectLinkObjects(Class<? extends LinkObject>...linkTypes)
    {
        this.linkTypes=linkTypes;
        return this;
    }

    public Predicate with(Predicate predicate)
    {
        if (this.predicates==null)
        {
        	this.predicates=new ArrayList<>();
        }
        this.predicates.add(predicate);
        return this;
    }    
}
