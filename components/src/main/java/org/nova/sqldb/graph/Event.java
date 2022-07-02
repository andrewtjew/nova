package org.nova.sqldb.graph;

import java.sql.Timestamp;

public class Event
{
    public long id;
    public Timestamp created;
    public long creatorId;
    public String source;
}
