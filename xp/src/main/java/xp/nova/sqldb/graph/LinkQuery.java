package xp.nova.sqldb.graph;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map.Entry;

import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;

import xp.nova.sqldb.graph.Graph.ColumnAccessor;
import xp.nova.sqldb.graph.Graph.GraphObjectType;
import xp.nova.sqldb.graph.Graph.Meta;

public class LinkQuery
{
    final private Graph graph;
    final private Trace parent;
    
    private GraphAccess access;
    private String expression;
    private Object[] parameters;
    private String orderBy;
    

    public LinkQuery(GraphAccess access)
    {
        this.parent=null;
        this.access=access;
        this.graph=null;
    }
    public LinkQuery(Trace parent,Graph graph)
    {
        this.parent=parent;
        this.graph=graph;
        this.access=null;
    }
    
    public LinkQuery orderBy(String orderBy)
    {
        this.orderBy=orderBy;
        return this;
    }
    public LinkQuery where(String expression,Object...parameters)
    {
        this.expression=expression;
        this.parameters=parameters;
        for (int i=0;i<parameters.length;i++)
        {
            Object parameter=parameters[i];
            if (parameter instanceof ShortEnummerable)
            {
                parameters[i]=((ShortEnummerable)parameter).getValue();
            }
        }
        return this;
    }    
    
    @SafeVarargs
    final ToNodeResult[] _execute(long[] fromNodeIds,Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>...optionalObjectTypes) throws Throwable
    {
        NodeLinkTypes nodeLinkTypes=new NodeLinkTypes(requiredObjectType,optionalObjectTypes);
        if (this.access==null)
        {
            try (GraphAccess access=this.graph.openAccess(this.parent,"Graph.LinkQuery",null, false))
            {
                this.access=access;
                return __execute(fromNodeIds,new NodeLinkTypes[] {nodeLinkTypes});
            }
            finally
            {
                this.access=null;
            }
        }
        else
        {
            return __execute(fromNodeIds,new NodeLinkTypes[] {nodeLinkTypes});
        }
    }   
    @SafeVarargs
    final ToNodeResult[] _execute(long[] fromNodeIds,NodeLinkTypes...nodeLinkTypesArray) throws Throwable
    {
        if (this.access==null)
        {
            try (GraphAccess access=this.graph.openAccess(this.parent,"Graph.LinkQuery",null, false))
            {
                this.access=access;
                return __execute(fromNodeIds,nodeLinkTypesArray);
            }
            finally
            {
                this.access=null;
            }
        }
        else
        {
            return __execute(fromNodeIds,nodeLinkTypesArray);
        }
    }   
    final ToNodeResult[] __execute(long[] fromNodeIds,NodeLinkTypes[] nodeLinkTypesArray) throws Throwable
    {
        Trace parent=this.access.parent;
        StringBuilder select = new StringBuilder();
        StringBuilder join = new StringBuilder();

        Graph graph = access.graph;
        int totalResultTypes=0;
        for (NodeLinkTypes nodeLinkTypes:nodeLinkTypesArray )
        {
            Class<? extends NodeObject> requiredObjectType=nodeLinkTypes.requiredType;
            Class<? extends NodeObject>[] optionalObjectTypes=nodeLinkTypes.optionalObjectTypes;
            
            if (requiredObjectType!=null)
            {
                totalResultTypes++;
                Meta meta=graph.getMeta(requiredObjectType);
                String typeName = meta.getTypeName();
                String table = meta.getTableName();
                String alias= meta.getTableAlias();
                join.append(" JOIN " + table + "AS "+alias+" ON _link.toNodeId=" + alias+ "._nodeId");
                for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                {
                    String fieldColumnName = columnAccessor.getColumnName(typeName);
                    String tableColumnName = columnAccessor.getColumnName(alias);
                    select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
                }
            }
            totalResultTypes+=optionalObjectTypes.length;
            for (Class<? extends GraphObject> type : optionalObjectTypes)
            {
                Meta meta=graph.getMeta(type);
                String typeName = meta.getTypeName();
                String table = meta.getTableName();
                String alias= meta.getTableAlias();
                switch (meta.getObjectType())
                {
                case LINK_OBJECT:
                    join.append(" LEFT JOIN " + table + "AS "+alias+" ON _link.id=" + alias+ "._linkId");
                    break;
                case NODE_OBJECT:
                    join.append(" LEFT JOIN " + table + "AS "+alias+" ON _link.toNodeId=" + alias+ "._nodeId");
                    break;
                default:
                    throw new Exception();
                }
                for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                {
                    String fieldColumnName = columnAccessor.getColumnName(typeName);
                    String tableColumnName = columnAccessor.getColumnName(alias);
                    select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
                }
            }
        }
        StringBuilder query = new StringBuilder("SELECT _link.id AS '_link.id',_link.fromNodeId AS '_link.fromNodeId',_link.toNodeId AS '_link.toNodeId'" + select + "FROM _link" + join);
        
        query.append(" WHERE ");
        if (fromNodeIds.length==1)
        {
            query.append("_link.fromNodeId="+fromNodeIds[0]);
        }
        else
        {
            query.append("_link.fromNodeId IN ("+Utils.combine(fromNodeIds,",")+")");
        }
        if (expression!=null)
        {
            query.append(" AND "+expression);
        }
        if (this.orderBy != null)
        {
            query.append(" ORDER BY ");
            query.append(orderBy);
        }
        System.out.println(query);
        RowSet rowSet;
        if (parameters!=null)
        {
            rowSet = access.getAccessor().executeQuery(parent, null,
                    query.toString(), parameters);
        }
        else
        {
            rowSet = access.getAccessor().executeQuery(parent, null,
                    query.toString());
            
        }

        ToNodeResult[] results = new ToNodeResult[rowSet.size()];
        for (NodeLinkTypes nodeLinkTypes:nodeLinkTypesArray )
        {
            Class<? extends NodeObject> requiredType=nodeLinkTypes.requiredType;
            Class<? extends NodeObject>[] optionalTypes=nodeLinkTypes.optionalObjectTypes;
            
            for (int i = 0; i < rowSet.size();i++)
            {
                Row row = rowSet.getRow(i);
                long linkId = row.getBIGINT("_link.id");
                long fromNodeId = row.getBIGINT("_link.fromNodeId");
                long toNodeId = row.getBIGINT("_link.toNodeId");
    
                GraphObject[] objects=new GraphObject[totalResultTypes];
                int index=0;
                if (requiredType!=null)
                {
                    Meta meta=graph.getMeta(requiredType);
                    String typeName=meta.getTypeName();
                    Long typeNodeId = row.getNullableBIGINT(typeName + "._nodeId");
                    if (typeNodeId != null)
                    {
                        NodeObject nodeObject = (NodeObject) requiredType.newInstance();
                        for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                        {
                            columnAccessor.set(nodeObject, typeName, row);
                        }
                        objects[index++]=nodeObject;
                    }
                }
                for (Class<? extends GraphObject> type:optionalTypes)
                {
                    Meta meta=graph.getMeta(type);
                    String typeName=meta.getTypeName();
                    switch (meta.getObjectType())
                    {
                    case LINK_OBJECT:
                        Long typeLinkId = row.getNullableBIGINT(typeName + "._linkId");
                        if (typeLinkId != null)
                        {
                            LinkObject attribute = (LinkObject) type.newInstance();
                            for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                            {
                                columnAccessor.set(attribute, typeName, row);
                            }
                            objects[index++]=attribute;
                            attribute._linkId=typeLinkId;
                        }
                        break;
                    case NODE_OBJECT:
                        Long typeNodeId = row.getNullableBIGINT(typeName + "._nodeId");
                        if (typeNodeId != null)
                        {
                            NodeObject nodeObject = (NodeObject) type.newInstance();
                            for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                            {
                                columnAccessor.set(nodeObject, typeName, row);
                            }
                            nodeObject._nodeId=typeNodeId;
                            objects[index]=nodeObject;
                        }
                        break;
                    default:
                        throw new Exception();
                    
                    }
                    index++;
                }
                results[i]=new ToNodeResult(linkId, fromNodeId,toNodeId,objects);
            }
        }
        return results;
    }

    static void buildMap(ToNodeResult[] results,Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>[] optionalObjectTypes)
    {
        HashMap<String,Integer> map=new HashMap<String, Integer>();
        int index=0;
        if (requiredObjectType!=null)
        {
            map.put(requiredObjectType.getSimpleName(), index++);
        }
        for (Class<? extends NodeObject> type:optionalObjectTypes)
        {
            map.put(type.getSimpleName(), index++);
        }
        for (int i=0;i<results.length;i++)
        {
            results[i].setMap(map);
        }
    }
    
    
    public ToNodeResult[] combine(ToNodeResult[] fromResults,ToNodeResult[]...results)
    {
        int totalObjects=0;
        if (fromResults.length==0)
        {
            return new ToNodeResult[0];
        }
        totalObjects+=fromResults[0].objects.length;
        for (ToNodeResult[] array:results)
        {
            if (array.length>0)
            {
                totalObjects+=array[0].objects.length;
            }
        }
        HashMap<String,Integer> map=new HashMap<String, Integer>();
        HashMap<Long,Integer> indexMap=new HashMap<Long, Integer>();
        
        GraphObject[][] resultsObjects=new GraphObject[fromResults.length][];
        
        int resultsIndex=0;
        for (int i=0;i<fromResults.length;i++)
        {
            ToNodeResult result=fromResults[i];
            GraphObject[] objects=new GraphObject[totalObjects];
            for (int j=0;j<result.objects.length;j++)
            {
                objects[resultsIndex+j]=result.objects[j];
            }
            indexMap.put(result.toNodeId,i);
            resultsObjects[i]=objects;
        }
        for (Entry<String, Integer> entry:fromResults[0].map.entrySet())
        {
            map.put(entry.getKey(), entry.getValue()+resultsIndex);
        }
        resultsIndex+=fromResults[0].objects.length;
              
        for (ToNodeResult[] array:results)
        {
            if (array.length==0)
            {
                continue;
            }
            for (int i=0;i<array.length;i++)
            {
                ToNodeResult result=array[i];
                Integer index=indexMap.get(result.fromNodeId);
                GraphObject[] objects=resultsObjects[index];
                for (int j=0;j<result.objects.length;j++)
                {
                    objects[resultsIndex+j]=result.objects[j];
                }
            }
            for (Entry<String, Integer> entry:array[0].map.entrySet())
            {
                map.put(entry.getKey(), entry.getValue()+resultsIndex);
            }
            resultsIndex+=array[0].objects.length;
        }
        
        ToNodeResult[] combinedResults=new ToNodeResult[fromResults.length];
        for (int i=0;i<fromResults.length;i++)
        {
            ToNodeResult result=fromResults[i];
            combinedResults[i]=new ToNodeResult(result.linkId,result.fromNodeId,result.toNodeId,resultsObjects[i]);
        }
        return combinedResults;
    }
    //get linked node entities and attributes
    final public ToNodeResult[] getToNodesWithRequiredObjectAndToNodes(ToNodeResult[] fromResults,Class<? extends NodeObject> type,Class<? extends NodeObject>...types) throws Throwable
    {
        if (fromResults.length==0)
        {
            return new ToNodeResult[0];
        }
        long[] nodeIds=new long[fromResults.length];
        for (int i=0;i<nodeIds.length;i++)
        {
            nodeIds[i]=fromResults[i].toNodeId;
        }
        ToNodeResult[] results=_execute(nodeIds,type,types);
        buildMap(results,type,types);
        return results;
    }
    @SafeVarargs
    
    final public ToNodeResult[] getToNodesWithRequiredObjectAndToNodes(long fromNodeId,Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>...optionalObjectTypes) throws Throwable
    {
        ToNodeResult[] firstResults=_execute(new long[] {fromNodeId},requiredObjectType);

        if (firstResults.length==0)
        {
            return firstResults;
        }
        HashMap<String,Integer> map=new HashMap<String, Integer>();
        this.expression=null;
        this.parameters=null;
        this.orderBy=null;
        map.put(requiredObjectType.getSimpleName(),0);
        
        long[] nodeIds=new long[firstResults.length];
        for (int i=0;i<nodeIds.length;i++)
        {
            nodeIds[i]=firstResults[i].toNodeId;
        }

        ToNodeResult[][] toLinkResults=new ToNodeResult[optionalObjectTypes.length][];
        int toLinkResultsLength=0;
        int totalObjects=1; //from firstResults
        for (int i=0;i<optionalObjectTypes.length;i++)
        {
            Class<? extends NodeObject> type=optionalObjectTypes[i];
            ToNodeResult[] results=_execute(nodeIds,type);
            if (results.length==0)
            {
                continue;
            }
            map.put(type.getSimpleName(),totalObjects);
            toLinkResults[toLinkResultsLength++]=results;
            totalObjects++;
        }

        GraphObject[][] resultsObjects=new GraphObject[firstResults.length][];
        HashMap<Long,Integer> indexMap=new HashMap<Long, Integer>();
        
        int objectIndex=0;
        for (int i=0;i<firstResults.length;i++)
        {
            ToNodeResult result=firstResults[i];
            GraphObject[] objects=new GraphObject[totalObjects];
            objects[objectIndex]=result.objects[0];
            resultsObjects[i]=objects;
            indexMap.put(result.toNodeId,i);
        }
        objectIndex++;
        
        for (int j=0;j<toLinkResultsLength;j++)
        {
            ToNodeResult[] results=toLinkResults[j];
            for (int i=0;i<results.length;i++)
            {
                ToNodeResult result=results[i];
                Integer index=indexMap.get(result.fromNodeId);
                GraphObject[] objects=resultsObjects[index];
                objects[objectIndex]=result.objects[0];
            }
            objectIndex++;
        }
        ToNodeResult[] combinedResults=new ToNodeResult[firstResults.length];
        for (int i=0;i<firstResults.length;i++)
        {
            ToNodeResult result=firstResults[i];
            combinedResults[i]=new ToNodeResult(result.linkId,result.fromNodeId,result.toNodeId,resultsObjects[i]);
            combinedResults[i].setMap(map);
        }
        return combinedResults;
    }
    
    

    @SafeVarargs
    final public ToNodeResult[] getToNodesWithRequiredObject(long fromNodeId,Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>...optionalObjectTypes) throws Throwable
    {
        ToNodeResult[] results=_execute(new long[] {fromNodeId},requiredObjectType,optionalObjectTypes);
        buildMap(results,requiredObjectType,optionalObjectTypes);
        return results;
    }

    @SafeVarargs
    final public ToNodeResult[] getToNodes(long fromNodeId,Class<? extends NodeObject>...types) throws Throwable
    {
        ToNodeResult[] results=_execute(new long[] {fromNodeId},null,types);
        buildMap(results,null,types);
        return results;
    }
    
    @SafeVarargs
    final public ToNodeResult getToNodeWithRequiredObject(long fromNodeId,Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>...optionalObjectTypes) throws Throwable
    {
        ToNodeResult[] results=getToNodesWithRequiredObject(fromNodeId,requiredObjectType,optionalObjectTypes);
        if (results.length==0)
        {
            return null;
        }
        if (results.length>1)
        {
            throw new Exception();
        }
        buildMap(results,requiredObjectType,optionalObjectTypes);
        return results[0];
    }
    
    @SafeVarargs
    final public ToNodeResult getToNode(long fromNodeId,Class<? extends NodeObject>...types) throws Throwable
    {
        ToNodeResult[] results=getToNodesWithRequiredObject(fromNodeId,null,types);
        if (results.length==0)
        {
            return null;
        }
        if (results.length>1)
        {
            throw new Exception();
        }
        buildMap(results,null,types);
        return results[0];
    }
    
    public <OBJECT extends NodeObject> OBJECT[] getToNodeObjects(long fromNodeId,Class<? extends NodeObject> type) throws Throwable
    {
        ToNodeResult[] results=_execute(new long[] {fromNodeId},type);
        @SuppressWarnings("unchecked")
        OBJECT[] entities=(OBJECT[]) Array.newInstance(type,results.length);
        for (int i=0;i<entities.length;i++)
        {
            entities[i]=results[i].get(0);
        }
        return entities;
    }

    public <OBJECT extends NodeObject> OBJECT getNodeObject(long fromNodeId,Class<? extends NodeObject> type) throws Throwable
    {
        ToNodeResult[] results=_execute(new long[] {fromNodeId},type);
        if (results.length==0)
        {
            return null;
        }
        if (results.length>1)
        {
            throw new Exception();
        }
        return results[0].get(0);
     }

    @SafeVarargs
    final public ToNodeResult[] getToNodes(long fromNodeId,NodeLinkTypes...nodeLinkTypesArray) throws Throwable
    {
        ToNodeResult[] results=_execute(new long[] {fromNodeId},nodeLinkTypesArray);
        HashMap<String,Integer> map=new HashMap<String, Integer>();
        int index=0;
        for (NodeLinkTypes nodeLinkTypes:nodeLinkTypesArray)
        {
            Class<? extends NodeObject> requiredType=nodeLinkTypes.requiredType;
            Class<? extends NodeObject>[] optionalType=nodeLinkTypes.optionalObjectTypes;
            if (requiredType!=null)
            {
                map.put(requiredType.getSimpleName(), index++);
            }
            for (Class<? extends NodeObject> type:optionalType)
            {
                map.put(type.getSimpleName(), index++);
            }
            for (int i=0;i<results.length;i++)
            {
                results[i].setMap(map);
            }
        }
        return results;
    }


    
}
