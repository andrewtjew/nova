package xp.nova.sqldb.graph;

import java.util.ArrayList;

public class Query
{
    final private Class<? extends NodeObject> type;
    final private Long fromNodeId;
    final private Long toNodeId;
    final private ArrayList<Class<? extends NodeObject>> optionalTypes=new ArrayList<Class<? extends NodeObject>>();
    
    public Query(Class<? extends NodeObject> type)
    {
        this.type=type;
        this.fromNodeId=null;
        this.toNodeId=null;
    }
    public Query(long fromNodeId,Class<? extends NodeObject> type)
    {
        this.type=type;
        this.fromNodeId=fromNodeId;
        this.toNodeId=null;
    }
    public Query(Class<? extends NodeObject> type,long toNodeId)
    {
        this.type=type;
        this.fromNodeId=null;
        this.toNodeId=toNodeId;
    }
    public Query optionalTypes(Class<? extends NodeObject>...optionalTypes)
    {
        for (Class<? extends NodeObject> optionalType:optionalTypes)
        {
            this.optionalTypes.add(optionalType);
        }
        return this;
    }
    
}
