package xp.nova.sqldb.graph;


public enum Relation
{
    Links(0), // for when from and to nodes are of the same type like City. Also Connects
    IsEqualTo(1), 
    IsGreaterThan(2), 
    IsEqualToOrGreaterThan(3), 
    IsLessThan(4), 
    IsEqualToOrLessThan(5), 
    IsNotEqualTo(6),
    Contains(7), //Containment either physical or structural. From and to node should be able to provide context of containment type.
    Provides(8),
    IsUsing(9), //Also Selects. 
    Has(10),  //Also owns
    
    HasImage(-201),
    HasLogo(-202),
    HasVideo(-203),
    IsUsingDate(-204),
    
    ;   
    
    private final int value;
    private Relation(int value)
    {
        this.value=value;
    }
    public static Relation from(int value)
    {
        for (Relation item:values())
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

