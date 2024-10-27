package xp.nova.sqldb.graph;

import org.nova.utils.TypeUtils;

class QueryKey
{
    final Query query;
    final Long nodeId;
    final Object[] parameters;
    
    public QueryKey(Long nodeId,Query query,Object...parameters)
    {
        this.query=query;
        this.nodeId=nodeId;
        this.parameters=parameters;
    }
    @Override
    public final int hashCode()
    {
        int hashCode=this.query.hashCode();
        if (nodeId!=null)
        {
            hashCode+=nodeId.hashCode();
        }
        if (this.parameters!=null)
        {
            for (var parameter:this.parameters)
            {
                if (parameter!=null)
                {
                    hashCode+=parameter.hashCode();
                }
            }
        }
        if (this.nodeId!=null)
        {
            hashCode+=this.nodeId.hashCode();
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object o) 
    {
        if (o == this)
        {
            return true;
        }
        if (!(o instanceof QueryKey))
        {
            return false;
        }
        
        QueryKey other = (QueryKey) o;
        if (TypeUtils.equals(this.nodeId, other.nodeId)==false)
        {
            return false;
        }
        if (this.query.equals(other.query)==false)
        {
            return false;
        }
        return TypeUtils.equals(this.parameters, other.parameters);
    }        
}