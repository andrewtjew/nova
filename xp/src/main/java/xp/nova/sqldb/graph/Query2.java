package xp.nova.sqldb.graph;

import java.util.ArrayList;
import java.util.HashMap;

import org.nova.sqldb.Accessor;
import org.nova.sqldb.RowSet;
import org.nova.tracing.Trace;

public class Query2
{
    private Graph graph;

    private Class<? extends NodeObject>[] nodeTypes;
    private Class<? extends LinkObject>[] linkTypes;

    private Direction direction;
    private Long nodeId;
    private Relation relation;

    private Object[] parameters;
    private String whereExpression;
    private String orderby;
    private ArrayList<Predicate> predicates;

    public Query2 start(long nodeId,Direction direction,Relation relation)
    {
        this.nodeId=nodeId;
        this.direction=direction;
        this.relation=relation;
        return this;
    }
    public Query2 start(long nodeId,Direction direction)
    {
        return start(nodeId,direction,null);
    }

    public Query2 start(long nodeId)
    {
        this.nodeId=nodeId;
        return this;
    }
    
    @SafeVarargs
    final public Query2 selectNodeObjects(Class<? extends NodeObject>... nodeTypes)
    {
        this.nodeTypes = nodeTypes;
        return this;
    }

    @SafeVarargs
    final public Query2 selectLinkObjects(Class<? extends LinkObject>... linkTypes)
    {
        this.linkTypes = linkTypes;
        return this;
    }

    public Query2 with(Predicate predictate)
    {
        if (this.predicates == null)
        {
            this.predicates = new ArrayList<>();
        }
        this.predicates.add(predictate);
        return this;
    }

    static void translateParameters(Object[] parameters)
    {
        for (Object parameter:parameters)
        {
            if (parameters!=null)
            {
                if (parameter instanceof ShortEnummerable)
                {
                    parameter=((ShortEnummerable)parameter).getValue();
                }
                else if (parameter instanceof IntegerEnummerable)
                {
                    parameter=((IntegerEnummerable)parameter).getValue();
                }
            }
        }
    }
    
    public Query2 where(String expression, Object... parameters)
    {
        translateParameters(parameters);
        this.parameters=parameters;
        this.whereExpression=expression;
        return this;
    }

    public Query2 orderBy(String orderBy)
    {
        this.orderby = orderBy;
        return this;
    }

    private void addPredicate(ArrayList<Predicate> paths, String pathSource, int aliasIndex) throws Throwable
    {
        if (paths == null)
        {
            return;
        }
        for (Predicate path : paths)
        {
            String linkAlias = "_link" + aliasIndex;
            String nodeAlias = "_node" + aliasIndex;
            switch (path.direction)
            {
            case FROM:
                sources.append(
                        " LEFT JOIN _link AS " + linkAlias + " ON " + pathSource + ".id=" + linkAlias + ".fromNodeId");
                break;
            case TO:
                sources.append(
                        " LEFT JOIN _link AS " + linkAlias + " ON " + pathSource + ".id=" + linkAlias + ".toNodeId");
                break;
            default:
                break;
            }
            if (path.relation!=null)
            {
                String typeName=path.relation.getClass().getSimpleName();
                sources.append(" AND "+linkAlias+".type='"+typeName+"' AND "+linkAlias+".relation="+path.relation.getValue());
            }

            if (path.nodeTypes != null)
            {
                String on = null;
                switch (path.direction)
                {
                case FROM:
                    on = " ON " + linkAlias + ".toNodeId=";
                    break;
                case TO:
                    on = " ON " + linkAlias + ".fromNodeId=";
                    break;
                default:
                    break;
                }
                String namespace = path.namespace != null ? path.namespace + "." : "";
                for (int i = 0; i < path.nodeTypes.length; i++)
                {
                    Class<? extends NodeObject> type = path.nodeTypes[i];
                    Meta meta = graph.getMeta(type);
                    this.map.put(namespace+meta.getTypeName(), meta);
                    String typeName = meta.getTypeName();
                    String table = meta.getTableName();
                    String alias = meta.getTableAlias();
//                    if (i > 0)
//                    {
//                        sources.append(" LEFT");
//                    }
                    sources.append(" LEFT JOIN " + table + "AS " + alias + on + alias + "._nodeId");
                    for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                    {
                        String fieldColumnName = namespace + columnAccessor.getColumnName(typeName);
                        String tableColumnName = columnAccessor.getColumnName(alias);
                        select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
                    }
                }
            }
            addPredicate(path.predicates, nodeAlias, aliasIndex++);
        }
    }

    private StringBuilder sources;
    private StringBuilder select;
    
    private HashMap<String,Meta> map;
    
    
    public QueryResult[] execute(Trace parent,GraphAccessor graphAccessor) throws Throwable
    {
        this.graph=graphAccessor.graph;
        Accessor accessor=graphAccessor.accessor;
        this.map=new HashMap<String, Meta>();
        this.select = new StringBuilder();
        this.sources = new StringBuilder();
        StringBuilder where = new StringBuilder();
        if (this.direction != null)
        {
            switch (this.direction)
            {
            case FROM:
                sources.append(" _link JOIN _node ON _link.toNodeId=_node.id ");
                where.append(" _link.fromNodeId=" + this.nodeId);
                break;
            case TO:
                sources.append(" _link JOIN _node ON _link.fromNodeId=_node.id ");
                where.append(" _link.toNodeId=" + this.nodeId);
                break;
            default:
                break;
            }
            if (this.relation!=null)
            {
                String typeName=relation.getClass().getSimpleName();
                sources.append(" AND _link.type='"+typeName+"' AND _link.relation="+relation.getValue());
            }
        }
        else
        {
            sources.append(" _node");
            if (this.nodeId != null)
            {
                where.append(" _node.id=" + this.nodeId);
            }
        }
        select.append(" _node.id AS _nodeId");
        if (this.nodeTypes != null)
        {
            for (int i = 0; i < this.nodeTypes.length; i++)
            {
                Class<? extends NodeObject> type = this.nodeTypes[i];
                Meta meta = graph.getMeta(type);
                this.map.put(meta.getTypeName(), meta);
                String typeName = meta.getTypeName();
                String table = meta.getTableName();
                String alias = meta.getTableAlias();
                if ((i>0)||((nodeId!=null)&&(this.direction==null)))
                {
                    sources.append(" LEFT");
                }
                sources.append(" JOIN " + table + "AS " + alias + " ON _node.id=" + alias + "._nodeId");
                for (ColumnAccessor columnAccessor : meta.getColumnAccessors())
                {
                    String fieldColumnName = columnAccessor.getColumnName(typeName);
                    String tableColumnName = columnAccessor.getColumnName(alias);
                    select.append(',' + tableColumnName + " AS '" + fieldColumnName + '\'');
                }
            }
        }
        addPredicate(this.predicates, "_node", 0);
        if (this.whereExpression != null)
        {
            if (where.length() > 0)
            {
                where.append(" AND");
            }
            where.append(" (" + this.whereExpression + ")");
        }
        StringBuilder query = new StringBuilder("SELECT" + select + " FROM" + sources);

        if (where.length() > 0)
        {
            query.append(" WHERE" + where);
        }
        if (this.orderby != null)
        {
            query.append(" ORDER BY ");
            query.append(orderby);
        }
        System.out.println(query);
        RowSet rowSet;
        if (parameters != null)
        {
            rowSet = accessor.executeQuery(parent, null, query.toString(), parameters);
        }
        else
        {
            rowSet = accessor.executeQuery(parent, null, query.toString());
        }
        QueryResult[] results=new QueryResult[rowSet.size()];
        for (int i=0;i<results.length;i++)
        {
            results[i]=new QueryResult(rowSet.getRow(i), this.map);
        }
        return results;
    }

//    public QueryResult executeOne(Trace parent,GraphAccessor graphAccessor) throws Throwable
//    {
//        QueryResult[] results=execute(parent,graphAccessor);
//        if (results.length==0)
//        {
//            return null;
//        }
//        if (results.length>1)
//        {
//            throw new Exception("Expected one result. Actual="+results.length);
//        }
//        return results[0];
//    }

//    public QueryResult executeOne(Trace parent, Graph graph,String catalog) throws Throwable
//    {
//        try (GraphAccessor accessor=graph.openGraphAcessor(parent, catalog))
//        {
//            return executeOne(parent,accessor);
//        }
//    }

//    public <OBJECT extends NodeObject> OBJECT[] execute(Trace parent,GraphAccessor graphAccessor, Class<OBJECT> type) throws Throwable
//    {
//        selectNodeObjects(type);
//        QueryResult[] results=execute(parent,graphAccessor);
//        Object array=Array.newInstance(type, results.length);
//        for (int i=0;i<results.length;i++)
//        {
//            Array.set(array, i, results[i].get(type));
//        }
//        return (OBJECT[]) array;
//    }

//    public <OBJECT extends NodeObject> OBJECT executeOne(Trace parent,GraphAccessor graphAccessor, Class<OBJECT> type) throws Throwable
//    {
//        selectNodeObjects(type);
//        QueryResult result=executeOne(parent,graphAccessor);
//        if (result==null)
//        {
//            return null;
//        }
//        return result.get(type);
//    }

    
}
