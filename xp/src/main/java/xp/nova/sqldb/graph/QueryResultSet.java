package xp.nova.sqldb.graph;

import java.lang.reflect.Array;
import java.util.Map;

import org.nova.sqldb.RowSet;

public class QueryResultSet
{
    final QueryResult[] results;
    final Class<? extends GraphObject> one;
    
    QueryResultSet(RowSet rowSet,Class<? extends GraphObject> one,Map<String,GraphObjectDescriptor> map)
    {
        this.results=new QueryResult[rowSet.size()];
        for (int i=0;i<results.length;i++)
        {
            results[i]=new QueryResult(rowSet.getRow(i), map);
        }
        this.one=one;
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
            throw new Exception("Size:"+this.results);
        }
        return this.results[0];
    }


    @SuppressWarnings("unchecked")
    public <OBJECT extends GraphObject> OBJECT[] getObjects() throws Throwable
    {
        Object array=Array.newInstance(this.one, this.results.length);
        for (int i=0;i<this.results.length;i++)
        {
            Array.set(array, i, this.results[i].get(this.one));
        }
        return (OBJECT[]) array;
    }
    public <OBJECT extends GraphObject> OBJECT getObject() throws Throwable
    {
        if (this.results.length==0)
        {
            return null;
        }
        if (this.results.length>1)
        {
            throw new Exception("Length:"+this.results.length);
        }
        return this.results[0].get((Class<OBJECT>)this.one);
    }

    
}

