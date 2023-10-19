package xp.nova.sqldb.graph;

public enum Link_ implements Relation_
{
    LINKS_TO(-1),
    HAS(-2), //Possesses 
    IS(-3),
    USES(-4),
    SELECTS(-5),
    CONTAINS(-6),
    
    
    IS_EQUAL_TO(-1000),
    IS_NOT_EQUAL_TO(-1001),
    IS_GREATER_THAN(-1002),
    IS_LESS_THAN(-1003),
    IS_GREATER_OR_EQUAL_TO(-1004),
    IS_LESS_OR_EQUAL_TO(-1005),
    
    IS_ON(-1006),
    IS_NOT_ON(-1007),
    IS_OUTSIDE(-1008),
    IS_INSIDE(-1009),
    IS_ON_OR_OUTSIDE(-1010),
    IS_ON_OR_INSIDE(-1011),
    
    ;
    
    private final int value;
    
    private Link_(int value)
    {
        this.value=value;
    }
    
    public static Link_ from(int value)
    {
        for (Link_ item:values())
        {
            if (item.value==value)
            {
                return item;
            }
        }
        return null;
    }
    
    public int getValue()
    {
        return this.value;
    }
    
}
