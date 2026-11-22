package org.nova.services;

import org.nova.frameworks.OperatorPage;
import org.nova.frameworks.ServerOperatorPages.OperatorTable;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.operator.MenuBar;
import org.nova.html.operator.NameValueList;
import org.nova.html.operator.OperatorNameValueTable;
import org.nova.html.properties.Display_;
import org.nova.html.properties.Unit_;
import org.nova.html.properties.display;
import org.nova.html.properties.width;
import org.nova.html.tags.button_submit;
import org.nova.html.tags.div;
import org.nova.html.tags.form_post;
import org.nova.html.tags.input_number;
import org.nova.html.tags.input_password;
import org.nova.html.tags.input_text;
import org.nova.html.tags.label;
import org.nova.html.tags.p;
import org.nova.http.server.Context;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.HtmlContentWriter;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;


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
        menuBar.add("/showRoutes", "Routes","Show Routes");
    }
    @GET
    @Path("/showRoutes")
    public Element showRoutes(Trace parent) throws Throwable
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
}
