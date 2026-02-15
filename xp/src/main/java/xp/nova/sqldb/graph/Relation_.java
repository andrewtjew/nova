package xp.nova.sqldb.graph;

public interface Relation_
{
    public String getKey();
    
    
    public static String getKey(Relation_ relation)
    {
        if (relation==null)
        {
            return "";
        }
        return relation.getKey();
    }
}
