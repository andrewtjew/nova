package org.nova.sqldb.graph;

public class Where
{
    final private Object[] parameters;
    final private String where;
    
    public Where(String where,Object...parameters)
    {
        this.where=where;
        this.parameters=parameters;
    }
    
    public Object[] getParameters()
    {
        return this.parameters;
    }
    
    public String getWhere()
    {
        return this.where;
    }
}
