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
    final NodeResult[] _execute(Long nodeId,Class<? extends NodeObject> requriedObjectType,Class<? extends NodeObject>...optionalObjectTypes) throws Throwable
    {
        if (this.access==null)
        {
            try (GraphAccess access=this.graph.openAccess(this.parent,"Graph.NodeQuery",null, false))
            {
                this.access=access;
                return __execute(nodeId,requriedObjectType,optionalObjectTypes);
            }
            finally
            {
                this.access=null;
            }
        }
        else
        {
            return __execute(nodeId,requriedObjectType,optionalObjectTypes);
        }
    }

    NodeResult[] __execute(Long nodeId,Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>[] optionalObjectTypes) throws Throwable
    {
        Trace parent=this.access.parent;
        StringBuilder select = new StringBuilder();
        StringBuilder join = new StringBuilder();

        Graph graph = access.graph;
        int totalResultTypes=0;
        if (requiredObjectType!=null)
        {
            totalResultTypes++;
            Meta meta=graph.getMeta(requiredObjectType);
            String typeName = meta.getTypeName();
            String table = meta.getTableName();
            String alias= meta.getTableAlias();
            join.append(" JOIN " + table + "AS "+alias+" ON _node.id=" + alias+ "._nodeId");
            for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
            {
                String fieldColumnName = columnAccessor.getColumnName(typeName);
                String tableColumnName = columnAccessor.getColumnName(alias);
                select.append(','+tableColumnName + " AS '" + fieldColumnName + '\'');
            }
        }
        totalResultTypes+=optionalObjectTypes.length;
        for (Class<? extends NodeObject> type : optionalObjectTypes)
        {
            Meta meta=graph.getMeta(type);
            String typeName = meta.getTypeName();
            String table = meta.getTableName();
            String alias= meta.getTableAlias();
            join.append(" LEFT JOIN " + table + "AS "+alias+" ON _node.id=" + alias+ "._nodeId");
            for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
            {
                String fieldColumnName = columnAccessor.getColumnName(typeName);
                String tableColumnName = columnAccessor.getColumnName(alias);
                select.append(','+tableColumnName + " AS '" + fieldColumnName + '\'');
            }
        }
        StringBuilder query = new StringBuilder("SELECT _node.id AS '_node.id'" + select + "FROM _node" + join);
        if (nodeId!=null)
        {
            if (expression==null)
            {
                expression="_node.id="+nodeId;
            }
            else
            {
                expression="_node.id="+nodeId+" AND "+expression;
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
            if (requiredObjectType!=null)
            {
                Meta meta=graph.getMeta(requiredObjectType);
                String typeName=meta.getTypeName();
                Long typeNodeId = row.getNullableBIGINT(typeName + "._nodeId");
                if (typeNodeId != null)
                {
                    NodeObject nodeObject = (NodeObject) requiredObjectType.newInstance();
                    for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                    {
                        columnAccessor.set(nodeObject, typeName, row);
                    }
                    objects[index++]=nodeObject;
                }
            }
            for (Class<? extends NodeObject> type:optionalObjectTypes)
            {
//                    Class<? extends NodeAttribute> type=types[j];
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
                    objects[index]=nodeObject;
                }
                index++;
            }
            results[i]=new NodeResult(nodeId, objects);
        }
        return results;
    }
    
    Long[] _getNodeIds(Class<? extends NodeObject> type) throws Exception, Throwable
    {
        if (this.access==null)
        {
            try (GraphAccess access=this.graph.openAccess(this.parent,"Graph.NodeQuery",null, false))
            {
                this.access=access;
                return __getNodeIds(type);
            }
            finally
            {
                this.access=null;
            }
        }
        else
        {
            return __getNodeIds(type);
        }
    }
    
    Long[] __getNodeIds(Class<? extends NodeObject> type) throws Throwable
    {
        Trace parent=this.access.parent;

        Graph graph = access.graph;
        Meta meta=graph.getMeta(type);
        String table = meta.getTableName();
        StringBuilder query = new StringBuilder("SELECT _node.id FROM _node JOIN " + table+" ON _node.id=" +table+ "._nodeId WHERE "+this.expression);

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
    
    public Long[] getNodeIds(Class<? extends NodeObject> type) throws Exception, Throwable
    {
        return _getNodeIds(type);
    }
    
    public Long getNodeId(Class<? extends NodeObject> type) throws Exception, Throwable
    {
        Long[] results=_getNodeIds(type);
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

    static void buildMap(NodeResult[] results,Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>[] optionalObjectTypes)
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
    
    @SafeVarargs   
    final public NodeResult[] getNodesWithRequiredObject(Class<? extends NodeObject> requiredObjectType,Class<? extends NodeObject>...optionalObjectTypes) throws Throwable
    {
        NodeResult[] results=_execute(null,requiredObjectType,optionalObjectTypes);
        buildMap(results,requiredObjectType,optionalObjectTypes);
        return results;
    }


    @SafeVarargs   
    final public NodeResult[] getNodeObjects(Class<? extends NodeObject>...types) throws Throwable
    {
        return getNodesWithRequiredObject(null,types);
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
    final public NodeResult getNodeWithRequiredObject(Class<? extends NodeObject> requiredType,Class<? extends NodeObject>...requiredObjectTypes) throws Throwable
    {
        NodeResult[] results=_execute(null,requiredType,requiredObjectTypes);
        if (results.length==0)
        {
            return null;
        }
        if (results.length>1)
        {
            throw new Exception();
        }
        buildMap(results,requiredType,requiredObjectTypes);
        return results[0];
    }

    @SafeVarargs
    final public NodeResult getNodes(long nodeId,Class<? extends NodeObject>...optionalTypes) throws Throwable
    {
        NodeResult[] results=_execute(nodeId,null,optionalTypes);
        if (results.length==0)
        {
            return null;
        }
        if (results.length>1)
        {
            throw new Exception();
        }
        buildMap(results,null,optionalTypes);
        return results[0];
    }
    
    
    @SafeVarargs
    final public NodeResult getNodeWithRequiredObject(long nodeId,Class<? extends NodeObject> requiredType,Class<? extends NodeObject>...optionalTypes) throws Throwable
    {
        NodeResult[] results=_execute(nodeId,requiredType,optionalTypes);
        if (results.length==0)
        {
            return null;
        }
        if (results.length>1)
        {
            throw new Exception();
        }
        buildMap(results,requiredType,optionalTypes);
        return results[0];
    }

    
    public <OBJECT extends NodeObject> OBJECT[] getNodeObjects(Class<? extends NodeObject> type) throws Throwable
    {
        NodeResult[] results=_execute(null,type);
        @SuppressWarnings("unchecked")
        OBJECT[] entities=(OBJECT[]) Array.newInstance(type,results.length);
        for (int i=0;i<entities.length;i++)
        {
            entities[i]=results[i].get(0);
        }
        return entities;
    }

    public <OBJECT extends NodeObject> OBJECT getNodeObject(long nodeId,Class<? extends NodeObject> type) throws Throwable
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
    
    public <OBJECT extends NodeObject> OBJECT getNodeObject(Class<? extends NodeObject> type) throws Throwable
    {
        NodeResult[] results=_execute(null,type);
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
    
}
