package org.nova.sqldb.graph;

import java.lang.reflect.Array;
import java.util.HashMap;

import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.sqldb.graph.Graph.ColumnAccessor;
import org.nova.sqldb.graph.Graph.Meta;
import org.nova.sqldb.graph.Graph.EntityType;
import org.nova.tracing.Trace;

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
        return this;
    }    
    
    @SafeVarargs
    final LinkResult[] _execute(long fromNodeId,Class<? extends NodeEntity> entityType,Class<? extends NodeObject>...types) throws Throwable
    {
        NodeLinkTypes nodeLinkTypes=new NodeLinkTypes(entityType,types);
        if (this.access==null)
        {
            try (GraphAccess access=this.graph.beginAccess(this.parent,"Graph.LinkQuery",null, false))
            {
                this.access=access;
                return __execute(fromNodeId,new NodeLinkTypes[] {nodeLinkTypes});
            }
            finally
            {
                this.access=null;
            }
        }
        else
        {
            return __execute(fromNodeId,new NodeLinkTypes[] {nodeLinkTypes});
        }
    }   
    @SafeVarargs
    final LinkResult[] _execute(long fromNodeId,NodeLinkTypes...nodeLinkTypesArray) throws Throwable
    {
        if (this.access==null)
        {
            try (GraphAccess access=this.graph.beginAccess(this.parent,"Graph.LinkQuery",null, false))
            {
                this.access=access;
                return __execute(fromNodeId,nodeLinkTypesArray);
            }
            finally
            {
                this.access=null;
            }
        }
        else
        {
            return __execute(fromNodeId,nodeLinkTypesArray);
        }
    }   
    final LinkResult[] __execute(long fromNodeId,NodeLinkTypes[] nodeLinkTypesArray) throws Throwable
    {
        Trace parent=this.access.parent;
        StringBuilder select = new StringBuilder();
        StringBuilder join = new StringBuilder();

        Graph graph = access.graph;
        int totalResultTypes=0;
        for (NodeLinkTypes nodeLinkTypes:nodeLinkTypesArray )
        {
            Class<? extends NodeEntity> entityType=nodeLinkTypes.nodeEntityType;
            Class<? extends NodeObject>[] types=nodeLinkTypes.attributeTypes;
            
            if (entityType!=null)
            {
                totalResultTypes++;
                Meta meta=graph.getMeta(entityType);
                String typeName = meta.getTypeName();
                String table = meta.getTableName();
                String alias= meta.getTableAlias();
                join.append(" JOIN " + table + "AS "+alias+" ON s_link.toNodeId=" + alias+ "._nodeId");
                for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                {
                    String fieldColumnName = columnAccessor.getColumnName(typeName);
                    String tableColumnName = columnAccessor.getColumnName(alias);
                    select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
                }
            }
            totalResultTypes+=types.length;
            for (Class<? extends GraphObject> type : types)
            {
                Meta meta=graph.getMeta(type);
                String typeName = meta.getTypeName();
                String table = meta.getTableName();
                String alias= meta.getTableAlias();
                switch (meta.getEntityType())
                {
                case LINK_ATTRIBUTE:
                    join.append(" LEFT JOIN " + table + "AS "+alias+" ON s_link.id=" + alias+ "._linkId");
                    break;
                case NODE_ATTRIBUTE:
                    join.append(" LEFT JOIN " + table + "AS "+alias+" ON s_link.toNodeId=" + alias+ "._nodeId");
                    break;
                case NODE:
                    throw new Exception();
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
        StringBuilder query = new StringBuilder("SELECT s_link.id AS '_link.id',s_link.fromNodeId AS '_link.fromNodeId',s_link.toNodeId AS '_link.toNodeId'" + select + "FROM s_link" + join);
        if (expression==null)
        {
            expression="s_link.fromNodeId="+fromNodeId;
        }
        else
        {
            expression="s_link.fromNodeId="+fromNodeId+" AND "+expression;
        }
        query.append(" WHERE ");
        query.append(expression);
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

        LinkResult[] results = new LinkResult[rowSet.size()];
        for (NodeLinkTypes nodeLinkTypes:nodeLinkTypesArray )
        {
            Class<? extends NodeEntity> entityType=nodeLinkTypes.nodeEntityType;
            Class<? extends NodeObject>[] types=nodeLinkTypes.attributeTypes;
            
            for (int i = 0; i < rowSet.size();i++)
            {
                Row row = rowSet.getRow(i);
                long linkId = row.getBIGINT("_link.id");
    //            long fromNodeId = row.getBIGINT("_link.fromNodeId");
                long toNodeId = row.getBIGINT("_link.toNodeId");
    
                GraphObject[] objects=new GraphObject[totalResultTypes];
                int index=0;
                if (entityType!=null)
                {
                    Meta meta=graph.getMeta(entityType);
                    String typeName=meta.getTypeName();
                    Long typeNodeId = row.getNullableBIGINT(typeName + "._nodeId");
                    if (typeNodeId != null)
                    {
                        NodeEntity entity = (NodeEntity) entityType.newInstance();
                        for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                        {
                            columnAccessor.set(entity, typeName, row);
                        }
                        objects[index++]=entity;
                    }
                }
                for (Class<? extends GraphObject> type:types)
                {
                    Meta meta=graph.getMeta(type);
                    String typeName=meta.getTypeName();
                    switch (meta.getEntityType())
                    {
                    case LINK_ATTRIBUTE:
                        Long typeLinkId = row.getNullableBIGINT(typeName + "._linkId");
                        if (typeLinkId != null)
                        {
                            LinkAttribute attribute = (LinkAttribute) type.newInstance();
                            for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                            {
                                columnAccessor.set(attribute, typeName, row);
                            }
                            objects[index++]=attribute;
                            attribute._linkId=typeLinkId;
                        }
                        break;
                    case NODE_ATTRIBUTE:
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
                    case NODE:
                        throw new Exception();
                    default:
                        throw new Exception();
                    
                    }
                    index++;
                }
                results[i]=new LinkResult(linkId, fromNodeId,toNodeId,objects);
            }
        }
        return results;
    }

    static void buildMap(LinkResult[] results,Class<? extends NodeEntity> entityType,Class<? extends NodeObject>[] types)
    {
        HashMap<String,Integer> map=new HashMap<String, Integer>();
        int index=0;
        if (entityType!=null)
        {
            map.put(entityType.getSimpleName(), index++);
        }
        for (Class<? extends NodeObject> type:types)
        {
            map.put(type.getSimpleName(), index++);
        }
        for (int i=0;i<results.length;i++)
        {
            results[i].setMap(map);
        }
    }
    

    @SafeVarargs
    final public LinkResult[] getLinkResults(long fromNodeId,Class<? extends NodeEntity> entityType,Class<? extends NodeObject>...types) throws Throwable
    {
        LinkResult[] results=_execute(fromNodeId,entityType,types);
        buildMap(results,entityType,types);
        return results;
    }

    @SafeVarargs
    final public LinkResult[] getLinkResults(long fromNodeId,Class<? extends NodeObject>...types) throws Throwable
    {
        return getLinkResults(fromNodeId,null,types);
    }
    
    @SafeVarargs
    final public LinkResult getLinkResult(long fromNodeId,Class<? extends NodeEntity> entityType,Class<? extends NodeObject>...types) throws Throwable
    {
        LinkResult[] results=getLinkResults(fromNodeId,entityType,types);
        if (results.length==0)
        {
            return null;
        }
        if (results.length>1)
        {
            throw new Exception();
        }
        buildMap(results,entityType,types);
        return results[0];
    }
    
    @SafeVarargs
    final public LinkResult getLinkResult(long fromNodeId,Class<? extends NodeObject>...types) throws Throwable
    {
        LinkResult[] results=getLinkResults(fromNodeId,null,types);
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
    
    public <ENTITY extends NodeObject> ENTITY[] getEntities(long fromNodeId,Class<? extends NodeEntity> entityType) throws Throwable
    {
        LinkResult[] results=_execute(fromNodeId,entityType);
        @SuppressWarnings("unchecked")
        ENTITY[] entities=(ENTITY[]) Array.newInstance(entityType,results.length);
        for (int i=0;i<entities.length;i++)
        {
            entities[i]=results[i].get(0);
        }
        return entities;
    }

    public <ENTITY extends NodeObject> ENTITY getEntity(long fromNodeId,Class<? extends NodeEntity> entityType) throws Throwable
    {
        LinkResult[] results=_execute(fromNodeId,entityType);
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
    final public LinkResult[] getLinkResults(long fromNodeId,NodeLinkTypes...nodeLinkTypesArray) throws Throwable
    {
        LinkResult[] results=_execute(fromNodeId,nodeLinkTypesArray);
        HashMap<String,Integer> map=new HashMap<String, Integer>();
        int index=0;
        for (NodeLinkTypes nodeLinkTypes:nodeLinkTypesArray)
        {
            Class<? extends NodeEntity> entityType=nodeLinkTypes.nodeEntityType;
            Class<? extends NodeObject>[] types=nodeLinkTypes.attributeTypes;
            if (entityType!=null)
            {
                map.put(entityType.getSimpleName(), index++);
            }
            for (Class<? extends NodeObject> type:types)
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


//    public <ENTITY extends Entity> EntityMap<ENTITY> getEntityMap(Class<ENTITY> type) throws Throwable
//    {
//        
//        ENTITY[] entities=getEntities(type);
//        EntityMap<ENTITY> map=new EntityMap<ENTITY>();
//        EntityMeta meta=this.access.graph.getEntityMeta(type);
//        switch (meta.getEntityType())
//        {
//        case LINK:
//            for (ENTITY entity:entities)
//            {
//                map.put(((LinkEntity)entity).getLinkId(), entity);
//            }
//            break;
//        case NODE:
//            for (ENTITY entity:entities)
//            {
//                map.put(((NodeEntity)entity).getNodeId(), entity);
//            }
//            break;
//        default:
//            throw new Exception();
//        
//        }
//        return map;
//    }
    
}
