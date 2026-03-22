package org.nova.service.deviceSession;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.nova.concurrent.Lock;
import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.debug.LogLevel;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.Redirect;
import org.nova.html.remote.RemoteResponse;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.Context;
import org.nova.http.server.Filter;
import org.nova.http.server.RequestMethod;
import org.nova.http.server.Response;
import org.nova.json.ObjectMapper;
import org.nova.services.AllowNoLock;
import org.nova.services.AllowNoSession;
import org.nova.services.SessionManager;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;

public abstract class DeviceSessionFilter<ROLE extends Enum<?>> extends Filter
{
    final private SessionManager<DeviceSession<ROLE>> sessionManager;
    final private String deviceSessionControllerPath;
    final private String cookieName;
    final private int cookieAge;
    public DeviceSessionFilter(SessionManager<DeviceSession<ROLE>> sessionManager,String deviceSessionControllerPath,String cookieName,int cookieAge)
    {
        this.sessionManager=sessionManager;
        this.deviceSessionControllerPath=deviceSessionControllerPath;
        this.cookieName=cookieName;
        this.cookieAge=cookieAge;
    }
    public SessionManager<DeviceSession<ROLE>> getSessionManager()
    {
        return this.sessionManager;
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
    
    public Cookie encodeCookieState(DeviceSessionCookieState state) throws Throwable
    {
        String json=ObjectMapper.writeObjectToString(state);
        String value=URLEncoder.encode(json,StandardCharsets.UTF_8);
        return new Cookie(DeviceSessionController.COOKIE_NAME, value);
    }    
    
    final static String LOG_CATEGORY_DEBUG=DeviceSessionFilter.class.getSimpleName();
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
        context.getCookieState("test");
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
            
            
            if (requestMethodStateType==deviceSession.getClass())
            {
                context.setState(deviceSession);
            }
            else
            {
                var state=deviceSession.getState();
                if ((method.getAnnotation(AllowNoSession.class)==null)&&(state==null))
                {
                    return handleInvalidQuery(parent, context);
                }
                context.setState(state);
            }

            Response<?> response;
            try (Trace trace=new Trace(parent,requestMethod.getKey()))
            {
                response=context.next(parent);
            }
            deviceSession.endRequest(response);
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

    abstract protected Response<?> handleNoDeviceSession(Trace parent,Context context) throws Throwable;
    abstract protected Response<?> handleInvalidQuery(Trace parent,Context context) throws Throwable;
    abstract protected Response<?> handleNoLock(Trace parent,Context context) throws Throwable;
    abstract protected Response<?> handleException(Trace parent,Context context,Throwable t) throws Throwable;
}
