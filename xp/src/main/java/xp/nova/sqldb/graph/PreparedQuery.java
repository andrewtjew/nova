package xp.nova.sqldb.graph;

import java.util.HashMap;

import org.nova.utils.TypeUtils;

class PreparedQuery
{
    String sql;
    String start;
    String orderBy;
    String countSql;
    String limit;
    Object[] parameters;
    HashMap<String,GraphObjectDescriptor> typeDescriptorMap;
    
    @Override
    public final int hashCode()
    {
        int code=sql.hashCode();
        if (start!=null)
        {
            code+=start.hashCode();
        }
        if (orderBy!=null)
        {
            code+=orderBy.hashCode();
        }
        if (countSql!=null)
        {
            code+=countSql.hashCode();
        }
        if (limit!=null)
        {
            code+=limit.hashCode();
        }
        if (parameters!=null)
        {
            for (Object parameter:this.parameters)
            {
                if (parameter!=null)
                {
                    code+=parameter.hashCode();
                }
            }
        }
        return code;
    }
    @Override
    public boolean equals(Object o) 
    {
        if (o == this)
        {
            return true;
        }
        if (!(o instanceof PreparedQuery))
        {
            return false;
        }
        
        PreparedQuery other = (PreparedQuery) o;
        if (TypeUtils.equals(this.sql,other.sql)==false)
        {
            return false;
        }
        if (TypeUtils.equals(this.start,other.start)==false)
        {
            return false;
        }
        if (TypeUtils.equals(this.orderBy,other.orderBy)==false)
        {
            return false;
        }
        if (TypeUtils.equals(this.countSql,other.countSql)==false)
        {
            return false;
        }
        if (TypeUtils.equals(this.limit,other.limit)==false)
        {
            return false;
        }
        if ((this.parameters!=null)&&(other.parameters==null))
        {
            return false;
        }
        if ((this.parameters==null)&&(other.parameters!=null))
        {
            return false;
        }
        if ((this.parameters!=null)&&(other.parameters!=null))
        {
            if (this.parameters.length!=other.parameters.length)
            {
                return false;
            }
            for (int i=0;i<this.parameters.length;i++)
            {
                if (this.parameters[i]==other.parameters[i])
                {
                    continue;
                }
                if ((this.parameters[i]!=null)&&(other.parameters[i]==null))
                {
                    return false;
                }
                if ((this.parameters[i]==null)&&(other.parameters[i]!=null))
                {
                    return false;
                }
                if (this.parameters[i].equals(other.parameters[i])==false)
                {
                    return false;
                }
            }
        }
        return true;
    }        
}