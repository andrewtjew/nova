package org.nova.sqldb.graph;

import java.lang.reflect.Array;
import java.util.HashMap;

import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.sqldb.graph.Graph.ColumnAccessor;
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
    final NodeResult[] _execute(Class<? extends Entity>...types) throws Throwable
    {
        Trace parent=this.access.parent;
        StringBuilder select = new StringBuilder();
        StringBuilder join = new StringBuilder();

        Graph graph = access.graph;

        for (Class<? extends Entity> type : types)
        {
            String typeName = type.getSimpleName();
            String table = graph.getTableName(typeName);
            String alias= graph.getTableAlias(typeName);
            join.append(" LEFT JOIN " + table + "AS "+alias+" ON s_node.id=" + alias+ "._nodeId");
            ColumnAccessor[] columnAccessors = graph.getColumnAccessors(type);
            for (ColumnAccessor columnAccessor : columnAccessors)
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

            Entity[] entities=new Entity[types.length];
            for (int j=0;j<types.length;j++)
            {
                Class<? extends Entity> type=types[j];
                String typeName = type.getSimpleName();
                Long typeNodeId = row.getNullableBIGINT(typeName + "._nodeId");
                if (typeNodeId != null)
                {
                    ColumnAccessor[] columnAccessors = graph.getColumnAccessors(type);
                    Entity entity = (Entity) type.newInstance();
                    for (ColumnAccessor columnAccessor : columnAccessors)
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

    public NodeResult[] getNodeResults(Class<? extends Entity>...types) throws Throwable
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
    final public NodeResult getNodeResult(Class<? extends Entity>...types) throws Throwable
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
    
    public <ENTITY extends Entity> ENTITY getEntity(Class<? extends Entity> type) throws Throwable
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

    public <ENTITY extends Entity> ENTITY[] getEntities(Class<? extends Entity> type) throws Throwable
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
}
