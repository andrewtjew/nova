package xp.nova.sqldb.graph;

public class IdentityNodeObject extends NodeObject
{
    @Internal
    protected long identity;
    public long getIdentity()
    {
        return this.identity;
    }        
}
