package org.nova.sqldb.graph;

import java.lang.reflect.Array;
import java.util.HashMap;

import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.sqldb.graph.Graph.ColumnAccessor;
import org.nova.sqldb.graph.Graph.EntityMeta;
import org.nova.tracing.Trace;

public class NodeQuery
{
//    final private ;
    final private Long nodeId;
    final private GraphAccess access;
    private String where;
    final private Object[] parameters;
    private String orderBy;

    @SafeVarargs
    public NodeQuery(GraphAccess access,long nodeId,String where,Object...parameters)
    {
        this.nodeId=nodeId;
        this.access=access;
        this.parameters=parameters;
        this.where=where;
    }
    
    public NodeQuery(GraphAccess access,long nodeId)
    {
        this(access,nodeId,null);
    }
    public NodeQuery(GraphAccess access,String where,Object...parameters)
    {
        this.nodeId=null;
        this.access=access;
        this.parameters=parameters;
        this.where=where;
    }
    public NodeQuery orderBy(String orderBy)
    {
        this.orderBy=orderBy;
        return this;
    }
    
    @SafeVarargs
    final NodeResult[] _execute(Class<? extends NodeEntity>...types) throws Throwable
    {
        Trace parent=this.access.parent;
        StringBuilder select = new StringBuilder();
        StringBuilder join = new StringBuilder();

        Graph graph = access.graph;

        for (Class<? extends NodeEntity> type : types)
        {
            EntityMeta meta=graph.getEntityMeta(type);
            String typeName = meta.getTypeName();
            String table = meta.getTableName();
            String alias= meta.getTableAlias();
            join.append(" LEFT JOIN " + table + "AS "+alias+" ON s_node.id=" + alias+ "._nodeId");
            for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
            {
//                if (columnAccessor.isGraphfield())
//                {
//                    continue;
//                }
                String fieldColumnName = columnAccessor.getColumnName(typeName);
                String tableColumnName = columnAccessor.getColumnName(alias);
                select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
            }
        }
        StringBuilder query = new StringBuilder("SELECT s_node.id AS '_node.id'" + select + "FROM s_node" + join);
        if (this.nodeId!=null)
        {
            if (where==null)
            {
                where="s_node.id="+this.nodeId;
            }
            else
            {
                where="s_node.id="+this.nodeId+" AND "+where;
            }
        }
        if (where != null)
        {
            query.append(" WHERE ");
            query.append(where);
        }
        if (this.orderBy != null)
        {
            query.append(" ORDER BY ");
            query.append(orderBy);
        }
        System.out.println(query);
        RowSet rowSet = access.getAccessor().executeQuery(parent, null,
                query.toString(), parameters);

        NodeResult[] results = new NodeResult[rowSet.size()];
        for (int i = 0; i < rowSet.size();i++)
        {
            Row row = rowSet.getRow(i);
            long nodeId = row.getBIGINT("_node.id");

            NodeEntity[] entities=new NodeEntity[types.length];
            for (int j=0;j<types.length;j++)
            {
                Class<? extends NodeEntity> type=types[j];
                EntityMeta meta=graph.getEntityMeta(type);
                String typeName=meta.getTypeName();
                Long typeNodeId = row.getNullableBIGINT(typeName + "._nodeId");
                if (typeNodeId != null)
                {
                    NodeEntity entity = (NodeEntity) type.newInstance();
                    for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                    {
                        columnAccessor.set(entity, typeName, row);
                    }
                    entities[j]=entity;
                }
            }
            results[i]=new NodeResult(nodeId, entities);
        }
        return results;
    }

    public NodeResult[] getNodeResults(Class<? extends NodeEntity>...types) throws Throwable
    {
        NodeResult[] results=_execute(types);
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
    final public NodeResult getNodeResult(Class<? extends NodeEntity>...types) throws Throwable
    {
        NodeResult[] results=getNodeResults(types);
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
    
    public <ENTITY extends NodeEntity> ENTITY getEntity(Class<? extends NodeEntity> type) throws Throwable
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

    public <ENTITY extends NodeEntity> ENTITY[] getEntities(Class<? extends NodeEntity> type) throws Throwable
    {
        NodeResult[] results=_execute(type);
        @SuppressWarnings("unchecked")
        ENTITY[] entities=(ENTITY[]) Array.newInstance(type,results.length);
        for (int i=0;i<entities.length;i++)
        {
            entities[i]=results[i].get(0);
        }
        return entities;
    }
//    public <ENTITY extends NodeEntity> EntityMap<ENTITY> getEntityMap(Class<ENTITY> type) throws Throwable
//    {
//        ENTITY[] entities=getEntities(type);
//        EntityMap<ENTITY> map=new EntityMap<ENTITY>();
//        for (ENTITY entity:entities)
//        {
//            map.put(entity.getNodeId(), entity);
//        }
//        return map;
//    }
}
