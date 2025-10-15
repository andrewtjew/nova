package org.sample;

import org.nova.html.elements.Element;
import org.nova.html.ext.Redirect;
import org.nova.html.tags.img;
import org.nova.http.client.SecurePathAndQuery;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.StateParam;
import org.nova.services.RequiredRoles;
import org.nova.tracing.Trace;

public class UserController extends PageController
{
    public UserController(Service service) throws Throwable
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
        page.body().addInner("Hello world");
        return page;
    }

    @GET
    @Path("/nova")
    @RequiredRoles("User")
    public UserPage nova(Trace parent,@StateParam UserSession session) throws Throwable
    {
        UserPage page = new UserPage(null, null);
        img img=page.body().returnAddInner(new img()).src("/$/images/nova.png");
        return page;
    }

    @GET
    @Path("/secure")
    @RequiredRoles("User")
    public Element query(Trace parent,@StateParam UserSession session) throws Throwable
    {
        return new Redirect(new SecurePathAndQuery(session,"/query").addQuery("value",12345).toString());
    }

    @GET
    @Path("/query")
    @RequiredRoles("User")
    public UserPage query1(Trace parent,@StateParam UserSession session,@QueryParam("value") long value) throws Throwable
    {
        UserPage page = new UserPage(null, null);
        page.body().addInner("value="+value);
        return page;
    }


}
