package org.nova.service.deviceSession;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

import jakarta.servlet.http.HttpServletRequest;

import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.Page;
import org.nova.html.ext.Redirect;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.html.tags.div;
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
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.StateParam;
import org.nova.json.ObjectMapper;
import org.nova.localization.CountryCode;
import org.nova.localization.LanguageCode;
import org.nova.services.RequiredRoles;
import org.nova.services.SessionManager;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;
import org.nova.html.elements.Element;
import org.nova.geo.GeoLocation;
import org.nova.geo.LatitudeLongitude;

@ContentWriters({HtmlElementWriter.class,RemoteResponseWriter.class,JSONContentWriter.class})
@ContentReaders({JSONContentReader.class})
@ContentEncoders({BrotliContentEncoder.class,DeflaterContentEncoder.class,GzipContentEncoder.class})
@RequiredRoles()

abstract public class DeviceSessionController<STATE,COOKIESTATE extends DeviceSessionCookieState,ROLE extends Enum<?>>
{
    final private SessionManager<DeviceSession<STATE,ROLE>> sessionManager;
    public DeviceSessionController(SessionManager<DeviceSession<STATE,ROLE>> sessionManager) throws Throwable
    {
        this.sessionManager=sessionManager;
    }

    final public static String COOKIE_NAME="NOVA-DEVICE-SESSION";
    final public static int COOKIE_MAXAGE=3600*24*7;
    
    @Path("/initialize")
    public Element initialize(Trace parent, Context context, @QueryParam("redirect") String redirect) throws Throwable
    {
        return getInitializationPage(parent, context, redirect);
    }

    String resolveAcceptableLanguage(HttpServletRequest request)
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

    protected LatitudeLongitude resolveDeviceLocation(Trace parent,Double latitude,Double longitude,Context context) throws Throwable
    {
        if ((latitude!=null)&&(longitude!=null))
        {
            return new LatitudeLongitude(latitude, longitude);
        }
        return null;
    }

    protected ZoneId resolveDeviceZoneId(Trace parent,String timeZone,Context context) throws Throwable
    {
        try
        {
            if (TypeUtils.isNullOrSpace(timeZone)==false)
            {
                return TimeZone.getTimeZone(timeZone).toZoneId();
            }
        }
        catch (Throwable t)
        {
        }
        return null;
    }

    abstract protected CountryCode resolveDeviceCountryCode(Trace parent,LatitudeLongitude location,Context context) throws Throwable;
    abstract protected DeviceSession<STATE,ROLE> createDeviceSession(Trace parent,Context context,COOKIESTATE cookieSate,GeoLocation location,String language);
    abstract protected Element getInitializationPage(Trace parent,Context context,String redirect);
    
    @GET
    @Path("/createDeviceSession")
    public void session(Trace parent, Context context, @CookieStateParam(value = COOKIE_NAME, maxAge = COOKIE_MAXAGE, add = true) COOKIESTATE cookieState,
            @QueryParam("timeZone") String timeZone, @QueryParam("latitude") Double latitude, @QueryParam("longitude") Double longitude, @QueryParam("redirect") String redirect) throws Throwable
    {
        HttpServletRequest request = context.getHttpServletRequest();
        String language = resolveAcceptableLanguage(request);
        var location=getLocation(parent, context, timeZone, latitude, longitude);
        
        var deviceSession=createDeviceSession(parent, context, cookieState, location,language);
        cookieState.setToken(deviceSession.getToken());

        this.sessionManager.addSession(parent, deviceSession);
        context.seeOther(redirect);
    }

    GeoLocation getLocation(Trace parent,Context context,String timeZone,Double latitude,Double longitude) throws Throwable
    {
        var zoneId=resolveDeviceZoneId(parent, timeZone, context);
        var location=resolveDeviceLocation(parent, latitude, longitude, context);
        CountryCode countryCode=resolveDeviceCountryCode(parent,location,context);
        return new GeoLocation(location,countryCode,zoneId);
    }
    
    @GET
    @Path("/redirectWithLocation")
    public Redirect redirectWithCurrentLocation(Trace parent, Context context,@StateParam DeviceSession<STATE,ROLE> session,@QueryParam("redirect") String redirect, @QueryParam("timeZone") String timeZone, @QueryParam("latitude") Double latitude, @QueryParam("longitude") Double longitude) throws Throwable
    {
        var location=getLocation(parent, context, timeZone, latitude, longitude);
        session.updateLocation(location);
        return new Redirect(redirect);
    }

    @GET
    @Path("/check/time")
    public Page time(Trace parent, Context context,@StateParam DeviceSession<STATE,ROLE> session,@QueryParam("redirect") String redirect, @QueryParam("timeZone") String timeZone, @QueryParam("latitude") Double latitude, @QueryParam("longitude") Double longitude) throws Throwable
    {
        Page page=new Page();
        page.body().addInner(LocalDateTime.now());
        return page;
    }

    @GET
    @Path("/check/cookie")
    public Page cookie(Trace parent, Context context, @CookieStateParam(value = COOKIE_NAME) COOKIESTATE cookieState) throws Throwable
    {
        Page page=new Page();
        if (cookieState!=null)
        {
            page.body().addInner(ObjectMapper.writeObjectToString(cookieState));
        }
        else
        {
            page.body().addInner("null cookie");
        }
        return page;
    }

    @GET
    @Path("/check/initialize")
    public Element location(Trace parent, Context context) throws Throwable
    {
        return this.getInitializationPage(parent, context, context.getHttpServletRequest().getRequestURI()+"/result");
    }

    @GET
    @Path("/check/initialize/result")
    public Page session(Trace parent, Context context, @CookieStateParam(value = COOKIE_NAME) COOKIESTATE cookieState) throws Throwable
    {
        Page page=new Page();
        if (cookieState!=null)
        {
            page.body().addInner(ObjectMapper.writeObjectToString(cookieState));
        }
        else
        {
            page.body().addInner("null cookie");
        }
        return page;
    }


}
