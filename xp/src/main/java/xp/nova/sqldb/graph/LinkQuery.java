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
    
//    @SafeVarargs
//    final LinkNodeResult[] _execute(long[] fromNodeIds,long[] toNodeIds,Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>...optionalObjectTypes) throws Throwable
//    {
//        NodeLinkTypes nodeLinkTypes=new NodeLinkTypes(requiredObjectType,optionalObjectTypes);
//        if (this.access==null)
//        {
//            try (GraphAccess access=this.graph.openAccess(this.parent,"Graph.LinkQuery",null, false))
//            {
//                this.access=access;
//                return __execute(fromNodeIds,toNodeIds,new NodeLinkTypes[] {nodeLinkTypes});
//            }
//            finally
//            {
//                this.access=null;
//            }
//        }
//        else
//        {
//            return __execute(fromNodeIds,toNodeIds,new NodeLinkTypes[] {nodeLinkTypes});
//        }
//    }   

    final LinkNodeResult[] _execute(long[] fromNodeIds,long[] toNodeIds,Class<? extends NodeObject>[] requiredObjectTypes,Class<? extends NodeObject>[] optionalObjectTypes) throws Throwable
    {
        if (this.access==null)
        {
            try (GraphAccess access=this.graph.openAccess(this.parent,"Graph.LinkQuery",null, false))
            {
                this.access=access;
                return __execute(fromNodeIds,toNodeIds,requiredObjectTypes,optionalObjectTypes);
            }
            finally
            {
                this.access=null;
            }
        }
        else
        {
            return __execute(fromNodeIds,toNodeIds,requiredObjectTypes,optionalObjectTypes);
        }
    }   
    final LinkNodeResult[] __execute(long[] fromNodeIds,long[] toNodeIds,Class<? extends NodeObject>[] requiredObjectTypes,Class<? extends NodeObject>[] optionalObjectTypes) throws Throwable
    {
        RowSet rowSet=access.getLinkNodes(fromNodeIds, toNodeIds, requiredObjectTypes, optionalObjectTypes, orderBy, expression, parameters);

        LinkNodeResult[] results = new LinkNodeResult[rowSet.size()];
        Graph graph=this.access.graph;

        for (int i = 0; i < rowSet.size();i++)
        {
            Row row = rowSet.getRow(i);
            long linkId = row.getBIGINT("_link.id");
            long fromNodeId = row.getBIGINT("_link.fromNodeId");
            long toNodeId = row.getBIGINT("_link.toNodeId");

            GraphObject[] objects=new GraphObject[requiredObjectTypes.length+optionalObjectTypes.length];
            int index=0;
            for (Class<? extends GraphObject> type:requiredObjectTypes)
            {
                Meta meta=graph.getMeta(type);
                String typeName=meta.getTypeName();
                Long typeNodeId = row.getNullableBIGINT(typeName + "._nodeId");
                if (typeNodeId != null)
                {
                    NodeObject nodeObject = (NodeObject) type.newInstance();
                    for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                    {
                        columnAccessor.set(nodeObject, typeName, row);
                    }
                    objects[index++]=nodeObject;
                }
            }
            for (Class<? extends GraphObject> type:optionalObjectTypes)
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
            results[i]=new LinkNodeResult(linkId, fromNodeId,toNodeId,objects);
        }
        return results;
    }

    static void buildMap(LinkNodeResult[] results,Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>[] optionalObjectTypes)
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
    
    
    public LinkNodeResult[] combine(LinkNodeResult[] fromResults,LinkNodeResult[]...results)
    {
        int totalObjects=0;
        if (fromResults.length==0)
        {
            return new LinkNodeResult[0];
        }
        totalObjects+=fromResults[0].objects.length;
        for (LinkNodeResult[] array:results)
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
            LinkNodeResult result=fromResults[i];
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
              
        for (LinkNodeResult[] array:results)
        {
            if (array.length==0)
            {
                continue;
            }
            for (int i=0;i<array.length;i++)
            {
                LinkNodeResult result=array[i];
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
        
        LinkNodeResult[] combinedResults=new LinkNodeResult[fromResults.length];
        for (int i=0;i<fromResults.length;i++)
        {
            LinkNodeResult result=fromResults[i];
            combinedResults[i]=new LinkNodeResult(result.linkId,result.fromNodeId,result.toNodeId,resultsObjects[i]);
        }
        return combinedResults;
    }
    //get linked node entities and attributes
//    final public LinkNodeResult[] getToNodesWithRequiredObjectAndToNodes(LinkNodeResult[] fromResults,Class<? extends NodeObject> type,Class<? extends NodeObject>...types) throws Throwable
//    {
//        if (fromResults.length==0)
//        {
//            return new LinkNodeResult[0];
//        }
//        long[] nodeIds=new long[fromResults.length];
//        for (int i=0;i<nodeIds.length;i++)
//        {
//            nodeIds[i]=fromResults[i].toNodeId;
//        }
//        LinkNodeResult[] results=_execute(nodeIds,null,type,types);
//        buildMap(results,type,types);
//        return results;
//    }
    
//    final public ToNodeResult[] getFromNodesWithRequiredObjectAndToNodes(ToNodeResult[] fromResults,Class<? extends NodeObject> type,Class<? extends NodeObject>...types) throws Throwable
//    {
//        if (fromResults.length==0)
//        {
//            return new ToNodeResult[0];
//        }
//        long[] nodeIds=new long[fromResults.length];
//        for (int i=0;i<nodeIds.length;i++)
//        {
//            nodeIds[i]=fromResults[i].fromNodeId;
//        }
//        ToNodeResult[] results=_execute(null,nodeIds,type,types);
//        buildMap(results,type,types);
//        return results;
//    }
    
    
    @SuppressWarnings("rawtypes")
    final static Class[] EMPTY=new Class[] {};
    
    @SafeVarargs   
    final public LinkNodeResult[] getToNodesWithRequiredObjectAndToNodes(long fromNodeId,Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>...optionalObjectTypes) throws Throwable
    {
        @SuppressWarnings("unchecked")
        LinkNodeResult[] firstResults=_execute(new long[] {fromNodeId},null,new Class[] {requiredObjectType},EMPTY);

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

        LinkNodeResult[][] toLinkResults=new LinkNodeResult[optionalObjectTypes.length][];
        int toLinkResultsLength=0;
        int totalObjects=1; //from firstResults
        for (int i=0;i<optionalObjectTypes.length;i++)
        {
            Class<? extends NodeObject> type=optionalObjectTypes[i];
            @SuppressWarnings("unchecked")
            LinkNodeResult[] results=_execute(nodeIds,null,EMPTY,new Class[] {type});
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
            LinkNodeResult result=firstResults[i];
            GraphObject[] objects=new GraphObject[totalObjects];
            objects[objectIndex]=result.objects[0];
            resultsObjects[i]=objects;
            indexMap.put(result.toNodeId,i);
        }
        objectIndex++;
        
        for (int j=0;j<toLinkResultsLength;j++)
        {
            LinkNodeResult[] results=toLinkResults[j];
            for (int i=0;i<results.length;i++)
            {
                LinkNodeResult result=results[i];
                Integer index=indexMap.get(result.fromNodeId);
                GraphObject[] objects=resultsObjects[index];
                objects[objectIndex]=result.objects[0];
            }
            objectIndex++;
        }
        LinkNodeResult[] combinedResults=new LinkNodeResult[firstResults.length];
        for (int i=0;i<firstResults.length;i++)
        {
            LinkNodeResult result=firstResults[i];
            combinedResults[i]=new LinkNodeResult(result.linkId,result.fromNodeId,result.toNodeId,resultsObjects[i]);
            combinedResults[i].setMap(map);
        }
        return combinedResults;
    }
    
    

    @SafeVarargs
    final public LinkNodeResult[] getToNodesWithRequiredObject(long fromNodeId,Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>...optionalObjectTypes) throws Throwable
    {
        @SuppressWarnings("unchecked")
        LinkNodeResult[] results=_execute(new long[] {fromNodeId},null,new Class[] {requiredObjectType},optionalObjectTypes);
        buildMap(results,requiredObjectType,optionalObjectTypes);
        return results;
    }

    @SafeVarargs
    final public LinkNodeResult[] getFromNodesWithRequiredObject(long toNodeId,Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>...optionalObjectTypes) throws Throwable
    {
        @SuppressWarnings("unchecked")
        LinkNodeResult[] results=_execute(null,new long[] {toNodeId},new Class[] {requiredObjectType},optionalObjectTypes);
        buildMap(results,requiredObjectType,optionalObjectTypes);
        return results;
    }

    @SafeVarargs
    final public LinkNodeResult[] getToNodes(long fromNodeId,Class<? extends NodeObject>...types) throws Throwable
    {
        LinkNodeResult[] results=_execute(new long[] {fromNodeId},null,EMPTY,types);
        buildMap(results,null,types);
        return results;
    }

    @SafeVarargs
    final public LinkNodeResult[] getFromNodes(long toNodeId,Class<? extends NodeObject>...types) throws Throwable
    {
        LinkNodeResult[] results=_execute(null,new long[] {toNodeId},EMPTY,types);
        buildMap(results,null,types);
        return results;
    }
    
    @SafeVarargs
    final public LinkNodeResult getToNodeWithRequiredObject(long fromNodeId,Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>...optionalObjectTypes) throws Throwable
    {
        LinkNodeResult[] results=getToNodesWithRequiredObject(fromNodeId,requiredObjectType,optionalObjectTypes);
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
    final public LinkNodeResult getFromNodeWithRequiredObject(long toNodeId,Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>...optionalObjectTypes) throws Throwable
    {
        LinkNodeResult[] results=getFromNodesWithRequiredObject(toNodeId,requiredObjectType,optionalObjectTypes);
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
    
//    @SafeVarargs
//    final public LinkNodeResult getToNode(long fromNodeId,Class<? extends NodeObject>...types) throws Throwable
//    {
//        LinkNodeResult[] results=_execute(new long[] {fromNodeId},null,EMPTY,types);
//        if (results.length==0)
//        {
//            return null;
//        }
//        if (results.length>1)
//        {
//            throw new Exception();
//        }
//        buildMap(results,null,types);
//        return results[0];
//    }
    @SafeVarargs
    final public LinkNodeResult getFromNode(long toNodeId,Class<? extends NodeObject>...types) throws Throwable
    {
        LinkNodeResult[] results=getFromNodesWithRequiredObject(toNodeId,null,types);
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
        @SuppressWarnings("unchecked")
        LinkNodeResult[] results=_execute(new long[] {fromNodeId},null,new Class[] {type},EMPTY);
        @SuppressWarnings("unchecked")
        OBJECT[] entities=(OBJECT[]) Array.newInstance(type,results.length);
        for (int i=0;i<entities.length;i++)
        {
            entities[i]=results[i].get(0);
        }
        return entities;
    }
    public <OBJECT extends NodeObject> OBJECT[] getFromNodeObjects(long toNodeId,Class<? extends NodeObject> type) throws Throwable
    {
        LinkNodeResult[] results=_execute(null,new long[] {toNodeId},new Class[] {type},EMPTY);
        @SuppressWarnings("unchecked")
        OBJECT[] entities=(OBJECT[]) Array.newInstance(type,results.length);
        for (int i=0;i<entities.length;i++)
        {
            entities[i]=results[i].get(0);
        }
        return entities;
    }

    public <OBJECT extends NodeObject> OBJECT getToNodeObject(long fromNodeId,Class<? extends NodeObject> type) throws Throwable
    {
        LinkNodeResult[] results=_execute(new long[] {fromNodeId},null,new Class[] {type},EMPTY);
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
    public <OBJECT extends NodeObject> OBJECT getFromNodeObject(long toNodeId,Class<? extends NodeObject> type) throws Throwable
    {
        LinkNodeResult[] results=_execute(null,new long[] {toNodeId},new Class[] {type},EMPTY);
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

    //@SafeVarargs
//    final public LinkNodeResult[] getToNodes(long fromNodeId,NodeLinkTypes...nodeLinkTypesArray) throws Throwable
//    {
//        LinkNodeResult[] results=_execute(new long[] {fromNodeId},null,nodeLinkTypesArray);
//        HashMap<String,Integer> map=new HashMap<String, Integer>();
//        int index=0;
//        for (NodeLinkTypes nodeLinkTypes:nodeLinkTypesArray)
//        {
//            Class<? extends NodeObject> requiredType=nodeLinkTypes.requiredType;
//            Class<? extends NodeObject>[] optionalType=nodeLinkTypes.optionalObjectTypes;
//            if (requiredType!=null)
//            {
//                map.put(requiredType.getSimpleName(), index++);
//            }
//            for (Class<? extends NodeObject> type:optionalType)
//            {
//                map.put(type.getSimpleName(), index++);
//            }
//            for (int i=0;i<results.length;i++)
//            {
//                results[i].setMap(map);
//            }
//        }
//        return results;
//    }
//
//    @SafeVarargs
//    final public LinkNodeResult[] getFromNodes(long toNodeId,NodeLinkTypes...nodeLinkTypesArray) throws Throwable
//    {
//        LinkNodeResult[] results=_execute(null,new long[] {toNodeId},nodeLinkTypesArray);
//        HashMap<String,Integer> map=new HashMap<String, Integer>();
//        int index=0;
//        for (NodeLinkTypes nodeLinkTypes:nodeLinkTypesArray)
//        {
//            Class<? extends NodeObject> requiredType=nodeLinkTypes.requiredType;
//            Class<? extends NodeObject>[] optionalType=nodeLinkTypes.optionalObjectTypes;
//            if (requiredType!=null)
//            {
//                map.put(requiredType.getSimpleName(), index++);
//            }
//            for (Class<? extends NodeObject> type:optionalType)
//            {
//                map.put(type.getSimpleName(), index++);
//            }
//            for (int i=0;i<results.length;i++)
//            {
//                results[i].setMap(map);
//            }
//        }
//        return results;
//    }

    
}
