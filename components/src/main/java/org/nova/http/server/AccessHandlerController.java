package org.nova.http.server;

import org.nova.frameworks.OperatorPage;
import org.nova.frameworks.ServerApplication;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.operator.NameValueList;
import org.nova.html.tags.button_submit;
import org.nova.html.tags.form_post;
import org.nova.html.tags.input_checkbox;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.services.SessionFilter;
import org.nova.tracing.Trace;

@ContentDecoders(GzipContentDecoder.class)
@ContentEncoders(GzipContentEncoder.class)
@ContentWriters({HtmlContentWriter.class, HtmlElementWriter.class})
public class AccessHandlerController extends AccessHandler
{
    ServerApplication service;
    public AccessHandlerController(ServerApplication service,String location,SessionFilter sessionFilter) throws Throwable
    {
        super(location,sessionFilter);
        this.service=service;
        this.service.getPublicServer().registerFrontServletHandlers(this);
        this.service.getOperatorServer().registerHandlers(this);

        this.service.getMenuBar().add("/access/status","Access","Status");
    }

    @GET
    @Path("/access/status")
    public Element status(Trace parent,@QueryParam("message") String message) throws Throwable
    {
        OperatorPage page=this.service.buildOperatorPage("Access");
        if (message!=null)
        {
            page.content().addInner(message);
        }
        form_post form=page.content().returnAddInner(new form_post().action("/access/status"));
        NameValueList list=form.returnAddInner(new NameValueList());
        list.add("Online", new input_checkbox().name("online").checked(this.isOnline()));
        list.add("", new button_submit().addInner("Submit"));
        return page;
    }

    @POST
    @Path("/access/status")
    public Response<?> status(Trace parent,Context context,@QueryParam("online") boolean online) throws Throwable
    {
        setOnline(online);
        return Response.seeOther(new PathAndQuery("/access/status").addQuery("message",online?"Server is now online.":"Server is now offline.").toString());
    }
    
}
