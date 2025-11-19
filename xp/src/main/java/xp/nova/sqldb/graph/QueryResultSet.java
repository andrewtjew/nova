package xp.nova.sqldb.graph;

import java.lang.reflect.Array;
import java.util.Map;

import org.nova.sqldb.RowSet;

public class QueryResultSet
{
    final QueryResult[] results;
    final private Map<String,GraphObjectDescriptor> map;
    final String sql;
    final Object[] parameters;
    final Long nodeId;
    final PreparedQuery query;
    
    QueryResultSet(RowSet rowSet,PreparedQuery query,String sql,Long nodeId,Object[] parameters) throws Exception
    {
        this.query=query;
        this.sql=sql;
        this.parameters=parameters;
        this.nodeId=nodeId;
       
        this.results=new QueryResult[rowSet.size()];
        this.map=query.typeDescriptorMap;
        for (int i=0;i<results.length;i++)
        {
            results[i]=new QueryResult(rowSet.getRow(i), this.map);
        }
    }
    
    public String getSql()
    {
        return this.sql;
    }
    public Long getNodeId()
    {
        return this.nodeId;
    }
    public Object[] getParameters()
    {
        return this.parameters;
    }
    
    public QueryResult[] getResults()
    {
        return this.results;
    }

    public QueryResult getResult() throws Throwable
    {
        if (this.results.length==0)
        {
            return null;
        }
        if (this.results.length>1)
        {
            throw new Exception("Size:"+this.results.length);
        }
        return this.results[0];
    }
    public QueryResult getResult(int index) throws Throwable
    {
        return this.results[index];
    }


    @SuppressWarnings("unchecked")
    public <OBJECT extends NodeObject> OBJECT[] getNodeObjects(String namespace,Class<OBJECT> type) throws Throwable
    {
        Object array=Array.newInstance(type, this.results.length);
        for (int i=0;i<this.results.length;i++)
        {
            Array.set(array, i, this.results[i].getNodeObject(namespace,type));
        }
        return (OBJECT[]) array;
    }
    public <OBJECT extends NodeObject> OBJECT[] getNodeObjects(Class<OBJECT> type) throws Throwable
    {
        return getNodeObjects(null,type);
    }
    @SuppressWarnings("unchecked")
    public <OBJECT extends NodeObject> OBJECT getNodeObject() throws Throwable
    {
        if (this.results.length==0)
        {
            return null;
        }
        if (this.results.length>1)
        {
            throw new Exception("Length:"+this.results.length);
        }
        Class<? extends NodeObject> type=map.values().iterator().next().getType();
        return this.results[0].getNodeObject((Class<OBJECT>)type);
    }

    @Override
    public boolean equals(Object o) 
    {
        if (o == this)
        {
            return true;
        }
        if (!(o instanceof QueryResultSet))
        {
            return false;
        }
        
        QueryResultSet other = (QueryResultSet) o;
        if (this.results.length!=other.results.length)
        {
            return false;
        }
        for (int i=0;i<this.results.length;i++)
        {
            var thisRow=this.results[i].row;
            var otherRow=other.results[i].row;
            if (PreparedQuery.equals(thisRow.getObjects(),otherRow.getObjects())==false)
            {
                return false;
            }
            
        }
        return true;
    }        
    
}

