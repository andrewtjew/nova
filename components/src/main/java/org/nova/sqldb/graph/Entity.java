package org.nova.sqldb.graph;

import org.nova.sqldb.Row;

public class Entity<OBJECT>
{
    final private Row row;
    final private OBJECT object;
    final private String typeName;
    Entity(Row row,String typeName)
    {
        this.row=row;
        this.object=null;
        this.typeName=typeName;
        
    }
}
