package org.nova.services;

import java.time.format.DateTimeFormatter;

import org.nova.frameworks.OperatorPage;
import org.nova.frameworks.ServerOperatorPages.OperatorTable;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.operator.MenuBar;
import org.nova.html.operator.NameValueList;
import org.nova.html.properties.Length_;
import org.nova.html.properties.Unit_;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.HtmlContentWriter;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Path;
import org.nova.tracing.Trace;


@ContentDecoders(GzipContentDecoder.class)
@ContentEncoders(GzipContentEncoder.class)
@ContentWriters({HtmlContentWriter.class, HtmlElementWriter.class})

public class OperatorController
{
    final private MessageRouter messageRouter;
    public OperatorController(MessageRouter messageRouter) throws Exception
    {
        this.messageRouter=messageRouter;
        MenuBar menuBar=this.messageRouter.getMenuBar();
        menuBar.add("/routes/view", "Routes","View Routes");
        menuBar.add("/servers/view", "Servers","View Servers");
    }
    @GET
    @Path("/routes/view")
    public Element viewRoutes(Trace parent) throws Throwable
    {
        OperatorPage page=this.messageRouter.buildOperatorPage("Routes");
        OperatorTable table=page.content().returnAddInner(new OperatorTable(page.head()));
        var entries=this.messageRouter.getEndPoints();
        for (var entry:entries.entrySet())
        {
            table.addRow(entry.getKey(),entry.getValue());
        }
        return page;        
    }
    
    static String toGB(long bytes)
    {
        double gb=bytes/1024.0/1024.0/1024.0;
        return String.format("%.2f GB",gb);
    }

    static String toPercentage(long amount,long total)
    {
        double percentage=amount*100.0/total;
        return String.format("%.2f%%",percentage);
    }

    static String toPercentage(double amount)
    {
        return String.format("%.2f%%",amount*100.0);
    }
    
    
    static String toGhz(long frequency)
    {
        double ghz=frequency/1000000000.0;
        return String.format("%.2f GHz",ghz);
    } 
    
    
    
    @GET
    @Path("/servers/view")
    public Element viewServers(Trace parent) throws Throwable
    {
        OperatorPage page=this.messageRouter.buildOperatorPage("Servers");
        OperatorTable table=page.content().returnAddInner(new OperatorTable(page.head()));
        table.setHeader("Server","Last Updated","Computer","Processor","Physical Memory","Runtime Memory","StorageCapacity");
        var entries=this.messageRouter.getServers();
        for (var entry:entries.entrySet())
        {
            var value=entry.getValue();
            var status=value.status();
            

            NameValueList computer=new NameValueList(new Length_(6,Unit_.em),false);
            computer.add("Name:",status.computerName);
            computer.add("Model:",status.computerModel);
            computer.add("Processor:",status.processor);
            computer.add("Frequency:",toGhz(status.processorMaximumFrequency));

            NameValueList processor=new NameValueList(new Length_(12,Unit_.em),false);
            processor.add("Used:",toPercentage(MessageRouterService.adjustLoad(status,0.3)));
            processor.add("Load:",toPercentage(status.load));
            processor.add("Logical:",status.logicalProcessors);
            processor.add("Physical:",status.physicalProcessors);

            NameValueList physicalMemory=new NameValueList(new Length_(7,Unit_.em),false);
            physicalMemory.add("Used:",toPercentage(status.physicalMemoryCapacity-status.physicalMemoryAvailable,status.physicalMemoryCapacity));
            physicalMemory.add("Capacity:",toGB(status.physicalMemoryCapacity));
            physicalMemory.add("Available:",toGB(status.physicalMemoryAvailable));
            
            NameValueList runtimeMemory=new NameValueList(new Length_(7,Unit_.em),false);
            runtimeMemory.add("Used:",toPercentage(status.runtimeMemoryCapacity-status.runtimeMemoryAvailable,status.runtimeMemoryCapacity));
            runtimeMemory.add("Capacity:",toGB(status.runtimeMemoryCapacity));
            runtimeMemory.add("Available:",toGB(status.runtimeMemoryAvailable));

            NameValueList storage=new NameValueList(new Length_(7,Unit_.em),false);
            storage.add("Used:",toPercentage(status.storageCapacity-status.storageAvailable,status.storageCapacity));
            storage.add("Capacity:",toGB(status.storageCapacity));
            storage.add("Available:",toGB(status.storageAvailable));
            

            
            var updated=value.lastUpdated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            
            table.addRow(entry.getKey(),updated,
                    computer,
                    processor,
                    physicalMemory,
                    runtimeMemory,
                    storage
                    );
        }
        return page;        
    }
}
