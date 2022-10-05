package xp.nova.sqldb.graph;

import java.lang.reflect.Array;
import java.util.Map;

import org.nova.sqldb.RowSet;

public class QueryResultSet
{
    final QueryResult[] results;
    
    QueryResultSet(RowSet rowSet,Map<String,Meta> map)
    {
        this.results=new QueryResult[rowSet.size()];
        for (int i=0;i<results.length;i++)
        {
            results[i]=new QueryResult(rowSet.getRow(i), map);
        }
    }
    
    public QueryResult[] get()
    {
        return this.results;
    }

    public QueryResult getOne() throws Throwable
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
    public <OBJECT extends NodeObject> OBJECT[] get(Class<OBJECT> type) throws Throwable
    {
        Object array=Array.newInstance(type, this.results.length);
        for (int i=0;i<this.results.length;i++)
        {
            Array.set(array, i, this.results[i].get(type));
        }
        return (OBJECT[]) array;
    }
    public <OBJECT extends NodeObject> OBJECT getOne(Class<OBJECT> type) throws Throwable
    {
        if (this.results.length==0)
        {
            return null;
        }
        if (this.results.length>1)
        {
            throw new Exception("Size:"+this.results);
        }
        return this.results[0].get(type);
    }

    
}

