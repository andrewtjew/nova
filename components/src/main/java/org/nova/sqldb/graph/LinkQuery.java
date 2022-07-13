package org.nova.sqldb.graph;

import java.lang.reflect.Array;
import java.util.HashMap;

import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.sqldb.graph.Graph.ColumnAccessor;
import org.nova.sqldb.graph.Graph.EntityMeta;
import org.nova.sqldb.graph.Graph.EntityType;
import org.nova.tracing.Trace;

public class LinkQuery
{
//    final private ;
    final private long fromNodeId;
    final private GraphAccess access;
    private String where;
    final private Object[] parameters;
    private String orderBy;

    @SafeVarargs
    public LinkQuery(GraphAccess access,long fromNodeId,String where,Object...parameters)
    {
        this.fromNodeId=fromNodeId;
        this.access=access;
        this.parameters=parameters;
        this.where=where;
    }
    
    public LinkQuery(GraphAccess access,long nodeId)
    {
        this(access,nodeId,null);
    }
    public LinkQuery orderBy(String orderBy)
    {
        this.orderBy=orderBy;
        return this;
    }
    
    @SafeVarargs
    final LinkResult[] _execute(Class<? extends NodeObject>...types) throws Throwable
    {
        Trace parent=this.access.parent;
        StringBuilder select = new StringBuilder();
        StringBuilder join = new StringBuilder();

        Graph graph = access.graph;

        boolean first=true;
        
        for (Class<? extends NodeObject> type : types)
        {
            EntityMeta meta=graph.getEntityMeta(type);
            String typeName = meta.getTypeName();
            String table = meta.getTableName();
            String alias= meta.getTableAlias();
            switch (meta.getEntityType())
            {
            case LINK:
                if (first)
                {
                    throw new Exception("First type must be a node entity type");
                }
                join.append(" LEFT JOIN " + table + "AS "+alias+" ON s_link.id=" + alias+ "._linkId");
                break;
            case ATTRIBUTE:
                if (first)
                {
                    join.append(" JOIN " + table + "AS "+alias+" ON s_link.toNodeId=" + alias+ "._nodeId");
                }
                else
                {
                    join.append(" LEFT JOIN " + table + "AS "+alias+" ON s_link.toNodeId=" + alias+ "._nodeId");
                }
                break;
            }
            first=false;
            for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
            {
                String fieldColumnName = columnAccessor.getColumnName(typeName);
                String tableColumnName = columnAccessor.getColumnName(alias);
                select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
            }
        }
        StringBuilder query = new StringBuilder("SELECT s_link.id AS '_link.id',s_link.fromNodeId AS '_link.fromNodeId',s_link.toNodeId AS '_link.toNodeId'" + select + "FROM s_link" + join);
        if (where==null)
        {
            where="s_link.fromNodeId="+this.fromNodeId;
        }
        else
        {
            where="s_link.fromNodeId="+this.fromNodeId+" AND "+where;
        }
        query.append(" WHERE ");
        query.append(where);
        if (this.orderBy != null)
        {
            query.append(" ORDER BY ");
            query.append(orderBy);
        }
        System.out.println(query);
        RowSet rowSet = access.getAccessor().executeQuery(parent, null,
                query.toString(), parameters);

        LinkResult[] results = new LinkResult[rowSet.size()];
        for (int i = 0; i < rowSet.size();i++)
        {
            Row row = rowSet.getRow(i);
            long linkId = row.getBIGINT("_link.id");
            long fromNodeId = row.getBIGINT("_link.fromNodeId");
            long toNodeId = row.getBIGINT("_link.toNodeId");

            NodeObject[] entities=new NodeObject[types.length];
            for (int j=0;j<types.length;j++)
            {
                Class<? extends NodeObject> type=types[j];
                EntityMeta meta=graph.getEntityMeta(type);
                String typeName=meta.getTypeName();
                switch (meta.getEntityType())
                {
                case LINK:
                    Long typeLinkId = row.getNullableBIGINT(typeName + "._linkId");
                    if (typeLinkId != null)
                    {
                        NodeObject entity = (NodeObject) type.newInstance();
                        for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                        {
                            columnAccessor.set(entity, typeName, row);
                        }
                        entities[j]=entity;
                    }
                    break;
                case ATTRIBUTE:
                    Long typeNodeId = row.getNullableBIGINT(typeName + "._nodeId");
                    if (typeNodeId != null)
                    {
                        NodeObject entity = (NodeObject) type.newInstance();
                        for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                        {
                            columnAccessor.set(entity, typeName, row);
                        }
                        entities[j]=entity;
                    }
                    break;
                
                }
            }
            results[i]=new LinkResult(linkId, fromNodeId,toNodeId,entities);
        }
        return results;
    }

    public LinkResult[] getLinkResults(Class<? extends NodeObject>...types) throws Throwable
    {
        LinkResult[] results=_execute(types);
        HashMap<String,Integer> map=new HashMap<String, Integer>();
        for (int i=0;i<types.length;i++)
        {
            map.put(types[i].getSimpleName(), i);
        }
        for (int i=0;i<results.length;i++)
        {
            results[i].setMap(map);
        }
        return results;
    }
    @SafeVarargs
    final public LinkResult getLinkResult(Class<? extends NodeObject>...types) throws Throwable
    {
        LinkResult[] results=getLinkResults(types);
        if (results.length==0)
        {
            return null;
        }
        if (results.length>1)
        {
            throw new Exception();
        }
        return results[0];
    }
    
    public <ENTITY extends NodeObject> ENTITY getEntity(Class<? extends NodeObject> type) throws Throwable
    {
        ENTITY[] entities=getEntities(type);
        if (entities.length==0)
        {
            return null;
        }
        if (entities.length>1)
        {
            throw new Exception();
        }
        return entities[0];
    }

    public <ENTITY extends NodeObject> ENTITY[] getEntities(Class<? extends NodeObject> type) throws Throwable
    {
        LinkResult[] results=_execute(type);
        @SuppressWarnings("unchecked")
        ENTITY[] entities=(ENTITY[]) Array.newInstance(type,results.length);
        for (int i=0;i<entities.length;i++)
        {
            entities[i]=results[i].get(0);
        }
        return entities;
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
