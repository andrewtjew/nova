package org.sample;

import org.nova.concurrent.Progress;
import org.nova.html.tags.p;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.StateParam;
import org.nova.json.ObjectMapper;
import org.nova.services.RequiredRoles;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceRunnable;
import org.sample.graph.Castle;
import org.sample.graph.Paper;
import org.sample.graph.Rock;
import org.sample.graph.Scissor;

import com.mira.graphTypes.Relation;

import xp.nova.sqldb.graph.ArrayQuery;
import xp.nova.sqldb.graph.Direction;
import xp.nova.sqldb.graph.LinkQuery;
import xp.nova.sqldb.graph.Node;
import xp.nova.sqldb.graph.Query;
import xp.nova.sqldb.graph.QueryResult;
import xp.nova.sqldb.graph.QueryResultSet;
import xp.nova.sqldb.graph.Relation_;

public class TestController extends PageController
{
    
    static public class Relation implements Relation_
    {
        private final String key;
        public Relation()
        {
            this.key="";
        }
        public Relation(String key)
        {
            this.key=key;
        }
        public String getKey()
        {
            return this.key;
        }
        //Keep the static functions sorted alphabetically.
        static public Relation link()
        {
            return new Relation("link");
        }
    }
    
    public TestController(Service service) throws Throwable
    {
        super(service);
        
    }

    @GET
    @Path("/user")
    @RequiredRoles("User")
    public UserPage user(Trace parent,@StateParam UserSession session) throws Throwable
    {
        UserPage page = new UserPage(null, null);
        page.body().addInner("Hello, userId="+session.getUserId());
        return page;
    }

    @GET
    @Path("/")
    @RequiredRoles()
    public UserPage root(Trace parent,@StateParam UserSession session) throws Throwable
    {
        UserPage page = new UserPage(null, null);
        
        TestGraph graph=this.service.getGraph();
        try (var transaction=graph.beginGraphTransaction(parent))
        {
            Castle castle=new Castle();
            castle.name="test";
            long nodeId=transaction.createNode(castle);

            {
                castle=new Castle();
                castle.name="test3";
                nodeId=transaction.createNode(castle);
            }
            
            Query query=new Query().select(Castle.class);
            
            var accessor=transaction.getGraphAccessor();
            for (int i=0;i<2;i++)
            {
                Castle db=accessor.execute(parent, query).getResults()[0].getNodeObject(Castle.class);
                if (db!=null)
                {
                    page.body().addInner(new p());
                    page.body().addInner("1 DB castle "+db.name);
                }
                else
                {
                    page.body().addInner(new p());
                    page.body().addInner("1 No castle");
                }
            }
            
            long deleteId=nodeId;
            transaction.deleteNode(deleteId);
            for (int i=0;i<2;i++)
            {
                Castle db=accessor.execute(parent, deleteId,query).getNodeObject();
                if (db!=null)
                {
                    page.body().addInner(new p());
                    page.body().addInner("2 DB castle "+db.name);
                }
                else
                {
                    page.body().addInner(new p());
                    page.body().addInner("2 No castle");
                }
            }

//            {
//                castle=new Castle();
//                castle.name="test2";
//                nodeId=transaction.create(castle);
//            }
            for (int i=0;i<2;i++)
            {
                Castle db=accessor.execute(parent, query).getResults()[0].getNodeObject(Castle.class);
                if (db!=null)
                {
                    page.body().addInner(new p());
                    page.body().addInner("DB castle "+db.name);
                }
                else
                {
                    page.body().addInner(new p());
                    page.body().addInner("No castle");
                }
            }
            
            
            transaction.commit();
        }
        
        
        page.body().addInner(new p());
        page.body().addInner("Hello world");
        return page;
    }

    @GET
    @Path("/test2")
    @RequiredRoles()
    public UserPage test2(Trace parent,@StateParam UserSession session) throws Throwable
    {
        UserPage page = new UserPage(null, null);
        
        TestGraph graph=this.service.getGraph();
        try (var transaction=graph.beginGraphTransaction(parent))
        {
            var accessor=transaction.getGraphAccessor();

            Castle castle=new Castle();
            castle.name="castle";
            Rock rock=new Rock();
            rock.name="rock";
            
            long nodeId=transaction.createNode(castle);

            
            {
                Query query=new Query().select(Castle.class);
                Castle db=accessor.execute(parent, query).getResults()[0].getNodeObject(Castle.class);
                page.body().addInner("Castle: "+ObjectMapper.writeObjectToString(db));
                page.body().addInner(new p());
            }
            transaction.update(nodeId,rock);
            {
                Query query=new Query().select(Rock.class);
                Rock db=accessor.execute(parent, query).getResults()[0].getNodeObject(Rock.class);
                page.body().addInner("Rock: "+ObjectMapper.writeObjectToString(db));
                page.body().addInner(new p());
            }
            {
                Rock rock2=new Rock();
                rock2.name="rock2";
                transaction.createNode(rock2);
            }
            {
                Query query=new Query().select(Rock.class);
                var resultSet=accessor.execute(parent, query);
                page.body().addInner("Rocks: "+resultSet.getResults().length);
                page.body().addInner(new p());
            }
            {
                Query query=new Query().select(Rock.class);
                var resultSet=accessor.execute(parent, query);
                page.body().addInner("Rocks: "+resultSet.getResults().length);
                page.body().addInner(new p());
            }
            
            transaction.deleteNode(nodeId);
            transaction.commit();
            
            {
                Query query=new Query().select(Rock.class);
                var resultSet=accessor.execute(parent, query);
                page.body().addInner("Rocks: "+resultSet.getResults().length);
                page.body().addInner(new p());
            }
        }
        
        
        return page;
    }

    @GET
    @Path("/test3")
    @RequiredRoles()
    public UserPage test3(Trace parent,@StateParam UserSession session) throws Throwable
    {
        UserPage page = new UserPage(null, null);
        
        TestGraph graph=this.service.getGraph();
        try (var transaction=graph.beginGraphTransaction(parent))
        {
            var accessor=transaction.getGraphAccessor();

            Castle castle=new Castle();
            castle.name="castle";
            Rock rock=new Rock();
            rock.name="rock";
            
            transaction.createNode(rock);
            transaction.createNode(castle);
            
            transaction.createLink(castle, Relation.link(), rock);

            Query query=new Query().select(Castle.class).traverse(new LinkQuery(Direction.FROM,Relation.link(),Rock.class).select(Rock.class));
            QueryResult result=accessor.execute(parent, query).getResult();
            Rock rock2=result.getNodeObject(Rock.class);
            page.body().returnAddInner(new p()).addInner(rock2.name);
            
            Paper[] papers=new Paper[5];
            for (int i=0;i<papers.length;i++)
            {
                papers[i]=new Paper();
                papers[i].name="name:"+i;
            }
            Node node=new Node(papers);
            
            transaction.createArray(castle,papers);
            
            
            transaction.deleteNode(castle);
            transaction.deleteArray(castle);
            transaction.commit();
            
            
        }
        
        page.body().returnAddInner(new p()).addInner("OK");
        
        return page;
    }
    @GET
    @Path("/test4")
    @RequiredRoles()
    public UserPage test4(Trace parent,@StateParam UserSession session) throws Throwable
    {
        UserPage page = new UserPage(null, null);
        
        TestGraph graph=this.service.getGraph();
        try (var transaction=graph.beginGraphTransaction(parent))
        {
            var accessor=transaction.getGraphAccessor();

            Castle castle=new Castle();
            castle.name="castle";
            Rock rock=new Rock();
            rock.name="rock";
            Paper paper=new Paper();
            paper.name="paper";
            
            
            transaction.createNode(castle);
            transaction.createNode(rock);
            transaction.createNode(paper);
            
            transaction.createLink(castle, Relation.link(), rock);
            transaction.createLink(rock, Relation.link(), paper);
            transaction.commit();

            Query query=new Query().select(Castle.class)
                    .traverse(new LinkQuery(Direction.FROM,Relation.link(),Rock.class)
                            .traverse(new LinkQuery(Direction.FROM,Relation.link(),Paper.class).select(Paper.class))
                            );

            QueryResult[] results=accessor.execute(parent, query).getResults();
            for (QueryResult result:results)
            {
                Castle castle1=result.getNodeObject(Castle.class);
                Paper paper1=result.getNodeObject(Paper.class);
                page.body().returnAddInner(new p()).addInner(castle1.name);
                page.body().returnAddInner(new p()).addInner(paper1.name);
                transaction.deleteNode(paper1);
            }
            page.body().returnAddInner(new p()).addInner("---------");

            results=accessor.execute(parent, query).getResults();
            for (QueryResult result:results)
            {
                Castle castle1=result.getNodeObject(Castle.class);
                Paper paper1=result.getNodeObject(Paper.class);
                page.body().returnAddInner(new p()).addInner(castle1.name);
                page.body().returnAddInner(new p()).addInner(paper1.name);
            }
        }
        page.body().returnAddInner(new p()).addInner("OK");
        
        return page;
    }

    
    @GET
    @Path("/test5")
    @RequiredRoles()
    public UserPage test5(Trace parent,@StateParam UserSession session) throws Throwable
    {
        UserPage page = new UserPage(null, null);
        page.body().returnAddInner(new p()).addInner("Tests ArrayQuery");
        
        TestGraph graph=this.service.getGraph();
        try (var transaction=graph.beginGraphTransaction(parent))
        {
            var accessor=transaction.getGraphAccessor();

            Castle castle=new Castle();
            
            transaction.createNode(castle);
            
            Node[] nodes=new Node[5];
            for (int i=0;i<nodes.length;i++)
            {
                Paper paper=new Paper();
                paper.name="paper:"+i;

                Rock rock=new Rock();
                rock.name="rock:"+i;
                
                nodes[i]=new Node(paper,rock);
            }
            
            transaction.createArray(castle,nodes);
            {
                ArrayQuery query=new ArrayQuery().range(2, 3).select(Paper.class);
                QueryResult[] results=accessor.execute(parent, castle, query).getResults();
                for (QueryResult result:results)
                {
                    Paper paper1=result.getNodeObject(Paper.class);
                    page.body().returnAddInner(new p()).addInner(paper1.name);
    
                    Scissor scissor=new Scissor();
                    scissor.name="scissor:"+paper1.name;
                    transaction.createNode(scissor);
                    
                    transaction.createLink(paper1,Relation.link(),scissor); 
                    
                }
            }
            page.body().returnAddInner(new p()).addInner("---------");
            
            {
                ArrayQuery query=new ArrayQuery().select(Paper.class).range(2,3).traverse(new LinkQuery(Direction.FROM,Relation.link(),Scissor.class).select(Scissor.class)).where("scissor.name=?");
                QueryResult[] results=accessor.execute(parent, castle, query,"scissor:paper:3").getResults();
                for (QueryResult result:results)
                {
                    Paper paper1=result.getNodeObject(Paper.class);
                    page.body().returnAddInner(new p()).addInner(paper1.name);
        
                    Scissor scissor1=result.getNodeObject(Scissor.class);
                    page.body().returnAddInner(new p()).addInner(scissor1.name);
        
                }
            }
            
            transaction.commit();
            
            
        }
        
        page.body().returnAddInner(new p()).addInner("OK");
        
        return page;
    }
    @GET
    @Path("/test6")
    @RequiredRoles()
    public UserPage test6(Trace parent,@StateParam UserSession session) throws Throwable
    {
        UserPage page = new UserPage(null, null);
        page.body().returnAddInner(new p()).addInner("Tests ArrayQuery");
        
        TestGraph graph=this.service.getGraph();
        for (int i=0;i<1000;i++)
        {
            try (var transaction=graph.beginGraphTransaction(parent))
            {
                var accessor=transaction.getGraphAccessor();
                Paper paper=new Paper();
                paper.name="paper:"+i;

                Rock rock=new Rock();
                rock.name="rock:"+i;
        
                transaction.createNode(paper);
                transaction.createNode(rock);
                
                transaction.createLink(paper,Relation.link(),rock);
                transaction.commit();
            }
        }
        
        Query query=new Query().select(Paper.class).traverse(new LinkQuery(Direction.FROM,Relation.link(),Rock.class).select(Rock.class));
        var result=graph.execute(parent, 10, query).getResult();
        if (result!=null)
        {
            page.body().returnAddInner(new p()).addInner("result");
        }
        page.body().returnAddInner(new p()).addInner("OK");
        
        return page;
    }

    @GET
    @Path("/test7")
    @RequiredRoles()
    public UserPage test7(Trace parent,@StateParam UserSession session) throws Throwable
    {
        UserPage page = new UserPage(null, null);
        
        TestGraph graph=this.service.getGraph();
        Castle castle=new Castle();
        Rock rock=new Rock();
        try (var transaction=graph.beginGraphTransaction(parent))
        {
            var accessor=transaction.getGraphAccessor();

            castle.name="castle";
            rock.name="rock";
            
            transaction.createNode(rock);
            transaction.update(rock);
            transaction.createNode(castle);
            
            transaction.createLink(castle, Relation.link(), rock);
            transaction.commit();

            Query query=new Query().traverse(new LinkQuery(Direction.FROM,Relation.link(),Rock.class).select(Rock.class));
            QueryResultSet set=accessor.execute(parent, castle, query);
            Rock rock2=set.getNodeObject();
            page.body().returnAddInner(new p()).addInner("nodeId "+rock2.getNodeId()+":"+rock2.name);

        }
        try (var transaction=graph.beginGraphTransaction(parent))
        {
            var accessor=transaction.getGraphAccessor();

            rock.name="dust";
            transaction.update(rock);
            transaction.commit();

            Query query=new Query().traverse(new LinkQuery(Direction.FROM,Relation.link(),Rock.class).select(Rock.class));
            QueryResultSet set=accessor.execute(parent, castle, query);
            Rock rock2=set.getNodeObject();
            page.body().returnAddInner(new p()).addInner("nodeId "+rock2.getNodeId()+":"+rock2.name);
        }
        try (var transaction=graph.beginGraphTransaction(parent))
        {
            var accessor=transaction.getGraphAccessor();
            Query query=new Query().traverse(new LinkQuery(Direction.FROM,Relation.link(),Rock.class).select(Rock.class));
            QueryResultSet set=accessor.execute(parent, castle, query);
            Rock rock2=set.getNodeObject();
            page.body().returnAddInner(new p()).addInner("nodeId "+rock2.getNodeId()+":"+rock2.name);
        }
        
        page.body().returnAddInner(new p()).addInner("OK");
        
        return page;
    }

    @GET
    @Path("/test8")
    @RequiredRoles()
    public UserPage test8(Trace parent,@StateParam UserSession session) throws Throwable
    {
        UserPage page = new UserPage(null, null);
        
        TestGraph graph=this.service.getGraph();
        Castle castle=new Castle();
        castle.name="castle";
        try (var transaction=graph.beginGraphTransaction(parent))
        {
            transaction.createNode(castle);
        }            
        try (var transaction=graph.beginGraphTransaction(parent))
        {
            Paper[] papers=new Paper[5];
            for (int i=0;i<papers.length;i++)
            {
                if (i!=2)
                {
                    papers[i]=new Paper();
                    papers[i].name="name:"+i;
                }
            }
            transaction.createArray(castle,papers);
            transaction.commit();
        }

        try (var transaction=graph.beginGraphTransaction(parent))
        {
            transaction.exchangeArrayElements(castle, 1, 0);
            transaction.commit();
        }

        try (var transaction=graph.beginGraphTransaction(parent))
        {
            transaction.deleteArrayElement(castle, 1);
            transaction.commit();
        }
        
        try (var transaction=graph.beginGraphTransaction(parent))
        {
            var accessor=transaction.getGraphAccessor();
            ArrayQuery query=new ArrayQuery().range(2, 3).select(Paper.class);
            QueryResult[] results=accessor.execute(parent, castle, query).getResults();
            for (QueryResult result:results)
            {
                Paper paper=result.getNodeObject(Paper.class);
                System.out.println("paper:"+paper.name);
                paper.name=paper.name+" burned";
                transaction.update(paper);
            }
            transaction.commit();
        }
        try (var transaction=graph.beginGraphTransaction(parent))
        {
            var accessor=transaction.getGraphAccessor();
            ArrayQuery query=new ArrayQuery().range(2, 3).select(Paper.class);
            QueryResult[] results=accessor.execute(parent, castle, query).getResults();
            for (QueryResult result:results)
            {
                Paper paper=result.getNodeObject(Paper.class);
                System.out.println("paper:"+paper.name);
            }
        }
        
        
        page.body().returnAddInner(new p()).addInner("OK");
        
        return page;
    }

    static class Bench1 implements TraceRunnable
    {
        int count;
        TestGraph graph;
        public Bench1(TestGraph graph,int count)
        {
            this.graph=graph;
            this.count=count;
        }
        
        @Override
        public void run(Trace parent) throws Throwable
        {
            for (int i=0;i<count;i++)
            {
                try (var transaction=graph.beginGraphTransaction(parent))
                {
                    Castle castle=new Castle();
                    castle.name="castle";
                    Rock rock=new Rock();
                    rock.name="rock";
                    
                    long nodeId=transaction.createNode(castle,rock);
                    transaction.commit();
                }
            }
        }
        
    }
    
    @GET
    @Path("/bench1")
    @RequiredRoles()
    public UserPage bench1(Trace parent,@StateParam UserSession session) throws Throwable
    {
        TestGraph graph=this.service.getGraph();
        
        int count=1000;
        int threads=16;
        
        Bench1[] benches=new Bench1[threads];
        for (int i=0;i<threads;i++)
        {
            benches[i]=new Bench1(graph, count);
        }
        try (Trace trace=new Trace(parent,"bench1"))
        {
            Progress<?> progress=this.service.getMultiTaskScheduler().schedule(parent, "bench1", benches);
            progress.waitAll();
            double tps=count*threads/trace.getDurationS();
            UserPage page = new UserPage(null, null);
            page.body().addInner("duration:"+trace.getDurationMs()+",tps="+tps);
            return page;
        }
    }

    static class Bench2 implements TraceRunnable
    {
        int count;
        TestGraph graph;
        long nodeId;
        public Bench2(TestGraph graph,long nodeId,int count)
        {
            this.nodeId=nodeId;
            this.graph=graph;
            this.count=count;
        }
        
        @Override
        public void run(Trace parent) throws Throwable
        {
//            System.out.println("thread:"+Thread.currentThread().getName());
            Query query=new Query().select(Castle.class,Rock.class);
            try (var accessor=this.graph.openGraphAccessor(parent))
            {
                for (int i=0;i<count;i++)
                {
                    accessor.execute(parent, nodeId,query);
                }
            }
        }
        
    }
    
    @GET
    @Path("/bench2")
    @RequiredRoles()
    public UserPage bench2(Trace parent,@StateParam UserSession session) throws Throwable
    {
        TestGraph graph=this.service.getGraph();
        int count=5000;
        int threads=16;
        long nodeId;
        try (var transaction=graph.beginGraphTransaction(parent))
        {
            Castle castle=new Castle();
            castle.name="castle";
            Rock rock=new Rock();
            rock.name="rock";
            
            nodeId=transaction.createNode(castle,rock);
            transaction.commit();
        }

        
        Bench2[] benches=new Bench2[threads];
        for (int i=0;i<threads;i++)
        {
            benches[i]=new Bench2(graph, nodeId, count);
        }
        try (Trace trace=new Trace(parent,"bench2"))
        {
            Progress<?> progress=this.service.getMultiTaskScheduler().schedule(parent, "bench2", benches);
            progress.waitAll();
            double tps=count*threads/trace.getDurationS();
            UserPage page = new UserPage(null, null);
            page.body().addInner("duration:"+trace.getDurationMs()+",tps="+tps);
            return page;
        }
    }

}
