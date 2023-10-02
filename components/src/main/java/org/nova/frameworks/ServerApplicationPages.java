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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.eclipse.jetty.http.HttpStatus;
import org.nova.annotations.Description;
import org.nova.concurrent.Progress;
import org.nova.concurrent.TimerTask;
import org.nova.configuration.Configuration;
import org.nova.configuration.ConfigurationItem;
import org.nova.flow.Tapper;
import org.nova.html.tags.a;
import org.nova.html.tags.br;
import org.nova.html.tags.button_button;
import org.nova.html.tags.button_submit;
import org.nova.html.tags.div;
import org.nova.html.tags.fieldset;
import org.nova.html.tags.form_get;
import org.nova.html.tags.form_post;
import org.nova.html.tags.h3;
import org.nova.html.tags.hr;
import org.nova.html.tags.input_checkbox;
import org.nova.html.tags.input_hidden;
import org.nova.html.tags.input_number;
import org.nova.html.tags.input_reset;
import org.nova.html.tags.input_submit;
import org.nova.html.tags.input_text;
import org.nova.html.tags.label;
import org.nova.html.tags.legend;
import org.nova.html.tags.meta;
import org.nova.html.tags.p;
import org.nova.html.tags.span;
import org.nova.html.tags.strong;
import org.nova.html.tags.style;
import org.nova.html.tags.td;
import org.nova.html.tags.textarea;
import org.nova.html.tags.th;
import org.nova.html.tags.tr;
import org.nova.http.Header;
import org.nova.http.client.HttpClientConfiguration;
import org.nova.http.client.HttpClientFactory;
import org.nova.http.client.JSONClient;
import org.nova.http.client.PathAndQuery;
import org.nova.http.client.TextResponse;
import org.nova.http.server.CSharpClassWriter;
import org.nova.http.server.ContentDecoder;
import org.nova.http.server.ContentEncoder;
import org.nova.http.server.ContentReader;
import org.nova.http.server.ContentWriter;
import org.nova.http.server.Context;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.Filter;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.HttpServer;
import org.nova.http.server.HttpTransport;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.http.server.JSONPatchContentReader;
import org.nova.http.server.ParameterInfo;
import org.nova.http.server.ParameterSource;
import org.nova.http.server.Queries;
import org.nova.http.server.RequestHandler;
import org.nova.http.server.RequestHandlerNotFoundLogEntry;
import org.nova.http.server.RequestLogEntry;
import org.nova.http.server.Response;
import org.nova.http.server.TypeScriptClassWriter;
import org.nova.http.server.HtmlContentWriter;
import org.nova.html.attributes.Color;
import org.nova.html.attributes.Size;
import org.nova.html.attributes.Style;
import org.nova.html.attributes.unit;
import org.nova.html.deprecated.th_title;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.elements.NodeElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.enums.http_equiv;
import org.nova.html.ext.BasicPage;
import org.nova.html.ext.Content;
import org.nova.html.ext.Head;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.InputHidden;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.ext.Redirect;
import org.nova.html.ext.Refresher;
import org.nova.html.ext.SelectEnums;
import org.nova.html.jsTree.TreeNode;
import org.nova.html.jsTree.Tree;
import org.nova.html.operator.Accordion;
import org.nova.html.operator.AjaxButton;
import org.nova.html.operator.AjaxQueryResult;
import org.nova.html.operator.AjaxQueryResultWriter;
import org.nova.html.operator.ClearTimeout;
import org.nova.html.operator.ConfirmButton;
import org.nova.html.operator.DisableElementToggler;
import org.nova.html.operator.HttpRequestWidget;
import org.nova.html.operator.MenuBar;
import org.nova.html.operator.MoreButton;
import org.nova.html.operator.NameInputValueList;
import org.nova.html.operator.NameValueList;
import org.nova.html.operator.OperatorDataTable;
import org.nova.html.operator.OperatorUtils;
import org.nova.html.operator.Panel;
import org.nova.html.operator.Panel1;
import org.nova.html.operator.Panel2;
import org.nova.html.operator.Panel3;
import org.nova.html.operator.Panel4;
import org.nova.html.operator.SelectOptions;
import org.nova.html.operator.Table;
import org.nova.html.operator.TableHeader;
import org.nova.html.operator.TableRow;
import org.nova.html.operator.TitleText;
import org.nova.html.operator.TraceWidget;
import org.nova.html.remote.Inputs;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.http.server.annotations.ParamName;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.DefaultValue;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Log;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.PathParam;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.Test;
import org.nova.logging.Item;
import org.nova.logging.HighPerformanceLogger;
import org.nova.logging.LogDirectoryInfo;
import org.nova.logging.LogDirectoryManager;
import org.nova.logging.LogEntry;
import org.nova.logging.Logger;
import org.nova.logging.SourceQueueLogger;
import org.nova.metrics.CountMeter;
import org.nova.metrics.CountSample;
import org.nova.metrics.LevelMeter;
import org.nova.metrics.LevelSample;
import org.nova.metrics.LongValueMeter;
import org.nova.metrics.LongValueSample;
import org.nova.metrics.MeterAttribute;
import org.nova.metrics.MeterAttributeValue;
import org.nova.metrics.MeterStore;
import org.nova.metrics.PrecisionTimer;
import org.nova.metrics.RateMeter;
import org.nova.metrics.RateSample;
import org.nova.metrics.RecentSourceEventMeter;
import org.nova.metrics.RecentSourceEventSample;
import org.nova.metrics.SamplingMethod;
import org.nova.metrics.SourceEvent;
import org.nova.metrics.StackTraceNode;
import org.nova.metrics.ThreadExecutionProfiler;
import org.nova.metrics.ThreadExecutionSample;
import org.nova.metrics.TraceSample;
import org.nova.operations.OperatorVariable;
import org.nova.operations.Status;
import org.nova.operations.ValidationResult;
import org.nova.operations.VariableInstance;
import org.nova.security.Vault;
import org.nova.testing.Debugging;
import org.nova.tracing.CategorySample;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;
import org.nova.tracing.TraceNode;
import org.nova.utils.DateTimeUtils;
import org.nova.utils.FileUtils;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

import com.google.common.base.Strings;

@Description("Handlers for server operator pages")
@ContentDecoders(GzipContentDecoder.class)
@ContentEncoders({DeflaterContentEncoder.class,GzipContentEncoder.class})
@ContentReaders({ JSONContentReader.class, JSONPatchContentReader.class })
@ContentWriters({HtmlContentWriter.class, HtmlElementWriter.class,RemoteResponseWriter.class})
public class ServerApplicationPages
{
    static public class OperatorTable extends OperatorDataTable
    {
        public OperatorTable(Head head) throws Throwable
        {
            super(head);
            this.lengthMenu(-1,20,30,40,50);
        }
    }

    static public class WideTable extends Table
    {
        public WideTable(Head head)
        {
            super(head);
            table().style("width:100%;");
        }
    }
    final static LiteralHtml WARNING=new LiteralHtml("&#9888;");
    
    public final ServerApplication serverApplication;

    @org.nova.operations.OperatorVariable(description = "Sampling duration (seconds)", minimum = "0.1")
    private double rateSamplingDuration = 10;

    @org.nova.operations.OperatorVariable(description = "cache max-age in (seconds)", minimum = "1")
    private int cacheMaxAge = 300;

    @OperatorVariable(description = "cache control value returned to client other than max-age (e.g.: no-transform, public)")
    private String cacheControlValue = "public";

    public ServerApplicationPages(ServerApplication serverApplication,String namespace) throws Throwable
    {
        this.namespace=namespace;
        this.rateSamplingDuration = serverApplication.getConfiguration().getDoubleValue("ServerOperatorPages.meters.rateSamplingDuration", 10);
        this.cacheMaxAge = serverApplication.getConfiguration().getIntegerValue("ServerOperatorPages.cache.maxAgeS", 300);
        this.cacheControlValue = serverApplication.getConfiguration().getValue("ServerOperatorPages.cache.controlValue", "public");
        this.serverApplication = serverApplication;

        MenuBar menuBar=serverApplication.getMenuBar();
        menuBar.add("/","Environment","Status");
        menuBar.add("/operator/application/configuration","Environment","Configuration");
        menuBar.add("/operator/jvm","Environment","JVM");
        menuBar.addDivider("Environment");       
        menuBar.add("/operator/application/tasks","Environment","Tasks");
        menuBar.add("/operator/application/timers","Environment","Timer Tasks");
        menuBar.addDivider("Environment");       
        menuBar.add("/operator/environment/sourceEventBoard","Environment","Source Event Board");
        menuBar.add("/operator/application/meters","Environment","Meters");
        menuBar.addDivider("Environment");       
        menuBar.add("/operator/exception","Environment","Startup Exception");

        menuBar.add("/operator/tracing/currentTraceSummary?excludeWaiting=false","Tracing","Current Trace Summary");
//        menuBar.add("/operator/tracing/currentTraceSummary?excludeWaiting=true","Tracing","Current Non Waiting Trace Summary");
        menuBar.add("/operator/tracing/currentTraces","Tracing","Current Traces");
        menuBar.add("/operator/tracing/sampleCurrent?excludeWaiting=false","Tracing","Sample Current Traces");
        menuBar.add("/operator/tracing/sampleCurrent?excludeWaiting=true","Tracing","Sample Current Non Waiting Traces");
        
        menuBar.addDivider("Tracing");       
        menuBar.add("/operator/tracing/lastTraces","Tracing","Last Traces");
        menuBar.add("/operator/tracing/sampleLast","Tracing","Sample Last Traces");
        menuBar.add("/operator/tracing/sampleAndResetLast","Tracing","Sample and Reset Last Traces");
        menuBar.add("/operator/tracing/sampleLastTraceBuffer","Tracing","Sample Last Trace Buffer");
        menuBar.add("/operator/tracing/lastExceptions","Tracing","Last Exception Traces");

        menuBar.add("/operator/tracing/traceTree","Tracing","Trace Tree");
        
        menuBar.add("/operator/tracing/sampleAll","Tracing","Sample All Trace Categories");
        
        menuBar.addDivider("Tracing");
        menuBar.add("/operator/tracing/watchList","Tracing","Watch","Set Watch Categories");
        menuBar.add(false,"/operator/tracing/sampleWatch","Tracing","Watch","Sample Watch Traces");
        menuBar.add(false,"/operator/tracing/sampleAndResetWatch","Tracing","Watch","Sample and Reset Watch Traces");
        menuBar.add(false,"/operator/tracing/sampleWatchTraceBuffer","Tracing","Watch","Sample Watch Trace Buffer");
        menuBar.add(false,"/operator/tracing/watchListLastTraces","Tracing","Watch","Last Watch Traces");

        menuBar.addDivider("Tracing");
        menuBar.add("/operator/tracing/secondaryCategories","Tracing","Secondary Categories","Set Secondary Categories");
        menuBar.add("/operator/tracing/sampleLastSecondary","Tracing","Secondary Categories","Sample Secondary Last Traces");
        menuBar.add("/operator/tracing/sampleAndResetLastSecondary","Tracing","Secondary Categories","Sample and Reset Secondary Last Traces");
        menuBar.add("/operator/tracing/lastSecondaryExceptions","Tracing","Secondary Categories","Last Secondary Exception Traces");
        
        
        menuBar.addDivider("Tracing");
        menuBar.add("/operator/tracing/stats","Tracing","TraceManager","Stats");
        menuBar.add("/operator/tracing/settings","Tracing","TraceManager","Settings");

        menuBar.add("/operator/logging/status","Logging","Status");
        menuBar.add("/operator/logging/categories","Logging","Category Loggers");
//        menuBar.add("/operator/logging/files","Logging","Log Files");

        menuBar.add("/operator/httpServer/status/public","Servers","Public","Status");
        menuBar.add("/operator/httpServer/performance/public","Servers","Public","Last Performance");
        menuBar.add("/operator/httpServer/allPerformance/public","Servers","Public","All Performance");
        menuBar.add("/operator/httpServer/lastRequests/public","Servers","Public","Last Requests");
        menuBar.add("/operator/httpServer/lastExceptionRequests/public","Servers","Public","Last Requests With Exceptions");
        menuBar.add("/operator/httpServer/lastNotFounds/public","Servers","Public","Last Not Founds (404s)");
        menuBar.add("/operator/httpServer/methods/public","Servers","Public","Methods");
        menuBar.add("/operator/httpServer/classDefinitions/public","Servers","Public","Class Definitions");

        menuBar.add("/operator/httpServer/status/private","Servers","Private","Status");
        menuBar.add("/operator/httpServer/performance/private","Servers","Private","Last Performance");
        menuBar.add("/operator/httpServer/allPerformance/private","Servers","Private","All Performance");
        menuBar.add("/operator/httpServer/lastRequests/private","Servers","Private","Last Requests");
        menuBar.add("/operator/httpServer/lastExceptionRequests/private","Servers","Private","Last Requests With Exceptions");
        menuBar.add("/operator/httpServer/lastNotFounds/private","Servers","Private","Last Not Founds (404s)");
        menuBar.add("/operator/httpServer/methods/private","Servers","Private","Methods");
        menuBar.add("/operator/httpServer/classDefinitions/private","Servers","Private","Class Definitions");
        
        menuBar.add("/operator/httpServer/status/operator","Servers","Operator","Status");
        menuBar.add("/operator/httpServer/performance/operator","Servers","Operator","Last Performance");
        menuBar.add("/operator/httpServer/allPerformance/operator","Servers","Operator","All Performance");
        menuBar.add("/operator/httpServer/lastRequests/operator","Servers","Operator","Last Requests");
        menuBar.add("/operator/httpServer/lastExceptionRequests/operator","Servers","Operator","Last Requests With Exceptions");
        menuBar.add("/operator/httpServer/lastNotFounds/operator","Servers","Operator","Last Not Founds (404s)");
        menuBar.add("/operator/httpServer/methods/operator","Servers","Operator","Methods");
        menuBar.add("/operator/httpServer/classDefinitions/operator","Servers","Operator","Class Definitions");

//        menuBar.add("/operator/variables/view","Variables","View");
//        menuBar.add("/operator/variables/modify","Variables","Modify");

        serverApplication.getOperatorVariableManager().register(serverApplication.getTraceManager());
        serverApplication.getOperatorVariableManager().register(this);
    }

    @GET
    @Path("/operator/noStartupExceptions")
    public Element noStartupExceptions() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Startup Exceptions");
        page.content().addInner("No exceptions");
        return page;
    }
    
    @GET
    @Path("/operator/application/configuration")
    public Element configuration() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Configuration"); 
        OperatorDataTable table=page.content().returnAddInner(new OperatorTable(page.head()));
        
        table.setHeader("Name","Value","Source");
        for (ConfigurationItem item : this.serverApplication.getConfiguration().getConfigurationItemSnapshot())
        {
            TableRow row=new TableRow();
            		
            row.add(new td().addInner(item.getName()).title(item.getDescription()));
            if (item.getName().startsWith("$"))
            {
            	row.add(new td().addInner("Hidden").style("color:gray;"));
            }
            else
            {
            	row.add(new td().addInner(item.getValue()).style("word-wrap:break-word;word-break:break-all;"));
            }
            row.add(new td().addInner(item.getSource()).title(item.getSourceContext()));
            table.addRow(row);
        }
        return page;
    }
    
    
    static class Meters
    {
        final TreeMap<String,MeterAttributeValue> countMeterAttributeValues;
        final TreeMap<String,MeterAttributeValue> levelMeterAttributeValues;
        final TreeMap<String,MeterAttributeValue> rateMeterAttributeValues;
        final TreeMap<String,MeterAttributeValue> longValueMeterAttributeValues;
        final TreeMap<String,MeterAttributeValue> recentSourceEventMeterAttributeValues;
        
        Meters(List<MeterAttributeValue> list)
        {
            this.countMeterAttributeValues=new TreeMap<>();
            this.levelMeterAttributeValues=new TreeMap<>();
            this.rateMeterAttributeValues=new TreeMap<>();
            this.longValueMeterAttributeValues=new TreeMap<>();
            this.recentSourceEventMeterAttributeValues=new TreeMap<>();
            
            for (MeterAttributeValue item:list)
            {
                if (item.getCountMeter()!=null)
                {
                    countMeterAttributeValues.put(item.getPath(), item);
                }
                else if (item.getLevelMeter()!=null)
                {
                    levelMeterAttributeValues.put(item.getPath(), item);
                }
                else if (item.getRateMeter()!=null)
                {
                    rateMeterAttributeValues.put(item.getPath(), item);
                }
                else if (item.getLongValueMeter()!=null)
                {
                    longValueMeterAttributeValues.put(item.getPath(), item);
                }
                else if (item.getRecentStateEventMeter()!=null)
                {
                    recentSourceEventMeterAttributeValues.put(item.getPath(), item);
                }
            }
        }
    
    }
    
    @GET
    @Path("/operator/application/meters")
    public Element meters(@DefaultValue("/") @QueryParam("path") String path) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Meters from path:"+path);
        
        MeterStore store=this.serverApplication.getMeterStore();
        List<MeterAttributeValue> list=store.getAllMeterAttributeValues(path);

        Meters meters=new Meters(list);
        writeMeters(meters,page.head(),page.content());
        
        return page;
    }

    private div buildPath(MeterAttributeValue av) throws Exception
    {
        MeterAttribute attribute=av.getAttribute();
        div div=new div();
        if (attribute.getDescription()!=null)
        {
            div.title(attribute.getDescription());
        }
        StringBuilder sb=new StringBuilder();
        for (String pathElement:av.getPathElements())
        {
            div.addInner("/");
            sb.append("/"+pathElement);
            a a=div.returnAddInner(new a());
            a.href(new PathAndQuery("/operator/application/meters").addQuery("path",sb.toString()).toString());
            a.addInner(pathElement);
        }
        return div;
    }
    
    
    public void writeMeters(Meters meters,Head head,NodeElement<?> element) throws Throwable
    {
        if (meters.countMeterAttributeValues.size()>0)
        {
            Accordion accordion=element.returnAddInner(new Accordion(head, null, true,"Count Meters"));
            OperatorDataTable table=accordion.content().returnAddInner(new OperatorTable(head));
            table.setHeader("Path","Count");
            for (MeterAttributeValue av:meters.countMeterAttributeValues.values())
            {
                TableRow row=new TableRow();
                row.add(buildPath(av));
                CountSample sample=av.getCountMeter().sample();
                row.add(sample.getCount());
                table.addRow(row);
            }
        }
        if (meters.levelMeterAttributeValues.size()>0)
        {
            Accordion accordion=element.returnAddInner(new Accordion(head, null, true,"Level Meters"));
            OperatorDataTable table=accordion.content().returnAddInner(new OperatorTable(head));
            table.setHeader("Path","Level","Base","Min","Min Instant","Max","Max Instant");
            for (MeterAttributeValue av:meters.levelMeterAttributeValues.values())
            {
                TableRow row=new TableRow();
                row.add(buildPath(av));
                LevelSample sample=av.getLevelMeter().sample();
                row.add(sample.getLevel());
                row.add(sample.getBaseLevel());
                row.add(sample.getMinLevel());
                row.add(sample.getMinLevel()<sample.getBaseLevel()?DateTimeUtils.toSystemDateTimeString(sample.getMinLevelInstantMs()):"");
                row.add(sample.getMaxLevel());
                row.add(sample.getMaxLevel()>sample.getBaseLevel()?DateTimeUtils.toSystemDateTimeString(sample.getMaxLevelInstantMs()):"");
                table.addRow(row);
            }
        }
        if (meters.rateMeterAttributeValues.size()>0)
        {
            Accordion accordion=element.returnAddInner(new Accordion(head, null, true,"Rate Meters"));
            OperatorDataTable table=accordion.content().returnAddInner(new OperatorTable(head));
            table.setHeader("Path","Rate","Total","Samples");
            for (MeterAttributeValue av:meters.rateMeterAttributeValues.values())
            {
                TableRow row=new TableRow();
                row.add(buildPath(av));
                RateSample sample=av.getRateMeter().sample();
                row.add(sample.getRate());
                row.add(sample.getTotalCount());
                row.add(sample.getSamples());
                table.addRow(row);
            }
        }
        if (meters.longValueMeterAttributeValues.size()>0)
        {
            Accordion accordion=element.returnAddInner(new Accordion(head, null, true,"Long Value Meters"));
            OperatorDataTable table=accordion.content().returnAddInner(new OperatorTable(head));
            table.setHeader("Path","Value","Average","Deviation","Rate","Min","Max","Samples");
            for (MeterAttributeValue av:meters.longValueMeterAttributeValues.values())
            {
                TableRow row=new TableRow();
                row.add(buildPath(av));
                LongValueSample sample=av.getLongValueMeter().sample();
                row.add(sample.getSamples()>0?sample.getValue():"");
                row.add(sample.getSamples()>=1?sample.getAverage():"");
                row.add(sample.getSamples()>=2?sample.getStandardDeviation():"");
                row.add(sample.getSamples()>=1?sample.getRate():"");
                row.add(sample.getSamples()>0?sample.getMin():"");
                row.add(sample.getSamples()>0?sample.getMax():"");
                row.add(sample.getSamples());
                table.addRow(row);
            }
        }
        if (meters.recentSourceEventMeterAttributeValues.size()>0)
        {
            Accordion accordion=element.returnAddInner(new Accordion(head, null, true,"Count Meters"));
            OperatorDataTable table=accordion.content().returnAddInner(new OperatorTable(head));
            table.setHeader("Path","Most recent","Most recent instant","State 1","Instant 1","State 2","Instant 2","Count");
            for (MeterAttributeValue av:meters.recentSourceEventMeterAttributeValues.values())
            {
                TableRow row=new TableRow();
                row.add(buildPath(av));
                RecentSourceEventSample sample=av.getRecentStateEventMeter().sample();
                List<SourceEvent> events=sample.getEvents();
                int show=events.size();
                if (show>3)
                {
                    show=3;
                }
                for (int i=0;i<show;i++)
                {
                    SourceEvent event=events.get(i);
                    Object state=event.getState();
                    
                    row.add(state!=null?state.toString():"");
                    row.add(Utils.millisToLocalDateTime(event.getInstantMs()));
                }
                for (int i=show;i<3;i++)
                {
                    row.add("","");
                }
                row.add(sample.getCount());
                
                table.addRow(row);
            }
        }
    }

    @GET
    @Path("/operator/application/tasks")
    public Element futures() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Tasks");
        OperatorDataTable table=page.content().returnAddInner(new OperatorTable(page.head()));

        table.setHeader("Category","Number","Duration","Waiting","Executing","Completed");
        Progress<?>[] array = this.serverApplication.getMultiTaskScheduler().getProgressSnapshot();
        long now=System.currentTimeMillis();
        for (Progress<?> item : array)
        {
            table.addRow(item.getTrace().getCategory(),item.getTrace().getNumber()
                    ,Utils.millisToNiceDurationString((now-item.getTrace().getCreatedMs())),item.getWaiting(),item.getExecuting(),item.getCompleted());
        }
        return page;
    }

    @GET
    @Path("/operator/environment/sourceEventBoard")
    public Element statusBoard() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Source Event Board");
        OperatorDataTable table=page.content().returnAddInner(new OperatorTable(page.head()));
        table.lengthMenu(-1,20,40,60);

        
        table.setHeader("Name","State","Type","Instant","Source","Count");
        for (Entry<String, RecentSourceEventMeter> entry:this.serverApplication.getSourceEventBoard().getSnapshot().entrySet())
        {
            TableRow row=new TableRow();
            RecentSourceEventSample sample=entry.getValue().sample();
            SourceEvent event=sample.getEvents().get(0);
            Object state=event.getState();
            String type=state!=null?state.getClass().getSimpleName():"";

            row.add(entry.getKey());
            row.add(new TitleText(state!=null?state.toString():"",80));
            row.add(type,DateTimeUtils.toSystemDateTimeString(event.getInstantMs()),event.getStackTrace()[event.getStackTraceStartIndex()].toString(),sample.getCount());
            table.addRow(row);
        }
        
        return page;
    }
    
    @GET
    @Path("/operator/logging/capture")
    public Element captureLogging(@QueryParam("capacity") @DefaultValue("100") int capacity) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Capture Logs");
        HighPerformanceLogger logger=(HighPerformanceLogger)this.serverApplication.getCoreEnvironment().getLogQueue();
        form_get form=page.content().returnAddInner(new form_get());
        form.action("/operator/logging/capture");
        label label=form.returnAddInner(new label());
        input_checkbox checkBox=label.returnAddInner(new input_checkbox());
        checkBox.name("overwrite");
        label.addInner("Overwrite");
        form.addInner(new LiteralHtml("Buffer capacity:&nbsp;"));
        input_number capacityInput=form.returnAddInner(new input_number());
        capacityInput.name("capacity");
        capacityInput.min(1).max(1000).value(capacity);
        
        Tapper tapper=logger.getTapper();
        if (tapper.getTap()==null)
        {
            button_submit submit=form.returnAddInner(new button_submit());
            submit.addInner("Start");
        }
        return page;
    }
    @GET
    @Path("/operator/logging/files")
    public Element logFiles() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Log Files");
        form_post form=page.content().returnAddInner(new form_post()).action("/operator/logging/files/download");
        OperatorDataTable table=new OperatorDataTable(page.head());
        page.content().addInner(table);
        table.lengthMenu(20,30,40,-1);
        table.setHeader("Name","","","");
        table.sort(false);
        LogDirectoryManager directory=this.serverApplication.getLogDirectoryManager();
        File[] files=directory.getSnapshotOfFiles();
        int index=0;
        TableRow row=null;
        for (File file:files)
        {
            if (row==null)
            {
                index=0;
                row=new TableRow();
            }
            index++;
            td td=new td();
            String name=file.getName();
            input_checkbox checkbox=td.returnAddInner(new input_checkbox().name("file_"+name));
            td.addInner(new label().addInner(new LiteralHtml("&nbsp;"+name)).for_(checkbox));
            row.add(td);
            
            if (index==4)
            {
                table.addRow(row);
                row=null;
            }
        }
        if (row!=null)
        {
            for (;index<4;index++)
            {
                row.add("");
            }
            table.addRow(row);
        }
        return page;
    }
    @GET
    @Path("/operator/logging/categories")
    public Element viewLogCategories(@QueryParam("samplingInterval") @DefaultValue("10") double minimalResetDurationS) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Log Categories");
        OperatorDataTable table=page.content().returnAddInner(new OperatorTable(page.head()));

        table.setHeader("Category","Active","Count","Rate","Log Failures","Last Failure","");
        Logger[] loggers=this.serverApplication.getCoreEnvironment().getLoggers();
        for (Logger item : loggers)
        {
            TableRow row=new TableRow();
            row.add(item.getCategory());
            if (item instanceof SourceQueueLogger)
            {
                SourceQueueLogger sourceQueueLogger=(SourceQueueLogger)item;
                div div=new div();
                div.addInner(new LiteralHtml(sourceQueueLogger.isActive()+"&nbsp;"));
                if (sourceQueueLogger.isActive())
                {
                    div.addInner(new button_button()
                            .addInner("Disable")
                            .onclick(HtmlUtils
                            .js_location(new PathAndQuery("/operator/logging/category/status")
                                .addQuery("active", false)
                                .addQuery("category", sourceQueueLogger.getCategory())
                            )));
                }
                else
                {
                    div.addInner(new button_button()
                            .addInner("Enable")
                            .onclick(HtmlUtils
                            .js_location(new PathAndQuery("/operator/logging/category/status")
                                .addQuery("active", true)
                                .addQuery("category", sourceQueueLogger.getCategory())
                            )));
                }
                row.add(div);
                RateSample sample=sourceQueueLogger.getRateMeter().sample(minimalResetDurationS);
                row.add(sample.getSamples());
                row.add(sample.getRate());
                row.add(sourceQueueLogger.getLogFailures().getCount());
                Throwable exception=sourceQueueLogger.getLogFailureThrowable();
                if (exception!=null)
                {
                    row.add(Utils.toString(exception));
                }
                else
                {
                    row.add("");
                    
                }
                row.add(new RightMoreLink(page.head(),new PathAndQuery("/operator/logging/category/lastEntries").addQuery("category",item.getCategory()).toString()));
                
            }
            table.addRow(row);
        }
        return page;
    }
    @GET
    @Path("/operator/logging/category/status")
    public Element setLoggerCategoryStatus(@QueryParam("category") String category,@QueryParam("active") boolean active,@QueryParam("samplingInterval") @DefaultValue("10") double samplingInterval) throws Throwable
    {
        SourceQueueLogger logger=(SourceQueueLogger)this.serverApplication.getCoreEnvironment().getLogger(category);
        logger.setActive(active);
        return viewLogCategories(samplingInterval);
    }
    @GET
    @Path("/operator/logging/category/lastEntries")
    public Element viewLastEntries(Trace parent,@QueryParam("category") String category) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Last Entries: Category="+category);

        SourceQueueLogger logger=(SourceQueueLogger)this.serverApplication.getCoreEnvironment().getLogger(category);
        List<LogEntry> entries=logger.getLastLogEntries();
        for (LogEntry entry:entries)
        {
            Panel panel=page.content().returnAddInner(new Panel3(page.head(), "Number:"+entry.getNumber()));
            WideTable table=panel.content().returnAddInner(new WideTable(page.head()));
            TableHeader header=new TableHeader()
//                .addWithTitle("Number", "Sequence number")
                .add("Created")
                .add("LogLevel")
                .add("Message")
                ;
            table.setHeader(header);
            TableRow row=new TableRow();
//            row.addInner(new td().style("width:3em;").addInner(entry.getNumber()));
            row.add(new td().style("width:12em;").addInner(DateTimeUtils.toSystemDateTimeString(entry.getCreated())));
            row.add(new td().style("width:5em;").addInner(entry.getLogLevel()));
            row.add(entry.getMessage());
            table.addRow(row);
            
            if (entry.getItems().length>0)
            {
                //TwoColumnTable itemTable=panel.content().returnAddInner(new TwoColumnTable(":"));
                Table itemTable=panel.content().returnAddInner(new Table());
                for (Item item:entry.getItems())
                {
                    tr tr=itemTable.tbody().returnAddInner(new tr());
                    tr.addInner(new td().style("width:12em;").addInner(item.getName()));
                    if (item.getValue()!=null)
                    {
                        tr.addInner(new td().style("width:100%;").addInner(new textarea().style("width:100%;").readonly().addInner(HtmlUtils.toHtmlText(item.getValue()))));
                    }
                    else
                    {
                        tr.addInner(new td());
                    }
                }
            }
            
            if (entry.getException()!=null)
            {
                writeContent(page.head(),"Exception",page.content(),Utils.toString(entry.getException()),null);
            }
            
            if (entry.getTrace()!=null)
            {
                OperatorUtils.writeTrace(page.head(),panel.content(),entry.getTrace(),true);
            }
            
        }
        return page;
    }

    @GET
    @Path("/operator/application/timers")
    public Element timers() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Timers");
        OperatorDataTable table=page.content().returnAddInner(new OperatorTable(page.head()));
        table.setHeader("Category"
                ,new th_title("ID", "Timer ID Number")
                ,"Created","Status","Due","Countdown","Duration"
                ,new th_title("Mode","Timer scheduling mode")
                ,new th_title("Delay","Days hours:minutes:seconds.milliseconds")
                ,new th_title("Period","Days hours:minutes:seconds.milliseconds")
                ,new th_title("\u231B", "Number of attempts")
                ,new th_title("\u2705", "Number of successful executions")
                ,new th_title("\u274C", "Number of misses")
                ,new th_title("\u26A0", "Number of exceptions")
                );


        long now = System.currentTimeMillis();
        TimerTask[] timerTasks = this.serverApplication.getTimerScheduler().getTimerTaskSnapshot();
        for (TimerTask timerTask : timerTasks)
        {
            StackTraceElement element=timerTask.getSource();
            String source=element.getClassName()+"("+element.getLineNumber()+")";
            table.addRow(new TitleText(source,timerTask.getCategory())
                    ,timerTask.getNumber()
                    ,DateTimeUtils.toSystemDateTimeString(timerTask.getCreated())
                    ,timerTask.getExecutableStatus()
                    ,DateTimeUtils.toSystemDateTimeString(timerTask.getDue())
                    ,Utils.millisToDurationString(timerTask.getDue() - now)
                    ,Utils.nanosToDurationString(timerTask.getTotalDuration())
                    ,timerTask.getShedulingMode()
                    ,Utils.millisToDurationString(timerTask.getDelay())
                    ,Utils.millisToDurationString(timerTask.getPeriod())
                    ,timerTask.getAttempts()
                    ,timerTask.getSuccesses()
                    ,timerTask.getMisses()
                    ,timerTask.getThrowables()
                    );
        }
        return page;
    }


    void write(Table table, Trace trace, Object family) throws Exception
    {
        TableRow row=new TableRow().
        add(family,trace.getNumber()).
        add(new TitleText(trace.getCategory(),80)).
        add(new TitleText(trace.getDetails(),80)).
        add(
        DateTimeUtils.toSystemDateTimeString(trace.getCreatedMs()),
        formatNsToMs(trace.getActiveNs()),
        formatNsToMs(trace.getWaitNs()),
        formatNsToMs(trace.getDurationNs()),
        trace.isWaiting(),
        trace.getThread().getName()
        );
        row.tr().onclick("window.location='"+new PathAndQuery("./activeTrace").addQuery("number", trace.getNumber()).toString()+"'");
        table.addRow(row);
    }

    
    static class ActiveTraceTreeElement extends Element
    {
        final private Trace trace;
        
        
        public ActiveTraceTreeElement(Trace trace)
        {
            this.trace=trace;
        }
        @Override
        public void compose(Composer composer) throws Throwable
        {
            Content content=new Content();
            content.addInner(trace.getCategory());
            content.addInner(new NameValueRow(", Number: ",trace.getNumber(),null));
            if (trace.getParent()!=null)
            {
                content.addInner(new NameValueRow(", Parent=",trace.getParent().getNumber(),null));
            }
            content.addInner(new NameValueRow(", Duration: ",formatNsToMs(trace.getDurationNs())," ms"));
            content.addInner(new NameValueRow(", Wait: ",formatNsToMs(trace.getWaitNs())," ms"));
            composer.compose(content);
        }
        
    }
    
    
    static enum Units
    {
        Seconds,
        Milliseconds,
        Microseconds,
    }
    
    @GET
    @Path("/operator/tracing/activeTrace")
    public Element activeTrace(@QueryParam("number") long number) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Active Trace");
        Trace[] traces = this.serverApplication.getTraceManager().getCurrentTraces();
        Trace found = null;
        for (Trace trace : traces)
        {
            if (trace.getNumber() == number)
            {
                found = trace;
                break;
            }
        }
        if (found != null)
        {
            TreeNode root=new TreeNode(new ActiveTraceTreeElement(found));
            TreeNode foundNode=root;
            foundNode.opened(true);
            foundNode.selected(true);
            for (Trace trace = found.getParent(); trace != null; trace = trace.getParent())
            {
                TreeNode newRoot=new TreeNode(new ActiveTraceTreeElement(trace));
                newRoot.add(root);
                newRoot.opened(true);
                root=newRoot;
            }
           
            ArrayList<ArrayList<Trace>> childeren = new ArrayList<>();
            for (Trace trace : traces)
            {
                ArrayList<Trace> list = new ArrayList<>();
                for (Trace parent = trace; parent != null; parent = parent.getParent())
                {
                    if (parent.getNumber() == number)
                    {
                        childeren.add(list);
                        break;
                    }
                    list.add(parent);
                }
            }

            HashMap<Long,TreeNode> childerenMap=new HashMap<>();
            childerenMap.put(found.getNumber(),foundNode);
            
            for (ArrayList<Trace> list : childeren)
            {
                for (int index=list.size()-1;index>=0;index--)
                {
                    Trace trace=list.get(index);
                    for (Trace parent = trace; parent != null; parent = parent.getParent())
                    {
                        TreeNode treeNode=childerenMap.get(trace.getNumber());
                        if (treeNode==null)
                        {
                            continue;
                        }
                        TreeNode childNode=new TreeNode(new ActiveTraceTreeElement(trace));
                        treeNode.add(childNode);
                        childerenMap.put(trace.getNumber(), childNode);
                    }
                }
            }
            Tree tree=new Tree(page.head());
            tree.add(root);
            Panel3 tracePanel=page.content().returnAddInner(new Panel3(page.head(),"Trace"));
            tracePanel.content().addInner(tree);
            Panel3 stackTracePanel=page.content().returnAddInner(new Panel3(page.head(),"Stack Trace"));
            StackTraceElement[] stackTrace = found.getThread().getStackTrace();
            stackTracePanel.content().addInner(toLiteralHtml(stackTrace, 0));

            Panel3 samplePanel=page.content().returnAddInner(new Panel3(page.head(),"Profile"));
            form_post form=samplePanel.content().returnAddInner(new form_post());
            form.addInner(new InputHidden("number",number));
            form.action("/operator/tracing/sample");
            
            SelectOptions options=new SelectOptions("threadPriorityLevel");
            options.add(Thread.MAX_PRIORITY, "Highest", true);
            options.add(Thread.NORM_PRIORITY+1, "High");
            options.add(Thread.NORM_PRIORITY, "Normal");
            options.add(Thread.NORM_PRIORITY-1, "Low");
            options.add(Thread.MIN_PRIORITY, "Lowest");
            
            NameValueList list=form.returnAddInner(new NameValueList());
            list.add("Sampling Interval Time Units", new SelectEnums<Units>("unit",Units.class,Units.Milliseconds));
            list.add("Minimum Sampling Interval", new input_number().name("minimumSamplingInterval").value(1).min(0));
            list.add("Maximum Sampling Interval", new input_number().name("maximumSamplingInterval").value(1).min(0));
            list.add("Measurements Per Sample", new input_number().name("measurementsPerSample").value(1).min(1));
            list.add("Profiler Thread Priority Level", options);
            list.add("Sampling Method", new SelectEnums<SamplingMethod>("samplingMethod", SamplingMethod.class));
            list.add("Sampling Duration (Milliseconds)", new input_number().name("duration").value(5000).min(1).max(1000000));
            list.add("Refresh Interval (Milliseconds)", new input_number().name("refresh").value(1000).min(0));
            list.add(null, new input_submit().value("Start"));
        }
        else
        {
            page.content().addInner("Trace ended");
        }
        return page;
    }
    
    
    ThreadExecutionProfiler threadExecutionProfiler;

    @POST
    @Path("/operator/tracing/sample")
    public Element sampleTrace(Trace parent,@QueryParam("number") long number,
            @QueryParam("threadPriorityLevel") int threadPriorityLevel,
            @QueryParam("unit") Units units,
            @QueryParam("minimumSamplingInterval") long minimumSamplingInterval,
            @QueryParam("maximumSamplingInterval") long maximumSamplingInterval,
            @QueryParam("duration") long duration,
            @QueryParam("measurementsPerSample") int measurementsPerSample,
            @QueryParam("refresh") long refresh,
            @QueryParam("samplingMethod") SamplingMethod samplingMethod
            
            ) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Trace Execution Profiling");
        Trace[] traces = this.serverApplication.getTraceManager().getCurrentTraces();
        Trace found = null;
        for (Trace trace : traces)
        {
            if (trace.getNumber() == number)
            {
                found = trace;
                break;
            }
        }
        if (found != null)
        {
            switch (units)
            {
                case Seconds:
                    minimumSamplingInterval*=1000000000;
                    maximumSamplingInterval*=1000000000;
                    break;
                case Milliseconds:
                    minimumSamplingInterval*=1000000;
                    maximumSamplingInterval*=1000000;
                    break;
                case Microseconds:
                    minimumSamplingInterval*=1000;
                    maximumSamplingInterval*=1000;
                    break;
                default:
                    break;
                
            }
            
            synchronized(this)
            {
                if (this.threadExecutionProfiler!=null)
                {
                    this.threadExecutionProfiler.stop(0);
                }
                this.threadExecutionProfiler=new ThreadExecutionProfiler(found.getThread(), samplingMethod, minimumSamplingInterval, maximumSamplingInterval, duration*1000000, measurementsPerSample,threadPriorityLevel);
                this.threadExecutionProfiler.start();
            }
            button_button button=new button_button().addInner("Stop Sampling");
            button.onclick("$.post('/operator/tracing/sample/stop',function(data){$('#stackTraceTree').html(data);});");
            page.content().addInner(button);

            new Tree(page.head());
            new Panel3(page.head(),null);
            
            Refresher refresher=new Refresher("stackTraceTree","/operator/tracing/sample/refresh", refresh,"refreshTimer");
            page.content().addInner(refresher);
//            Thread.sleep(3000);
//            page.content().addInner(sampleTrace(parent));
          
        }
        else
        {
            page.content().addInner("Trace ended");
        }
        return page;
    }

    @POST
    @Path("/operator/tracing/sample/stop")
    public Element stopSampleTrace(Trace trace) throws Throwable
    {
        ThreadExecutionProfiler meter;
        synchronized(this)
        {
            meter=this.threadExecutionProfiler;
        }
        
        if (meter!=null)
        {
            meter.stop(0);
        }
        System.out.println("Stopped");
        return sampleTrace(trace);
    }
    
    @GET
    @Path("/operator/tracing/sample/refresh")
    public Element sampleTrace(Trace trace) throws Throwable
    {
        Content content=new Content();
        ThreadExecutionProfiler profiler;
        synchronized(this)
        {
            profiler=this.threadExecutionProfiler;
        }
        
        if (profiler==null)
        {
            content.addInner("No Profiler");
        }
        else
        {
            ThreadExecutionSample sample=profiler.sample();
            if (sample.isEnded())
            {
                content.addInner(new ClearTimeout("refreshTimer"));
                content.addInner(new h3().addInner("Sampling: Stopped"));
            }
            else
            {
                content.addInner(new h3().addInner("Sampling: Active"));
                
            }
            content.addInner(new p());
            long totalActiveNs=sample.getRoot().getActiveNs();
            long totalWaitNs=sample.getRoot().getWaitNs();
            String text;
            long nodeDurationNs=totalActiveNs+totalWaitNs;
            if (nodeDurationNs%1000000000>0)
            {
                text=toSeconds(nodeDurationNs)+", active="+toSeconds(totalActiveNs)+", wait="+toSeconds(totalWaitNs)+" seconds, count="+sample.getRoot().getCount();
            }
            else if (nodeDurationNs%1000000>0)
            {
                text=toSeconds(nodeDurationNs)+", active="+toSeconds(totalActiveNs)+", wait="+toSeconds(totalWaitNs)+" seconds, count="+sample.getRoot().getCount();
            }
            else
            {
                text=toSeconds(nodeDurationNs)+", active="+toSeconds(totalActiveNs)+", wait="+toSeconds(totalWaitNs)+" seconds, count="+sample.getRoot().getCount();
            }
                    
            
            
            TreeNode root=new TreeNode(text);
            root.opened(true);
            Tree tree=new Tree(null);
            tree.add(root);
            content.addInner(tree);
            if (sample.getRoot().getChildNodes()!=null)
            {
                for (StackTraceNode node:sample.getRoot().getChildNodes())
                {
                    root.add(buildStackTraceBranch(node,false,totalActiveNs,totalActiveNs,true,totalWaitNs,totalActiveNs,true));
                }
            }
        }
        return content;
    }
    
    String toSeconds(long nanos)
    {
        return String.format("%.3f", nanos/1.0e9);
    }
    String toMilliseconds(long nanos)
    {
        return String.format("%.3f", nanos/1.6e9);
    }
    String toMicroseconds(long nanos)
    {
        return String.format("%.3f", nanos/1.6e9);
    }
    private TreeNode buildStackTraceBranch(StackTraceNode node,boolean showDuration,long activeNs,long hotActiveNs,boolean hotActive,long waitNs,long hotWaitNs,boolean hotWait)
    {
        Content content=new Content();
        TreeNode treeNode=new TreeNode(content);
        StackTraceElement element=node.getStackTraceElement();
        long childActiveNs=0;
        long childWaitNs=0;
        long durationNs=activeNs+waitNs;
        long nodeActiveNs=node.getActiveNs();
        long nodeWaitNs=node.getWaitNs();

        if (node.getChildNodes()!=null)
        {
            long childHotActiveNs=Long.MIN_VALUE;
            if (hotActive)
            {
                for (StackTraceNode childNode:node.getChildNodes())
                {
                    if (childHotActiveNs<childNode.getActiveNs())
                    {
                        childHotActiveNs=childNode.getActiveNs();
                    }
                }
            }
            long childHotWaitNs=Long.MIN_VALUE;
            if (hotWait)
            {
                for (StackTraceNode childNode:node.getChildNodes())
                {
                    if (childHotWaitNs<childNode.getWaitNs())
                    {
                        childHotWaitNs=childNode.getWaitNs();
                    }
                }
            }
            for (StackTraceNode childNode:node.getChildNodes())
            {
                childActiveNs+=childNode.getActiveNs();
                childWaitNs+=childNode.getWaitNs();
            }
            if (showDuration==false)
            {
                showDuration=node.getChildNodes().length>1;
            }
            if (nodeActiveNs>childActiveNs)
            {
                showDuration=true;
            }
            boolean open=false;
            for (StackTraceNode childNode:node.getChildNodes())
            {
                boolean childHotActive=(childHotActiveNs==childNode.getActiveNs())&&(childHotActiveNs!=0);
                boolean childHotWait=(childHotWaitNs==childNode.getWaitNs())&&(childHotWaitNs!=0);
                
                treeNode.add(buildStackTraceBranch(childNode,showDuration,activeNs,childHotActiveNs,childHotActive,waitNs,childHotWaitNs,childHotWait));
                if (childHotActive||childHotWait)
                {
                    open=true;
                }
            }
            treeNode.opened(open);
        }
        else
        {
            showDuration=true;
        }
        
        
        String elementColor="#000";

        long nodeDurationNs=nodeActiveNs+nodeWaitNs;
        long selfDurationNs=nodeDurationNs-childActiveNs-childWaitNs;
        double durationPercent=(100.0*nodeDurationNs)/durationNs;
        double selfDurationPercent=(100.0*selfDurationNs)/durationNs;
        double activePercent=(100.0*nodeActiveNs)/durationNs;
        String text="";
        if (showDuration)
        {
            text=String.format("%.3f", durationPercent)+"%, active="+String.format("%.3f", activePercent)+"%, self="+String.format("%.3f", selfDurationPercent)+"%, count="+node.getCount()+" ";
            
            if (!((nodeActiveNs==hotActiveNs)&&(nodeWaitNs==hotWaitNs)))
            {
                if (nodeActiveNs==hotActiveNs)
                {                
                    content.addInner(new span().addInner(new LiteralHtml("&#128293; ")));
                }
                if (nodeWaitNs==hotWaitNs)
                {
                    content.addInner(new span().addInner(new LiteralHtml("&#x1F4A4; ")));
                }
            }
        }
        else
        {
            elementColor="color:#888;";
        }
        String title;
        if (nodeDurationNs%1000000000>0)
        {
            title=toSeconds(nodeDurationNs)+", self="+toSeconds(selfDurationNs)+", active="+toSeconds(nodeActiveNs)+", wait="+toSeconds(nodeWaitNs)+" seconds";
        }
        else if (nodeDurationNs%1000000>0)
        {
            title=toMilliseconds(nodeDurationNs)+", self="+toMilliseconds(selfDurationNs)+", active="+toMilliseconds(nodeActiveNs)+", wait="+toMilliseconds(nodeWaitNs)+" milliseconds";
        }
        else
        {
            title=toMicroseconds(nodeDurationNs)+", self="+toMicroseconds(selfDurationNs)+", active="+toMicroseconds(nodeActiveNs)+", wait="+toMicroseconds(nodeWaitNs)+" microseconds";
        }

        if (element.getFileName()!=null)
        {
            text+=element.getClassName()+"."+element.getMethodName()+'('+element.getFileName()+':'+element.getLineNumber()+')';
        }
        else
        {
            text+=element.getClassName()+"."+element.getMethodName()+"(Unknown Source)";
        }
        content.addInner(new span().style(elementColor).addInner(text).title(title));
        
        return treeNode;
    }
    
    //    final static TitleText TOTAL_DURATION_COLUMN=new TitleText("Total duration in milliseconds","Tot &#x23F1;");
//    final static TitleText TOTAL_PERCENTAGE_DURATION_COLUMN=new; TitleText("Total duration in milliseconds as percentage","Tot% &#x23F1;");
//    final static TitleText TOTAL_WAIT_COLUMN=new TitleText("Total wait duration in milliseconds","Total &#8987;");

    static class RightMoreLink extends MoreButton
    {

        public RightMoreLink(Head head, String href)
        {
            super(head, href);
            style("float:right;");
        }
    }
    
    @GET
    @Path("/operator/tracing/currentTraceSummary")
    public Element currentTraces(@QueryParam("excludeWaiting") boolean excludeWaiting) throws Throwable
    {
        String title=excludeWaiting?"Current Non Waiting Trace Summary":"Current Trace Summary";
        OperatorPage page=this.serverApplication.buildOperatorPage(title);
        Trace[] traces = this.serverApplication.getTraceManager().getCurrentTraces();
        OperatorDataTable table=page.content().returnAddInner(new OperatorTable(page.head()));
        
        TableHeader header=new TableHeader();
        writeTraceRowHeading(header);
        header.add("");
        table.setHeader(header);
        
        for (Trace trace : traces)
        {
            TableRow row=new TableRow();
            writeTraceRow(row,trace);
            row.add(new RightMoreLink(page.head(),new PathAndQuery("./activeTrace").addQuery("number", trace.getNumber()).toString()));
            table.addRow(row);
        }
        return page;
    }

    /*
    private Element formatCategory(CategoryTraceSample item)
    {
        span span=new span();
        String category=item.getCategory();
        int max=160;
        if (category!=null)
        {
            if (category.length()>max)
            {
                span.style("color:#00D;width:100%;");
                span.addInner(category.substring(0,max)+"...");
                span.title(category);
            }
            else
            {
                span.addInner(category);
            }
        }
        return span;
    }
    */
    
    static String format_3(double value)
    {
        return String.format("%.3f", value);
    }
    
    static String format_2(double value)
    {
        return String.format("%.2f", value);
    }

    @GET
    @Path("/operator/tracing/sampleCurrent")
    public Element sampleCurrent(@QueryParam("excludeWaiting") boolean excludeWaiting) throws Throwable
    {
        String heading=excludeWaiting?"Current Non Waiting Trace Samples":"Current Trace Samples";
        OperatorPage page=this.serverApplication.buildOperatorPage(heading);
        CategorySample[] samples=this.serverApplication.getTraceManager().sampleCurrentTraceCategories(excludeWaiting);
        OperatorDataTable table=page.content().returnAddInner(new OperatorTable(page.head()));

        TableHeader header=new TableHeader();
//        writeTraceRowHeading(header,excludeWaiting);
        addTraceSampleColumns(header);
        header.add("");
        table.setHeader(header);

        SlowTraceSampleDetector detector=new SlowTraceSampleDetector(samples.length);
        for (CategorySample sample:samples)
        {
            detector.update(sample.getSample());
        }

        for (CategorySample sample:samples)
        {
            TableRow row=new TableRow();
            row.add(new TitleText(sample.getCategory(),80));
            writeTraceSample(detector,row,sample.getSample());
            String location=new PathAndQuery("/operator/tracing/sampleCurrent/category").addQuery("category", sample.getCategory()).toString();
            row.add(new RightMoreLink(page.head(),location));
//            row.addDetailButton(location);
            table.addRow(row);

        }
        return page;
    }
    
    @GET
    @Path("/operator/tracing/sampleLast")
    public Element sampleLast() throws Throwable
    {
        return showSamples("Sample Last Traces",this.serverApplication.getTraceManager().sampleLastCategories());
    }

    @GET
    @Path("/operator/tracing/sampleLastSecondary")
    public Element sampleSecondaryLast() throws Throwable
    {
        return showSamples("Sample Last Secondary Traces",this.serverApplication.getTraceManager().sampleLastSecondaryCategories());
    }

    private Element showSamples(String title,CategorySample[] samples) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage(title);
        OperatorDataTable table=page.content().returnAddInner(new OperatorTable(page.head()));

        TableHeader header=new TableHeader();
        addTraceSampleColumns(header);
        header.add("");
        table.setHeader(header);

        SlowTraceSampleDetector detector=new SlowTraceSampleDetector(samples.length);
        for (CategorySample sample:samples)
        {
            detector.update(sample.getSample());
        }

        for (CategorySample sample:samples)
        {
            TableRow row=new TableRow();
   //         row.add("&#128462;");
            row.add(new TitleText(sample.getCategory(),80));
            writeTraceSample(detector,row,sample.getSample());
            String location=new PathAndQuery("./trace").addQuery("category", sample.getCategory()).toString();
            row.add(new RightMoreLink(page.head(),location));
            table.addRow(row);

        }
        return page;
    }

    @GET
    @Path("/operator/tracing/sampleAndResetLast")
    public Element sampleAndResetLast() throws Throwable
    {
        return showSamples("Sample and Reset Last Traces",this.serverApplication.getTraceManager().sampleAndResetLastCategories());
    }

    @GET
    @Path("/operator/tracing/sampleAndResetLastSecondary")
    public Element sampleAndResetLastSecondary() throws Throwable
    {
        return showSamples("Sample and Reset Last Traces",this.serverApplication.getTraceManager().sampleAndResetLastSecondaryCategories());
    }

    @GET
    @Path("/operator/tracing/sampleLastTraceBuffer")
    public Element sampleLastTraceBuffer() throws Throwable
    {
        return showSamples("Sample Last Trace Buffer",this.serverApplication.getTraceManager().sampleLastTraces());
    }
    
    @GET
    @Path("/operator/tracing/sampleLastSecondaryTraceBuffer")
    public Element sampleLastSecondaryTraceBuffer() throws Throwable
    {
        return showSamples("Sample Last Trace Buffer",this.serverApplication.getTraceManager().sampleLastTraces());
    }

    void buildCategories(HashMap<String,Boolean> categories, Entry<String, TraceNode> entry)
    {
        categories.put(entry.getKey(),false);
        TraceNode node=entry.getValue();
        if (node.getChildTraceNodesSnapshot() != null)
        {
            for (Entry<String, TraceNode> child : node.getChildTraceNodesSnapshot().entrySet())
            {
                buildCategories(categories, child);
            }
        }
    }
    
    @POST
    @Path("/operator/tracing/secondaryCategories/set")
    public Element setSecondaryList(@ParamName("change") boolean change,Queries parameters,Context context) throws Throwable
    {
        if (change==false)
        {
            return secondaryCategories();
        }

        /*
        MenuBar menuBar=this.serverApplication.getMenuBar();
        menuBar.setEnabled(true,"Tracing","Sample Watch Traces");
        menuBar.setEnabled(true,"Tracing","Sample and Reset Watch Traces");
        menuBar.setEnabled(true,"Tracing","Sample Watch Trace Buffer");
        menuBar.setEnabled(true,"Tracing","Last Watch Traces");
        */
        this.serverApplication.buildOperatorPageTemplate();

        OperatorPage page=this.serverApplication.buildOperatorPage("Secondary Category List");
        TraceManager traceManager=this.serverApplication.getTraceManager();
        Map<String,TraceNode> roots=traceManager.getTraceTreeRootsSnapshot();
        HashMap<String,Boolean> categories=new HashMap<>();
        for (Entry<String, TraceNode> entry:roots.entrySet())
        {
            buildCategories(categories,entry);
        }
        ArrayList<String> list=new ArrayList<>();
        Panel2 panel=page.content().returnAddInner(new Panel2(page.head(),"Categories"));
        panel.content().addInner(new hr());
        for (String category:categories.keySet())
        {
            if (parameters.containsName("~"+category))
            {
                list.add(category);
                panel.content().addInner(category);
                panel.content().addInner(new hr());
            }
        }
        traceManager.setSecondaryCategories(list.toArray(new String[list.size()]));
        if (categories.size()==0)
        {
            page=this.serverApplication.buildOperatorPage("Secondary Category List");
            page.content().addInner(new strong().addInner("No categories selected as secondary."));
            return page;
        }
        return page;
    }
    
    
    @GET
    @Path("/operator/tracing/secondaryCategories")
    public Element secondaryCategories() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Set Secondary Categories");
        TraceManager traceManager=this.serverApplication.getTraceManager();
        String legendText="Select categories as secondary";
                
        HashSet<String> secondaryCategories=new HashSet<>();
        for (String category:traceManager.getSecondaryCategories())
        {
            secondaryCategories.add(category);
        }

        form_post form=page.content().returnAddInner(new form_post()).action("/operator/tracing/secondaryCategories/set");
        fieldset fieldset=form.returnAddInner(new fieldset());
        fieldset.addInner(new legend().addInner(legendText));
        OperatorDataTable table=fieldset.returnAddInner(new OperatorTable(page.head()));

        TableHeader header=new TableHeader();
        header.add("");
        addTraceSampleColumns(header);
        header.add("");
        table.setHeader(header);

        Map<String, TraceNode> roots = this.serverApplication.getTraceManager().getTraceTreeRootsSnapshot();
        HashMap<String, TraceSample> categorySamples = buildCategorySamples(roots);
        
        SlowTraceSampleDetector detector=new SlowTraceSampleDetector(categorySamples.size());
        for (TraceSample sample:categorySamples.values())
        {
            detector.update(sample);
        }

        for (Entry<String, TraceSample> entry:categorySamples.entrySet())
        {
            TraceSample sample=entry.getValue();
            TableRow row=new TableRow();
            row.add(new input_checkbox().checked(secondaryCategories.contains(entry.getKey())).name("~"+entry.getKey()));
            row.add(new TitleText(entry.getKey(),80));
            writeTraceSample(detector,row,sample);
            String location=new PathAndQuery("/operator/tracing/sampleAll/category").addQuery("category", entry.getKey()).toString();
            row.add(new RightMoreLink(page.head(),location));
            table.addRow(row);
        }
        
        fieldset.returnAddInner(new hr());
        fieldset.returnAddInner(new p()).addInner(new input_checkbox().name("change")).addInner(new LiteralHtml("&nbsp;")).addInner(new input_submit().value("Set")).addInner(new LiteralHtml("&nbsp;&nbsp;&nbsp;")).addInner(new input_reset());

        return page;
    }

    
    @GET
    @Path("/operator/tracing/watchList")
    public Element traceWatchList() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Set Watch Categories");
        TraceManager traceManager=this.serverApplication.getTraceManager();
        String legendText;
        if (traceManager.isEnableLastTraceWatching())
        {
            legendText="Select categories for watching - Trace watching is enabled.";
        }
        else
        {
            legendText="Select categories for watching - Trace watching is currently disabled.";
        }
                
        HashSet<String> watches=new HashSet<>();
        for (String category:traceManager.getWatchList())
        {
            watches.add(category);
        }

        form_post form=page.content().returnAddInner(new form_post()).action("/operator/tracing/watchList/set");
        fieldset fieldset=form.returnAddInner(new fieldset());
        fieldset.addInner(new legend().addInner(legendText));
        OperatorDataTable table=fieldset.returnAddInner(new OperatorTable(page.head()));

        TableHeader header=new TableHeader();
        header.add("");
        addTraceSampleColumns(header);
        header.add("");
        table.setHeader(header);

        Map<String, TraceNode> roots = this.serverApplication.getTraceManager().getTraceTreeRootsSnapshot();
        HashMap<String, TraceSample> categorySamples = buildCategorySamples(roots);
        
        SlowTraceSampleDetector detector=new SlowTraceSampleDetector(categorySamples.size());
        for (TraceSample sample:categorySamples.values())
        {
            detector.update(sample);
        }

        for (Entry<String, TraceSample> entry:categorySamples.entrySet())
        {
            TraceSample sample=entry.getValue();
            TableRow row=new TableRow();
            row.add(new input_checkbox().checked(watches.contains(entry.getKey())).name("~"+entry.getKey()));
            row.add(new TitleText(entry.getKey(),80));
            writeTraceSample(detector,row,sample);
            String location=new PathAndQuery("/operator/tracing/sampleAll/category").addQuery("category", entry.getKey()).toString();
            row.add(new RightMoreLink(page.head(),location));
            table.addRow(row);
        }
        
        fieldset.returnAddInner(new hr());
        fieldset.returnAddInner(new p()).addInner(new input_checkbox().name("change")).addInner(new LiteralHtml("&nbsp;")).addInner(new input_submit().value("Set")).addInner(new LiteralHtml("&nbsp;&nbsp;&nbsp;")).addInner(new input_reset());

        if (traceManager.isEnableLastTraceWatching())
        {
            form_post disableForm=page.content().returnAddInner(new form_post()).action("/operator/tracing/watchList/disable");
            fieldset disableFieldset=disableForm.returnAddInner(new fieldset());
            disableFieldset.addInner(new legend().addInner("Disable trace watching"));
            disableFieldset.returnAddInner(new p()).addInner(new input_checkbox().name("change")).addInner(new LiteralHtml("&nbsp;")).addInner(new input_submit().value("Disable"));
        }        
        return page;
    }

    @POST
    @Path("/operator/tracing/watchList/disable")
    public Element disableTraceWatchList(@ParamName("change") boolean change) throws Throwable
    {
        if (change==false)
        {
            return traceWatchList();
        }
        setWatchMenuItemStatus(false);

        OperatorPage page=this.serverApplication.buildOperatorPage("Disable Trace Watching");
        this.serverApplication.buildOperatorPageTemplate();
        this.serverApplication.getTraceManager().disableWatchListLastTraces();
        page.content().addInner("Trace watching is now disabled.");
        return page;
    }
    
    void setWatchMenuItemStatus(boolean status)
    {
        MenuBar menuBar=this.serverApplication.getMenuBar();
        menuBar.setEnabled(status,"Tracing","Watch","Sample Watch Traces");
        menuBar.setEnabled(status,"Tracing","Watch","Sample and Reset Watch Traces");
        menuBar.setEnabled(status,"Tracing","Watch","Sample Watch Trace Buffer");
        menuBar.setEnabled(status,"Tracing","Watch","Last Watch Traces");
    }
    
    @POST
    @Path("/operator/tracing/watchList/set")
    public Element setTraceWatchList(@ParamName("change") boolean change,Queries parameters,Context context) throws Throwable
    {
        if (change==false)
        {
            return traceWatchList();
        }
        setWatchMenuItemStatus(true);

        this.serverApplication.buildOperatorPageTemplate();

        OperatorPage page=this.serverApplication.buildOperatorPage("Trace Watch List");
        TraceManager traceManager=this.serverApplication.getTraceManager();
        Map<String,TraceNode> roots=traceManager.getTraceTreeRootsSnapshot();
        HashMap<String,Boolean> categories=new HashMap<>();
        for (Entry<String, TraceNode> entry:roots.entrySet())
        {
            buildCategories(categories,entry);
        }
        ArrayList<String> list=new ArrayList<>();
        Panel2 panel=page.content().returnAddInner(new Panel2(page.head(),"Categories in watch list"));
        panel.content().addInner(new hr());
        for (String category:categories.keySet())
        {
            if (parameters.containsName("~"+category))
            {
                list.add(category);
                panel.content().addInner(category);
                panel.content().addInner(new hr());
            }
        }
        if (categories.size()==0)
        {
            setWatchMenuItemStatus(false);
            page=this.serverApplication.buildOperatorPage("Trace Watch List");
            page.content().addInner(new strong().addInner("No category selected. Trace watching is disabled."));
            return page;
        }
        setWatchMenuItemStatus(true);
        traceManager.enableAndAddWatchListCategories(list.toArray(new String[list.size()]));
        return page;
    }
    

    static class NameValueRow extends Element
    {
        final private Element span;
        final private Element value;
        final private Element unit;
        static String STYLE="color:#448;border:1px;";
        public NameValueRow(String name,Element value,String unit)
        {
            this.span=new span().style(STYLE).addInner(name);
            this.value=new span().style(STYLE).addInner(value);
            this.unit=new span().style(STYLE).addInner(unit);
        }
        public NameValueRow(String name,Object value,String unit)
        {
            this.span=new span().style(STYLE).addInner(name);
            this.value=new span().style(STYLE).addInner(value);
            this.unit=new span().style(STYLE).addInner(unit);
        }

        @Override
        public void compose(Composer composer) throws Throwable
        {
            composer.compose(this.span);
            composer.compose(this.value);
            composer.compose(this.unit);
            
        }
    }
    
    private TreeNode writeTraceNode(Entry<String, TraceNode> entry,boolean opened,String focusCategory)
    {
        TraceNode traceNode=entry.getValue();
        TraceSample sample=traceNode.sampleTrace();
        Content content=new Content();
        content.addInner(entry.getKey());
        content.addInner(new NameValueRow(", Count: ",sample.getCount(),null));
        if (sample.getCount()>0)
        {
            content.addInner((new NameValueRow(", Average: ",formatNsToMs(sample.getTotalDurationNs() / sample.getCount())," ms")));  //Milliseconds
        }
        content.addInner(new NameValueRow(", Duration: ",formatNsToMs(sample.getTotalDurationNs())," ms"));
        content.addInner(new NameValueRow(", Wait: ",formatNsToMs(sample.getTotalWaitNs())," ms"));

        TreeNode treeNode=new TreeNode(content);
        if (Utils.equals(entry.getKey(),focusCategory))
        {
            treeNode.selected(true);
            treeNode.opened(true);
        }
        else
        {
            treeNode.opened(opened);
        }

        Map<String,TraceNode> snapshot=traceNode.getChildTraceNodesSnapshot();
        if (snapshot!=null)
        {
            for (Entry<String, TraceNode> child: snapshot.entrySet())
            {
                treeNode.add(writeTraceNode(child,false,focusCategory));
            }
        }
        
        return treeNode;
    }
    
    
    private Element writeTraceNodeTree(Head head,String focusCategory)
    {
        Map<String, TraceNode> map = this.serverApplication.getTraceManager().getTraceTreeRootsSnapshot();
        Tree tree=new Tree(head);
        for (Entry<String, TraceNode> entry : map.entrySet())
        {
            tree.add(writeTraceNode(entry,true,focusCategory));
        }
        return tree;
    }
    
    @GET
    @Path("/operator/tracing/traceTree")
    public Element traceTree() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Trace Tree");
        page.content().addInner(writeTraceNodeTree(page.head(),null));
        return page;
    }

    static private TitleText formatNsToMs(long ns,long instantMs)
    {
        return new TitleText(Utils.millisToNiceDurationString(ns/1000000)+" on "+DateTimeUtils.toSystemDateTimeString(instantMs),format_3(ns/1.0e6));        
    }
    private static TitleText formatNsToMs(long durationNs)
    {
        return new TitleText(Utils.millisToNiceDurationString(durationNs/1000000),String.format("%.3f",durationNs/1.0e6));
    }
    static private TitleText formatNsToMs(double durationNs)
    {
        return new TitleText(Utils.millisToNiceDurationString((long)(durationNs/1.0e6)),String.format("%.3f",durationNs/1.0e6));
    }
    /*
    private TitleText formatMsToMs(double durationMs)
    {
        return new TitleText(Utils.millisToNiceDurationString((long)durationMs),String.format("%.3f",durationMs));
    }
    */
    
    private String formatMemorySize(long size)
    {
        return size+" bytes, "+format_3(size/1000.0)+" KB, "+format_3(size/1000000.0)+" MB, "+format_3(size/1000000000.0)+" GB";
    }
    
    @GET
    @Path("/operator/jvm")
    public Element jvm(@ParamName("collect") boolean collect) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("JVM");
        form_get form=page.content().returnAddInner(new form_get()).action("/operator/jvm");
        form.returnAddInner(new input_checkbox().name("collect"));
        form.returnAddInner(new input_submit().value("Force Garbage Collection"));
        page.content().addInner(new hr());
        
        NameValueList list=page.content().returnAddInner(new NameValueList());
        if (collect)
        {
            long freeBefore=Runtime.getRuntime().freeMemory();
            list.add("Free memory before collection",formatMemorySize(freeBefore));
            list.add("Total memory before collection",formatMemorySize(Runtime.getRuntime().totalMemory()));
            PrecisionTimer timer=new PrecisionTimer();
            timer.start();
            System.gc();
            timer.stop();
            long freeAfter=Runtime.getRuntime().freeMemory();
            list.add("Collection duration (ms)",formatNsToMs(timer.getElapsedNs())); 
            list.add("Free memory after collection",formatMemorySize(freeAfter));
            list.add("Total memory after collection",formatMemorySize(Runtime.getRuntime().totalMemory()));
            list.add("Memory freed",formatMemorySize(freeAfter-freeBefore));
        }
        else
        {
            list.add("Free memory",formatMemorySize(Runtime.getRuntime().freeMemory()));
            list.add("Total memory",formatMemorySize(Runtime.getRuntime().totalMemory()));
        }
        long max=Runtime.getRuntime().maxMemory();
        if (max<Long.MAX_VALUE)
        {
            list.add("Max memory",formatMemorySize(max));
        }
        list.add("Available processors",Runtime.getRuntime().availableProcessors());
        return page;
    }

    
    @GET
    @Path("/operator/tracing/trace")
    public Element trace(@QueryParam("category") String category) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Trace Branch");
          page.content().addInner(writeTraceNodeTree(page.head(), category));
          return page;
      }

      private String getTraceBoxStyle()
      {
          return "border-style:solid;border-color:#888;border-width:1px 0 1px 0;";
      }


      /*
    final static TitleText TRACE_NUMBER_COLUMN=new TitleText("Trace number","#");
    final static TitleText TRACE_CATEGORY_COLUMN=new TitleText("Trace category","Category");

    final static TitleText TOTAL_DURATION_COLUMN=new TitleText("Total duration in milliseconds","Tot &#x23F1;");
    final static TitleText TOTAL_PERCENTAGE_DURATION_COLUMN=new TitleText("Total duration in milliseconds as percentage","Tot% &#x23F1;");
    final static TitleText TOTAL_WAIT_COLUMN=new TitleText("Total wait duration in milliseconds","Total &#8987;");
    final static TitleText MIN_DURATION_COLUMN=new TitleText("Minimum duration in millisecond and last occurrance instant","Min &#x23F1;");
    final static TitleText MAX_DURATION_COLUMN=new TitleText("Maximum duration in milliseconds and last occurance instant","Max &#x23F1;");
    final static TitleText AVERAGE_DURATION_COLUMN=new TitleText("Average duration in milliseconds","Ave &#x23F1;");
    final static TitleText STANDARDDEVIATION_DURATION_COLUMN=new TitleText("Average duration in milliseconds","Stddev &#x23F1;");
    final static TitleText MIN_WAIT_COLUMN=new TitleText("Minimum wait duration in milliseconds and last occurrance instant","Min &#8987;");
    
    final static TitleText MAX_WAIT_COLUMN=new TitleText("Maximum wait duration in milliseconds and last occurrance instant","Max &#8987;");
    final static TitleText AVERAGE_WAIT_COLUMN=new TitleText("Average wait duration in milliseconds","Ave &#8987;");

    final static TitleText TS_COUNT=new TitleText("Sample count","Count");
    final static TitleText TS_RATE=new TitleText("Per second","Rate");
    final static TitleText TRACE_ROOT_COLUMN=new TitleText("Root of the trace tree","Root");
    final static TitleText TRACE_NODE_COLUMN=new TitleText("The number of times the trace category occurs as nodes in the trace graph","&#9738;");
    final static TitleText EXCEPTION_COUNT_COLUMN=new TitleText("Number of exceptions","&#9888;");
*/
    void addTraceSampleColumns(TableHeader header)
    {
        header.add(new th_title("Category","Trace category"));
        header.add(new th_title("Rate","Trace rate (per second)"));
        header.add(new th_title("Ct","Sample count"));

        header.add(new th_title(new LiteralHtml("Tot&#x23F1;"),"Total trace duration in category in milliseconds"));
        header.add(new th_title(new LiteralHtml("Ave&#x23F1;"),"Ave0rage trace duration in milliseconds"));
        header.add(new th_title(new LiteralHtml("Std &#x23F1;"),"Standard deviation of trace duration in milliseconds"));
        header.add(new th_title(new LiteralHtml("min&#x23F1;"),"Minimum trace duration in milliseconds"));
        header.add(new th_title(new LiteralHtml("max&#x23F1;"),"Maximum trace duration in milliseconds"));

        header.add(new th_title(new LiteralHtml("Tot&#8987;"),"Total trace wait in category in milliseconds"));
        header.add(new th_title(new LiteralHtml("Ave&#8987;"),"Average trace wait in milliseconds"));
        header.add(new th_title(new LiteralHtml("Std&#8987;"),"Standard deviation of trace wait in milliseconds"));
        header.add(new th_title(new LiteralHtml("min&#8987;"),"Minimum trace wait in milliseconds"));
        header.add(new th_title(new LiteralHtml("max&#8987;"),"Maximum trace wait in milliseconds"));

        header.add(new th_title(new LiteralHtml("&#9888;"),"Exception count and rate (in popup)"));
    }
    
    static final Style ATTENTION_STYLE=new Style().background_color(Color.rgb(255, 255, 192));
    static final Style EXCEPTION_STYLE=new Style().background_color(Color.rgb(255, 216, 216));
    void writeTraceSample(SlowTraceSampleDetector detector,TableRow row,TraceSample sample)
    {
        row.add(format_2(sample.getRate()));

        //count
        if (detector.isCountInsideTop(sample))
        {
            row.add(new td().style(ATTENTION_STYLE).addInner(sample.getCount()));
        }
        else
        {
            row.add(sample.getCount());
        }

        //total duration
        TitleText totalDuration=new TitleText(format_2(detector.getTotalDurationRatio(sample)*100.0)+"% "+Utils.millisToNiceDurationString(sample.getTotalDurationNs()/1000000),format_2(sample.getTotalDurationNs()/1.0e6));
        if (detector.isTotalDurationInsideTop(sample))
        {
            row.add(new td().style(ATTENTION_STYLE).addInner(totalDuration));
        }
        else
        {
            row.add(totalDuration);
        }
        
        //average
        TitleText averageDuration=formatNsToMs(sample.getAverageDurationNs());
        if (detector.isAverageDurationInsideTop(sample))
        {
            row.add(new td().style(ATTENTION_STYLE).addInner(averageDuration));
        }
        else
        {
            row.add(averageDuration);
        }
        
        //standard deviation
        row.add(formatNsToMs(sample.getStandardDeviationDurationNs()));
        
        //min
        Trace minDurationTrace=sample.getMinDurationTrace();
        row.add(formatNsToMs(minDurationTrace.getDurationNs(),minDurationTrace.getCreatedMs()));
        
        //max
        Trace maxDurationTrace=sample.getMaxDurationTrace();
        TitleText maxDuration=formatNsToMs(maxDurationTrace.getDurationNs(),maxDurationTrace.getCreatedMs());
        if (detector.isMaxDurationAnOutlier(sample))
        {
            row.add(new td().style(ATTENTION_STYLE).addInner(maxDuration));
        }
        else
        {
            row.add(maxDuration);
        }
//----
        //total wait
        TitleText totalWait=new TitleText(format_2(detector.getTotalWaitRatio(sample)*100.0)+"% "+Utils.millisToNiceDurationString(sample.getTotalWaitNs()/1000000),format_2(sample.getTotalWaitNs()/1.0e6));
        if (detector.isTotalWaitInsideTop(sample))
        {
            row.add(new td().style(ATTENTION_STYLE).addInner(totalWait));
        }
        else
        {
            row.add(totalWait);
        }
        //average
        TitleText averageWait=formatNsToMs(sample.getAverageWaitNs());
        if (detector.isAverageWaitInsideTop(sample))
        {
            row.add(new td().style(ATTENTION_STYLE).addInner(averageWait));
        }
        else
        {
            row.add(averageWait);
        }
        
        //standard deviation
        row.add(formatNsToMs(sample.getStandardDeviationWaitNs()));
        
        //min
        Trace minWaitTrace=sample.getMinWaitTrace();
        row.add(formatNsToMs(minWaitTrace.getWaitNs(),minWaitTrace.getCreatedMs()));
        
        //max
        Trace maxWaitTrace=sample.getMaxWaitTrace();
        TitleText maxWait=formatNsToMs(maxWaitTrace.getWaitNs(),maxWaitTrace.getCreatedMs());
        if (detector.isMaxWaitAnOutlier(sample))
        {
            row.add(new td().style(ATTENTION_STYLE).addInner(maxWait));
        }
        else
        {
            row.add(maxWait);
        }

        //exceptions 
        if (sample.getExceptionCount()>0)
        {
            TitleText exception=new TitleText("Rate (per second): "+format_3(sample.getExceptionRate()),Long.toString(sample.getExceptionCount()));
            row.add(new td().style(EXCEPTION_STYLE).addInner(exception));
        }
        else
        {
            row.add(0);
        }
    }

    

    private void buildCategorySamples(HashMap<String, TraceSample> categorySamples, Entry<String, TraceNode> entry)
    {
        TraceNode traceNode=entry.getValue();
        String category=entry.getKey();
        TraceSample sample=categorySamples.get(category);
        if (sample!=null)
        {
            traceNode.update(sample);
        }
        TraceSample result=traceNode.sampleTrace();
        if (result.getCount()>0)
        categorySamples.put(category, result);
        if (traceNode.getChildTraceNodesSnapshot() != null)
        {
            for (Entry<String, TraceNode> child : traceNode.getChildTraceNodesSnapshot().entrySet())
            {
                buildCategorySamples(categorySamples, child);
            }
        }
    }
    
    private HashMap<String, TraceSample> buildCategorySamples(Map<String, TraceNode> roots)
    {
        HashMap<String, TraceSample> categorySamples = new HashMap<>();
        for (Entry<String, TraceNode> entry : roots.entrySet())
        {
            buildCategorySamples(categorySamples, entry);
        }
        return categorySamples;
    }
    
    
    @GET
    @Path("/operator/tracing/sampleAll")
    public Element traceAllCategories() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Sample All Traces");

        form_post form=page.content().returnAddInner(new form_post()).action("/operator/tracing/all/reset");
        OperatorDataTable table=form.returnAddInner(new OperatorTable(page.head()));
        TableHeader header=new TableHeader();
        header.add("");
        addTraceSampleColumns(header);
        header.add("");
        table.setHeader(header);

        Map<String, TraceNode> roots = this.serverApplication.getTraceManager().getTraceTreeRootsSnapshot();
        HashMap<String, TraceSample> categorySamples = buildCategorySamples(roots);
        
        SlowTraceSampleDetector detector=new SlowTraceSampleDetector(categorySamples.size());
        for (TraceSample sample:categorySamples.values())
        {
            detector.update(sample);
        }

        for (Entry<String, TraceSample> entry:categorySamples.entrySet())
        {
            TraceSample sample=entry.getValue();
            TableRow row=new TableRow();
            row.add(new input_checkbox().name("~"+entry.getKey()));
            row.add(new td().addInner(entry.getKey()).style("word-wrap:break-word;word-break:break-all;"));
            writeTraceSample(detector,row,sample);
            String location=new PathAndQuery("/operator/tracing/sampleAll/category").addQuery("category", entry.getKey()).toString();
            row.add(new RightMoreLink(page.head(),location));
            table.addRow(row);

        }
        form.addInner(new hr());
        form.returnAddInner(new input_checkbox()).name("checkSelected");
        form.addInner(new LiteralHtml("&nbsp;"));
        form.returnAddInner(new input_submit()).value("Reset Selected Meters").name("resetSelected");
        form.addInner(new LiteralHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));

        form.returnAddInner(new input_checkbox()).name("checkAll");
        form.addInner(new LiteralHtml("&nbsp;"));
        form.returnAddInner(new input_submit()).value("Reset All Meters").name("resetAll");
        
        
        return page;
    }

    private void resetTraceNodes(NodeElement<?> element,Entry<String, TraceNode> entry,Queries queries)
    {
        TraceNode traceNode=entry.getValue();
        if (queries.containsName("~"+entry.getKey()))
        {
            element.addInner(entry.getKey());
            element.addInner(new hr());
            traceNode.reset();
        }
        if (traceNode.getChildTraceNodesSnapshot() != null)
        {
            for (Entry<String, TraceNode> child : traceNode.getChildTraceNodesSnapshot().entrySet())
            {
                resetTraceNodes(element,child,queries);
            }
        }
    }
    
    private void resetTraceNodes(NodeElement<?> element,Map<String, TraceNode> roots,Queries queries)
    {
        HashMap<String, TraceSample> categorySamples = new HashMap<>();
        for (Entry<String, TraceNode> entry : roots.entrySet())
        {
            resetTraceNodes(element,entry,queries);
        }
    }
    
    
    @POST
    @Path("/operator/tracing/all/reset")
    public Element traceResetTraceGraph(
            @ParamName("checkSelected") boolean checkSelected,@ParamName("resetSelected") boolean resetSelected,
            @ParamName("checkAll") boolean checkAll,@ParamName("resetAll") boolean resetAll,
            Queries queries
            ) throws Throwable
    {
        if ((checkAll)&&(resetAll))
        {
            OperatorPage page=this.serverApplication.buildOperatorPage("Reset All");
            page.content().addInner("All trace category meters reset.");
            this.serverApplication.getTraceManager().resetTraceGraph();
            return page;
        }
        if ((checkSelected)&&(resetSelected))
        {
            OperatorPage page=this.serverApplication.buildOperatorPage("Reset Selected");
            page.content().addInner("Trace Categories Reset:");
            page.content().addInner(new hr());
            resetTraceNodes(page.content(),this.serverApplication.getTraceManager().getTraceTreeRootsSnapshot(),queries);
            return page;
        }
        return traceAllCategories();
    }
    
    
    @GET
    @Path("/operator/tracing/sampleWatchTraceBuffer")
    public Element sampleLastWatchTraces() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Sample Watch Trace Buffer");
        if (this.serverApplication.getTraceManager().isEnableLastTraceWatching())
        {
            CategorySample[] samples=this.serverApplication.getTraceManager().sampleLastWatchTraces();
            OperatorDataTable table=page.content().returnAddInner(new OperatorTable(page.head()));

            TableHeader header=new TableHeader();
            addTraceSampleColumns(header);
            header.add("");
            table.setHeader(header);
            
            SlowTraceSampleDetector detector=new SlowTraceSampleDetector(samples.length);
            for (CategorySample sample:samples)
            {
                detector.update(sample.getSample());
            }

            for (CategorySample sample:samples)
            {
                TableRow row=new TableRow();
                row.add(new TitleText(sample.getCategory(),80));
                writeTraceSample(detector,row,sample.getSample());
                String location=new PathAndQuery("./trace").addQuery("category", sample.getCategory()).toString();
                row.add(new RightMoreLink(page.head(),location));
                table.addRow(row);

            }
            return page;
        }
        else
        {
            page.content().addInner("Trace watching is disabled.");
            return page;
        }

    }
    
    @GET
    @Path("/operator/tracing/sampleWatch")
    public Element sampleWatch() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Sample Watch Traces");
        if (this.serverApplication.getTraceManager().isEnableLastTraceWatching())
        {
            CategorySample[] samples=this.serverApplication.getTraceManager().sampleWatchCategories();
            OperatorDataTable table=page.content().returnAddInner(new OperatorTable(page.head()));

            TableHeader header=new TableHeader();
            addTraceSampleColumns(header);
            header.add("");
            table.setHeader(header);

            SlowTraceSampleDetector detector=new SlowTraceSampleDetector(samples.length);
            for (CategorySample sample:samples)
            {
                detector.update(sample.getSample());
            }

            for (CategorySample sample:samples)
            {
                TableRow row=new TableRow();
                row.add(new TitleText(sample.getCategory(),80));
                writeTraceSample(detector,row,sample.getSample());
                String location=new PathAndQuery("./trace").addQuery("category", sample.getCategory()).toString();
                row.add(new RightMoreLink(page.head(),location));
                table.addRow(row);

            }
            return page;
        }
        else
        {
            page.content().addInner("Trace watching is disabled.");
            return page;
        }

    }
    
    @GET
    @Path("/operator/tracing/sampleWatchAndReset")
    public Element sampleWatchAndReset() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Sample and Reset Watch Traces");
        if (this.serverApplication.getTraceManager().isEnableLastTraceWatching())
        {
            CategorySample[] samples=this.serverApplication.getTraceManager().sampleAndResetWatchCategories();
            OperatorDataTable table=page.content().returnAddInner(new OperatorTable(page.head()));

            TableHeader header=new TableHeader();
            addTraceSampleColumns(header);
            header.add("");
            table.setHeader(header);
            
            SlowTraceSampleDetector detector=new SlowTraceSampleDetector(samples.length);
            for (CategorySample sample:samples)
            {
                detector.update(sample.getSample());
            }

            for (CategorySample sample:samples)
            {
                TableRow row=new TableRow();
                row.add(new TitleText(sample.getCategory(),80));
                writeTraceSample(detector,row,sample.getSample());
                String location=new PathAndQuery("./trace").addQuery("category", sample.getCategory()).toString();
                row.add(new RightMoreLink(page.head(),location));
                table.addRow(row);

            }
            return page;
        }
        else
        {
            page.content().addInner("Trace watching is disabled.");
            return page;
        }

    }
    
    
    
    
    private div formatNsToMsWithInstantMsAsDiv(long ns,long instantMs)
    {
        double ms=ns/1.0e6;
        return new div().addInner(formatNsToMs(ns)).addInner(" ms on "+DateTimeUtils.toSystemDateTimeString(instantMs));
    }
    
    static private div divFormatNsToMs(double ns)
    {
        return new div().addInner(formatNsToMs(ns)).addInner(" ms");
    }
    private void writeTraceSample(Head head,NodeElement<?> content,TraceSample sample) throws Exception
    {
        Panel3 panel=content.returnAddInner(new Panel3(head,"Stats"));
        NameValueList list=panel.content().returnAddInner(new NameValueList(new Size(20,unit.em)));
        list.add("Rate", String.format("%.3f", sample.getRate())+" per second");
        list.add("Count", sample.getCount());
        list.add("Average Duration", divFormatNsToMs(sample.getAverageDurationNs()));
        list.add("Total Duration", divFormatNsToMs(sample.getTotalDurationNs()));
        list.add("Duration standard deviation ", divFormatNsToMs(sample.getStandardDeviationDurationNs()));
        list.add("Average Wait", divFormatNsToMs(sample.getAverageWaitNs()));
        list.add("Total Wait", divFormatNsToMs(sample.getTotalWaitNs()));
        list.add("Wait standard deviation ", divFormatNsToMs(sample.getStandardDeviationWaitNs()));
        list.add("Exceptions", sample.getExceptionCount());
        writeTrace(head,content,"Last Trace",sample.getLastTrace());
        writeTrace(head,content,"Minimum Duration Trace",sample.getMinDurationTrace());
        writeTrace(head,content,"Maximum Duration Trace",sample.getMaxDurationTrace());
        writeTrace(head,content,"Minimum Wait Trace",sample.getMinWaitTrace());
        writeTrace(head,content,"Maximum Wait Trace",sample.getMaxWaitTrace());
        if (sample.getExceptionCount()>0)
        {
            writeTrace(head,content,"First Exception Trace",sample.getFirstExceptionTrace());
            if (sample.getExceptionCount()>1)
            {
                writeTrace(head,content,"Last Exception Trace",sample.getLastExceptionTrace());
            }
        }
    }
    
    static public Element formatStackTrace(Head head,String heading,StackTraceElement[] stackTrace)
    {
        Accordion accordion=new Accordion(head, false, heading);
        accordion.content().addInner(new textarea().style("width:100%;border:0;").readonly().rows(stackTrace.length+1).addInner(Utils.toString(stackTrace)));
        return accordion;
    }
    static public Element formatStackTrace(Head head,String heading,StackTraceElement[] stackTrace,int start)
    {
        Accordion accordion=new Accordion(head, false, heading);
        accordion.content().addInner(new textarea().style("width:100%;border:0;").readonly().rows(stackTrace.length+1-start).addInner(Utils.toString(stackTrace,start)));
        return accordion;
    }
    static public Element formatThrowable(Head head,String heading,Throwable throwable) throws Exception
    {
        Accordion accordion=new Accordion(head, true, heading);
        String text=Utils.getStrackTraceAsString(throwable);
        int occurs=Utils.occurs(text,"\n");
        accordion.content().addInner(new textarea().style("width:100%;border:0;").readonly().rows(occurs+1).addInner(text));
        return accordion;
    }
    static public void writeTrace(Head head,NodeElement<?> content,String title,Trace trace) throws Exception
    {
        Panel4 panel=content.returnAddInner(new Panel4(head,title));
        NameValueList list=panel.content().returnAddInner(new NameValueList(new Size(20,unit.em)));
        list.add("Number",trace.getNumber());
        list.add("Category",trace.getCategory());
        list.add("Thread (id:name)", trace.getThread().getId()+":"+trace.getThread().getName());
        list.add("Created",Utils.millisToLocalDateTime(trace.getCreatedMs())+", "+Utils.millisToNiceDurationString(System.currentTimeMillis()-trace.getCreatedMs())+" ago ");
        list.add("Closed", trace.isClosed()); 
        list.add("Duration", divFormatNsToMs(trace.getDurationNs()));
        if (trace.isClosed()==false)
        {
            list.add("Waiting", trace.isWaiting());
            list.add("Active", divFormatNsToMs(trace.getActiveNs()));
        }
        list.add("Wait", divFormatNsToMs(trace.getWaitNs()));
        if (trace.getFromLink()!=null)
        {
            list.add("From Link", trace.getFromLink());
        }
        if (trace.getToLink()!=null)
        {
            list.add("To Link", trace.getToLink());
        }
        if (trace.getDetails()!=null)
        {
            list.add("Details",trace.getCategory());
        }
        if (trace.getParent()!=null)
        {
            list.add("Parent Number", trace.getParent().getNumber());
            list.add("Parent Category", trace.getParent().getCategory());
        }
        if (trace.getRoot()!=null)
        {
            list.add("Root Number", trace.getRoot().getNumber());
            list.add("Root Category", trace.getRoot().getCategory());
        }
        if (trace.getCreateStackTrace()!=null)
        {
            list.add("Create Stack Trace",formatStackTrace(head,null, trace.getCreateStackTrace()));
        }
        if (trace.getCloseStackTrace()!=null)
        {
            list.add("Close Stack Trace",formatStackTrace(head,null, trace.getCloseStackTrace()));
        }
        if (trace.getThrowable()!=null)
        {
            list.add("Exception",formatThrowable(head,null,trace.getThrowable()));
        }
        
    }
    
    @GET
    @Path("/operator/tracing/sampleCurrent/category")
    public Element sampleCurrentCategory(@QueryParam("category") String category) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Trace Category: "+category);
        
        CategorySample[] samples=this.serverApplication.getTraceManager().sampleCurrentTraceCategories(false);
        for (CategorySample sample:samples)
        {
            if (Utils.equals(sample.getCategory(),category))
            {
                writeTraceSample(page.head(),page.content(),sample.getSample());
                return page;
            }
        }
        page.content().addInner(category+" has expired or does not exist.");
        return page;
    }
    
    @GET
    @Path("/operator/tracing/sampleAll/category")
    public Element sampleAllCategory(@QueryParam("category") String category) throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Trace Category: "+category);
        Map<String, TraceNode> roots = this.serverApplication.getTraceManager().getTraceTreeRootsSnapshot();
        HashMap<String, TraceSample> samples = buildCategorySamples(roots);
        TraceSample sample=samples.get(category);
        if (sample==null)
        {
            page.content().addInner(category+" has expired or does not exist.");
            return page;
        }
        writeTraceSample(page.head(),page.content(),sample);
        return page;
    }
    
    @GET
    @Path("/operator/tracing/settings")
    public Element traceManagerSettings() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("TraceManager Settings");
        form_get form=page.content().returnAddInner(new form_get()).action("/operator/tracing/settings/change");
        fieldset fieldset=form.returnAddInner(new fieldset());
        fieldset.addInner(new legend().addInner("Settings"));
        TraceManager traceManager=this.serverApplication.getTraceManager();
        
        fieldset.returnAddInner(new p()).addInner(new input_checkbox().checked(traceManager.isLogExceptionTraces()).name("isLogExceptionTraces")).addInner(("Log exception traces. Performance overhead depends on traces having exceptions and ranges from minimal to high."));
        fieldset.returnAddInner(new p()).addInner(new input_checkbox().checked(traceManager.isCaptureCreateStackTrace()).name("isCaptureCreateStackTrace")).addInner(("Capture stack trace when trace is created. Performance overhead is high."));
        fieldset.returnAddInner(new p()).addInner(new input_checkbox().checked(traceManager.isCaptureCloseStackTrace()).name("isCaptureCloseStackTrace")).addInner(("Capture stack trace when trace is closed. Performance overhead is high."));
        fieldset.returnAddInner(new p()).addInner(new input_checkbox().checked(traceManager.isLogTraces()).name("isLogTraces")).addInner(("Log all traces. Performance overhead is high."));
        
        long logTracesWithGreaterDuration=traceManager.getLogTracesWithGreaterDuration();
        boolean enableLogTracesWithGreaterDuration;
        if (logTracesWithGreaterDuration>=0)
        {
            enableLogTracesWithGreaterDuration=true;
        }
        else
        {
            enableLogTracesWithGreaterDuration=false;
            logTracesWithGreaterDuration=-logTracesWithGreaterDuration;
        }
        
        fieldset.returnAddInner(new p()).
        addInner(new input_checkbox().checked(enableLogTracesWithGreaterDuration).name("enableLogTracesWithGreaterDuration")).
        addInner("Log traces with duration greater or equal to ").
        addInner(new input_number().min(0).value(logTracesWithGreaterDuration).style("width:6em;").name("logTracesWithGreaterDuration")).
        addInner(" ms. Performance overhead depends on number of traces logged and ranges from minimal to high.");
        
        fieldset.returnAddInner(new hr());
        fieldset.returnAddInner(new p()).addInner(new input_checkbox().name("change")).addInner(new input_submit().value("Change")).addInner(new LiteralHtml("&nbsp;&nbsp;&nbsp;")).addInner(new input_reset());
//        fieldset.returnAddInner(new p()).addInner(new input_checkbox().checked(traceManager.isEnableWatchListLastTraces()).name("isEnableWatchListLastTraces").addInner(("Enable watch list of last traces. Performance overhead: Low")));
        return page;
    }

    @GET
    @Path("/operator/tracing/settings/change")
    public Element traceManagerChangeSettings(@ParamName("change") boolean change,Queries parameters,@QueryParam ("logTracesWithGreaterDuration") long logTracesWithGreaterDuration) throws Throwable
    {
        if (change==false)
        {
            return traceManagerSettings();
        }
        OperatorPage page=this.serverApplication.buildOperatorPage("TraceManager Settings");
        TraceManager traceManager=this.serverApplication.getTraceManager();
        traceManager.setCaptureCreateStackTrace(parameters.containsName("isCaptureCreateStackTrace"));
        traceManager.setCaptureCloseStackTrace(parameters.containsName("isCaptureCloseStackTrace"));
        traceManager.setLogExceptionTraces(parameters.containsName("isLogExceptionTraces"));
        traceManager.setLogTraces(parameters.containsName("isLogTraces"));
        if (parameters.containsName("enableLogTracesWithGreaterDuration"))
        {
            traceManager.setLogTracesWithGreaterDuration(logTracesWithGreaterDuration);
        }
        else
        {
            traceManager.setLogTracesWithGreaterDuration(-logTracesWithGreaterDuration);
        }
        page.content().addInner("Settings changed. ");
        page.content().addInner(new a().addInner("View settings.").href("/operator/tracing/settings"));
        return page;
    }

    @GET
    @Path("/operator/tracing/stats")
    public Element traceManagerStats() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("TraceManager Stats");
        RateSample sample=this.serverApplication.getTraceManager().getRateMeter().sample();
        Panel3 panel=page.content().returnAddInner(new Panel3(page.head(),"Trace Stats"));
        NameValueList list=panel.content().returnAddInner(new NameValueList());
        list.add("Rate", format_3(sample.getRate())+"");
        list.add("Total", sample.getTotalCount());
        list.add("CurrentTracesOverflowCount", this.serverApplication.getTraceManager().getCurrentTracesOverflowCount());
        return page;
    }

    /*
    @GET
    @Path("/operator/tracing/stats")
    public Element traceManagerStats() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("TraceManager Stats");
        TraceManager traceManager=this.serverApplication.getTraceManager();
        traceManager.
        traceManager.setCaptureCreateStackTrace(parameters.containsName("isCaptureCreateStackTrace"));
        traceManager.setCaptureCloseStackTrace(parameters.containsName("isCaptureCloseStackTrace"));
        traceManager.setEnableLastExceptions(parameters.containsName("isEnableLastExceptions"));
        traceManager.setEnableLastTraces(parameters.containsName("isEnableLastTraces"));
        traceManager.setEnableTraceGraph(parameters.containsName("isEnableTraceGraph"));
        traceManager.setEnableTraceStats(parameters.containsName("isEnableTraceStats"));
        traceManager.setLogExceptionTraces(parameters.containsName("isLogExceptionTraces"));
        traceManager.setLogTraces(parameters.containsName("isLogTraces"));
        page.content().addInner("Settings changed. ");
        page.content().addInner(new a().addInner("View settings.").href("/operator/tracing/settings")
    */
    
    private LiteralHtml toLiteralHtml(StackTraceElement[] elements, int start)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < elements.length; i++)
        {
            sb.append("at " + elements[i].toString() + "<br/>");
        }
        return new LiteralHtml(sb.toString());
    }


    @GET
    @Path("/operator/tracing/lastTraces")
    public Element lastTraces() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Last Traces");
        writeTraces(page, this.serverApplication.getTraceManager().getLastTraces(),"/operator/traces/clear/lastTraces",false);
        return page;
    }
    @GET
    @Path("/operator/tracing/watchListLastTraces")
    public Element watchListLastTraces() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Last Watch Traces");
        if (this.serverApplication.getTraceManager().isEnableLastTraceWatching())
        {
            page.content().addInner("Trace watching is enabled.");
            writeTraces(page, this.serverApplication.getTraceManager().getLastWatchTraces(),"/operator/traces/clear/watchLastTraces",false);
        }
        else
        {
            page.content().addInner("Trace watching is disabled.");
        }
        return page;
    }

    @GET
    @Path("/operator/tracing/lastExceptions")
    public Element lastExeptions() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Last Exception Traces");
        writeTraces(page, this.serverApplication.getTraceManager().getLastExceptionTraces(),"/operator/traces/clear/lastExceptions",true);
        return page;
    }

    @GET
    @Path("/operator/tracing/lastSecondaryExceptions")
    public Element lastSecondaryExeptions() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Last Secondary Exception Traces");
        writeTraces(page, this.serverApplication.getTraceManager().getLastSecondaryExceptionTraces(),"/operator/traces/clear/lastSecondaryExceptions",false);
        return page;
    }

    @GET
    @Path("/operator/tracing/currentTraces")
    public Element activeTraces() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Current Traces");
        writeTraces(page, this.serverApplication.getTraceManager().getCurrentTraces(),null,false);
        return page;
    }

    private void writeTraces(OperatorPage page, Trace[] traces,String action,boolean enableSecondary) throws Throwable
    {
        if ((action!=null)&&(traces.length>0))
        {
            ConfirmButton confirm=new ConfirmButton(action, "Clear").style("float:right;");
            page.content().addInner(confirm);
            page.content().addInner(new br());
            page.content().addInner(new hr());
        }
        OperatorDataTable table=page.content().returnAddInner(new OperatorDataTable(page.head()));
        table.lengthMenu(10,25,50,100,-1);
        table.setHeader("#","Traces");
        for (int i = traces.length - 1; i >= 0; i--)
        {
            Trace trace=traces[i];
            TraceWidget traceWidget=new TraceWidget(page.head(), trace,true,false,false);
            if (enableSecondary)
            {
                traceWidget.enableSecondaryButton();
            }
            table.addRow(trace.getNumber(),traceWidget);
        }
    }
    
    @GET
    @Path("/operator/traces/exception/secondary")
    public Element addSecondary(@QueryParam("check") boolean check,@QueryParam("category") String category) throws Throwable
    {
        this.serverApplication.getTraceManager().addSecondaryCategories(new String[]{category});
        return lastExeptions();
    }

    @GET
    @Path("/operator/traces/exception/console")
    public Element printStackTrace(@QueryParam("category") String category) throws Throwable
    {
        this.serverApplication.getTraceManager().addSecondaryCategories(new String[]{category});
        return lastExeptions();
    }

    @GET
    @Path("/operator/traces/clear/lastTraces")
    public Element clearLastTraces() throws Throwable
    {
        this.serverApplication.getTraceManager().clearLastTraces();
        return new Redirect("/operator/tracing/lastTraces");
    }

    @GET
    @Path("/operator/traces/clear/lastExceptions")
    public Element clearLastExceptions() throws Throwable
    {
        this.serverApplication.getTraceManager().clearLastExceptionTraces();
        return new Redirect("/operator/tracing/lastExceptions");
    }
    
    @GET
    @Path("/operator/traces/clear/lastSecondaryExceptions")
    public Element clearLastSecondaryExceptions() throws Throwable
    {
        this.serverApplication.getTraceManager().clearLastSecondaryExceptionTraces();
        return new Redirect("/operator/tracing/lastSecondaryExceptions");
    }
    
    div buildCountRatio(int width,long value,long total)
    {
        double percentage=total>0?100.0*value/total:0;
        div div=new div().addInner(value).style("text-align:right;border:1px solid;margin:0;padding:4px;width:"+width+"px;background:linear-gradient(to right, #fff 0%, #fff "+(100.0-percentage)+"%, #ccc "+(100.0-percentage)+"%, #ccc 100%);");
        return div;
    }
    div buildDurationRatio(int width,long value,long total)
    {
        double percentage=total>0?100.0*value/total:0;
        
        div div=new div().addInner(Utils.millisToDurationString(value)).style("text-align:right;border:1px solid;margin:0;padding:4px;width:"+width+"px;background:linear-gradient(to right, #fff 0%, #fff "+(100.0-percentage)+"%, #ccc "+(100.0-percentage)+"%, #ccc 100%);");
        return div;
    }

    @GET
    @Path("/operator/httpServer/performance/{server}")
    public Element performance(@PathParam("server")String server) throws Throwable
    {
        OperatorPage page=buildServerOperatorPage("Last Performance",server,null);
        HttpServer httpServer=getHttpServer(server);
        RateMeter requestRateMeter = httpServer.getRequestRateMeter();
        RateSample sample=requestRateMeter.sample();
        WideTable infoTable=page.content().returnAddInner(new WideTable(page.head()));
        infoTable.setHeader("Request Rate","Total Requests");
        infoTable.addRow(DOUBLE_FORMAT.format(sample.getRate()),sample.getSamples());
        
        page.content().addInner(new p());
        Panel requestHandlerPanel=page.content().returnAddInner(new Panel2(page.head(),"RequestHandlers"));
        OperatorDataTable table=requestHandlerPanel.content().returnAddInner(new OperatorTable(page.head()));
        TableHeader header=new TableHeader();
        header.add("Method")
            .add(new th_title("Count","Number of requests in last sample"))
            .add(new th_title("Duration","Total duration of requests in days hours:minutes:seconds.milliseconds"))
            .add(new th_title("Ave Dur","Average duration of requests in milliseconds"))
            .add(new th_title("Rate","Request rate per second"))
            .add(new th_title("ReqSize","Average uncompressed size of request content in bytes"))
            .add(new th_title("RespSize","Average uncompressed size of response content in bytes"))
            .add("");
        table.setHeader(header);

        double totalAll = 0;
        long totalCount = 0;
        RequestHandler[] requestHandlers = httpServer.getRequestHandlers();
        HashMap<String,Map<Integer, LongValueSample>> statusResults=new HashMap<>();
        for (RequestHandler requestHandler : requestHandlers)
        {
            Map<Integer, LongValueSample> results = requestHandler.sampleStatusMeters();
            for (LongValueSample result : results.values())
            {
                totalAll += result.getTotal();
                totalCount += result.getSamples();
            }
            statusResults.put(requestHandler.getKey(),results);
        }
        for (RequestHandler requestHandler : requestHandlers)
        {
            Map<Integer, LongValueSample> results = statusResults.get(requestHandler.getKey());
            long total = 0;
            long count = 0;
            double rate = 0;
            for (LongValueSample result : results.values())
            {
                total += result.getTotal();
                count += result.getSamples();
                rate += result.getRate();
            }

            TableRow row=new TableRow();
            row.add(requestHandler.getHttpMethod() + " " + requestHandler.getPath())
            .add(buildCountRatio(100, count,totalCount))
            .add(buildDurationRatio(100,(long)(total/1.0e6),(long)(totalAll/1.0e6)))
            .add(count > 0 ? DOUBLE_FORMAT.format(total / (1.0e6 * count)) : 0)
            .add(DOUBLE_FORMAT.format(rate))
            .add(requestHandler.getRequestUncompressedContentSizeMeter().sample().getWeightedAverage(0))
            .add(requestHandler.getResponseUncompressedContentSizeMeter().sample().getWeightedAverage());
            row.add(new RightMoreLink(page.head(),new PathAndQuery("/operator/httpServer/info").addQuery("key", requestHandler.getKey()).addQuery("server", server).toString()));
            table.addRow(row);
            
        }
        return page;
    }

    @GET
    @Path("/operator/httpServer/allPerformance/{server}")
    public Element allPerformance(@PathParam("server")String server) throws Throwable
    {
        OperatorPage page=buildServerOperatorPage("All Performance",server,null);
        HttpServer httpServer=getHttpServer(server);
        RateMeter requestRateMeter = httpServer.getRequestRateMeter();
        RateSample sample=requestRateMeter.sample();
        WideTable infoTable=page.content().returnAddInner(new WideTable(page.head()));
        infoTable.setHeader("Request Rate","Total Requests");
        infoTable.addRow(DOUBLE_FORMAT.format(sample.getRate()),sample.getSamples());
        
        page.content().addInner(new p());
        Panel requestHandlerPanel=page.content().returnAddInner(new Panel2(page.head(),"RequestHandlers"));
        OperatorDataTable table=requestHandlerPanel.content().returnAddInner(new OperatorTable(page.head()));
        TableHeader header=new TableHeader();
        header.add("Method")
            .add(new th_title("Count","Count of total requests"))
            .add(new th_title("Duration","Total duration in method in days hours:minutes:seconds.milliseconds"))
            .add(new th_title("Ave Dur","Average duration of requests in milliseconds"))
            .add(new th_title("Ave ReqSize","Average uncompressed size of request content in bytes"))
            .add(new th_title("Min ReqSize","Minimum uncompressed size of request content in bytes"))
            .add(new th_title("Max ReqSize","Maximum uncompressed size of request content in bytes"))
            .add(new th_title("Ave RespSize","Average uncompressed size of response content in bytes"))
            .add(new th_title("Min RespSize","Minimum uncompressed size of response content in bytes"))
            .add(new th_title("Max RespSize","Maximum uncompressed size of response content in bytes"))
            .add("");
        table.setHeader(header);

        double totalAll = 0;
        long totalCount = 0;
        RequestHandler[] requestHandlers = httpServer.getRequestHandlers();
        HashMap<String,Map<Integer, LongValueMeter>> statusResults=new HashMap<>();
        for (RequestHandler requestHandler : requestHandlers)
        {
            Map<Integer, LongValueMeter> results = requestHandler.getStatusMeters();
            for (LongValueMeter result : results.values())
            {
                totalAll += result.getTotal();
                totalCount += result.getTotalCount();
            }
            statusResults.put(requestHandler.getKey(),results);
        }
        for (RequestHandler requestHandler : requestHandlers)
        {
            Map<Integer, LongValueMeter> results = statusResults.get(requestHandler.getKey());
            long total = 0;
            long count = 0;
            for (LongValueMeter result : results.values())
            {
                total += result.getTotal();
                count += result.getTotalCount();
            }
            LongValueSample requestSample=requestHandler.getRequestUncompressedContentSizeMeter().sample();
            LongValueSample responseSample=requestHandler.getResponseUncompressedContentSizeMeter().sample();

            TableRow row=new TableRow();
            row.add(requestHandler.getHttpMethod() + " " + requestHandler.getPath())
            .add(buildCountRatio(100, count,totalCount))
            .add(buildDurationRatio(100,(long)(total/1.0e6),(long)(totalAll/1.0e6)))
            .add(count > 0 ? DOUBLE_FORMAT.format(total / (1.0e6 * count)) : 0);
            if (requestSample.getMeterTotalCount()>0)
            {
                row.add((long)requestSample.getMeterAverage(0))
                .add(requestSample.getMin())
                .add(requestSample.getMax());
            }
            else
            {
                row.add("")
                .add("")
                .add("");
                
            }
            if (responseSample.getMeterTotalCount()>0)
            {
                row.add((long)responseSample.getMeterAverage(0))
                .add(responseSample.getMin())
                .add(responseSample.getMax());
            }
            else
            {
                row.add("")
                .add("")
                .add("");
                
            }
            
            row.add(new RightMoreLink(page.head(),new PathAndQuery("/operator/httpServer/info").addQuery("key", requestHandler.getKey()).addQuery("server", server).toString()));
            table.addRow(row);
            
        }
        return page;
    }

    private void writeTraceRowHeading(TableHeader header)
    {
//      row.addInner(new td().style("width:5em;").addInner(new TitleText("Trace Number","#")));
        header.add(new th().style("width:7em;").addInner(new TitleText("Trace Number","#")));
        header.add(new th().style("width:40em;").addInner("Category"));
        header.add(new th().style("width:13em;").addInner(new TitleText("When the thread was created","Created")));
        header.add(new TitleText("Amount of time active in milliseconds","Active"));
        
        header.add(new TitleText("Amount of time waiting in milliseconds","Wait"));
        header.add(new TitleText("StatusDuration in milliseconds","Duration"));
        header.add(new TitleText("Trace status","Status"));
        header.add(new TitleText("Type of code running","Code"));
//        header.add(new TitleText("Thread id and name shown as id:name","Thread"));
        header.add(new TitleText("Parent trace number","Parent"));
        header.add(new TitleText("Root trace number","Root"));
//        row.add(new TitleText("Thread details","Details"));
    }
    private void writeTraceRow(TableRow row,Trace trace)
    {
        row.add(trace.getNumber());
        row.add(new TitleText(trace.getCategory(),60));
        row.add(Utils.millisToLocalDateTime(trace.getCreatedMs()));
//                    .add(new TitleText(trace.getDetails(),60))
        row.add(formatNsToMs(trace.getActiveNs()));
        row.add(formatNsToMs(trace.getWaitNs()));
        row.add(formatNsToMs(trace.getDurationNs()));
        if (trace.getThrowable()!=null)
        {
            row.add("Exception");
        }
        else if (trace.isClosed())
        {
            row.add("Closed");
        }
        else if (trace.isWaiting())
        {
            row.add("Waiting");
        }
        else
        {
            row.add("Active");
        }

        
        StackTraceElement[] elements=trace.getThread().getStackTrace();
        String status;
        if (elements.length==0)
        {
            status="INVALID";
        }
        else if (elements[0].isNativeMethod())
        {
            status="NATIVE";
        }
        else
        {
            String methodName=elements[0].getMethodName();
            String className=elements[0].getClassName();
            if ((className.contains("java.lang.Object")&&methodName.contains("wait"))||(className.contains("java.lang.Thread")&&methodName.contains("sleep")))
            {
                if (methodName.contains("wait"))
                {
                    status="WAIT";
                }
                else
                {
                    status="SLEEP";
                }
            }
            else
            {
                status="USER";
            }
        }
        row.add(new td().addInner(status).title(Utils.toString(elements)));
        if (trace.getParent()!=null)
        {
            row.add(trace.getParent().getNumber());
        }
        else
        {
            row.add("-");
        }
        if (trace.getRoot()!=null)
        {
            row.add(trace.getRoot().getNumber());
        }
        else
        {
            row.add("-");
        }
//        row.add(trace.getThread().getId()+":"+trace.getThread().getName());
    }
    
    private void writeTrace(Head head,NodeElement<?> content,Trace trace,boolean includeStackTraces) throws Exception
    {
        WideTable table=content.returnAddInner(new WideTable(head));
        TableHeader header=new TableHeader();
        writeTraceRowHeading(header);
        table.setHeader(header);
    
        TableRow row=new TableRow();
        writeTraceRow(row,trace);
        table.addRow(row);

        NameValueList list=null;
        String details = trace.getDetails();
        if (details != null)
        {
            if (list==null)
            {
                list=new NameValueList();
            }
            list.add("Details",details);
        }

        String fromLink = trace.getFromLink();
        if (fromLink != null)
        {
            if (list==null)
            {
                list=new NameValueList();
            }
            list.add("FromLink",fromLink);
        }

        String toLink = trace.getToLink();
        if (toLink != null)
        {
            if (list==null)
            {
                list=new NameValueList();
            }
            list.add("ToLink",toLink);
        }
        if (list!=null)
        {
            Panel listPanel=content.returnAddInner(new Panel4(head,"Additional Trace Info"));
            listPanel.content().addInner(list);
        }

        content.addInner(new p());
        if (trace.getThrowable() != null)
        {
            content.addInner(formatThrowable(head, "Exception Stack Trace" ,trace.getThrowable()));
            content.addInner(new p());
        }
        if (includeStackTraces)
        {
            if (trace.isClosed()==false)
            {
                StackTraceElement[] currentStackTrace = trace.getThread().getStackTrace();
                if (currentStackTrace != null)
                {
                    content.addInner(formatStackTrace(head, "Current Stack Trace",currentStackTrace ));
//                    Accordion accordion=content.returnAddInner(new Accordion(head, null, false, "Current Stack Trace"));
//                    accordion.content().addInner(toString(currentStackTrace, 0));
                }
            }
            StackTraceElement[] createStackTrace = trace.getCreateStackTrace();
            if (createStackTrace != null)
            {
                content.addInner(formatStackTrace(head, "Create Stack Trace",createStackTrace));
//                Accordion accordion=content.returnAddInner(new Accordion(head, null, false, "Create Stack Trace"));
//                accordion.content().addInner(toString(createStackTrace, 0));
            }
            StackTraceElement[] closeStackTrace = trace.getCloseStackTrace();
            if (closeStackTrace != null)
            {
                content.addInner(formatStackTrace(head, "Close Stack Trace",closeStackTrace));
//                Accordion accordion=content.returnAddInner(new Accordion(head, null, false, "Close Stack Trace"));
//                accordion.content().addInner(toString(closeStackTrace, 0));
            }
        }
    }
    
    private void writeHeaders(Head head,Panel panel,String headers)
    {
        if (headers==null)
        {
            return;
        }
        panel.content().addInner(new p());
        String[] array=Utils.split(headers, '\r');
        Accordion accordion=panel.content().returnAddInner(new Accordion(head,null,false, "Headers (Length: "+(array.length-1)+")"));
        NameValueList list=accordion.content().returnAddInner(new NameValueList());
        for (String item:array)
        {
            int index=item.indexOf(':');
            if (index<0)
            {
                continue;
            }
            String name=item.substring(0, index);
            String value=item.substring(index+1);
            list.add(name,value);
        }
    }    
    private void writeHeaders(Head head,String heading,NodeElement<?> content,String headers)
    {
        if (headers==null)
        {
            return;
        }
        content.addInner(new p());
        String[] array=Utils.split(headers, '\r');
        Accordion accordion=content.returnAddInner(new Accordion(head,null,false, heading+", count: "+(array.length-1)));
        NameValueList list=accordion.content().returnAddInner(new NameValueList());
        for (String item:array)
        {
            int index=item.indexOf(':');
            if (index<0)
            {
                continue;
            }
            String name=item.substring(0, index);
            String value=item.substring(index+1);
            list.add(name,value);
        }
    }    
    
    private void writeContent(Head head,String heading,NodeElement<?> content,String text,String contentType)
    {
        if (text==null)
        {
            return;
        }
//        panel.content().addInner(new p());
//        Panel textpanel=panel.content().returnAddInner(new Level3Panel(head,"Request Content (Length: "+text.length()+")"));
        Accordion textAccodion=content.returnAddInner(new Accordion(head,false,heading+", length: "+text.length()));
        int rows=text.length()/120+2;
        if (rows>20)
        {
            rows=20;
        }
//      textAccodion.content().addInner(new textarea().readonly().style("width:100%;resize:none;").addInner(text).rows(rows));
//        if ((contentType!=null)&&(contentType.toLowerCase().contains("/html")))
//        {
//            text=HtmlUtils.toHtmlText(text);
//        }
        textAccodion.content().addInner(new textarea().readonly().style("width:100%;").addInner(new LiteralHtml(text)).rows(rows));
    }
    
//    private void writeRequest(OperatorDataTable dataTable,OperatorPage page,RequestLogEntry entry) throws Exception
//    {
//        Head head=page.head();
//        String title;
//        if (entry.getQueryString()!=null)
//        {
//            title=entry.getStatusCode()+" | "+entry.getRemoteEndPoint()+" | "+entry.getRequest()+"?"+entry.getQueryString();
//        }
//        else
//        {
//            title=entry.getStatusCode()+" | "+entry.getRemoteEndPoint()+" | "+entry.getRequest();
//        }
//        Panel panel=new Panel3(page.head(),title);
//        panel.style("width:100%;");
//
//        tr tr=dataTable.tbody().returnAddInner(new tr());
//        tr.addInner(new td().addInner(new Text(entry.getTrace().getNumber())));
//        tr.addInner(new td().style("height:100%").addInner(panel));
//
//        writeTrace(head,panel.content(),entry.getTrace(),false);
//        
//        writeHeaders(head,"Request Headers",panel.content(),entry.getRequestHeaders());
//        writeHeaders(head,"Request Parameters",panel.content(),entry.getRequestParameters());
//        writeContent(head,"Request Content",panel.content(),entry.getRequestContentText(),null);
//
//        writeHeaders(head,"Response Headers",panel.content(),entry.getResponseHeaders());
//        writeContent(head,"Response Content",panel.content(),entry.getResponseContentText(),entry.getContentType());
//    }    
    private Element writeRequestLogEntries(Head head, RequestLogEntry[] entries) throws Throwable
    {
        OperatorDataTable dataTable=new OperatorDataTable(head);
        dataTable.setHeader("Number","Entry");
        dataTable.lengthMenu(-1,5,10,25);
        for (RequestLogEntry entry : entries)
        {
//            writeRequest(dataTable,page, entry);
            HttpRequestWidget element=new HttpRequestWidget(head,entry);

            TableRow row=new TableRow();
            row.add(entry.getTrace().getNumber());
            row.add(element);
            dataTable.addRow(row);
        }
        return dataTable;
    }

    @Log(lastRequestsInMemory=false,responseContent=false)
    @GET
    @Path("/operator/httpServer/lastRequests/{server}")
    public Element lastRequests(@PathParam("server") String server) throws Throwable
    {
        HttpServer httpServer=this.getHttpServer(server);
        OperatorPage page=buildServerOperatorPage("Last Requests", server,"lastRequests");
        
        RequestLogEntry[] entries = httpServer.getLastRequestLogEntries();
        page.content().addInner(writeRequestLogEntries(page.head(), entries));
        return page;
    }

    @Log(lastRequestsInMemory=false,responseContent=false)
    @GET
    @Path("/operator/httpServer/lastExceptionRequests/{server}")
    public Element lastExceptionRequests(@PathParam("server") String server) throws Throwable
    {
        
        HttpServer httpServer=this.getHttpServer(server);
        OperatorPage page=buildServerOperatorPage("Last Exception Requests", server,"lastExceptionRequests");
        RequestLogEntry[] entries = httpServer.getLastExceptionRequestLogEntries();
        page.content().addInner(writeRequestLogEntries(page.head(), entries));
        return page;
    }


    @GET
    @Path("/operator/httpServer/lastNotFounds/{server}")
    public Element lastNotFoundRequests(@PathParam("server") String server) throws Throwable
    {
        HttpServer httpServer=this.getHttpServer(server);
        OperatorPage page=buildServerOperatorPage("Last Not Founds (404s)",server,"lastNotFounds");
        
        RequestHandlerNotFoundLogEntry[] entries = httpServer.getRequestHandlerNotFoundLogEntries();
        for (RequestHandlerNotFoundLogEntry entry : entries)
        {
            Trace trace = entry.getTrace();
            String title= entry.getRemoteEndPoint()+" | "+entry.getMethod() + " " + entry.getURI();
            Panel panel=page.content().returnAddInner(new Panel3(page.head(),title));
            writeTrace(page.head(),panel.content(),trace,true);
            writeHeaders(page.head(),"Request Headers",panel.content(),entry.getRequestHeaders());
        }
        return page;
    }

    @GET
    @Path("/operator/httpServer/status/{server}")
    public Element status(@PathParam("server") String server) throws Throwable
    {
        HttpServer httpServer=getHttpServer(server);
        RequestHandler[] requestHandlers = httpServer.getRequestHandlers();
        OperatorPage page=buildServerOperatorPage("Server Status",server,null);
        OperatorDataTable table=page.content().returnAddInner(new OperatorTable(page.head()));
        TableHeader header=new TableHeader();
        header.add("Method","Path","200","300","400","500","");
        table.setHeader(header);
        for (RequestHandler requestHandler : requestHandlers)
        {
            HashMap<Integer, Long> statusCodes = new HashMap<>();
            statusCodes.put(200, 0L);
            statusCodes.put(300, 0L);
            statusCodes.put(400, 0L);
            statusCodes.put(500, 0L);
            Map<Integer, LongValueSample> results = requestHandler.sampleStatusMeters();
            for (Entry<Integer, LongValueSample> entry : results.entrySet())
            {
                int status = entry.getKey() / 100 * 100;
                Long count = statusCodes.get(status);
                if (count == null)
                {
                    count = entry.getValue().getSamples();
                }
                else
                {
                    count += entry.getValue().getSamples();
                }
                statusCodes.put(status, count);
            }
            TableRow row=new TableRow();
            row.add(requestHandler.getHttpMethod())
            .add(requestHandler.getPath())
            .add(statusCodes.get(200)
                ,statusCodes.get(300)
                ,statusCodes.get(400)
                ,statusCodes.get(500)
            );
            row.add(new RightMoreLink(page.head(),new PathAndQuery("/operator/httpServer/info").addQuery("key", requestHandler.getKey()).addQuery("server", server).toString()));
            table.addRow(row);
        }
        return page;
    }
    
    static class ParameterDescription
    {
        final String type;
        final String name;
        final String defaultValue;
        final String description;
        ParameterDescription(String name,String type,String defaultValue,String description)
        {
            this.type=type;
            this.name=name;
            this.defaultValue=defaultValue;
            this.description=description;
        }
        
    }

    static class ContentTypeDescription
    {
        final String mediaType;
        final String schema;
        ContentTypeDescription(String mediaType,String schema)
        {
            this.mediaType=mediaType;
            this.schema=schema;
        }
    }
    
    static class RequestHandlerParameterDescriptions
    {
        final String method;
        final String pathSchema;
        final ParameterDescription[] cookieParameters;
        final ParameterDescription[] pathParameters;
        final ParameterDescription[] queryParameters;
        final ParameterDescription[] headerParameters;
        final ContentTypeDescription[] requestContentTypes;
        final ContentTypeDescription[] responseContentTypes;
        
        RequestHandlerParameterDescriptions(String method,String pathSchema,ParameterDescription[] cookieParameters,
                ParameterDescription[] pathParameters,
                ParameterDescription[] queryParameters,
                ParameterDescription[] headerParameters,
                ContentTypeDescription[] requestContentTypes,
                ContentTypeDescription[] responseContentTypes)
        {
            this.method=method;
            this.pathSchema=pathSchema;
            this.cookieParameters=cookieParameters;
            this.pathParameters=pathParameters;
            this.queryParameters=queryParameters;
            this.headerParameters=headerParameters;
            this.requestContentTypes=requestContentTypes;
            this.responseContentTypes=responseContentTypes;
        }
    }
    
    private RequestHandlerParameterDescriptions extractParameterDescriptions(RequestHandler requestHandler) throws Throwable
    {
        Method method=requestHandler.getMethod();
        ArrayList<ParameterDescription> cookieParameters=new ArrayList<>();
        ArrayList<ParameterDescription> pathParameters=new ArrayList<>();
        ArrayList<ParameterDescription> queryParameters=new ArrayList<>();
        ArrayList<ParameterDescription> headerParameters=new ArrayList<>();
        ParameterInfo contentParameterInfo = null;

        for (ParameterInfo info:requestHandler.getParameterInfos())
        {
            Parameter parameter = method.getParameters()[info.getIndex()];
            switch (info.getSource())
            {
            case CONTEXT:
            case STATE:
            case TRACE:
                continue;
            case CONTENT:
                contentParameterInfo=info;
                break;
            case COOKIE:
                cookieParameters.add(new ParameterDescription(info.getName(),info.getType().getSimpleName(),info.getDefaultValue()!=null?info.getDefaultValue().toString():null,getDescription(parameter)));
                break;
            case HEADER:
                headerParameters.add(new ParameterDescription(info.getName(),info.getType().getSimpleName(),info.getDefaultValue()!=null?info.getDefaultValue().toString():null,getDescription(parameter)));
                break;
            case PATH:
                pathParameters.add(new ParameterDescription(info.getName(),info.getType().getSimpleName(),info.getDefaultValue()!=null?info.getDefaultValue().toString():null,getDescription(parameter)));
                break;
            case QUERY:
                queryParameters.add(new ParameterDescription(info.getName(),info.getType().getSimpleName(),info.getDefaultValue()!=null?info.getDefaultValue().toString():null,getDescription(parameter)));
                break;
            case SECURE_QUERY:
                queryParameters.add(new ParameterDescription(info.getName(),info.getType().getSimpleName(),info.getDefaultValue()!=null?info.getDefaultValue().toString():null,getDescription(parameter)));
                break;                
            }
        }

        ArrayList<ContentTypeDescription> requestContentDescriptions=new ArrayList<>();
        if (contentParameterInfo != null)
        {
            Parameter contentParameter = method.getParameters()[contentParameterInfo.getIndex()];
            if (requestHandler.getContentReaders().size() > 0)
            {
                for (ContentReader contentReader : requestHandler.getContentReaders().values())
                {
                    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                    contentReader.writeSchema(byteOutputStream, contentParameter.getType());
                    String schema = new String(byteOutputStream.toByteArray());
                    if (schema.length()==0)
                    {
                        schema=null;
                    }
                    else
                    {
                        schema=schema.replace("\r\n", "");
                    }
                    requestContentDescriptions.add(new ContentTypeDescription(contentReader.getMediaType(), schema));
                }
            }
        }

        ArrayList<ContentTypeDescription> responseContentDescriptions=new ArrayList<>();
        Class<?> returnType = method.getReturnType();
        Type innerReturnType = null;
        if (returnType != void.class)
        {
            if (returnType == Response.class)
            {
                ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
                innerReturnType = type.getActualTypeArguments()[0];
                String typeName = innerReturnType.getTypeName();
                if (typeName.equals("?")==false)
                {
                    returnType = (Class<?>) innerReturnType;
                    innerReturnType = null;
                }
            }
            if (requestHandler.getContentWriters().size() > 0)
            {
                HashMap<String, ContentWriterList> lists = new HashMap<>();
                for (Entry<String, ContentWriter> entry : requestHandler.getContentWriters().entrySet())
                {
                    ContentWriterList types = lists.get(entry.getValue().getMediaType().toLowerCase());
                    if (types == null)
                    {
                        types = new ContentWriterList(entry.getValue());
                        lists.put(entry.getValue().getMediaType(), types);
                    }
                    types.types.add(entry.getKey());
                }

                for (ContentWriterList list : lists.values())
                {
                    if (innerReturnType == null)
                    {
                        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                        list.contentWriter.writeSchema(byteOutputStream, returnType);
                        String schema = new String(byteOutputStream.toByteArray());
                        if (schema.length()==0)
                        {
                            schema=null;
                        }
                        else
                        {
                            schema=schema.replace("\r\n", "");
                        }
                        responseContentDescriptions.add(new ContentTypeDescription(list.contentWriter.getMediaType(), schema));
                    }
                }
            }
        }
        ParameterDescription[] cookieParameterArray=cookieParameters.size()>0?cookieParameters.toArray(new ParameterDescription[cookieParameters.size()]):null;
        ParameterDescription[] pathParameterArray=pathParameters.size()>0?pathParameters.toArray(new ParameterDescription[pathParameters.size()]):null;
        ParameterDescription[] queryParameterArray=queryParameters.size()>0?queryParameters.toArray(new ParameterDescription[queryParameters.size()]):null;
        ParameterDescription[] headerParameterArray=headerParameters.size()>0?headerParameters.toArray(new ParameterDescription[headerParameters.size()]):null;
        ContentTypeDescription[] requestContentDescriptionArray=requestContentDescriptions.size()>0?requestContentDescriptions.toArray(new ContentTypeDescription[requestContentDescriptions.size()]):null;
        ContentTypeDescription[] responseContentDescriptionArray=responseContentDescriptions.size()>0?responseContentDescriptions.toArray(new ContentTypeDescription[responseContentDescriptions.size()]):null;
        
        return new RequestHandlerParameterDescriptions(requestHandler.getHttpMethod(),requestHandler.getPath(),cookieParameterArray, pathParameterArray, queryParameterArray, headerParameterArray, requestContentDescriptionArray, responseContentDescriptionArray); 
    }
    
    @GET
    @ContentWriters(JSONContentWriter.class)
    @Path("/operator/httpServer/apis")
    public RequestHandlerParameterDescriptions[] apis() throws Throwable
    {
        RequestHandler[] requestHandlers = this.serverApplication.getOperatorServer().getRequestHandlers();
        ArrayList<RequestHandlerParameterDescriptions> list=new ArrayList<>();
        for (RequestHandler requestHandler : requestHandlers)
        {
            list.add(extractParameterDescriptions(requestHandler));
        }
        return list.toArray(new RequestHandlerParameterDescriptions[list.size()]);
    }

    @GET
    @ContentWriters(JSONContentWriter.class)
    @Path("/operator/httpServer/api")
    public RequestHandlerParameterDescriptions api(@QueryParam("key") String key) throws Throwable
    {
        RequestHandler requestHandler = this.serverApplication.getOperatorServer().getRequestHandler(key);
        return extractParameterDescriptions(requestHandler);
    }    

    
    private HttpServer getHttpServer(String server) throws Exception
    {
        if ("public".equals(server))
        {
            return this.serverApplication.getPublicServer();
        }
        else if ("private".equals(server))
        {
            return this.serverApplication.getPrivateServer();
        }
        else if ("operator".equals(server))
        {
            return this.serverApplication.getOperatorServer();
        }
        throw new Exception();
    }

    private HttpTransport getHttpTransport(String server) throws Exception
    {
        if ("public".equals(server))
        {
            return this.serverApplication.getPublicTransport();
        }
        else if ("private".equals(server))
        {
            return this.serverApplication.getPrivateTransport();
        }
        else if ("operator".equals(server))
        {
            return this.serverApplication.getOperatorTransport();
        }
        throw new Exception();
    }
    
    private OperatorPage buildServerOperatorPage(String title,String server,String clear) throws Throwable
    {
        HttpTransport httpTransport=getHttpTransport(server);
        OperatorPage page=this.serverApplication.buildOperatorPage(title);
        int[] ports=httpTransport.getPorts();
        if (ports.length==1)
        {
            page.content().addInner("Server: "+server+", port: "+ports[0]);
        }
        else
        {
            page.content().addInner("Server: "+server+", ports: "+Utils.combine(TypeUtils.intArrayToList(ports),","));
        }
        if (clear!=null)
        {
            String action="/operator/httpServer/clearLastRequests/"+server+"/"+clear;
            ConfirmButton confirm=new ConfirmButton(action, "Clear").style("float:right;");
            page.content().addInner(confirm);
            page.content().addInner(new br());
            page.content().addInner(new hr());
        }
        
        page.content().addInner(new hr());
        return page;
    }

    @GET
    @Path("/operator/httpServer/clearLastRequests/{server}/{clear}")
    public Element clearLastRequests(@PathParam("server") String server,@PathParam("clear") String clear) throws Throwable
    {
        HttpServer httpServer=this.getHttpServer(server);
        switch (clear)
        {
            case "lastRequests":
                httpServer.clearLastRequestLogEntries();
                break;
                
            case "lastExceptionRequests":
                httpServer.clearLastExceptionRequestLogEntries();
                break;
                
            case "lastNotFounds":
                httpServer.clearRequestHandlerNotFoundLogEntries();
                break;

        }
        return new Redirect("/operator/httpServer/"+clear+"/"+server);
    }
    
    
    @GET
    @Path("/operator/httpServer/methods/{server}")
    public Element methods(@PathParam("server") String server) throws Throwable
    {
        HttpTransport httpTransport=getHttpTransport(server);
        OperatorPage page=buildServerOperatorPage("Methods",server,null);
        RequestHandler[] requestHandlers = httpTransport.getHttpServer().getRequestHandlers();
        OperatorDataTable table=page.content().returnAddInner(new OperatorTable(page.head()));
        table.setHeader("Method","Path","Description","");
        for (RequestHandler requestHandler : requestHandlers)
        {
            TableRow row=new TableRow().add(requestHandler.getHttpMethod());
            row.add(requestHandler.getPath());
            
            Method method = requestHandler.getMethod();
            Description description = method.getAnnotation(Description.class);
            if (description != null)
            {
                row.add(description.value());
            }
            else
            {
                row.add("");
            }

            row.add(new RightMoreLink(page.head(),new PathAndQuery("/operator/httpServer/method/"+server).addQuery("key", requestHandler.getKey()).toString()));
            table.addRow(row);
        }
        return page;
    }

    private String getDescription(Method method)
    {
        Description description = method.getAnnotation(Description.class);
        if (description != null)
        {
            return description.value();
        }
        return null;
    }
    private String getDescription(Parameter parameter)
    {
        Description description = parameter.getAnnotation(Description.class);
        if (description != null)
        {
            return description.value();
        }
        return null;
    }

    private void writeParameterInfos(Head head,Panel panel, String heading, RequestHandler handler, ParameterSource filter)
    {
        if (Arrays.stream(handler.getParameterInfos()).filter(info ->
        {
            ParameterSource source=info.getSource();
            if (source==ParameterSource.SECURE_QUERY)
            {
                source=ParameterSource.QUERY;
            }
            return source == filter;
        }).count() == 0)
        {
            return;
        }

        Method method = handler.getMethod();
        Panel3 panel2=panel.content().returnAddInner(new Panel3(head, heading));
        
        WideTable table=panel2.content().returnAddInner(new WideTable(head));
        table.setHeader("Name","Type","Description","Default");
        for (ParameterInfo info : handler.getParameterInfos())
        {
            ParameterSource source=info.getSource();
            if (source==ParameterSource.SECURE_QUERY)
            {
                source=ParameterSource.QUERY;
            }
            if (source == filter)
            {
                Parameter parameter = method.getParameters()[info.getIndex()];
                table.addRow(new TableRow().add(info.getName(),parameter.getType().getName(),getDescription(parameter),info.getDefaultValue()));
            }
        }
        panel.content().addInner(new p());
    }

    private void writeInputParameterInfos(Head head,NodeElement<?> container, AjaxButton button, String heading, RequestHandler handler, ParameterSource source) throws Exception
    {
        if (Arrays.stream(handler.getParameterInfos()).filter(info ->
        {
            return info.getSource() == source;
        }).count() == 0)
        {
            return;
        }

        Method method = handler.getMethod();
        fieldset field=container.returnAddInner(new fieldset());
        field.addInner(new legend().addInner(heading));
        WideTable table=field.returnAddInner(new WideTable(head));
        table.setHeader("Name","Type","Description","Default","Null","Value");
        for (ParameterInfo info : handler.getParameterInfos())
        {
            if (info.getSource() == source)
            {
                String name = info.getName();
                Parameter parameter = method.getParameters()[info.getIndex()];
                TableRow row=new TableRow().add(info.getName(),parameter.getType().getName(),getDescription(parameter),info.getDefaultValue());
                table.addRow(row);
                String key = source.toString() + name;
                

                if (parameter.getType().isPrimitive()) 
                {
                    row.add("");
                }
                else
                {
                    String nullKey="null"+key;
                    button.prop(nullKey, nullKey,"checked");
                    row.add(new DisableElementToggler(nullKey, false, key,nullKey));
                }
                
                if ((parameter.getType() == boolean.class)||(parameter.getType() == Boolean.class))
                {
                    button.prop(key, key,"checked");
                    row.add(new input_checkbox().id(key).name(key).checked(info.getDefaultValue()==null?false:(boolean)info.getDefaultValue()));
                }
                else
                {
                    button.val(key, key);
                    input_text input=new input_text().id(key).name(key).style("width:100%;");

                    if (info.getDefaultValue()!=null)
                    {
                        input.value(info.getDefaultValue().toString());
                    }
                    row.add(input);
                }
            }
        }
    }

    private ParameterInfo findContentParameter(RequestHandler requestHandler)
    {
        for (ParameterInfo info : requestHandler.getParameterInfos())
        {
            if (info.getSource() == ParameterSource.CONTENT)
            {
//                Method method = requestHandler.getMethod();
                return info;
            }
        }
        return null;
    }
    private static String[] HTML_ESCAPES=new String[]{"&", "\"", "<", ">"};
    private static String[] HTML_ESCAPEDS=new String[]{"&amp;", "&quot;", "&lt;", "&gt;"};
      public static String escapeHtml(String text)
      {
          StringBuilder sb=new StringBuilder();
          int start=0;
          for (int i=0;i<text.length();i++)
          {
              char c=text.charAt(i);
              if (c=='&')
              {
                  if (start<i)
                  {
                      sb.append(text.substring(start,i));
                      sb.append("&amp;");
                  }
                  start=i+1;
              }
              else if (c=='"')
              {
                  if (start<i)
                  {
                      sb.append(text.substring(start,i));
                      sb.append("&quot;");
                  }
                  start=i+1;
              }
              else if (c=='<')
              {
                  if (start<i)
                  {
                      sb.append(text.substring(start,i));
                      sb.append("&lt;");
                  }
                  start=i+1;
              }
              else if (c=='>')
              {
                  if (start<i)
                  {
                      sb.append(text.substring(start,i));
                      sb.append("&gt;");
                  }
                  start=i+1;
              }
    
          }
          if (start<text.length())
          {
              sb.append(text.substring(start,text.length()));
          }
          return sb.toString();
      }

    static public class ParameterWriter
    {
        final ArrayList<Class<?>> usedClasses = new ArrayList<>();
        final HashSet<String> shownClasses = new HashSet<>();
        final Panel parentPanel;
        final Head head;

        ParameterWriter(Head head,Panel parentPanel)
        {
            this.parentPanel=parentPanel;
            this.head=head;
        }

        void write(Class<?> type)
        {
            String typeName=type.isArray()?type.getComponentType().getName():type.getName();
            String displayTypeName=type.isArray()?type.getComponentType().getName()+"[]":type.getName();
            if (this.shownClasses.contains(typeName))
            {
                return;
            }
            this.shownClasses.add(typeName);
            Panel4 panel=this.parentPanel.content().returnAddInner(new Panel4(this.head,"Type: "+displayTypeName));
            if (TypeUtils.isDerivedFrom(type,Element.class))
            {
                return;
            }
            Description description = type.getAnnotation(Description.class);
            if (description != null)
            {
                panel.content().addInner(description.value());
            }
            ArrayList<Field> fields=new ArrayList<>();
            Class<?> componentType=type.isArray()?type.getComponentType():type;
            for (Field field : componentType.getDeclaredFields())
            {
                int modifiers = field.getModifiers();
                if (Modifier.isTransient(modifiers))
                {
                    continue;
                }
                if (Modifier.isStatic(modifiers))
                {
                    continue;
                }
                fields.add(field);
            }
            if (fields.size()>0)
            {
                WideTable table=panel.content().returnAddInner(new WideTable(head));
                table.setHeader("Name","Type","Description");
                for (Field field : fields)
                {
                    TableRow row=new TableRow();
                    table.addRow(row);
                    row.add(field.getName());
                    Class<?> fieldType = field.getType();
    
                    if (fieldType.isArray())
                    {
                        row.add(escapeHtml(fieldType.getComponentType().getName() + "[]"));
                        fieldType = fieldType.getComponentType();
                    }
                    else
                    {
                        String fieldName=fieldType.getName();
                        if (fieldName.startsWith("java.lang."))
                        {
                            fieldName=fieldName.substring("java.lang.".length());
                        }
                        row.add(escapeHtml(fieldName));
                    }
                    description = field.getAnnotation(Description.class);
                    if (description != null)
                    {
                        row.add(description.value());
                    }
                    else
                    {
                        row.add("");
                    }
                    if (fieldType.isPrimitive())
                    {
                        continue;
                    }
                    if (fieldType == Number.class)
                    {
                        continue;
                    }
                    if (fieldType == String.class)
                    {
                        continue;
                    }
                    this.usedClasses.add(fieldType);
                }
            }
            this.parentPanel.content().addInner(new p());

            if (this.usedClasses.size() > 0)
            {
                Class<?> next = this.usedClasses.remove(0);
                write(next);
            }
        }
    }

    @GET
    @Path("/operator/httpServer/info")
    public Element info(@QueryParam("key") String key,@QueryParam("server") String server) throws Throwable
    {
        OperatorPage page=buildServerOperatorPage("Method: "+key, server,null);
        HttpServer httpServer=getHttpServer(server);
        RequestHandler requestHandler = httpServer.getRequestHandler(key);

        Map<Integer, LongValueSample> meters = requestHandler.sampleStatusMeters();
        if (meters.size()>0)
        {
            Panel durationPanel=page.content().returnAddInner(new Panel2(page.head(),"Durations"));
            WideTable table=durationPanel.content().returnAddInner(new WideTable(page.head()));
                table.setHeader(
                        new th_title("Code", "Status Code")
                        ,new th_title("Total","Total count")
                        ,new th_title("Samples","Sample count")
                        ,new th_title("Rate", "per second")
                        ,new th_title("Average", "Milliseconds")
                        ,new th_title("StdDev", "Standard Deviation")
                        ,new th_title("Total","Total duration in milliseconds")
                        );
            for (Entry<Integer, LongValueSample> entry : meters.entrySet())
            {
                LongValueSample result = entry.getValue();
                if (result.getSamples()!=0)
                {
                    table.addRow(new TableRow().add(
                            entry.getKey()
                            ,result.getMeterTotalCount()
                            ,result.getSamples()
                            ,format_3(result.getRate())
                            ,format_3(result.getAverage() / 1.0e6)
                            ,format_3(result.getStandardDeviation() / 1.0e6)
                            ,format_3(result.getTotal() / 1.0e6)
                            ));
                }
                else
                {
                    table.addRow(new TableRow().add(
                            entry.getKey()
                            ,result.getMeterTotalCount()
                            ,result.getSamples()
                            ,""
                            ,""
                            ,""
                            ,""
                            ));
    
                }
            }
        }
        {
            long requestUncompressed = 0;
            long requestCompressed = 0;
            long responseUncompressed = 0;
            long responseCompressed = 0;
    
            page.content().addInner(new p());
            Panel panel=page.content().returnAddInner(new Panel2(page.head(), "Content Sizes"));
            WideTable table=panel.content().returnAddInner(new WideTable(page.head()));
            table.setHeader(
                    "Type"
                    ,new th_title("Average", "Average bytes")
                    ,new th_title("StDev", "Standard Deviation")
                    ,"Total Bytes","KB","MB","GB","TB"
                    );
            
            LongValueMeter requestMeter = requestHandler.getRequestUncompressedContentSizeMeter();
            LongValueSample result = requestMeter.sample();
            if (result.getSamples()>0)
            {
                long total = requestUncompressed = requestMeter.getTotal();
                table.addRow(new TableRow().add(
                        "Request uncompressed content"
                        ,DOUBLE_FORMAT.format(result.getAverage())
                        ,DOUBLE_FORMAT.format(result.getStandardDeviation())
                        ,total
                        ,total / 1024
                        ,total / 1024 / 1024
                        ,total / 1024 / 1024 / 1024
                        ,total / 1024 / 1024 / 1024 / 1024));
            }
            else
            {
                long total = requestUncompressed = requestMeter.getTotal();
                table.addRow(new TableRow().add(
                        "Request uncompressed content"
                        ,""
                        ,""
                        ,total
                        ,total / 1024
                        ,total / 1024 / 1024
                        ,total / 1024 / 1024 / 1024
                        ,total / 1024 / 1024 / 1024 / 1024));
            }
    
            LongValueMeter compressedRequestMeter = requestHandler.getRequestCompressedContentSizeMeter();
            result = compressedRequestMeter.sample();
            if (result.getSamples()>0)
            {
                long total = requestCompressed = compressedRequestMeter.getTotal();
                table.addRow(new TableRow().add(
                        "Request content"
                        ,format_3(result.getAverage())
                        ,format_3(result.getStandardDeviation())
                        ,total
                        ,total / 1024
                        ,total / 1024 / 1024
                        ,total / 1024 / 1024 / 1024
                        ,total / 1024 / 1024 / 1024 / 1024));
            }
            else
            {
                long total = requestCompressed = compressedRequestMeter.getTotal();
                table.addRow(new TableRow().add(
                        "Request content"
                        ,""
                        ,""
                        ,total
                        ,total / 1024
                        ,total / 1024 / 1024
                        ,total / 1024 / 1024 / 1024
                        ,total / 1024 / 1024 / 1024 / 1024));
            }
    
            LongValueMeter responseMeter = requestHandler.getResponseUncompressedContentSizeMeter();
            result = responseMeter.sample();
            if (result.getSamples()>0)
            {
                long total = responseUncompressed = responseMeter.getTotal();
                table.addRow(new TableRow().add(
                        "Response uncompressed content"
                        ,format_3(result.getAverage())
                        ,format_3(result.getStandardDeviation())
                        ,total
                        ,total / 1024
                        ,total / 1024 / 1024
                        ,total / 1024 / 1024 / 1024
                        ,total / 1024 / 1024 / 1024 / 1024));
            }
            else
            {
                long total = responseUncompressed = responseMeter.getTotal();
                table.addRow(new TableRow().add(
                        "Response uncompressed content"
                        ,""
                        ,""
                        ,total
                        ,total / 1024
                        ,total / 1024 / 1024
                        ,total / 1024 / 1024 / 1024
                        ,total / 1024 / 1024 / 1024 / 1024));
            }
    
            LongValueMeter compressedResponseMeter = requestHandler.getResponseCompressedContentSizeMeter();
            result = compressedResponseMeter.sample();
            if (result.getSamples()>0)
            {
                long total = responseCompressed = compressedResponseMeter.getTotal();
                table.addRow(new TableRow().add(
                        "Response content"
                        ,format_3(result.getAverage())
                        ,format_3(result.getStandardDeviation())
                        ,total
                        ,total / 1024
                        ,total / 1024 / 1024
                        ,total / 1024 / 1024 / 1024
                        ,total / 1024 / 1024 / 1024 / 1024));
            }
            else
            {
                long total = responseCompressed = compressedResponseMeter.getTotal();
                table.addRow(new TableRow().add(
                        "Response content"
                        ,""
                        ,""
                        ,total
                        ,total / 1024
                        ,total / 1024 / 1024
                        ,total / 1024 / 1024 / 1024
                        ,total / 1024 / 1024 / 1024 / 1024));
            }

            page.content().addInner(new p());
            Panel ratioPanel=page.content().returnAddInner(new Panel2(page.head(), "Compression Ratios"));
            NameValueList list=ratioPanel.content().returnAddInner(new NameValueList());
            list.add("Request", requestUncompressed != 0?DOUBLE_FORMAT.format((double) requestCompressed / requestUncompressed):"");
            list.add("Response",responseUncompressed != 0?DOUBLE_FORMAT.format((double) responseCompressed / responseUncompressed):"");

            RequestLogEntry[] entries = requestHandler.getLastRequestLogEntries();
            if (entries.length>0)
            {
                Panel logPanel=page.content().returnAddInner(new Panel2(page.head(), "Last Log Entries"));
                Element logElement=writeRequestLogEntries(page.head(), entries);
                logPanel.content().addInner(logElement);
            }
            
        }
        return page;
    }

    static class ContentWriterList
    {
        ContentWriter contentWriter;
        ArrayList<String> types;

        public ContentWriterList(ContentWriter contentWriter)
        {
            this.contentWriter = contentWriter;
            this.types = new ArrayList<>();
        }
    }


    private String generateClassDefinitions(String server,String namespace,int columns,InteropTarget target) throws Exception
    {
        this.namespace=namespace;
        HttpServer httpServer=getHttpServer(server);
        HashMap<String,Class<?>> roots=new HashMap<>();
        for (RequestHandler requestHandler:httpServer.getRequestHandlers())
        {
            Method method = requestHandler.getMethod();
            ParameterInfo contentParameterInfo = findContentParameter(requestHandler);

            if (contentParameterInfo!=null)
            {
                for (ContentReader reader:requestHandler.getContentReaders().values())
                {
                    if (reader instanceof JSONContentReader)
                    {
                        Class<?> contentType=contentParameterInfo.getType();
                        roots.put(contentType.getCanonicalName(),contentType);
                        break;
                    }
                }
            }
    
            Class<?> returnType = method.getReturnType();
            Type innerReturnType = null;
            if (returnType != void.class)
            {
                for (ContentWriter writer:requestHandler.getContentWriters().values())
                {
                    if (writer instanceof JSONContentWriter)
                    {
                        if (returnType == Response.class)
                        {
                            ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
                            innerReturnType = type.getActualTypeArguments()[0];
                            String typeName = innerReturnType.getTypeName();
                            if (typeName.equals("?")==false)
                            {
                                returnType = (Class<?>) innerReturnType;
                            }
                        }
                        String name=returnType.getCanonicalName();
                        roots.put(name,returnType);
                        break;
                    }
                }
            }
        }
        switch (target)
        {
            case CSHARP_DATACONTRACT:
            case CSHARP_PLAIN:
            {
                CSharpClassWriter classWriter=new CSharpClassWriter();
                String source=Utils.getLocalHostName();
                return classWriter.write(source,namespace, roots.values(),columns,target);
            }
            case TYPESCRIPT:
            {
                TypeScriptClassWriter classWriter=new TypeScriptClassWriter();
                String source=Utils.getLocalHostName();
                return classWriter.write(source,namespace, roots.values(),columns);
            }
            default:
                throw new Exception();
            
        }
    }
    
    private String namespace;

    @GET
    @Path("/operator/httpServer/classDefinitions/{server}")
    public Element classDefinitions(Context context,@PathParam("server") String server) throws Throwable
    {
        OperatorPage page=buildServerOperatorPage("Interoperability", server,null);
        
        form_get form=page.content().returnAddInner(new form_get().action("/operator/httpServer/classDefinitions/download/"+server));
        fieldset fieldset=form.returnAddInner(new fieldset());
        fieldset.addInner(new legend().addInner("C# Target"));
        NameInputValueList list=fieldset.returnAddInner(new NameInputValueList());
        SelectOptions options=new SelectOptions();
        options.name("target").id("target");
        options.style("width:400px;");
        for (InteropTarget value:InteropTarget.values())
        {
            options.addOption(value);
        }
        list.add("Code",options);
        list.add("Columns", new input_number().name("columns").id("columns").min(40).style("width:396px;").value(80));
        list.add("Namespace", new input_text().name("namespace").id("namespace").style("width:396px;").value(this.namespace));
        list.add("",new input_submit().value("Download").style("width:400px;"));
        AjaxButton button = new AjaxButton("button", "Preview", "/operator/httpServer/classDefinitions/preview/"+server);
        list.add("",button);
        button.type("post");
        button.val("namespace", "namespace");
        button.val("target", "target");
        button.val("columns", "columns");
        button.async(true);
        page.content().addInner(new p());
        page.content().addInner(new div().id("result"));
        return page;
    }
    
    @POST
    @Path("/operator/httpServer/classDefinitions/preview/{server}")
    @ContentWriters(AjaxQueryResultWriter.class)
    public AjaxQueryResult previewClassDefinitions(Trace parent, Context context, @PathParam("server") String server,@QueryParam("namespace") String namespace,@QueryParam ("columns") int columns,@QueryParam("target") InteropTarget target) throws Throwable
    {
        AjaxQueryResult result = new AjaxQueryResult();
        String text=generateClassDefinitions(server, namespace, columns, target);
        Panel panel=new Panel1(null,"Interfaces");
        
        textarea textarea=new textarea().readonly().style("width:100%;").rows(Utils.occurs(text, "\r")+1).addInner(text);
        textarea.id();
        String id=textarea.id();
        
        button_button button=new button_button().addInner(new LiteralHtml("&#128203;")).title("Copy to clipboard");
        button.onclick("var copyText=document.getElementById('"+id+"');copyText.select();document.execCommand('Copy');");
        
        
        panel.addRightInHeader(button);
        panel.content().addInner(textarea);
        result.put("result", panel.toString());
        return result;
    }

    @GET
    @Path("/operator/httpServer/classDefinitions/download/{server}")
    public Response<String> downloadClassDefinitions(Context context,@PathParam("server") String server,@QueryParam("namespace") String namespace,@QueryParam ("columns") int columns,@QueryParam("target") InteropTarget target) throws Throwable
    {
        String text=generateClassDefinitions(server, namespace, columns, target);
        String filename=namespace.replace(".", "_")+".cs";
        Response<String> response=new Response<>(text);
        response.addHeader("Content-Disposition","attachment;filename="+filename);
        return response;
    }
    
    @Description("Displays information and documentation of method.")
    @GET
    @Path("/operator/httpServer/method/{server}")
    public Element method(Context context,@PathParam("server") String server,@QueryParam("key") String key) throws Throwable
    {
        OperatorPage page=buildServerOperatorPage("Method: "+key,server,null);
        HttpServer httpServer=getHttpServer(server);
        RequestHandler requestHandler = httpServer.getRequestHandler(key);
        Method method = requestHandler.getMethod();
        String text = getDescription(method);
        if (text != null)
        {
            Panel panel=page.content().returnAddInner(new Panel2(page.head(),"Description"));
            panel.content().addInner(text);
            page.content().addInner(new p());
        }
        
        Panel2 requestPanel=page.content().returnAddInner(new Panel2(page.head(),"Request"));
        writeParameterInfos(page.head(),requestPanel, "Query Parameters", requestHandler, ParameterSource.QUERY);
        writeParameterInfos(page.head(),requestPanel, "Path Parameters", requestHandler, ParameterSource.PATH);
        writeParameterInfos(page.head(),requestPanel, "Header Parameters", requestHandler, ParameterSource.HEADER);
        writeParameterInfos(page.head(),requestPanel, "Cookie Parameters", requestHandler, ParameterSource.COOKIE);
        ParameterInfo contentParameterInfo = findContentParameter(requestHandler);
        if (contentParameterInfo != null)
        {
            Panel3 contentParameterPanel=requestPanel.content().returnAddInner(new Panel3(page.head(),"Content Parameter"));

            Parameter contentParameter = method.getParameters()[contentParameterInfo.getIndex()];
            ParameterWriter parameterWriter = new ParameterWriter(page.head(),contentParameterPanel);
            parameterWriter.write(contentParameter.getType());

            if (requestHandler.getContentReaders().size() > 0)
            {
                requestPanel.content().addInner(new p());
                Panel3 contentReaderPanel=requestPanel.content().returnAddInner(new Panel3(page.head(),"Content Readers"));
                for (ContentReader contentReader : requestHandler.getContentReaders().values())
                {
                    WideTable table=contentReaderPanel.content().returnAddInner(new WideTable(page.head()));
                    table.setHeader("Class","Media Type");
                    table.addRow(new TableRow().add(contentReader.getClass().getName(),contentReader.getMediaType())); 
                    contentReaderPanel.content().addInner(new p());
                    
                    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                    contentReader.writeSchema(byteOutputStream, contentParameter.getType());
                    String schema = new String(byteOutputStream.toByteArray());

                    byteOutputStream.reset();
                    contentReader.writeExample(byteOutputStream, contentParameter.getType());
                    String example = new String(byteOutputStream.toByteArray());
                    if (schema.length() > 0)
                    {
                        Accordion accordion=contentReaderPanel.content().returnAddInner(new Accordion(page.head(), null, false, "Schema"));
                        textarea area=accordion.content().returnAddInner(new textarea().readonly().style("width:100%;").rows(Utils.occurs(schema, "\r")+1));
                        area.addInner(schema);
                    }
                    if (example.length() > 0)
                    {
                        Accordion accordion=contentReaderPanel.content().returnAddInner(new Accordion(page.head(), null, false, "Example"));
                        textarea area=accordion.content().returnAddInner(new textarea().readonly().style("width:100%;").rows(Utils.occurs(example,"\r")+1));
                        area.addInner(example);
                    }
                    contentReaderPanel.content().addInner(new p());
                }
            }
        }
        page.content().addInner(new p());


        Class<?> returnType = method.getReturnType();
        Type innerReturnType = null;
        if (returnType != void.class)
        {
            Panel2 responsePanel=page.content().returnAddInner(new Panel2(page.head(), "Response"));
            
            Panel3 returnParameterPanel=responsePanel.content().returnAddInner(new Panel3(page.head(),"Return Type"));
            
            ParameterWriter parameterWriter = new ParameterWriter(page.head(),returnParameterPanel);
            if (returnType == Response.class)
            {
                ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
                innerReturnType = type.getActualTypeArguments()[0];
                String typeName = innerReturnType.getTypeName();
                if (typeName.equals("?"))
                {
                    parameterWriter.write(returnType);
                }
                else
                {
                    returnType = (Class<?>) innerReturnType;
                    innerReturnType = null;
                    parameterWriter.write(returnType);
                }
            }
            else
            {
                parameterWriter.write(returnType); 
            }

            if (requestHandler.getContentWriters().size() > 0)
            {
                HashMap<String, ContentWriterList> lists = new HashMap<>();
                for (Entry<String, ContentWriter> entry : requestHandler.getContentWriters().entrySet())
                {
                    ContentWriterList types = lists.get(entry.getValue().getMediaType().toLowerCase());
                    if (types == null)
                    {
                        types = new ContentWriterList(entry.getValue());
                        lists.put(entry.getValue().getMediaType(), types);
                    }
                    types.types.add(entry.getKey());
                }

                responsePanel.content().addInner(new p());
                Panel3 contentWriterPanel=responsePanel.content().returnAddInner(new Panel3(page.head(),"Content Writers"));
                for (ContentWriterList list : lists.values())
                {
                    WideTable table=contentWriterPanel.content().returnAddInner(new WideTable(page.head()));
                    table.setHeader("Class","Media Type","Accept Types");
                    table.addRow(new TableRow().add(list.contentWriter.getClass().getName(),list.contentWriter.getMediaType(),Utils.combine(list.types, ", ")));

                    if (innerReturnType==null)
                    {
                        contentWriterPanel.content().addInner(new p());
                        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                        list.contentWriter.writeSchema(byteOutputStream, returnType);
                        String schema = new String(byteOutputStream.toByteArray());
    
                        byteOutputStream.reset();
                        list.contentWriter.writeExample(byteOutputStream, returnType);
                        String example = new String(byteOutputStream.toByteArray());
                        if (schema.length() > 0)
                        {
                            Accordion accordion=contentWriterPanel.content().returnAddInner(new Accordion(page.head(), null, false, "Schema"));
                            textarea area=accordion.content().returnAddInner(new textarea().readonly().style("width:100%;").rows(Utils.occurs(schema, "\r")+1));
                            area.addInner(schema);
                        }
                        if (example.length() > 0)
                        {
                            Accordion accordion=contentWriterPanel.content().returnAddInner(new Accordion(page.head(), null, false, "Example"));
                            textarea area=accordion.content().returnAddInner(new textarea().readonly().style("width:100%;").rows(Utils.occurs(example,"\r")+1));
                            area.addInner(example);
                        }
                    }
                    contentWriterPanel.content().addInner(new p());
                }
            }
            page.content().addInner(new p());
        }
        
        

        if (requestHandler.getContentDecoders().size() > 0)
        {
            Panel2 panel=page.content().returnAddInner(new Panel2(page.head(),"Content Decoders"));
            WideTable table=panel.content().returnAddInner(new WideTable(page.head()));
            table.setHeader("Class","Content-Encoding","Encoder");
            for (Entry<String, ContentDecoder> entry : requestHandler.getContentDecoders().entrySet())
            {
                table.addRow(new TableRow().add(entry.getValue().getClass().getName(),entry.getValue().getCoding(),entry.getKey()));
            }
            page.content().addInner(new p());
     }
         if (requestHandler.getContentEncoders().length> 0)
        {
            Panel2 panel=page.content().returnAddInner(new Panel2(page.head(),"Content Encoders"));
            WideTable table=panel.content().returnAddInner(new WideTable(page.head()));
            table.setHeader("Class","Content-Encoding","Encoder");
            for (ContentEncoder encoder:requestHandler.getContentEncoders())
            {
                table.addRow(new TableRow().add(encoder.getClass().getName(),encoder,encoder.getCoding()));
            }
            page.content().addInner(new p());
        }
        if (requestHandler.getTopFilters().length > 0)
        {
            Panel2 panel=page.content().returnAddInner(new Panel2(page.head(),"Top Filters"));
            WideTable table=panel.content().returnAddInner(new WideTable(page.head()));
            TableRow row=new TableRow();
            for (Filter filter : requestHandler.getTopFilters())
            {
                row.add(filter.getClass().getName());
            }
            table.addRow(row);
            page.content().addInner(new p());
        }
        if (requestHandler.getBottomFilters().length > 0)
        {
            Panel2 panel=page.content().returnAddInner(new Panel2(page.head(),"Bottom Filters"));
            WideTable table=panel.content().returnAddInner(new WideTable(page.head()));
            TableRow row=new TableRow();
            for (Filter filter : requestHandler.getBottomFilters())
            {
                row.add(filter.getClass().getName());
            }
            table.addRow(row);
            page.content().addInner(new p());
        }
        Panel2 methodPanel=page.content().returnAddInner(new Panel2(page.head(),"Class Method"));
        methodPanel.content().addInner(escapeHtml(method.toGenericString()+";"));
        page.content().addInner(new p());
        
        Set<String> attributes=requestHandler.getAttributes();
        if (attributes!=null)
        {
            String[] array=attributes.toArray(new String[attributes.size()]);
            Panel2 attributesPanel=page.content().returnAddInner(new Panel2(page.head(),"Attributes"));
            attributesPanel.content().addInner(escapeHtml(Utils.combine(array,", ")));
            page.content().addInner(new p());
        }
        
        Panel2 executePanel=page.content().returnAddInner(new Panel2(page.head(),"Execute: "+key));
        String httpMethod = requestHandler.getHttpMethod();
        {
            form_get form=executePanel.content().returnAddInner(new form_get());
            AjaxButton button = new AjaxButton("button", "Execute Call", "/operator/httpServer/method/execute/"+server);
            button.parameter("key", key);
            button.style("height:3em;width:10em;margin:10px;");
            writeInputParameterInfos(page.head(),form, button, "Query Parameters", requestHandler, ParameterSource.QUERY);
            writeInputParameterInfos(page.head(),form, button, "Path Parameters", requestHandler, ParameterSource.PATH);
            writeInputParameterInfos(page.head(),form, button, "Header Parameters", requestHandler, ParameterSource.HEADER);
            writeInputParameterInfos(page.head(),form, button, "Cookie Parameters", requestHandler, ParameterSource.COOKIE);
            button.async(false);

            if ("POST".equals(httpMethod) || "PUT".equals(httpMethod)|| "PATCH".equals(httpMethod))
            {
                for (ParameterInfo info:requestHandler.getParameterInfos())
                {
                    if (info.getSource()==ParameterSource.CONTENT)
                    {
                        Collection<ContentReader> readers = requestHandler.getContentReaders().values();
                        if (readers.size() > 0)
                        {
                            fieldset field=form.returnAddInner(new fieldset());
                            field.addInner(new legend().addInner("Request Content"));
                            textarea input=new textarea().id("contentText").style("width:99%;height:10em;");
                            field.addInner(input);
                            button.val("contentText", "contentText");
                        }
                    }
                }
            }
            fieldset field=form.returnAddInner(new fieldset());
            field.addInner(new legend().addInner("Additional Headers"));
            field.addInner(new input_text().name("headers").id("headers").style("width:100%;").placeholder("Example: X-test:debug,X-session:123"));
            
            button.val("headers", "headers");

            NameInputValueList list=new NameInputValueList();
            if ("POST".equals(httpMethod) || "PUT".equals(httpMethod))
            {
                Collection<ContentReader> readers = requestHandler.getContentReaders().values();
                if (readers.size() > 0)
                {
                    HashSet<String> set=new HashSet<>();
                    for (ContentReader reader : readers)
                    {
                        set.add(reader.getMediaType());
                    }
                    SelectOptions options=new SelectOptions();
                    options.id("contentType");
                    for (String type:set)
                    {
                        options.add(type);
                    }
                    list.add("ContentType", options);
                    button.val("contentType", "contentType");
                }
            }
            Collection<ContentWriter> writers = requestHandler.getContentWriters().values();
            if (writers.size() > 0)
            {
                HashSet<String> set=new HashSet<>();
                for (ContentWriter contentWriter : writers)
                {
                    set.add(contentWriter.getMediaType());
                }
                SelectOptions options=new SelectOptions();
                options.id("accept");
                for (String type:set)
                {
                    options.add(type);
                }
                list.add("Accept", options);
                button.val("accept", "accept");
            }
            if (list.size()>0)
            {
                form.addInner(new fieldset()
                        .addInner(new legend().addInner("Media Types"))
                        .addInner(list));
            }
            div buttons=form.returnAddInner(new div()).style("display:inline;");
            button.style("height:3em;width:10em;margin:10px;");
            buttons.addInner(button);
            if ("GET".equals(httpMethod))
            {
                form.action("/operator/httpServer/method/run/"+server);
                form.addInner(new input_hidden().name("key").value(key));
                form.addInner(new input_hidden().name("server").value(server));
              buttons.addInner(new input_submit().value("Open in browser").style("height:2.5em;width:10em;margin:10px;"));
            }
            executePanel.content().addInner(new p());
            executePanel.content().addInner(new div().id("result"));

        }
        return page;
    }

    static class HttpClientEndPoint
    {
        final HttpClient httpClient;
        final String endPoint;
        HttpClientEndPoint(HttpClient httpClient,String endPoint)
        {
            this.httpClient=httpClient;
            this.endPoint=endPoint;
        }
    }
    
    private HttpClientEndPoint getExecuteClient(HttpTransport httpTransport,Context context) throws Throwable
    {
        String endPoint = context.getHttpServletRequest().getHeader("Referer");
        int index=endPoint.lastIndexOf(':');
        if (index>0)
        {
            endPoint=endPoint.substring(0,index);
        }
        Configuration configuration=this.serverApplication.getConfiguration();
        boolean http=configuration.getBooleanValue("HttpServer.public.http",false);
        if (http==false)
        {
            int[] ports=httpTransport.getPorts();
            endPoint=endPoint+":"+ports[0];
            boolean https=configuration.getBooleanValue("HttpServer.public.https",false);
            if (https)
            {
                if (this.serverApplication.getPublicTransport().getPorts()[0]==ports[0])
                {
                    Vault vault=this.serverApplication.getVault();
                    String serverCertificatePassword=vault.get("KeyStore.serverCertificate.password");
                    String clientCertificatePassword=vault.get("KeyStore.clientCertificate.password");
                    String serverCertificateKeyStorePath=configuration.getValue("HttpServer.serverCertificate.keyStorePath",null);
                    String clientCertificateKeyStorePath=configuration.getValue("HttpServer.clientCertificate.keyStorePath",null);
                    String clusterName=configuration.getValue("HttpServer.public.clusterName",null);
                    endPoint=endPoint.replace("http", "https");
                    return new HttpClientEndPoint(HttpClientFactory.createSSLClient(new HttpClientConfiguration(), clientCertificateKeyStorePath, clientCertificatePassword, serverCertificateKeyStorePath, serverCertificatePassword, clusterName),endPoint);
                }
            }
            throw new Exception();
        }
        int[] ports=httpTransport.getPorts();
        endPoint=endPoint+":"+ports[ports.length-1];
        return new HttpClientEndPoint(HttpClientFactory.createClient(),endPoint);
    }
    
    //TODO: Use PathAndQueryBuilder to ensure strings are escaped properly.
    @GET
    @Path("/operator/httpServer/method/execute/{server}")
    @ContentWriters(AjaxQueryResultWriter.class)
    public AjaxQueryResult execute(Trace parent, Context context, @PathParam("server") String server,@QueryParam("key") String key) throws Throwable
    {
        try
        {
            HttpTransport httpTransport=getHttpTransport(server);
            HttpServletRequest request = context.getHttpServletRequest();
            ArrayList<Header> headers = new ArrayList<>();
            ArrayList<Cookie> cookies = new ArrayList<>();
            String[] methodSchema = Utils.split(key, ' ');
            String method = methodSchema[0];
            String schema = methodSchema[1];
            String[] fragments = Utils.split(schema, '/');

            StringBuilder pathAndQuery = new StringBuilder();
            for (int i = 1; i < fragments.length; i++)
            {
                String fragment = fragments[i];
                pathAndQuery.append("/");
                if (fragment.startsWith("{"))
                {
                    String name = ParameterSource.PATH + fragment.substring(1, fragment.length() - 2);
                    pathAndQuery.append(request.getParameter(name));
                }
                else
                {
                    pathAndQuery.append(fragment);
                }
            }
            String prefix = "?";
            HashSet<String> nulls=new HashSet<>();
            for (Enumeration<String> enumerator = request.getParameterNames(); enumerator.hasMoreElements(); enumerator.hasMoreElements())
            {
                String mangledName = enumerator.nextElement();
                if (mangledName.startsWith("null"))
                {
                    String value=request.getParameter(mangledName);
                    if ("true".equals(value))
                    {
                        nulls.add(mangledName);
                    }
                }
            }
            for (Enumeration<String> enumerator = request.getParameterNames(); enumerator.hasMoreElements(); enumerator.hasMoreElements())
            {
                String mangledName = enumerator.nextElement();
                if (mangledName.startsWith(ParameterSource.QUERY.toString()))
                {
                    if (nulls.contains("null"+mangledName)==false)
                    {
                        String name = mangledName.substring(ParameterSource.QUERY.toString().length());
                        pathAndQuery.append(prefix);
                        pathAndQuery.append(name);
                        pathAndQuery.append("=");
                        pathAndQuery.append(request.getParameter(mangledName));
                        prefix = "&";
                    }
                }
                else if (mangledName.startsWith(ParameterSource.HEADER.toString()))
                {
                    String name = mangledName.substring(ParameterSource.QUERY.toString().length());
                    headers.add(new Header(name, request.getParameter(mangledName)));
                }
                else if (mangledName.startsWith(ParameterSource.COOKIE.toString()))
                {
                    String name = mangledName.substring(ParameterSource.QUERY.toString().length());
                    cookies.add(new Cookie(name, request.getParameter(mangledName)));
                }
            }
            String additionalHeaders=request.getParameter("headers");
            if (Strings.isNullOrEmpty(additionalHeaders)==false)
            {
                String[] nameValues=Utils.split(additionalHeaders,';');
                for (String nameValue:nameValues)
                {
                    String[] parts=Utils.split(nameValue, ':');
                    if (parts.length==2)
                    {
                        headers.add(new Header(parts[0].trim(),parts[1].trim()));
                    }
                }
            }

            HttpClientEndPoint httpClientEndPoint=getExecuteClient(httpTransport,context);
            JSONClient client = new JSONClient(this.serverApplication.getTraceManager(), this.serverApplication.getLogger(), httpClientEndPoint.endPoint,httpClientEndPoint.httpClient);
            int statusCode;
            TextResponse response=null;
            double duration = 0;
            String accept = request.getParameter("accept");
            accept="text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
            if (Strings.isNullOrEmpty(accept)==false)
            {
                headers.add(new Header("Accept", accept));
            }

            if (method.equals("POST"))
            {
                String contentType = request.getParameter("contentType");
                String contentText = request.getParameter("contentText");
                if (Strings.isNullOrEmpty(contentType)==false)
                {
                    headers.add(new Header("Content-Type", contentType));
                }
                try (Trace trace = new Trace(this.serverApplication.getTraceManager(), parent, "Execute"))
                {
                    response = client.postText(trace, null, pathAndQuery.toString(), contentText, headers.toArray(new Header[headers.size()]));
                    statusCode=response.getStatusCode();
                    duration = trace.getDurationS();
                }
            }
            else if (method.equals("GET"))
            {
                String browser=request.getParameter("browser");
                try (Trace trace = new Trace(this.serverApplication.getTraceManager(), parent, "Execute"))
                {
                    response = client.getText(trace, null, pathAndQuery.toString(), headers.toArray(new Header[headers.size()]));
                    statusCode=response.getStatusCode();
                    duration = trace.getDurationS();
                }
            }
            else if (method.equals("PUT"))
            {
                String contentType = request.getParameter("contentType");
                String contentText = request.getParameter("contentText");
                if (Strings.isNullOrEmpty(contentType)==false)
                {
                    headers.add(new Header("Content-Type", contentType));
                }
                try (Trace trace = new Trace(this.serverApplication.getTraceManager(), parent, "Execute"))
                {
                    statusCode=client.put(trace, null, pathAndQuery.toString(), contentText, headers.toArray(new Header[headers.size()]));
                    duration = trace.getDurationS();
                }
            }
            else if (method.equals("PATCH"))
            {
                String contentType = request.getParameter("contentType");
                String contentText = request.getParameter("contentText");
                if (Strings.isNullOrEmpty(contentType)==false)
                {
                    headers.add(new Header("Content-Type", contentType));
                }
                try (Trace trace = new Trace(this.serverApplication.getTraceManager(), parent, "Execute"))
                {
                    statusCode=client.patch(trace, null, pathAndQuery.toString(), contentText, headers.toArray(new Header[headers.size()]));
                    duration = trace.getDurationS();
                }
            }
            else if (method.equals("DELETE"))
            {
                try (Trace trace = new Trace(this.serverApplication.getTraceManager(), parent, "Execute"))
                {
                    statusCode=client.delete(trace, null, pathAndQuery.toString(), headers.toArray(new Header[headers.size()]));
                    duration = trace.getDurationS();
                }
            }
            else
            {
                AjaxQueryResult result = new AjaxQueryResult();
                Panel3 panel=new Panel3(null, "Not implemented");
                panel.content().addInner("Method=" + method);
                result.put("result",panel.toString());
                return result;
                
                
            }
            AjaxQueryResult result = new AjaxQueryResult();
            Panel3 resultPanel=new Panel3(null, "Result");
            Panel4 statusPanel=resultPanel.content().returnAddInner(new Panel4(null, "Performance and Status"));
            NameValueList list=statusPanel.content().returnAddInner(new NameValueList());
            list.add("Time",DateTimeUtils.toSystemDateTimeString(System.currentTimeMillis()));
            list.add("Duration", duration * 1000 + " ms");
            list.add("Status Code",statusCode);
            if (response!=null)
            {
                if (response.getHeaders().length > 0)
                {
                    resultPanel.content().addInner(new p());
                    Panel4 panel=resultPanel.content().returnAddInner(new Panel4(null, "Response Headers"));
                    NameValueList headerList=panel.content().returnAddInner(new NameValueList());
                    for (Header header : response.getHeaders())
                    {
                        headerList.add(header.getName(),header.getValue());
                    }
                }
                if (response.get().length() > 0)
                {
                    resultPanel.content().addInner(new p());
                    Panel4 contentPanel=resultPanel.content().returnAddInner(new Panel4(null,"Content"));
                    textarea area=contentPanel.content().returnAddInner(new textarea());
                    String text=response.get();
                    area.readonly().style("width:100%;").rows(Utils.occurs(text,"\r")+1);
                    area.addInner(text);
                }
            }
            result.put("result", resultPanel.toString());
            return result;
        }
        catch (Throwable t)
        {
            AjaxQueryResult result = new AjaxQueryResult();
            Panel3 panel=new Panel3(null, "Internal Execution Exception");
            String text=Utils.getStrackTraceAsString(t);
            textarea area=panel.content().returnAddInner(new textarea());
            area.readonly().style("width:100%;").rows(Utils.occurs(text, "\r")+1);
            area.addInner(text);
            
            result.put("result", panel.toString());
            return result;
            
        }
    }
    @GET
    @Path("/operator/httpServer/method/run/{server}")
    @ContentWriters(HtmlElementWriter.class)
    public Element run(Trace parent, Context context, @PathParam("server") String server,@QueryParam("key") String key) throws Throwable
    {
        HttpTransport httpTransport=getHttpTransport(server);
        HttpServletRequest request = context.getHttpServletRequest();
        String[] methodSchema = Utils.split(key, ' ');
        String method = methodSchema[0];
        String schema = methodSchema[1];
        String[] fragments = Utils.split(schema, '/');

        StringBuilder pathAndQuery = new StringBuilder();
        for (int i = 1; i < fragments.length; i++)
        {
            String fragment = fragments[i];
            pathAndQuery.append("/");
            if (fragment.startsWith("{"))
            {
                String name = ParameterSource.PATH + fragment.substring(1, fragment.length() - 2);
                pathAndQuery.append(request.getParameter(name));
            }
            else
            {
                pathAndQuery.append(fragment);
            }
        }
        String prefix = "?";
        for (Enumeration<String> enumerator = request.getParameterNames(); enumerator.hasMoreElements(); enumerator.hasMoreElements())
        {
            String mangledName = enumerator.nextElement();
            if (mangledName.startsWith(ParameterSource.QUERY.toString()))
            {
                String name = mangledName.substring(ParameterSource.QUERY.toString().length());
                pathAndQuery.append(prefix);
                pathAndQuery.append(name);
                pathAndQuery.append("=");
                pathAndQuery.append(request.getParameter(mangledName));
                prefix = "&";
            }
        }
        
        HttpClientEndPoint httpClientEndPoint=getExecuteClient(httpTransport,context);
        String content=httpClientEndPoint.endPoint+pathAndQuery.toString();
        BasicPage page=new BasicPage();
        page.head().addInner(new meta().http_equiv(http_equiv.refresh).content("0;URL='"+content+"'"));
        return page;
    }

    @GET
    @Path("/")
    public Element main() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Main");
        long now = System.currentTimeMillis();
     
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMxBean.getInputArguments();
        String args=Utils.combine(arguments, " ");
        page.content().returnAddInner(new NameValueList())
        .add("JVM Parameters",args)
        .add("Started",DateTimeUtils.toSystemDateTimeString(this.serverApplication.getStartTime()))
        .add("Current",DateTimeUtils.toSystemDateTimeString(now))
        .add("Uptime",Utils.millisToNiceDurationString(now - this.serverApplication.getStartTime()))
        .add("Base Directory",this.serverApplication.getBaseDirectory());
     
        try
        {
            String info=FileUtils.readTextFile("./build-info.txt");
            page.content().addInner("build-info:");
            page.content().addInner(new textarea().style("width:100%;").readonly().rows(8).addInner(info));
        }
        catch (Throwable t)
        {
            page.content().addInner("No build-info available.");
        }
        
        
        
        return page;
    }

    private static DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#.###");


    private void writeSize(Table table, String label, double value)
    {
        table.addRow(label,DOUBLE_FORMAT.format(value),DOUBLE_FORMAT.format(value / 1024),DOUBLE_FORMAT.format(value / 1024 / 1024)
                ,DOUBLE_FORMAT.format(value / 1024 / 1024 / 1024));
    }

    private void write(Table table, String label, LevelMeter meter)
    {
        LevelSample sample=meter.sample();
        table.addRow(label,sample.getLevel(),sample.getMaxLevel(),Utils.millisToLocalDateTime(sample.getMaxLevelInstantMs()));
    }

    private void write(Table table, String label, CountMeter meter)
    {
        table.addRow(label,meter.getCount(),"","");
    }

    @GET
    @Path("/operator/logging/status")
    public Element loggingStatus() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Logging Status");
        LogDirectoryManager manager = this.serverApplication.getLogDirectoryManager();
        if (manager != null)
        {
            if (this.serverApplication.getLogQueue() instanceof HighPerformanceLogger)
            {
                HighPerformanceLogger sink = (HighPerformanceLogger) this.serverApplication.getLogQueue();
                {
                    Panel panel=page.content().returnAddInner(new Panel2(page.head(),"Logger"));
                    {
                        Panel statsPanel=panel.content().returnAddInner(new Panel3(page.head(), "Worker Stats"));
                        Table table=statsPanel.content().returnAddInner(new WideTable(page.head()));
                        table.setHeader("Name","Value","Max","Max Instant");
                        write(table,"Thread Workers Used",sink.getThreadWorkerQueueInUseMeter());
                        write(table,"Waiting in Thread Workers",sink.getThreadWorkerQueueWaitingMeter());
                        write(table,"Stalled in Thread Workers",sink.getThreadWorkerQueueStalledMeter());
                        write(table,"Dropped in Thread Workers",sink.getThreadWorkerQueueDroppedMeter());
                        write(table,"Waiting in Source",sink.getWaitingMeter());
                        write(table,"Stalled in Source ",sink.getStalledMeter());
                        write(table,"Dropped in Source ",sink.getDroppedMeter());
                    }
                    {
                        panel.content().addInner(new p());
                        Panel performancePanel=panel.content().returnAddInner(new Panel3(page.head(),"Performance"));
                        Table table=performancePanel.content().returnAddInner(new WideTable(page.head()));
                        table.setHeader("","Bytes","KB","MB","GB");
                        RateSample sample=sink.getWriteRateMeter().sample(this.rateSamplingDuration);
                        writeSize(table, "Write Rate (per second)", sample.getRate());
                        writeSize(table, "Written", sample.getSamples());
                    }
                }
            }
            
            {
                Panel panel=page.content().returnAddInner(new Panel2(page.head(),"Volume"));

                Panel infoPanel=panel.content().returnAddInner(new Panel3(page.head(), "Info"));
                LogDirectoryInfo info = manager.getLogDirectoryInfo();
                NameValueList list=infoPanel.content().returnAddInner(new NameValueList());
                list.addInner(new style().addInner("thead {background-color:#eee;} td {border:1px solid #888;padding:4px;} table{border-collapse:collapse;} "));
                list.add("Path",manager.getFullDirectoryPath());
                list.add("Deletes due to directory size exceeded",manager.getDirectorySizeDeleteMeter().getCount());
                list.add("Deletes due to maximum files exceeded",manager.getMaximumFilesDeleteMeter().getCount());
                list.add("Deletes due to reserve space exceeded",manager.getReserveSpaceDeleteMeter().getCount());
                list.add("File delete failures",manager.getFileDeleteFailedMeter().getCount());
                list.add("Number of files",info.getFileCount());
                list.add("Oldest file name",info.getOldestFileName());
                list.add("Oldest file date",DateTimeUtils.toSystemDateTimeString(info.getOldestFileDate()));
                list.add("Newest file name",info.getNewestFileName());
                list.add("Newest file date",DateTimeUtils.toSystemDateTimeString(info.getNewestFileDate()));

                panel.content().addInner(new p());
                Panel usagePanel=panel.content().returnAddInner(new Panel3(page.head(), "Usage"));
                Table table=usagePanel.content().returnAddInner(new WideTable(page.head()));
                table.setHeader("","Bytes","KB","MB","GB");
                writeSize(table, "Directory size", info.getDirectorySize());
                writeSize(table, "Volume free space", info.getFreeSpace());
                writeSize(table, "Volume free space", info.getFreeSpace());
                writeSize(table, "Volume usable space", info.getUsableSpace());
                writeSize(table, "Volume total space", info.getTotalSpace());
                if (info.getFileCount() > 0)
                {
                    writeSize(table, "Oldest file size", info.getOldestFileSize());
                    writeSize(table, "Newest file size", info.getNewestFileSize());
                }
                if (info.getThrowable() != null)
                {
                    panel.content().addInner(new p());
                    Panel exceptionPanel=page.content().returnAddInner(new Panel3(page.head(),"Exception"));
                    exceptionPanel.content().addInner(Utils.toString(info.getThrowable()));
                }
            }
        }
        return page;
    }
    
    final static boolean TESTING=false;
    
    @GET
    @Path("/content/{+}")
    public void content(@PathParam(PathParam.AT_LEAST_ONE_SEGMENT) String file, Context context, Trace trace) throws Throwable
    {
        HttpServletResponse response = context.getHttpServletResponse();
        byte[] bytes = this.serverApplication.getFileCache().get(trace, file);
        context.setCaptured(true);
        if (bytes == null)
        {
            if (TESTING)
            {
                Debugging.log(file + " not found");
            }
            response.setStatus(HttpStatus.NOT_FOUND_404);
            return;
        }
        String contentType = this.serverApplication.getContentTypeMappings().getContentType(file);
        if (contentType != null)
        {
            response.setContentType(contentType);
        }
        response.setStatus(HttpStatus.OK_200);
        response.getOutputStream().write(bytes);
    }

    @Log(responseContent=false)
    @GET
    @Path("/resources/{+}")
    public void cache(@PathParam(PathParam.AT_LEAST_ONE_SEGMENT) String file, Context context, Trace trace) throws Throwable
    {
        HttpServletResponse response = context.getHttpServletResponse();
        byte[] bytes;
        try
        {
            bytes = this.serverApplication.getFileCache().get(trace, file);
        }
        catch (Throwable t)
        {
            if (TESTING)
            {
                Debugging.log(file + " not found");
            }
            response.setStatus(HttpStatus.NOT_FOUND_404);
            return;
        }
        String contentType = this.serverApplication.getContentTypeMappings().getContentType(file);
        if (contentType != null)
        {
            response.setContentType(contentType);
        }
        response.setHeader("Cache-Control",
                (this.cacheControlValue == null || this.cacheControlValue.length() == 0) ? "max-age=" + this.cacheMaxAge : this.cacheControlValue + ",max-age=" + this.cacheMaxAge);
        response.setStatus(HttpStatus.OK_200);
        context.writeContent(bytes);
    }

    @Test
    @GET
    @Path("/operator/test/exception")
    public void testException(Trace parent) throws Throwable
    {
        throw new Exception("test");
    }

    @GET
    @Path("/operator/variables/view")
    public Element list() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("View Operator Variables"); 

        List<String> list=Arrays.asList(serverApplication.getOperatorVariableManager().getCategories());
        Collections.sort(list);

        for (String category:list)
        {
            List<VariableInstance> instances=Arrays.asList(this.serverApplication.getOperatorVariableManager().getInstances(category));
//          Collections.sort(variables);

            Panel3 panel=page.content().returnAddInner(new Panel3(page.head(),category));
            page.content().addInner(new p());
            WideTable table=panel.content().returnAddInner(new WideTable(page.head()));
            
            table.setHeader("Name","Type","Validator","Default","Value","Modified","Description");

            for (VariableInstance instance:instances)
            {
                Field field=instance.getField();
                OperatorVariable variable=instance.getOperatorVariable();
                TableRow row=new TableRow();
                row.add(instance.getName());
                row.add(field.getType().getSimpleName());
                row.add(variable.validator().getSimpleName());
                row.add(instance.getDefaultValue());
                row.add(instance.getValue());
                row.add(instance.getModified()==0?"":DateTimeUtils.toSystemDateTimeString(instance.getModified()));
                row.add(variable.description());
                table.addRow(row);

            }
        }
        
        return page;
    }

    @GET
    @Path("/operator/variables/modify")
    public Element modify() throws Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Modify Operator Variables"); 

        List<String> list=Arrays.asList(serverApplication.getOperatorVariableManager().getCategories());
        Collections.sort(list);

        for (String category:list)
        {
            List<VariableInstance> instances=Arrays.asList(this.serverApplication.getOperatorVariableManager().getInstances(category));
//          Collections.sort(variables);
            Panel3 panel=page.content().returnAddInner(new Panel3(page.head(),category));
            page.content().addInner(new p());
            WideTable table=panel.content().returnAddInner(new WideTable(page.head()));

            TableHeader header=new TableHeader();
            header.add("Name");
            header.add("Type");
            header.add("Default");
            header.add("Value");
            header.add(new th_title("Min","Minimum"));
            header.add(new th_title("Max","Maximum"));
            header.add(new th_title("\u2205","Null String"));
            header.add("New Value");
            header.add("Action");
            header.add("Result");
            table.setHeader(header);
            int textSize=10;

            for (VariableInstance instance:instances)
            {
                Field field=instance.getField();
                OperatorVariable variable=instance.getOperatorVariable();
                String name=instance.getName();
                Class<?> type=field.getType();
                Object value=instance.getValue();
                String resultElementId=(category+name+"Result").replace('.', '_');
                String valueKey=(category+name+"Value").replace('.', '_');

                TableRow row=new TableRow();
                row.add(new td().style("text-align:left;").addInner(new TitleText(variable.description(),name)));
                row.add(type.getSimpleName());
                row.add(instance.getDefaultValue());
                row.add(instance.getValue());//,new Attribute("id",valueKey));
                String buttonKey=(category+name+"Button").replace('.', '_');
                String[] options=variable.options();
                Inputs inputs=new Inputs(QuotationMark.QOUT);
                inputs.add(new InputHidden("resultElementId", resultElementId));
                if (options[0].length()!=0)
                {
                    row.add("","","");
                    
                    SelectOptions selectOptions=new SelectOptions();
                    inputs.add(selectOptions);
                    for (String option:options)
                    {
                        selectOptions.add(option);
                    }
                    row.add(selectOptions);
                    
                }
                else if (type==boolean.class)
                {
                    row.add("","","");
                    row.add(inputs.returnAdd(new input_checkbox().id(name).checked((boolean)value)));
                    
                }
                else if (type.isEnum())
                {
                    row.add("","","");
                    
                    SelectOptions selectOptions=new SelectOptions();
                    for (Object enumConstant:field.getType().getEnumConstants())
                    {
                        String option=enumConstant.toString();
                        selectOptions.add(option);
                    }
                    row.add(selectOptions);
                }
                else if (type==String.class) 
                {
                    row.add("","");
                    if (value!=null)
                    {
                        row.add(new input_checkbox().id("nullString").checked(false));
                        row.add(new input_text().id(name).value(value.toString()).size(textSize));
                    }
                    else
                    {
                        row.add(new input_checkbox().id("nullString").checked(true));
                        row.add(new input_text().id(name).size(textSize));
                    }
                }
                else
                {
                    row.add(variable.minimum(),variable.maximum(),"");
                    row.add(new input_text().size(textSize).id(name).value(value==null?"":value.toString()));
                }
                button_button button=new button_button().addInner("Update");
                button.onclick(inputs.js_post("/operator/variable"));
                row.add(button);
                row.add(new div().id(resultElementId));
                table.addRow(row);
            }
        }
        
        return page;
    }

    @POST
    @Path("/operator/variable")
    public RemoteResponse update(@QueryParam("category") String category,@QueryParam("name") String name,@QueryParam("value") String value,@QueryParam("nullValue") boolean nullValue,@QueryParam("resultElementId") String resultElementId) throws Throwable
    {
        RemoteResponse response=new RemoteResponse();
        VariableInstance instance=this.serverApplication.getOperatorVariableManager().getInstance(category, name);
        OperatorVariable variable=instance.getOperatorVariable();
        Object old=instance.getValue();
        ValidationResult validationResult=instance.set(value);
        if (validationResult.getStatus()==Status.SUCCESS)
        {
            response.innerText(resultElementId, "old="+old+", new="+value);
        }
        else
        {
          response.innerText(resultElementId, "Error: "+validationResult.getMessage());
        }
        return response;
      }

}
