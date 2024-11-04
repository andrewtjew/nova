package xp.nova.sqldb.graph;

import java.util.ArrayList;
import java.util.HashMap;
import org.nova.utils.TypeUtils;

public class PreparedQuery
{
    String sql;
    String start;
    Object[] parameters;
    Integer limit;
    Integer offset;
    String orderBy;
    HashMap<String,GraphObjectDescriptor> typeDescriptorMap;
    ArrayList<GraphObjectDescriptor> descriptors;

    @Override
    public final int hashCode()
    {
        int code=sql.hashCode();
        if (start!=null)
        {
            code+=start.hashCode();
        }
        if (offset!=null)
        {
            code+=offset.hashCode();
        }
        if (limit!=null)
        {
            code+=limit.hashCode();
        }
        if (orderBy!=null)
        {
            code+=orderBy.hashCode();
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
        if (TypeUtils.equals(this.offset, other.offset)==false)
        {
            return false;
        }
        if (TypeUtils.equals(this.limit, other.limit)==false)
        {
            return false;
        }
        
        if (TypeUtils.equals(this.sql,other.sql)==false)
        {
            return false;
        }
        if (TypeUtils.equals(this.orderBy,other.orderBy)==false)
        {
            return false;
        }
        if (TypeUtils.equals(this.start,other.start)==false)
        {
            return false;
        }
        return TypeUtils.equals(this.parameters,other.parameters);
    }        
}
