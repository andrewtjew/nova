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
    final private Graph graph;
    final private Trace parent;
    private GraphAccess access;
    private String expression;
    private Object[] parameters;
    private String orderBy;

    public NodeQuery(GraphAccess access)
    {
        this.access=access;
        this.graph=null;
        this.parent=null;
    }
    public NodeQuery(Trace parent,Graph graph)
    {
        this.access=null;
        this.graph=graph;
        this.parent=parent;
    }

    public NodeQuery orderBy(String orderBy)
    {
        this.orderBy=orderBy;
        return this;
    }
    public NodeQuery where(String expression,Object...parameters)
    {
        this.expression=expression;
        this.parameters=parameters;
        return this;
    }
    
    NodeResult[] _execute(Long nodeId,Class<? extends NodeEntity>...types) throws Throwable
    {
        if (this.access==null)
        {
            try (GraphAccess access=this.graph.beginAccess(this.parent,"Graph.NodeQuery",null, false))
            {
                this.access=access;
                return __execute(nodeId,types);
            }
            finally
            {
                this.access=null;
            }
        }
        else
        {
            return __execute(nodeId,types);
        }
    }
    

    NodeResult[] __execute(Long nodeId,Class<? extends NodeEntity>[] types) throws Throwable
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
                String fieldColumnName = columnAccessor.getColumnName(typeName);
                String tableColumnName = columnAccessor.getColumnName(alias);
                select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
            }
        }
        StringBuilder query = new StringBuilder("SELECT s_node.id AS '_node.id'" + select + "FROM s_node" + join);
        if (nodeId!=null)
        {
            if (expression==null)
            {
                expression="s_node.id="+nodeId;
            }
            else
            {
                expression="s_node.id="+nodeId+" AND "+expression;
            }
        }
        if (expression != null)
        {
            query.append(" WHERE ");
            query.append(expression);
        }
        if (this.orderBy != null)
        {
            query.append(" ORDER BY ");
            query.append(orderBy);
        }
        System.out.println(query);
        RowSet rowSet;
        if (this.parameters!=null)
        {
            rowSet = access.getAccessor().executeQuery(parent, null,
                    query.toString(), parameters);
        }
        else
        {
            rowSet = access.getAccessor().executeQuery(parent, null,
                query.toString());
        }
        NodeResult[] results = new NodeResult[rowSet.size()];
        for (int i = 0; i < rowSet.size();i++)
        {
            Row row = rowSet.getRow(i);
            nodeId = row.getBIGINT("_node.id");

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
        NodeResult[] results=_execute(null,types);
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
    final public NodeResult getNodeResult(long nodeId,Class<? extends NodeEntity>...types) throws Throwable
    {
        NodeResult[] results=_execute(nodeId,types);
        if (results.length==0)
        {
            return null;
        }
        if (results.length>1)
        {
            throw new Exception();
        }
        HashMap<String,Integer> map=new HashMap<String, Integer>();
        for (int i=0;i<types.length;i++)
        {
            map.put(types[i].getSimpleName(), i);
        }
        results[0].setMap(map);
        return results[0];
    }
    
    public <ENTITY extends NodeEntity> ENTITY getEntity(long nodeId,Class<? extends NodeEntity> type) throws Throwable
    {
        NodeResult[] results=_execute(nodeId,type);
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

    public <ENTITY extends NodeEntity> ENTITY[] getEntities(Class<? extends NodeEntity> type) throws Throwable
    {
        NodeResult[] results=_execute(null,type);
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
