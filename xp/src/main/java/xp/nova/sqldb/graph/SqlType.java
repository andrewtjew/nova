package xp.nova.sqldb.graph;

import org.nova.utils.TypeUtils;

public class SqlType
{
    private final String name;
    private final Long length;
    private final boolean nullable;
    public SqlType(String name,boolean nullable,Long length)
    {
        this.name=name;
        this.length=length;
        this.nullable=nullable;
    }
    public SqlType(String name,boolean nullable)
    {
        this(name,nullable,null);
    }
    public boolean isEqual(String name,boolean nullable,Long length)
    {
        if (this.name.equals(name)==false)
        {
            return false;
        }
        if (this.nullable!=nullable)
        {
            return false;
        }
        return TypeUtils.equals(this.length, length);
    }
    public String getType()
    {
        if (this.length!=null)
        {
            return this.name+"("+this.length+")";
        }
        return this.name;
    }
    public Long getLength()
    {
        return this.length;
    }
    public String getSql()
    {
        StringBuilder sb=new StringBuilder(this.name);
        if (this.length!=null)
        {
            sb.append("("+this.length+")");
        }
        if (this.nullable==false)
        {
            sb.append(" NOT NULL");
        }
        else
        {
            sb.append(" NULL DEFAULT NULL");
        }
        return sb.toString();
        
    }
    public boolean isLengthAcceptable(Long length)
    {
        if (this.length==null)
        {
            return true;
        }
        if (length==null)
        {
            return false;
        }
        return length<this.length;
    }
}