package xp.nova.sqldb.graph;

public class Path
{
    final Direction direction;
    Class<? extends NodeObject>[] nodeTypes;
    Class<? extends LinkObject>[] linkTypes;
    Path followPath;
    
    public Path(Direction direction)
    {
        this.direction=direction;
    }
    @SuppressWarnings("unchecked")
    public Path selectNodes(Class<? extends NodeObject>...nodeTypes)
    {
        this.nodeTypes=nodeTypes;
        return this;
    }
    @SuppressWarnings("unchecked")
    public Path selectLinks(Class<? extends LinkObject>...linkTypes)
    {
        this.linkTypes=linkTypes;
        return this;
    }    
    public Path follow(Path path)
    {
        this.followPath=path;;
        return this;
    }    
}
