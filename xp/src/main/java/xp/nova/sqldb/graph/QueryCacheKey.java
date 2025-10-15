package xp.nova.sqldb.graph;

import org.nova.utils.TypeUtils;

public class QueryCacheKey
{
    final PreparedQuery preparedQuery;
    final Long nodeId;
    final Object[] parameters;
    
    public QueryCacheKey(Long nodeId,PreparedQuery query,Object...parameters)
    {
        this.preparedQuery=query;
        this.nodeId=nodeId;
        this.parameters=parameters;
    }
    @Override
    public final int hashCode()
    {
        int hashCode=this.preparedQuery.hashCode();
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
        if (!(o instanceof QueryCacheKey))
        {
            return false;
        }
        
        QueryCacheKey other = (QueryCacheKey) o;
        if (TypeUtils.equals(this.nodeId, other.nodeId)==false)
        {
            return false;
        }
        if (this.preparedQuery.equals(other.preparedQuery)==false)
        {
            return false;
        }
        return PreparedQuery.equals(this.parameters, other.parameters);
    }        
    public Long getNodeId()
    {
        return this.nodeId;
    }
    public Object[] getParameters()
    {
        return this.parameters;
    }
}