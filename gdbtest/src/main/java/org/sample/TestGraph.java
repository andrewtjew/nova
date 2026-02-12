package org.sample;

import java.util.List;

import org.nova.http.server.Context;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.tracing.Trace;
import org.sample.graph.Castle;
import org.sample.graph.Paper;
import org.sample.graph.Rock;
import org.sample.graph.Scissor;
import org.sample.graph.User;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import xp.nova.sqldb.graph.Graph;
import xp.nova.sqldb.graph.GraphAccessor;
import xp.nova.sqldb.graph.NodeObject;
import xp.nova.sqldb.graph.GraphPerformanceMonitor;
import xp.nova.sqldb.graph.GraphTransaction;
import xp.nova.sqldb.graph.Query;
import xp.nova.sqldb.graph.QueryResultSet;

public class TestGraph extends Graph
{
    @SuppressWarnings("unchecked")
    static Class<? extends NodeObject>[] TYPES=new Class[]
    {
        User.class,
        Castle.class,
        Rock.class,
        Paper.class,
        Scissor.class,
    };
    
    public static void setup(Trace parent,Connector connector) throws Throwable
    {
        Graph.setup(parent, connector, CATALOG);
    }
    
    public TestGraph(Trace parent,Connector connector,GraphPerformanceMonitor performanceCollector) throws Throwable
    {
        super(connector,CATALOG,performanceCollector);
        initialize(parent);
    }

    List<Class<?>> scanforGraphTrypes()
    {
        try (ScanResult scanResult = new ClassGraph().acceptPackages("com.example.mypackage").scan()) 
        {
            return scanResult.getAllClasses().loadClasses();
        }
    }
    private void initialize(Trace parent) throws Throwable
    {
//        setup(parent,this.getConnector(),CATALOG);
        try (GraphAccessor graphAccessor=openGraphAccessor(parent))
        {
            Accessor accessor=graphAccessor.getAccessor();
//            if (accessor.executeQuery(parent,null,"SELECT count(*) FROM information_schema.tables WHERE table_name=? AND table_schema=?","_domain",CATALOG).getRow(0).getBIGINT(0)==0)
//            {
//                String sql="CREATE TABLE `_domain` (`name` varchar(30) NOT NULL,`accountId` bigint DEFAULT NULL, PRIMARY KEY (`name`), UNIQUE KEY `name_UNIQUE` (`name`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
//                accessor.executeUpdate(parent, null, sql);
//            }
            
            
            for (Class<? extends NodeObject> type:this.TYPES)
            {
                this.upgradeTable(parent, graphAccessor, type);
            }
        }
        
    }
    public void reset(Trace parent) throws Throwable
    {
        TestGraph.setup(parent, this.getConnector());
        initialize(parent);
    }
    
    
    public GraphTransaction beginGraphTransaction(Trace parent) throws Throwable
    {
        GraphAccessor graphAccessor=openGraphAccessor(parent);
        return graphAccessor.beginTransaction(parent,CATALOG,-1,true);
    }
    public GraphTransaction beginGraphTransaction(Trace parent,Context context) throws Throwable
    {
        GraphAccessor graphAccessor=openGraphAccessor(parent);
        return graphAccessor.beginTransaction(parent,context.getRequestMethod().getKey(),-1,true);
    }
    public GraphTransaction beginGraphTransaction(Trace parent,UserSession session) throws Throwable
    {
        GraphAccessor graphAccessor=openGraphAccessor(parent);
        return graphAccessor.beginTransaction(parent, CATALOG,session.getDeviceSessionId(),true);
    }
    

    public QueryResultSet execute(Trace parent,long startNodeId,Query query,Object...parameters) throws Throwable
    {
        try (GraphAccessor graphAccessor=openGraphAccessor(parent))
        {
            return graphAccessor.execute(parent, parameters, startNodeId, query);
        }
    }
    public QueryResultSet execute(Trace parent,NodeObject startNode,Query query,Object...parameters) throws Throwable
    {
        try (GraphAccessor graphAccessor=openGraphAccessor(parent))
        {
            return graphAccessor.execute(parent, startNode, query, parameters);
        }
    }
    public QueryResultSet execute(Trace parent,Query query,Object...parameters) throws Throwable
    {
        try (GraphAccessor graphAccessor=openGraphAccessor(parent))
        {
            return graphAccessor.execute(parent, parameters, null, query);
        }
    }
    
    public void put(Trace parent,Context context,long nodeId,NodeObject...objects) throws Throwable
    {
        try (GraphTransaction transaction=beginGraphTransaction(parent,context))
        {
            transaction.update(nodeId,objects);
            transaction.commit();
        }
    }
    
    
    final static public String CATALOG="test";
}
