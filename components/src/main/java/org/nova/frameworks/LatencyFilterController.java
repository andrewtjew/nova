/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.frameworks;

import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.operator.LinkButton;
import org.nova.html.operator.MoreButton;
import org.nova.html.operator.NameValueList;
import org.nova.html.operator.OperatorDataTable;
import org.nova.html.operator.Panel3;
import org.nova.html.operator.TableHeader;
import org.nova.html.operator.TableRow;
import org.nova.html.tags.form_post;
import org.nova.html.tags.input_checkbox;
import org.nova.html.tags.input_hidden;
import org.nova.html.tags.input_number;
import org.nova.html.tags.input_submit;
import org.nova.html.tags.p;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.http.server.LatencyDescriptor;
import org.nova.http.server.LatencyFilter;
import org.nova.http.server.LatencyStatistics;
import org.nova.http.server.RequestHandler;
import org.nova.http.server.RequestHandlerLatencyTracker;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.tracing.Trace;

@ContentWriters({HtmlElementWriter.class, JSONContentWriter.class})
@ContentReaders({JSONContentReader.class})
@ContentDecoders(GzipContentDecoder.class)
@ContentEncoders(GzipContentEncoder.class)
public class LatencyFilterController
{
    final private ServerApplication application;
    final private LatencyFilter latencyFilter;
    
    public LatencyFilterController(ServerApplication application,LatencyFilter latencyFilter)
    {
        this.latencyFilter=latencyFilter;
        this.application=application;
    }
    
    
    @GET
    @Path("/operator/latency/view")
    public Element view(Trace parent) throws Throwable
    {
        OperatorPage page=this.application.buildOperatorPage("View and Set Handler Latencies");
        {
            Panel3 panel=page.content().returnAddInner(new Panel3(page.head(),"Master Enable"));
            form_post form=panel.content().returnAddInner(new form_post());
            form.action("/operator/latency/enable");
            form.addInner(new input_checkbox().name("enable").checked(this.latencyFilter.isEnabled()));
            form.addInner(new input_submit().value("Set"));
            
        }
        {
            
            Panel3 panel=page.content().returnAddInner(new Panel3(page.head(),"Override Settings"));
            form_post form=panel.content().returnAddInner(new form_post());
            form.action("/operator/latency/override");
            NameValueList list=form.returnAddInner(new NameValueList());
            LatencyDescriptor descriptor=this.latencyFilter.getOverrideLatencyDescriptor();
            if (descriptor==null)
            {
                descriptor=new LatencyDescriptor(false, 0, 0, 0, 0);
            }
            String style="width:5em;";
            list.add("Enable", new input_checkbox().name("enable").checked(descriptor.isEnabled()));
            list.add("Before Execute Minimum Latency (ms)", new input_number().style(style).min(0).name("beforeExecuteMinimumLatencyMs").value(descriptor.getBeforeExecuteMinimumMs()));
            list.add("Before Execute Maximum Latency (ms)", new input_number().style(style).min(0).name("beforeExecuteMaximumLatencyMs").value(descriptor.getBeforeExecuteMaximumMs()));
            list.add("AFter Execute Minimum Latency (ms)", new input_number().style(style).min(0).name("afterExecuteMinimumLatencyMs").value(descriptor.getAfterExecuteMinimumMs()));
            list.add("After Execute Maximum Latency (ms)", new input_number().style(style).min(0).name("afterExecuteMaximumLatencyMs").value(descriptor.getAfterExecuteMaximumMs()));
            list.add(null,new input_submit().value("Set"));
        }
        {
            Panel3 panel=page.content().returnAddInner(new Panel3(page.head(),"Handler Latencies"));
            OperatorDataTable table=panel.content().returnAddInner(new OperatorDataTable(page.head()));
            table.lengthMenu(-1,40,60,100);
            TableHeader header=new TableHeader();
            header.add("Method","Path","Count","Total Before Execute latencies (ms)","Total After Execute latencies (ms)","Enable","Before Execute Minimum Latency (ms)","Before Execute Maximum Latency (ms)","After Execute Minimum Latency (ms)","After Execute Maximum Latency (ms)","");
            table.setHeader(header);
            RequestHandlerLatencyTracker[] trackers=this.latencyFilter.getSnapshot();
            for (RequestHandlerLatencyTracker tracker:trackers)
            {
                RequestHandler requestHandler=tracker.getRequestHandler();
                TableRow tr=table.returnAddRow();

                LatencyDescriptor descriptor=tracker.getLatencyDescriptor();
                if (descriptor==null)
                {
                    descriptor=new LatencyDescriptor(false,0,0,0,0);
                }
                
                LatencyStatistics stats=tracker.getLatencyStatistics();
                tr.add(requestHandler.getHttpMethod());
                tr.add(requestHandler.getPath());
                tr.add(stats.getUpdateCount());
                tr.add(stats.getTotalBeforeExecuteLatencyMs());
                tr.add(stats.getTotalAfterExecuteLatencyMs());
                tr.add(new input_checkbox().disabled().checked(descriptor.isEnabled()));
                tr.add(descriptor.getBeforeExecuteMinimumMs());
                tr.add(descriptor.getBeforeExecuteMaximumMs());
                tr.add(descriptor.getAfterExecuteMinimumMs());
                tr.add(descriptor.getAfterExecuteMaximumMs());
                tr.add(new MoreButton(page.head(), new PathAndQuery("/operator/latency/set").addQuery("key", requestHandler.getKey()).toString()));
            }
        }        
        return page;
    }
    
    
    
    
    @POST
    @Path("/operator/latency/enable")
    public Element enable(Trace parent,@QueryParam("enable") boolean enable) throws Throwable
    {
        OperatorPage page=this.application.buildOperatorPage("Master Latency Setting");
        this.latencyFilter.setEnabled(enable);
        if (enable)
        {
            page.content().addInner("Latency is enabled.");
        }
        else
        {
            page.content().addInner("Latency is disabled.");
        }
        page.content().addInner(new p());
        page.content().addInner(new LinkButton(page.head(),"/operator/latency/view",new LiteralHtml("&#129104; Back")));
        return page;
    }
    
    @POST
    @Path("/operator/latency/override")
    public Element enable(Trace parent
            ,@QueryParam("enable") boolean enable
            ,@QueryParam("beforeExecuteMinimumLatencyMs") long beforeExecuteMinimumLatencyMs 
            ,@QueryParam("beforeExecuteMaximumLatencyMs") long beforeExecuteMaximumLatencyMs 
            ,@QueryParam("afterExecuteMinimumLatencyMs") long afterExecuteMinimumLatencyMs 
            ,@QueryParam("afterExecuteMaximumLatencyMs") long afterExecuteMaximumLatencyMs 
            ) throws Throwable
    {
        OperatorPage page=this.application.buildOperatorPage("Override Latency Settings");
        LatencyDescriptor descriptor=new LatencyDescriptor(enable, beforeExecuteMinimumLatencyMs, beforeExecuteMaximumLatencyMs, afterExecuteMinimumLatencyMs, afterExecuteMaximumLatencyMs);
        this.latencyFilter.setOverrideLatencyDescriptor(descriptor);

        NameValueList list=page.content().returnAddInner(new NameValueList());
        String style="width:5em;";
        list.add("Enable", new input_checkbox().name("enable").disabled().checked(descriptor.isEnabled()));
        list.add("Before Execute Minimum Latency (ms)", descriptor.getBeforeExecuteMinimumMs());
        list.add("Before Execute Maximum Latency (ms)", descriptor.getBeforeExecuteMaximumMs());
        list.add("After Execute Minimum Latency (ms)", descriptor.getAfterExecuteMinimumMs());
        list.add("After Execute Maximum Latency (ms)", descriptor.getAfterExecuteMaximumMs());
        page.content().addInner(new p());
        page.content().addInner(new LinkButton(page.head(),"/operator/latency/view",new LiteralHtml("&#129104; Back")));
        return page;
    }
    
    @GET
    @Path("/operator/latency/set")
    public Element set(Trace parent,@QueryParam("key") String key) throws Throwable
    {
        RequestHandlerLatencyTracker tracker=this.latencyFilter.getRequestHandlerLatencyTracker(key);
        RequestHandler requestHandler=tracker.getRequestHandler();
        OperatorPage page=this.application.buildOperatorPage("Set Latency: "+requestHandler.getHttpMethod()+" "+requestHandler.getPath());

        LatencyDescriptor descriptor=tracker.getLatencyDescriptor();
        if (descriptor==null)
        {
            descriptor=new LatencyDescriptor(false,0,0,0,0);
        }

        form_post form=page.content().returnAddInner(new form_post());
        form.action("/operator/latency/set");
        form.addInner(new input_hidden().name("key").value(key));
        NameValueList list=form.returnAddInner(new NameValueList());
        String style="width:5em;";
        list.add("Enable", new input_checkbox().name("enable").checked(descriptor.isEnabled()));
        list.add("Before Execute Minimum Latency (ms)", new input_number().style(style).min(0).name("beforeExecuteMinimumLatencyMs").value(descriptor.getBeforeExecuteMinimumMs()));
        list.add("Before Execute Maximum Latency (ms)", new input_number().style(style).min(0).name("beforeExecuteMaximumLatencyMs").value(descriptor.getBeforeExecuteMaximumMs()));
        list.add("After Execute Minimum Latency (ms)", new input_number().style(style).min(0).name("afterExecuteMinimumLatencyMs").value(descriptor.getAfterExecuteMinimumMs()));
        list.add("After Execute Maximum Latency (ms)", new input_number().style(style).min(0).name("afterExecuteMaximumLatencyMs").value(descriptor.getAfterExecuteMaximumMs()));
        list.add(null,new input_submit().value("Set"));
        return page;
    }
    
    @POST
    @Path("/operator/latency/set")
    public Element doSet(Trace parent
            ,@QueryParam("key") String key
            ,@QueryParam("enable") boolean enable
            ,@QueryParam("beforeExecuteMinimumLatencyMs") long beforeExecuteMinimumLatencyMs 
            ,@QueryParam("beforeExecuteMaximumLatencyMs") long beforeExecuteMaximumLatencyMs 
            ,@QueryParam("afterExecuteMinimumLatencyMs") long afterExecuteMinimumLatencyMs 
            ,@QueryParam("afterExecuteMaximumLatencyMs") long afterExecuteMaximumLatencyMs 
            ) throws Throwable
    {
        RequestHandlerLatencyTracker tracker=this.latencyFilter.getRequestHandlerLatencyTracker(key);
        RequestHandler requestHandler=tracker.getRequestHandler();
        OperatorPage page=this.application.buildOperatorPage("Set Latency: "+requestHandler.getHttpMethod()+" "+requestHandler.getPath());
        LatencyDescriptor descriptor=new LatencyDescriptor(enable, beforeExecuteMinimumLatencyMs, beforeExecuteMaximumLatencyMs, afterExecuteMinimumLatencyMs, afterExecuteMaximumLatencyMs);
        tracker.setLatencyDescriptor(descriptor);

        NameValueList list=page.content().returnAddInner(new NameValueList());
        String style="width:5em;";
        list.add("Enable", new input_checkbox().name("enable").disabled().checked(descriptor.isEnabled()));
        list.add("Before Execute Minimum Latency (ms)", descriptor.getBeforeExecuteMinimumMs());
        list.add("Before Execute Maximum Latency (ms)", descriptor.getBeforeExecuteMaximumMs());
        list.add("After Execute Minimum Latency (ms)", descriptor.getAfterExecuteMinimumMs());
        list.add("After Execute Maximum Latency (ms)", descriptor.getAfterExecuteMaximumMs());
        page.content().addInner(new p());
        page.content().addInner(new LinkButton(page.head(),"/operator/latency/view",new LiteralHtml("&#129104; Back")));
        return page;
    }
    
}
