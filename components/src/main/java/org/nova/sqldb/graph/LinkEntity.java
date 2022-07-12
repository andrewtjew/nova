package org.nova.sqldb.graph;

import java.sql.Timestamp;

public class LinkEntity extends NodeObject
{
    Long _linkId;    
    public long getLinkId()
    {
        return this._linkId;
    }
}
