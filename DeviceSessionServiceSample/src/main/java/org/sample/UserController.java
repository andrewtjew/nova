package org.sample;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.nova.configuration.Configuration;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.Redirect;
import org.nova.html.google.GoogleMap;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.html.tags.img;
import org.nova.html.tags.script;
import org.nova.http.server.BrotliContentEncoder;
import org.nova.http.server.Context;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.http.server.ValueQ;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.CookieStateParam;
import org.nova.http.server.annotations.Filters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.StateParam;
import org.nova.localization.LanguageCode;
import org.nova.services.DeviceSessionFilter;
import org.nova.services.RequiredRoles;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Insert;
import org.nova.sqldb.Row;
import org.nova.sqldb.Select;
import org.nova.sqldb.Transaction;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;


@ContentWriters({HtmlElementWriter.class,RemoteResponseWriter.class,JSONContentWriter.class})
@ContentReaders({JSONContentReader.class})
@ContentEncoders({DeflaterContentEncoder.class,GzipContentEncoder.class})
@Filters({DeviceSessionFilter.class})
public class UserController 
{
    final private Service service;
    public UserController(Service service) throws Throwable
    {
        this.service=service;
        
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


}
