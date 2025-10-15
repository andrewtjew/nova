package xp.nova.sqldb.graph;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    ArrayList<NamespaceGraphObjectDescriptor> descriptors;

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
        return equals(this.parameters,other.parameters);
    }     
    
    static public boolean equals(Object[] a,Object[] b) 
    {
        if (a!=null)
        {
            if (b==null)
            {
                return false;
            }
            if (a.length!=b.length)
            {
                return false;
            }
            for (int i=0;i<a.length;i++)
            {
                Object oa=a[i];
                Object ob=b[i];
                if (oa==null)
                {
                    if (ob!=null)
                    {
                        return false;
                    }
                }
                else if (oa.equals(ob)==false)
                {
                    Class<?> ta=oa.getClass();
                    if (ta.isArray()==false)
                    {
                        return false;
                    }
                    Class<?> tb=ob.getClass();
                    Class<?> tca=ta.getComponentType();
                    Class<?> tcb=tb.getComponentType();
                    if (tca!=tcb)
                    {
                        return false;
                    }
                    if (tca==byte.class)
                    {
                        return Arrays.equals((byte[])oa,(byte[])ob);
                    }
                    else if (tca==char.class)
                    {
                        return Arrays.equals((char[])oa,(char[])ob);
                    }
                    else if (tca==int.class)
                    {
                        return Arrays.equals((int[])oa,(int[])ob);
                    }
                    else if (tca==long.class)
                    {
                        return Arrays.equals((long[])oa,(long[])ob);
                    }
                    else if (tca==boolean.class)
                    {
                        return Arrays.equals((boolean[])oa,(boolean[])ob);
                    }
                    else if (tca==double.class)
                    {
                        return Arrays.equals((double[])oa,(double[])ob);
                    }
                    else if (tca==float.class)
                    {
                        return Arrays.equals((float[])oa,(float[])ob);
                    }
                    else if (tca==short.class)
                    {
                        return Arrays.equals((short[])oa,(short[])ob);
                    }
                    //Need to add support for LocalDateTime
                    throw new RuntimeException();
                }
            }
            return true;
        }
        return b==null;
    }

}
