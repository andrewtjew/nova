package org.nova.sqldb.graph;

import org.nova.sqldb.Accessor;
import org.nova.sqldb.Insert;
import org.nova.sqldb.Row;
import org.nova.sqldb.Select;
import org.nova.sqldb.graph.GraphTransaction.ColumnAccessor;
import org.nova.tracing.Trace;

public class Node
{
    final private long id;
    final private GraphTransaction graph;
    
    Node(GraphTransaction graph,long id)
    {
        this.id=id;
        this.graph=graph;
    }
    

    public EntityMap getEntitities(Class<?>...types) throws Exception
    {
        StringBuilder columns=new StringBuilder("_node.id as '_node.id',_node.created as '_node.created'");
        StringBuilder joins=new StringBuilder("FROM _node");
        for (Class<?> type:types)
        {
            ColumnAccessor[] columnAccessors=GraphTransaction.getColumnAccessors(type);
        }
        return null;
    }

    
    public long[] put(Object...objects) throws Throwable
    {
        Accessor accessor=this.graph.getAccessor();
        long eventId=graph.getEventId();
        Trace parent=this.graph.getTrace();
        long[] ids=new long[objects.length];
        for (int i=0;i<objects.length;i++)
        {
            Object object=objects[i];
            String table=object.getClass().getSimpleName();
            accessor.executeUpdate(parent, "node.put", "UPDATE "+table+" SET _retiredEventId=? WHERE _nodeId=?",eventId,this.id);
            
            ColumnAccessor[] columnAccessors=GraphTransaction.getColumnAccessors(object.getClass());
            Insert insert=Insert.table(table);
            insert.value("_nodeId", this.id);
            insert.value("_createdEventId", eventId);
            for (ColumnAccessor columnAccessor:columnAccessors)
            {
                insert.value(columnAccessor.getName(), columnAccessor.get(object));
            }
            ids[i]=insert.executeAndReturnLongKey(parent, accessor);
        }
        return ids;
    }
    
    @SuppressWarnings("unchecked")
    public <OBJECT> OBJECT get(Class<?> type) throws Throwable
    {
        Accessor accessor=this.graph.getAccessor();
        String table=type.getSimpleName();
        Trace parent=this.graph.getTrace();
        Row row=Select.source(table).executeOne(parent, accessor, "_nodeId=? AND _retiredEventId IS NULL",this.id);
        if (row==null)
        {
            return null;
        }
        ColumnAccessor[] columnAccessors=GraphTransaction.getColumnAccessors(type);
        Object object=type.newInstance();
        for (ColumnAccessor columnAccessor:columnAccessors)
        {
            columnAccessor.set(object, null, row);
        }        
        return (OBJECT)object;
    }
    
    
    
}
