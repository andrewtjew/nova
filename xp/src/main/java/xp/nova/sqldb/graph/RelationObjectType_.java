package xp.nova.sqldb.graph;

//This is needed to provide a richer link meaning, specifically application defined meaning. Just knowing the target type is not always enough when the target type is something generic. 
//For example if the target type is an image, then we often need to know the purpose of the image, for example logo,   
public interface RelationObjectType_
{
    public int getValue();
}
