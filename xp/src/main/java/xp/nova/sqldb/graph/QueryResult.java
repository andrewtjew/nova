package xp.nova.sqldb.graph;

import java.util.Map;

public class QueryResult
{
    final private Long fromNodeId;
    final private Long toNodeId;
   
    final private GraphObject[] objects;
    private Map<String,Integer> map;
    
    QueryResult(Long fromNodeId,Long toNodeId,GraphObject[] objects,Map<String,Integer> map)
    {
        this.fromNodeId=fromNodeId;
        this.toNodeId=toNodeId;
        this.objects=objects;
        this.map=map;
    }
    public Long getFromNodeId()
    {
        return this.fromNodeId;
    }
    public Long getToNodeId()
    {
        return this.toNodeId;
    }

    @SuppressWarnings("unchecked")
    public <OBJECT extends GraphObject> OBJECT get(Class<OBJECT> type) throws Exception
    {
        Integer index=this.map.get(type.getSimpleName());
        if (index==null)
        {
            throw new Exception();
        }
        return (OBJECT) this.objects[index];
    }

    @SuppressWarnings("unchecked")
    public <OBJECT extends GraphObject> OBJECT get(int index) throws Exception
    {
        return (OBJECT) this.objects[index];
    }


}

