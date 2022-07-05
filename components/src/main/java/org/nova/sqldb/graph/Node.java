package org.nova.sqldb.graph;

import org.nova.sqldb.Accessor;
import org.nova.sqldb.Insert;
import org.nova.sqldb.Row;
import org.nova.sqldb.Select;
import org.nova.sqldb.graph.Graph.ColumnAccessor;
import org.nova.tracing.Trace;

public class Node
{
    final private long id;
    final private GraphTransaction graphTransaction;
    Node(GraphTransaction graphTransaction,long id)
    {
        this.id=id;
        this.graphTransaction=graphTransaction;
    }
    
//
//    public EntityMap getEntitities(Class<?>...types) throws Exception
//    {
//        StringBuilder columns=new StringBuilder("_node.id as '_node.id',_node.created as '_node.created'");
//        StringBuilder joins=new StringBuilder("FROM _node");
//        for (Class<?> type:types)
//        {
//            ColumnAccessor[] columnAccessors=GraphTransaction.getColumnAccessors(type);
//        }
//        return null;
//    }
    
    public void put(NodeObject object) throws Throwable
    {
        this.graphTransaction.put(object,this.id);
    }
    
    public <OBJECT extends NodeObject> OBJECT get(Class<? extends NodeObject> type) throws Throwable
    {
        return this.graphTransaction.get(type, this.id);
    }
    
    public long getNodeId()
    {
        return this.id;
    }
    
    
    
}
