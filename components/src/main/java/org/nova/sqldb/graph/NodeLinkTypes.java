package org.nova.sqldb.graph;

public class NodeLinkTypes
{
    Class<? extends NodeEntity> nodeEntityType;
    Class<? extends NodeObject>[] attributeTypes; 
    
    public NodeLinkTypes(Class<? extends NodeEntity> nodeEntityType,Class<? extends NodeObject>...attributeTypes)
    {
        this.nodeEntityType=nodeEntityType;
        this.attributeTypes=attributeTypes;
    }

}
