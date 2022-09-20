package xp.nova.sqldb.graph;

import org.nova.sqldb.Accessor;
import org.nova.tracing.Trace;

public class GraphAccessor implements AutoCloseable
{
    final Accessor accessor;
    final Graph graph;
   
    GraphAccessor(Graph graph,Accessor accessor)
    {
        this.accessor=accessor;
        this.graph=graph;
    }

    @Override
    public void close() throws Exception
    {
        this.accessor.close();
    }
    
    public GraphTransaction beginTransaction(Trace parent,String source,long creatorId,boolean autoCloseGraphAccesoor) throws Throwable
    {
        return new GraphTransaction(parent, this, source, creatorId,autoCloseGraphAccesoor);
    }
    public GraphTransaction beginTransaction(Trace parent,String source,Long creatorId) throws Throwable
    {
        return beginTransaction(parent,source,creatorId,false);
    }
    
    public long getCount(Trace parent,Class<? extends NodeObject> type,String where,Object...parameters) throws Throwable
    {
        Meta meta=this.graph.getMeta(type);
        String table=meta.getTableName();
        return accessor.executeQuery(parent,null,"SELECT count(*) FROM "+table+" WHERE "+where,parameters).getRow(0).getBIGINT(0);
    }
    
    
}
