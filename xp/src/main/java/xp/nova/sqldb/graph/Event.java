package xp.nova.sqldb.graph;

import java.sql.Timestamp;

public class Event
{
    public final long id;
    public final Timestamp created;
    public final long creatorId;
    public final String source;
    
    public Event(long id,Timestamp created,long creatorId,String source)
    {
        this.id=id;
        this.created=created;
        this.creatorId=creatorId;
        this.source=source;
    }
}
