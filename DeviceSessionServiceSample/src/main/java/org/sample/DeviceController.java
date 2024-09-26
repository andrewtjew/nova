package org.sample;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;

import jakarta.servlet.http.HttpServletRequest;

import org.nova.configuration.Configuration;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.Redirect;
import org.nova.html.google.GoogleMap;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.html.tags.script;
import org.nova.http.client.PathAndQuery;
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
import org.nova.localization.LanguageCode;
import org.nova.services.DeviceSessionFilter;
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
@Path(DeviceController.PATH)
public class DeviceController 
{
    public static final String PATH = "/$/device";
    final public static String COOKIE_NAME="X-Sample";
    final public static int COOKIE_MAXAGE=3600*24*30;
    

    final private Service service;
    private long loggedInDays;
    public DeviceController(Service service) throws Throwable
    {
        this.service=service;
        Configuration configuration=service.getConfiguration();
        this.loggedInDays=configuration.getIntegerValue("Application.loggedInDays",30);
        
    }

    @GET
    @Path("/initialize")
    public UserPage initialize(Trace parent, Context context, @CookieStateParam(value = COOKIE_NAME, add = true) CookieState cookieState, @QueryParam("redirect") String redirect) throws Throwable
    {
        UserPage page = new UserPage(null, null);
//        String path=new PathAndQuery(PATH+"/session").addQuery("redirect",redirect).toString();
        String path=PATH+"/session?redirect="+redirect;
        page.body().onload(HtmlUtils.js_call("nova.device.initialize", path));
        return page;
    }

    String getAcceptableLanguage(HttpServletRequest request)
    {
        String accepts = request.getHeader("Accept-Language");
        if (accepts != null)
        {
            List<ValueQ> values = ValueQ.sortDescending(accepts);
            for (ValueQ valueQ : values)
            {
                LanguageCode code = LanguageCode.fromCodeISO_639_1(valueQ.value);
                if (code != null)
                {
                    return valueQ.value;
                }
            }
        }
        return "en";
    }

    static class DeviceSessionResult
    {
        public long deviceSessionId;
        public Long userId;
        public LocalDateTime lastLoggedIn;
    }

    public DeviceSessionResult createDeviceSession(Trace parent, CookieState userState, HttpServletRequest request) throws Throwable
    {
        DeviceSessionResult result = new DeviceSessionResult();
//        Timestamp now = Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC));
//        String userAgent = request.getHeader("User-Agent");
//        String remote = request.getRemoteHost();
//        try (Accessor accessor = this.service.getConnector().openAccessor(parent))
//        {
//            Long deviceId = null;
//            Row row = Select.source("devicesession JOIN device ON device.id=devicesession.deviceId").columns("devicesession.id","devicesession.keepLoggedIn","userId","deviceId", "remote", "userAgent").where("token=?", userState.token).executeOne(parent, accessor);
//            if (row != null)
//            {
//                String sessionRemote = row.getVARCHAR("remote");
//                String deviceUserAgent = row.getVARCHAR("userAgent");
//                if (TypeUtils.equals(remote, sessionRemote) && TypeUtils.equals(userAgent, deviceUserAgent))
//                {
//                    result.deviceSessionId = row.getBIGINT("id");
//                    deviceId = row.getBIGINT("deviceId");
//                    result.userId=row.getNullableBIGINT("userId");
//                    if (result.userId!=null)
//                    {
//                        result.remembered=row.getTIMESTAMP("keepLoggedIn").toLocalDateTime();
//                    }
//                }
//            }
//
//            if (deviceId == null)
//            {
//                try (Transaction transaction = accessor.beginTransaction("createDeviceSession"))
//                {
//                    deviceId = Insert.table("device").value("created", now).value("userAgent", userAgent).executeAndReturnLongKey(parent, accessor);
//                    for (int i = 0; true; i++)
//                    {
//                        userState.token = this.service.generateToken();
//                        try
//                        {
//                            result.deviceSessionId = Insert.table("devicesession").value("deviceId", deviceId).value("token", userState.token).value("loggedInDays", remote).value("created", now)
////                                    .value("latitude", latitude).value("longitude", longitude).value("country", country)
//                                    .executeAndReturnLongKey(parent, accessor);
//                            transaction.commit();
//                            break;
//                        } catch (Throwable t)
//                        {
//                            if (i == 10)
//                            {
//                                throw t;
//                            }
//                        }
//                    }
//                }
//            }
//        }
        result.deviceSessionId=0L;
        result.lastLoggedIn=LocalDateTime.now().minusDays(this.loggedInDays-1);
        result.userId=0L;
        return result;
    }

    @GET
    @Path("/session")
    public Element session(Trace parent, Context context, @CookieStateParam(value = COOKIE_NAME, maxAge = COOKIE_MAXAGE, add = true) CookieState cookieState,
            @QueryParam("timeZone") String timeZone, @QueryParam("redirect") String redirect) throws Throwable
    {
        HttpServletRequest request = context.getHttpServletRequest();
        String language = getAcceptableLanguage(request);
        if (cookieState.getToken() == null)
        {
            cookieState.setToken(this.service.generateSessionToken());
        }

        DeviceSessionResult result = createDeviceSession(parent, cookieState, request);
        
        UserSession session = new UserSession(this.service, result.deviceSessionId,cookieState.getToken(), TimeZone.getTimeZone(timeZone).toZoneId());
        if (result.userId!=null)
        {
            LocalDateTime now=LocalDateTime.now();
            long days=Duration.between(result.lastLoggedIn, now).toDays();
            if ((days<this.loggedInDays)&&(result.userId!=null))
            {
                session.login(parent,result.userId,Role.User);
            }
        }
        this.service.getSessionManager().addSession(parent, session);
        return new Redirect(redirect);
    }

    @GET
    @Path("/debug/clear")
    public Element clear(Trace parent, @CookieStateParam(value = COOKIE_NAME, add = true) CookieState cookieState) throws Throwable
    {
        cookieState.setToken(null);
        return new Redirect("/");
    }

}
