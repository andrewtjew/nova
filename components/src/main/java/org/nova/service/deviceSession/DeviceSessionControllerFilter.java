package org.nova.service.deviceSession;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.nova.concurrent.Lock;
import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.debug.LogLevel;
import org.nova.geo.GeoLocation;
import org.nova.geo.LatitudeLongitude;
import org.nova.html.elements.Element;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.Page;
import org.nova.html.ext.Redirect;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.tags.div;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.Context;
import org.nova.http.server.Filter;
import org.nova.http.server.RequestMethod;
import org.nova.http.server.Response;
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
import org.nova.services.AllowNoLock;
import org.nova.services.AllowNoSession;
import org.nova.services.RequiredRoles;
import org.nova.services.SessionManager;
import org.nova.services.TokenGenerator;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.http.server.BrotliContentEncoder;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;


@ContentWriters({HtmlElementWriter.class,RemoteResponseWriter.class,JSONContentWriter.class})
@ContentReaders({JSONContentReader.class})
@ContentEncoders({BrotliContentEncoder.class,DeflaterContentEncoder.class,GzipContentEncoder.class})
@RequiredRoles()
public abstract class DeviceSessionControllerFilter<ROLE extends Enum<?>> extends Filter
{
    static public record DeviceSessionResult<ROLE extends Enum<?>>(DeviceSession<ROLE> deviceSession,Response<?> response) 
    {
        public DeviceSessionResult(Response<?> response)
        {
            this(null,response);
        }
        public DeviceSessionResult()
        {
            this(null,null);
        }
        public DeviceSessionResult(DeviceSession<ROLE> deviceSession)
        {
            this(deviceSession,null);
        }
    }

    abstract protected Response<?> handleNoDeviceSession(Trace parent,Context context) throws Throwable;
    abstract protected Response<?> handleInvalidQuery(Trace parent,Context context) throws Throwable;
    abstract protected Response<?> handleNoLock(Trace parent,Context context) throws Throwable;
    abstract protected Response<?> handleException(Trace parent,Context context,Throwable t) throws Throwable;
    
    abstract protected CountryCode resolveDeviceCountryCode(Trace parent,LatitudeLongitude position,Context context) throws Throwable;
    abstract protected DeviceSession<ROLE> createDeviceSession(Trace parent,Context context,String token,String language,LatitudeLongitude position,CountryCode countryCode,ZoneId zoneId) throws Throwable;
    abstract protected DeviceSessionInitializationPage getInitializationPage(Trace parent,Context context,String redirect);
    abstract protected Element getSessionLostPage(Trace parent,Context context) throws Throwable;

    final private SessionManager<DeviceSession<ROLE>> sessionManager;
    final private String deviceSessionControllerPath;
    final private String cookieName;
    final private int cookieAge;
    final private TokenGenerator tokenGenerator;
    
    public DeviceSessionControllerFilter(SessionManager<DeviceSession<ROLE>> sessionManager,String deviceSessionControllerPath,String cookieName,int cookieAge)
    {
        this.tokenGenerator=new TokenGenerator();
        this.sessionManager=sessionManager;
        this.deviceSessionControllerPath=deviceSessionControllerPath;
        this.cookieName=cookieName;
        this.cookieAge=cookieAge;
    }
    public SessionManager<DeviceSession<ROLE>> getSessionManager()
    {
        return this.sessionManager;
    }
    public String generateToken()
    {
        return this.tokenGenerator.next();
    }    
    private String getCookieToken(HttpServletRequest request)
    {
        try
        {
            Cookie[] cookies = request.getCookies();
            if (cookies != null)
            {
                for (Cookie cookie : cookies)
                {
                    if (this.cookieName.equals(cookie.getName()))
                    {
                        String value = cookie.getValue();
                        return value;
                    }
                }
            }
        } 
        catch (Throwable t)
        {
        }
        return null;
    }
    void setCookieToken(HttpServletResponse response,String token) throws Throwable
    {
        Cookie cookie=new Cookie(this.cookieName, token);
        cookie.setPath("/");
        response.addCookie(cookie);    
     }    
    
//    public Cookie encodeCookieState(DeviceSessionCookieState state) throws Throwable
//    {
//        String json=ObjectMapper.writeObjectToString(state);
//        String value=URLEncoder.encode(json,StandardCharsets.UTF_8);
//        return new Cookie(DeviceSessionController.COOKIE_NAME, value);
//    }    
    
    final static String LOG_CATEGORY_DEBUG=DeviceSessionControllerFilter.class.getSimpleName();
    final static boolean DEBUG=true;
    final static boolean DEBUG_ACCESS=true;
    final static boolean DEBUG_EXCEPTION_PRINT_STACK_TRACE=true;

    @Override
    public Response<?> executeNext(Trace parent, Context context) throws Throwable 
    {
        String token=getCookieToken(context.getHttpServletRequest());
        DeviceSession<ROLE> deviceSession=this.sessionManager.getSessionByToken(token);
        RequestMethod requestMethod=context.getRequestMethod();
        Method method=requestMethod.getMethod();
        if (deviceSession==null)
        {
            var result=initializeDeviceSession(parent,context);
            if (result==null)
            {
                return handleNoDeviceSession(parent, context);
            }
            if (result.deviceSession==null)
            {
                return result.response;
            }
            deviceSession=result.deviceSession;
            setCookieToken(context.getHttpServletResponse(), deviceSession.getToken());
        }

        Lock<String> lock=null;
        if (method.getAnnotation(AllowNoLock.class)==null)
        {
            lock=sessionManager.waitForLock(parent,deviceSession.getToken());
            if (lock==null)
            {
                return handleNoLock(parent, context);
            }
        }
        
        try
        {
            AbnormalResult abnormalResult=deviceSession.verifyRequest(parent, context,this);
            if (abnormalResult!=null)
            {
                if (abnormalResult.statusCode()!=null)
                {
                    context.getHttpServletResponse().setStatus(abnormalResult.statusCode());
                }
                if (abnormalResult.response()!=null)
                {
                    return abnormalResult.response();
                }
                if (TypeUtils.isNullOrEmpty(abnormalResult.seeOther())==false)
                {
                    context.seeOther(abnormalResult.seeOther());
                    return null;
                }
                if (Debug.ENABLE && DEBUG && DEBUG_ACCESS)
                {
                    Debugging.log(LOG_CATEGORY_DEBUG, "Access denied: key="+requestMethod.getKey()+", method="+Debugging.toString(requestMethod.getMethod()),LogLevel.ERROR);
                }
                return handleInvalidQuery(parent, context);
            }
            var requestMethodStateType=requestMethod.getStateType();
            
            Response<?> response=null;
            try (Trace trace=new Trace(parent,requestMethod.getKey()))
            {
                PageStateRequestHandling stateHandling=null;
                if (requestMethodStateType==deviceSession.getClass())
                {
                    context.setState(deviceSession);
                }
                else
                {
                    stateHandling=deviceSession.getState();
                    if ((method.getAnnotation(AllowNoSession.class)==null)&&(stateHandling==null))
                    {
                        return handleInvalidQuery(parent, context);
                    }
                    context.setState(stateHandling);
                    if (stateHandling!=null)
                    {
                        stateHandling.beginRequest(parent, context);
                    }
                }
                try
                {
                    response=context.next(parent);
                }
                finally
                {
                    if (stateHandling!=null)
                    {
                        stateHandling.endRequest(parent, context,response);
                    }
                }
            }
            return response;
        }
        catch (Throwable t)
        {
            parent.close(t);
            if (Debug.ENABLE && DEBUG && DEBUG_EXCEPTION_PRINT_STACK_TRACE)
            {
                t.printStackTrace();
            }
            Response<?> exceptionResponse=handleException(parent, context, parent.getThrowable());
            if (exceptionResponse!=null)
            {
                return exceptionResponse;
            }
            throw t;
        }
        finally
        {
            if (lock!=null)
            {
                lock.close();
            }
            HttpServletResponse response=context.getHttpServletResponse();
            response.setHeader("Cache-Control","no-store, no-cache, must-revalidate, max-age=0");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }
    }
    

    protected DeviceSessionResult<ROLE> initializeDeviceSession(Trace parent,Context context) throws Throwable
    {
        HttpServletRequest request = context.getHttpServletRequest();
        var returnType=context.getRequestMethod().getMethod().getReturnType();
        var pathAndQuery=HtmlUtils.getRequestPathAndQuery(context);
        String redirect=new PathAndQuery(this.deviceSessionControllerPath+ "/initialize").addQuery("redirect", pathAndQuery).toString();
        if (returnType==RemoteResponse.class)
        {
            
            RemoteResponse response=new RemoteResponse();
            response.location(this.deviceSessionControllerPath+"/sessionLost");
            return new DeviceSessionResult<>(new Response<>(response));
        }
        else if (returnType==null)
        {
            context.seeOther(redirect);
            return new DeviceSessionResult<>();
        }
        else
        {
            context.seeOther(redirect);
            return new DeviceSessionResult<>(new Response<>(new Redirect(redirect)));
        }
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

    
    GeoLocation getLocation(Trace parent,Context context,String timeZone,Double latitude,Double longitude) throws Throwable
    {
        var zoneId=resolveDeviceZoneId(parent, timeZone, context);
        var location=resolveDeviceLocation(parent, latitude, longitude, context);
        CountryCode countryCode=resolveDeviceCountryCode(parent,location,context);
        return new GeoLocation(location,countryCode,zoneId);
    }
    
//    ------------------------------
    @GET
    @Path("/initialize")
    public Element initialize(Trace parent, Context context, @QueryParam("redirect") String redirect) throws Throwable
    {
        try
        {
            return getInitializationPage(parent, context, redirect);
        }
        catch (Throwable t)
        {
            Object element=handleNoDeviceSession(parent, context);
            if (element instanceof Element)
            {
                return (Element)element;
            }
            return null;
        }
    }

    @GET
    @Path("/createDeviceSession")
    public Element createDeviceSession(Trace parent, Context context, @QueryParam("timeZone") String timeZone, @QueryParam("latitude") Double latitude, @QueryParam("longitude") Double longitude, @QueryParam("redirect") String redirect) throws Throwable
    {
        try
        {
            HttpServletRequest request = context.getHttpServletRequest();
            String language = resolveAcceptableLanguage(request);
            var location=getLocation(parent, context, timeZone, latitude, longitude);
            
            String token=generateToken();
            var deviceSession=createDeviceSession(parent, context, token, language,location.position(),location.countryCode(),location.zoneId());
            if (deviceSession==null)
            {
                throw new Exception();
            }
            this.setCookieToken(context.getHttpServletResponse(), token);
            this.sessionManager.addSession(parent, deviceSession);
            return new Redirect(redirect);
        }
        catch (Throwable t)
        {
            Object element=handleNoDeviceSession(parent, context);
            if (element instanceof Element)
            {
                return (Element)element;
            }
            return null;
        }
    }

    
    @GET
    @Path("/redirectWithLocation")
    public Redirect redirectWithCurrentLocation(Trace parent, Context context,@StateParam DeviceSession<ROLE> session,@QueryParam("redirect") String redirect, @QueryParam("timeZone") String timeZone, @QueryParam("latitude") Double latitude, @QueryParam("longitude") Double longitude) throws Throwable
    {
        try
        {
            var location=getLocation(parent, context, timeZone, latitude, longitude);
            session.updateLocation(location.position(),location.countryCode(),location.zoneId());
        }
        catch (Throwable t)
        {
            //OK if we cannot update location
        }
        return new Redirect(redirect);
    }

    @GET
    @Path("/sessionLost")
    public Element sessionLost(Trace parent, Context context,@StateParam DeviceSession<ROLE> session,@QueryParam("redirect") String redirect, @QueryParam("timeZone") String timeZone, @QueryParam("latitude") Double latitude, @QueryParam("longitude") Double longitude) throws Throwable
    {
        return this.getSessionLostPage(parent, context);
    }

    @GET
    @Path("/check/token")
    public Page token(Trace parent, Context context) throws Throwable
    {
        Page page=new Page();
        String token=this.getCookieToken(context.getHttpServletRequest());
        page.body().returnAddInner(new div()).addInner("token="+token);
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
    public Page session(Trace parent, Context context,@StateParam DeviceSession<?> deviceSession) throws Throwable
    {
        Page page=new Page();
        page.body().returnAddInner(new div()).addInner("deviceSessionId="+deviceSession.getDeviceSessionId());
        page.body().returnAddInner(new div()).addInner("language="+deviceSession.getLanguage());

        page.body().returnAddInner(new div()).addInner("zoneId="+deviceSession.getZoneId()+", countryCode="+deviceSession.getCountryCode());
        var position=deviceSession.getPosition();
        if (position!=null)
        {
            page.body().returnAddInner(new div()).addInner("latitude="+position.latitude+", longitude="+position.longitude);
        }
        return page;
    }
}
