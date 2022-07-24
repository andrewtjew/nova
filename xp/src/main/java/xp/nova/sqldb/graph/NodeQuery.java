package xp.nova.sqldb.graph;

import java.lang.reflect.Array;
import java.util.HashMap;

import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.tracing.Trace;

import xp.nova.sqldb.graph.Graph.ColumnAccessor;
import xp.nova.sqldb.graph.Graph.Meta;

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
    
    @SafeVarargs
    final NodeResult[] _execute(Long nodeId,Class<? extends NodeEntity> entityType,Class<? extends NodeAttribute>...types) throws Throwable
    {
        if (this.access==null)
        {
            try (GraphAccess access=this.graph.beginAccess(this.parent,"Graph.NodeQuery",null, false))
            {
                this.access=access;
                return __execute(nodeId,entityType,types);
            }
            finally
            {
                this.access=null;
            }
        }
        else
        {
            return __execute(nodeId,entityType,types);
        }
    }
    

    NodeResult[] __execute(Long nodeId,Class<? extends NodeEntity> entityType,Class<? extends NodeAttribute>[] types) throws Throwable
    {
        Trace parent=this.access.parent;
        StringBuilder select = new StringBuilder();
        StringBuilder join = new StringBuilder();

        Graph graph = access.graph;
        int totalResultTypes=0;
        if (entityType!=null)
        {
            totalResultTypes++;
            Meta meta=graph.getMeta(entityType);
            String typeName = meta.getTypeName();
            String table = meta.getTableName();
            String alias= meta.getTableAlias();
            join.append(" JOIN " + table + "AS "+alias+" ON s_node.id=" + alias+ "._nodeId");
            for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
            {
                String fieldColumnName = columnAccessor.getColumnName(typeName);
                String tableColumnName = columnAccessor.getColumnName(alias);
                select.append(','+tableColumnName + " AS '" + fieldColumnName + '\'');
            }
        }
        totalResultTypes+=types.length;
        for (Class<? extends NodeAttribute> type : types)
        {
            Meta meta=graph.getMeta(type);
            String typeName = meta.getTypeName();
            String table = meta.getTableName();
            String alias= meta.getTableAlias();
            join.append(" LEFT JOIN " + table + "AS "+alias+" ON s_node.id=" + alias+ "._nodeId");
            for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
            {
                String fieldColumnName = columnAccessor.getColumnName(typeName);
                String tableColumnName = columnAccessor.getColumnName(alias);
                select.append(','+tableColumnName + " AS '" + fieldColumnName + '\'');
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

            NodeObject[] objects=new NodeObject[totalResultTypes];
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
            for (Class<? extends NodeAttribute> type:types)
            {
//                    Class<? extends NodeAttribute> type=types[j];
                Meta meta=graph.getMeta(type);
                String typeName=meta.getTypeName();
                Long typeNodeId = row.getNullableBIGINT(typeName + "._nodeId");
                if (typeNodeId != null)
                {
                    NodeAttribute entity = (NodeAttribute) type.newInstance();
                    for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                    {
                        columnAccessor.set(entity, typeName, row);
                    }
                    objects[index]=entity;
                }
                index++;
            }
            results[i]=new NodeResult(nodeId, objects);
        }
        return results;
    }
    
    Long[] _getNodeIds(Class<? extends NodeEntity> entityType) throws Exception, Throwable
    {
        if (this.access==null)
        {
            try (GraphAccess access=this.graph.beginAccess(this.parent,"Graph.NodeQuery",null, false))
            {
                this.access=access;
                return __getNodeIds(entityType);
            }
            finally
            {
                this.access=null;
            }
        }
        else
        {
            return __getNodeIds(entityType);
        }
    }
    
    Long[] __getNodeIds(Class<? extends NodeEntity> entityType) throws Throwable
    {
        Trace parent=this.access.parent;

        Graph graph = access.graph;
        Meta meta=graph.getMeta(entityType);
        String table = meta.getTableName();
        StringBuilder query = new StringBuilder("SELECT s_node.id FROM s_node JOIN " + table+" ON s_node.id=" +table+ "._nodeId WHERE "+this.expression);

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
        Long[] results = new Long[rowSet.size()];
        for (int i = 0; i < rowSet.size();i++)
        {
            Row row = rowSet.getRow(i);
            results[0] = row.getBIGINT(0);
        }
        return results;
    }
    
    public Long[] getNodeIds(Class<? extends NodeEntity> entityType) throws Exception, Throwable
    {
        return _getNodeIds(entityType);
    }
    
    public Long getNodeId(Class<? extends NodeEntity> entityType) throws Exception, Throwable
    {
        Long[] results=_getNodeIds(entityType);
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

    static void buildMap(NodeResult[] results,Class<? extends NodeEntity> entityType,Class<? extends NodeAttribute>[] types)
    {
        HashMap<String,Integer> map=new HashMap<String, Integer>();
        int index=0;
        if (entityType!=null)
        {
            map.put(entityType.getSimpleName(), index++);
        }
        for (Class<? extends NodeAttribute> type:types)
        {
            map.put(type.getSimpleName(), index++);
        }
        for (int i=0;i<results.length;i++)
        {
            results[i].setMap(map);
        }
    }
    
    @SafeVarargs   
    final public NodeResult[] getNodeResults(Class<? extends NodeEntity> entityType,Class<? extends NodeAttribute>...types) throws Throwable
    {
        NodeResult[] results=_execute(null,entityType,types);
        buildMap(results,entityType,types);
        return results;
    }

    @SafeVarargs   
    final public NodeResult[] getNodeResults(Class<? extends NodeAttribute>...types) throws Throwable
    {
        return getNodeResults(null,types);
//        NodeResult[] results=_execute(null,null,types);
//        HashMap<String,Integer> map=new HashMap<String, Integer>();
//        for (int i=0;i<types.length;i++)
//        {
//            map.put(types[i].getSimpleName(), i);
//        }
//        for (int i=0;i<results.length;i++)
//        {
//            results[i].setMap(map);
//        }
//        return results;
    }


    @SafeVarargs
    final public NodeResult getNodeResult(Class<? extends NodeEntity> entityType,Class<? extends NodeAttribute>...types) throws Throwable
    {
        NodeResult[] results=_execute(null,entityType,types);
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
    final public NodeResult getNodeResult(long nodeId,Class<? extends NodeAttribute>...types) throws Throwable
    {
        NodeResult[] results=_execute(nodeId,null,types);
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
    
    
    @SafeVarargs
    final public NodeResult getNodeResult(long nodeId,Class<? extends NodeEntity> entityType,Class<? extends NodeAttribute>...types) throws Throwable
    {
        NodeResult[] results=_execute(nodeId,entityType,types);
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
    
    public <ENTITY extends NodeEntity> ENTITY[] getEntities(Class<? extends NodeEntity> entityType) throws Throwable
    {
        NodeResult[] results=_execute(null,entityType);
        @SuppressWarnings("unchecked")
        ENTITY[] entities=(ENTITY[]) Array.newInstance(entityType,results.length);
        for (int i=0;i<entities.length;i++)
        {
            entities[i]=results[i].get(0);
        }
        return entities;
    }

    public <ENTITY extends NodeAttribute> ENTITY getEntity(long nodeId,Class<? extends NodeEntity> entityType) throws Throwable
    {
        NodeResult[] results=_execute(nodeId,entityType);
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
    
    public <ENTITY extends NodeEntity> ENTITY getEntity(Class<? extends NodeEntity> entityType) throws Throwable
    {
        NodeResult[] results=_execute(null,entityType);
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
