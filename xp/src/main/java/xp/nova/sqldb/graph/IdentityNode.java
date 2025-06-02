package xp.nova.sqldb.graph;

public class IdentityNode extends Node
{
    @Internal
    protected Long _id;
    public long getId()
    {
        return this._id;
    }        
}
