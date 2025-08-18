package xp.nova.sqldb.graph;

public class IdentityNodeObject extends NodeObject
{
    @Internal
    protected Long _id;
    public long getId()
    {
        return this._id;
    }        
}
