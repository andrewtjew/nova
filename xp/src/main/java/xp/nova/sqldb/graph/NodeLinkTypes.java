package xp.nova.sqldb.graph;

public class NodeLinkTypes
{
    Class<? extends NodeObject> requiredType;
    Class<? extends NodeObject>[] optionalObjectTypes; 
    
    public NodeLinkTypes(Class<? extends NodeObject> type,Class<? extends NodeObject>...types)
    {
        this.requiredType=type;
        this.optionalObjectTypes=types;
    }

}
