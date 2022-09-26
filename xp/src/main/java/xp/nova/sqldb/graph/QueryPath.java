package xp.nova.sqldb.graph;

import java.util.ArrayList;

public class QueryPath
{
    final Direction direction;
    Class<? extends NodeObject>[] nodeTypes;
    Class<? extends LinkObject>[] linkTypes;
    ArrayList<QueryPath> followPaths;
    String namespace;
    public QueryPath(Direction direction)
    {
        this.direction=direction;
    }
    @SuppressWarnings("unchecked")
    public QueryPath namespace(String namespace)
    {
    	this.namespace=namespace;
    	return this;
    }
    public QueryPath selectNodes(Class<? extends NodeObject>...nodeTypes)
    {
        this.nodeTypes=nodeTypes;
        return this;
    }
    @SuppressWarnings("unchecked")
    public QueryPath selectLinks(Class<? extends LinkObject>...linkTypes)
    {
        this.linkTypes=linkTypes;
        return this;
    }    
    public QueryPath follow(QueryPath path)
    {
        if (this.followPaths==null)
        {
        	this.followPaths=new ArrayList<>();
        }
        this.followPaths.add(path);
        return this;
    }    
}
