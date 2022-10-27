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

import java.util.HashMap;
import java.util.Map.Entry;

import org.nebula.sqlserver.ConnectorAndMigrationConfiguration;
import org.nebula.sqlserver.DatabaseUpdatePermissions;
import org.nebula.sqlserver.DatabaseUpdater;
import org.nova.annotations.Description;
import org.nova.configuration.Configuration;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.ext.Text;
import org.nova.html.operator.NameValueList;
import org.nova.html.operator.OperatorDataTable;
import org.nova.html.operator.Panel;
import org.nova.html.operator.Panel1;
import org.nova.html.operator.TableHeader;
import org.nova.html.operator.TableRow;
import org.nova.html.operator.TraceWidget;
import org.nova.html.tags.a;
import org.nova.html.tags.hr;
import org.nova.html.tags.p;
import org.nova.html.tags.textarea;
import org.nova.html.tags.ext.th_title;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.DefaultValue;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.metrics.CountMeter;
import org.nova.metrics.LevelMeter;
import org.nova.metrics.LevelSample;
import org.nova.metrics.LongValueMeter;
import org.nova.metrics.LongValueSample;
import org.nova.metrics.RateMeter;
import org.nova.metrics.RateSample;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Transaction;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;

@Description("Handlers for server operator pages")
@ContentDecoders(GzipContentDecoder.class)
@ContentEncoders(GzipContentEncoder.class)
@ContentReaders({ JSONContentReader.class})
@ContentWriters({HtmlElementWriter.class})
public class ConnectorController
{
    final private HashMap<String,Connector> connectors;
    final private ServerApplication application;
    
    public ConnectorController(ServerApplication application) throws Exception
    {
        this.connectors=new HashMap<>();
        this.application=application;
        this.application.getMenuBar().add("/operator/connectors/view", "Connectors","View All");
        this.application.getMenuBar().add("/operator/connector/pools/view", "Connectors","View Pools");
        this.application.getMenuBar().add("/operator/connector/stackTraces/view", "Connectors","View Active StackTraces");
        this.application.getOperatorServer().registerHandlers(this);
    }


    public void track(String name,Connector connector) throws Throwable
    {
        synchronized(this)
        {
            if (this.connectors.containsKey(name))
            {
                throw new Exception("Provide unique names. Clashing name="+name);
            }
            this.connectors.put(name, connector);
        }
        this.application.getMeterStore().addMeters("/connectors/"+name,connector);
    }
    public void track(Connector connector) throws Throwable
    {
        track(connector.getName(),connector);
    }
    
    public Connector createConnector(Trace parent,String configurationNameFragment) throws Throwable
    {
        Configuration configuration=this.application.getConfiguration();
        DatabaseUpdatePermissions permisssions=configuration.getJSONObject("Application.Database.MigrationPermission."+configurationNameFragment,new DatabaseUpdatePermissions(),DatabaseUpdatePermissions.class);
        DatabaseUpdater migrator=new DatabaseUpdater(this.application.getCoreEnvironment(),permisssions);
        ConnectorAndMigrationConfiguration connectorAndMigrationconfiguration=configuration.getJSONObject("Application.Database.ConnectorAndMigrationConfiguration."+configurationNameFragment, ConnectorAndMigrationConfiguration.class);
        if (connectorAndMigrationconfiguration==null)
        {
            return null;
        }
        Connector connector=migrator.connectAndMigrate(parent, this.application.getCoreEnvironment(), connectorAndMigrationconfiguration);
        connector.setCaptureActivateStackTrace(connectorAndMigrationconfiguration.captureActivateStackTrace);
        track(configurationNameFragment,connector);
        return connector;
    }    


    @GET
    @Path("/operator/connectors/view")
    public Element viewAll(@QueryParam("minimalResetDurationS") @DefaultValue("10.0") double minimalResetDurationS) throws Throwable
    {
        OperatorPage page=this.application.buildOperatorPage("View Connector Stats");
        OperatorDataTable table=page.content().returnAddInner(new OperatorDataTable(page.head()));
        table.lengthMenu(-1,40,60,100);
        TableHeader header=new TableHeader();
        header.add("Name");
        header.add(new th_title("Updated","Row updated rate (per second)"));
        header.add(new th_title("Count","Rows updated"));
        header.add(new th_title("Updates","Update rate (per second)"));
        header.add(new th_title("Count","Update count"));

        header.add(new th_title("Queried","Rows queried rate (per second)"));
        header.add(new th_title("Count","Rows queried"));
        header.add(new th_title("Queries","Query rate (per second)"));
        header.add(new th_title("Count","Query count"));

        header.add(new th_title("Calls","Call rate (per second)"));
        header.add(new th_title("Count","Call count"));

        header.add(new th_title("Fails","Execute failures"));
        table.setHeader(header);
        

        synchronized(this)
        {
            for (Entry<String, Connector> entry:this.connectors.entrySet())
            {
                Connector connector=entry.getValue();
                TableRow row=new TableRow();
                row.add(entry.getKey());
                write(row,connector.getRowsUpdatedRate(),minimalResetDurationS);
                write(row,connector.getUpdateRate(),minimalResetDurationS);
                write(row,connector.getRowsQueriedRate(),minimalResetDurationS);
                write(row,connector.getQueryRate(),minimalResetDurationS);
                write(row,connector.getCallRate(),minimalResetDurationS);
                row.add(connector.getExecuteFailures().getCount());
                table.addRow(row);
            }
        }

        return page;
    }
    
    private void write(NameValueList list,String name,CountMeter meter)
    {
        list.add(name, meter.getCount());
    }

    private void write(TableRow row,RateMeter meter,double minimalResetDurationS)
    {
        RateSample sample=meter.sample(minimalResetDurationS);
        row.add(String.format("%.3f",sample.getWeightedRate()));
        row.add(sample.getSamples());
    }

    private void writeMax(TableRow row,LevelMeter meter)
    {
        LevelSample sample=meter.sample();
        row.add(sample.getLevel());
        row.add(sample.getMaxLevel());
        if (sample.getMaxLevel()>sample.getBaseLevel())
        {
            row.add(Utils.millisToLocalDateTime(sample.getMaxLevelInstantMs()));
        }
        else
        {
            row.add("");
        }
    }
    private void writeMin(TableRow row,LevelMeter meter)
    {
        LevelSample sample=meter.sample();
        row.add(sample.getBaseLevel());
        row.add(sample.getLevel());
        row.add(sample.getMinLevel());
        if (sample.getMinLevel()<sample.getBaseLevel())
        {
            row.add(Utils.millisToLocalDateTime(sample.getMinLevelInstantMs()));
        }
        else
        {
            row.add("");
        }
    }

    private void write(TableRow row,LongValueMeter meter,long used)
    {
        LongValueSample sample=meter.sample();
        row.add(sample.getMeterTotalCount());
        if (used>0)
        {
            double percentage=(sample.getMeterTotalCount()*100.0)/used;
            row.add(String.format("%.3f",percentage));
        }
        else
        {
            row.add("");
        }
        row.add(String.format("%.3f",sample.getWeightedAverage(0)/1.0e6));
        row.add(String.format("%.3f",sample.getWeightedStandardDeviation(0)/1.0e6));
        if (sample.getMax()>Long.MIN_VALUE)
        {
            row.add(String.format("%.3f",sample.getMax()/1.0e6));
        }
        else
        {
            row.add("");
        }
            
        row.add(Utils.millisToLocalDateTime(sample.getMaxInstantMs()));
    }

    @GET
    @Path("/operator/connector/pools/view")
    public Element viewUsage(@DefaultValue("1.0") @QueryParam("minimalResetDurationS") double minimalResetDurationS) throws Throwable
    {
        OperatorPage page=this.application.buildOperatorPage("View Connector Pools");
        OperatorDataTable table=page.content().returnAddInner(new OperatorDataTable(page.head()));
        table.lengthMenu(-1,40,60,100);
        TableHeader header=new TableHeader();
        header.add("Name");
        header.add(new th_title("Size","Accessor pool size"));
        header.add(new th_title("Available","Accessors available in pool"));
        header.add(new th_title("Min","Minimum accessors available"));
        header.add(new th_title("Min available Instant","Instant when minimum acccessors occurred"));
        header.add(new th_title("Waiting","Threads waiting for accessors"));
        header.add(new th_title("Max","Maximum threads waiting for accessors"));
        header.add(new th_title("Max Waiting Instant","Instant when maximum threads are waiting for accessors"));
        header.add(new th_title("Used","Number of times connector is used"));
        header.add(new th_title("Rate","Connector use rate (per second)"));
        header.add(new th_title("Waits","Number of times when waiting for accessor occurs"));
        header.add(new th_title("%","Percentage times waits occurred"));
        header.add(new th_title("Ave Wait","Average wait for accessor (ms)"));
        header.add(new th_title("StdDev","Standard deviation wait for accessor (ms)"));
        header.add(new th_title("Max","Maximum wait for accessor (ms)"));
        header.add(new th_title("Max Duration Instant","Instant when maximum wait occurs"));
        header.add(new th_title("PRT","Park rolled back transaction"));
        header.add(new th_title("Retires","Retired accessors"));
        table.setHeader(header);

        synchronized(this)
        {
            for (Entry<String, Connector> entry:this.connectors.entrySet())
            {
                Connector connector=entry.getValue();
                TableRow row=new TableRow();
                row.add(entry.getKey());

                writeMin(row,connector.getAccessorsAvailableMeter());
                writeMax(row,connector.getWaitingForAcessorsMeter());
                long used=connector.getUseMeter().getTotalCount();
                row.add(used);
                RateSample useSample=connector.getUseMeter().sample(minimalResetDurationS);
                row.add(String.format("%.3f", useSample.getWeightedRate()));
                
                write(row,connector.getAcessorsWaitNsMeter(),used);
                long parkRollbacks=connector.getParkRolledbacks(null).getCount();
                if (parkRollbacks==0)
                {
                    row.add(parkRollbacks);
                }
                else
                {
                    row.add(new a().addInner(new Text(parkRollbacks)).href(new PathAndQuery("/operator/connector/parkRolledbacks").addQuery("name", entry.getKey()).toString()));
                }

                int retires=connector.getSnapshotOfRetiredConnectors().length;
                if (retires==0)
                {
                    row.add(retires);
                }
                else
                {
                    row.add(new a().addInner(new Text(retires)).href(new PathAndQuery("/operator/connector/retiredConnectors").addQuery("name", entry.getKey()).toString()));
                }
                table.addRow(row);
            }
        }

        return page;
    }
    @GET
    @Path("/operator/connector/retiredConnectors")
    public Element viewRetiredAccessors(@QueryParam("name") String name) throws Throwable
    {
        Connector connector=this.connectors.get(name);
        OperatorPage page=this.application.buildOperatorPage("View Retired Accessors of Connector "+name);
        for (Accessor accessor:connector.getSnapshotOfRetiredConnectors())
        {
            Panel panel=page.content().returnAddInner(new Panel1(page.head(),"Accessor. Identifier="+accessor.getIdentifier()));

            {
                StackTraceElement[] activateStackTrace=accessor.getActivateStackTrace();
                if (activateStackTrace!=null)
                {
                    panel.content().addInner(ServerApplicationPages.formatStackTrace(page.head(),"Activate Stack Trace",activateStackTrace));
                }
            }
            
            TraceWidget traceWidget=new TraceWidget(page.head(), accessor.getTrace(), false, false, false);
            panel.content().addInner(traceWidget);
//            ServerApplicationPages.writeTrace(page.head(), panel.content(), "Parent Trace", accessor.getParent());
            Transaction transaction=accessor.getRetireTransaction();
            if (transaction!=null)
            {
                StackTraceElement[] stackTrace=transaction.getCreateStackTrace();
                if (stackTrace!=null)
                {
                    panel.content().addInner(ServerApplicationPages.formatStackTrace(page.head(),"Transaction Create Success Stack Trace",stackTrace,3));
                }
                
            }
            panel.content().addInner(ServerApplicationPages.formatStackTrace(page.head(),"Transaction Clash Stack Trace",accessor.getRetireStackTrace(),0));
            
            page.content().addInner(new p());
        }        
        
        return page;
    }

    @GET
    @Path("/operator/connector/parkRolledbacks")
    public Element viewParkRolledbacks(@QueryParam("name") String name) throws Throwable
    {
        Connector connector=this.connectors.get(name);
        OperatorPage page=this.application.buildOperatorPage("Park Rollback Transactions of Connector "+name);
        for (Transaction transaction:connector.getSnapshotOfLastParkRolledbackTransactions())
        {
            Panel panel=page.content().returnAddInner(new Panel1(page.head(),"Transaction"));
            {
//                Panel contentPanel=panel.content().returnAddInner(new Panel2(page.head(),"Activate Stack Trace"));
                StackTraceElement[] stackTrace=transaction.getCreateStackTrace();
                if (stackTrace!=null)
                {
                    panel.content().addInner(ServerApplicationPages.formatStackTrace(page.head(),"Transaction Create Stack Trace",stackTrace,3));
                }
            }
            
            TraceWidget traceWidget=new TraceWidget(page.head(), transaction.getTrace(), false, false, false);
            panel.content().addInner(traceWidget);
//            ServerApplicationPages.writeTrace(page.head(), panel.content(), "Parent Trace", accessor.getParent());
            page.content().addInner(new p());
        }        
        
        return page;
    }
    
    @GET
    @Path("/operator/connector/stackTraces/view")
    public Element viewStackTraces() throws Throwable
    {
        OperatorPage page=this.application.buildOperatorPage("View Connector Active Stack Traces");
        
        synchronized(this)
        {
            for (Entry<String, Connector> entry:this.connectors.entrySet())
            {
                Connector connector=entry.getValue();
                for (Accessor accessor:connector.getSnapshotOfAccessorsInUse())
                {
                    String stackTrace=Utils.toString(accessor.getActivateThread().getStackTrace());
                    page.content().addInner("Trace:"+accessor.getTrace().getCategory());
                    Trace parentTrace=accessor.getTrace().getParent();
                    if (parentTrace!=null)
                    {
                        page.content().addInner("Parent Trace:"+parentTrace.getCategory());
                    }
                    page.content().addInner(", Thread:"+accessor.getActivateThread().getName());
                    page.content().addInner(new p());
                    page.content().addInner("Current Stack Trace:");
                    page.content().returnAddInner(new textarea()).rows(15).addInner(stackTrace).style("width:100%;");
                    StackTraceElement[] stackTraceElements=accessor.getActivateStackTrace();
                    if (stackTraceElements!=null)
                    {
                        page.content().addInner("Activate Stack Trace:");
                        page.content().returnAddInner(new textarea()).rows(15).addInner(Utils.toString(stackTraceElements)).style("width:100%;");
                    }
                    page.content().addInner(new hr());
                }
            }
        }

        return page;
    }
    @GET
    @Path("/operator/connector/retiredAccessors")
    public Element viewRetiredAccessors() throws Throwable
    {
        OperatorPage page=this.application.buildOperatorPage("View Connector Pools");

        return page;
    }
}
