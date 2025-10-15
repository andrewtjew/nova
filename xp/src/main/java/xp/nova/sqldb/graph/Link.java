package xp.nova.sqldb.graph;

public class Link
{
    final String fromRelation;
//    final String toRelation;
    
    public Link(String fromObjectName,String relation,String toObjectName)
    {
        this.fromRelation=fromObjectName+relation;
    }
}
