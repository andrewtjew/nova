package org.nova.sqldb.graph;

import org.nova.sqldb.graph.Graph.ColumnAccessor;
import org.nova.utils.TypeUtils;

import com.amazonaws.services.xray.model.Trace;

public class LinkQuery
{
    private Class<? extends Entity>[] linkNodeEntityTypes;
    private Class<? extends Entity>[] toNodeEntityTypes;

    final private Node fromNode;
    
    public LinkQuery(Node fromNode)
    {
        this.fromNode=fromNode;
    }
    public LinkQuery selectLinkTypes(Class<? extends Entity>...types)
    {
        this.linkNodeEntityTypes=types;
        return this;
    }
    public LinkQuery selectToTypes(Class<? extends Entity>...types)
    {
        this.toNodeEntityTypes=types;
        return this;
    }
    public LinkQuery execute(Trace parent,String where,Object parameters)
    {
        StringBuilder select=new StringBuilder();
        StringBuilder join=new StringBuilder();

        select.append("_link.id AS _link.id");
        Graph graph=this.fromNode.graphEvent.graph;

        if (linkNodeEntityTypes!=null)
        {
//            for (Class<? extends NodeObject> type:linkNodeEntityTypes)
//            {
//                String table=graph.getTableName(type);
//                String linkFieldColumnName=type.getSimpleName()+".id";
//                join.append(" LEFT JOIN "+table+" ON _link.nodeId="+table+"._nodeId AND "+table+"._retiredEventId IS NULL");
//                select.append(','+linkTable+".id AS '"+linkFieldColumnName+'\'');
//                }
//                else
//                {
//                    nodeType=(Class<? extends NodeObject>) type;
//                    table=this.graph.getTableName(nodeType);
//                    join.append(" LEFT JOIN "+table+" ON _node.id="+table+"._nodeId AND "+table+"._retiredEventId IS NULL");
//                }
//                String typeName=nodeType.getSimpleName();
//                ColumnAccessor[] columnAccessors=this.graph.getColumnAccessors(nodeType);
//                for (ColumnAccessor columnAccessor:columnAccessors)
//                {
//                    String fieldColumnName=columnAccessor.getColumnName(typeName);
//                    String tableColumnName=columnAccessor.getColumnName(table);
//                    select.append(','+tableColumnName+" AS '"+fieldColumnName+'\'');
//                }
//            }
        }
        
        
        return null;
        
    }
   
}
