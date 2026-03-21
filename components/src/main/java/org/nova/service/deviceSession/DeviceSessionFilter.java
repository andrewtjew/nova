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

public abstract class DeviceSessionFilter<STATE,ROLE extends Enum<?>> extends Filter
{
    final private SessionManager<DeviceSession<STATE,ROLE>> sessionManager;
    final private Class<STATE> stateType;
    final private String deviceSessionControllerPath;
    public DeviceSessionFilter(SessionManager<DeviceSession<STATE,ROLE>> sessionManager,Class<STATE> stateType,String deviceSessionControllerPath)
    {
        this.sessionManager=sessionManager;
        this.stateType=stateType;
        this.deviceSessionControllerPath=deviceSessionControllerPath;
    }
    public SessionManager<DeviceSession<STATE,ROLE>> getSessionManager()
    {
        return this.sessionManager;
    }
    
    public DeviceSessionCookieState getCookieState(HttpServletRequest request)
    {
        try
        {
            Cookie[] cookies = request.getCookies();
            if (cookies != null)
            {
                for (Cookie cookie : cookies)
                {
                    if (DeviceSessionController.COOKIE_NAME.equals(cookie.getName()))
                    {
                        String value = cookie.getValue();
                        value = URLDecoder.decode(value, StandardCharsets.UTF_8);
                        return ObjectMapper.readObject(value, DeviceSessionCookieState.class);
                    }
                }
            }
        } 
        catch (Throwable t)
        {
        }
        return null;
    }
    public void setCookieState(HttpServletResponse response,DeviceSessionCookieState state) throws Throwable
    {
        String json=ObjectMapper.writeObjectToString(state);
        String value=URLEncoder.encode(json,StandardCharsets.UTF_8);
        Cookie cookie=new Cookie(DeviceSessionController.COOKIE_NAME, value);
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

    protected String getToken(Trace parent, Context context)
    {
          DeviceSessionCookieState cookieState=context.getState(); //If SessionParametesFilter is in the handler stack, get userState from SessionParametesFilter.  
          if (cookieState==null)
          {
              cookieState=getCookieState(context.getHttpServletRequest());
          }
          if (cookieState==null)
          {
              return null;
          }
          return cookieState.getToken();
    }
    
//    static public record DeviceSessionOrResponse<ROLE extends Enum<?>,USERSTATE>(DeviceSession<USERSTATE,ROLE> session,Response<?> response)
//    {
//        public DeviceSessionOrResponse(DeviceSession<USERSTATE,ROLE> session)
//        {
//            this(session, null);
//        }
//        public DeviceSessionOrResponse(Response<?> response)
//        {
//            this(null,response);
//        }
//        
//    }
    
    @Override
    public Response<?> executeNext(Trace parent, Context context) throws Throwable 
    {
        String token=getToken(parent,context);
        DeviceSession<STATE,ROLE> deviceSession=this.sessionManager.getSessionByToken(token);
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
            AbnormalResult<?> abnormalResult=deviceSession.verifyRequest(parent, context,this);
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
            
            
            if (requestMethodStateType==this.stateType)
            {
                var state=deviceSession.getState();
                if ((method.getAnnotation(AllowNoSession.class)==null)&&(state==null))
                {
                    return handleInvalidQuery(parent, context);
                }
                context.setState(state);
            }
            else if (requestMethodStateType==deviceSession.getClass())
            {
                context.setState(deviceSession);
            }

            Response<?> response;
            try (Trace trace=new Trace(parent,requestMethod.getKey()))
            {
                response=context.next(parent);
            }
            deviceSession.endRequest(response);
            
//            if (response!=null)
//            {
//                Object content=response.getContent();
//                if (content instanceof DeviceSession2Page)
//                {
//                    @SuppressWarnings("unchecked")
//                    DeviceSession2Page<SESSION> page=(DeviceSession2Page<SESSION>)content;
//                    page.end(parent, context, session);
//                }
//            }
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
    
    static public record DeviceSessionResult<STATE,ROLE extends Enum<?>>(DeviceSession<STATE,ROLE> deviceSession,Response<?> response) 
    {
        public DeviceSessionResult(Response<?> response)
        {
            this(null,response);
        }
        public DeviceSessionResult()
        {
            this(null,null);
        }
        public DeviceSessionResult(DeviceSession<STATE,ROLE> deviceSession)
        {
            this(deviceSession,null);
        }
    }

    protected DeviceSessionResult<STATE,ROLE> initializeDeviceSession(Trace parent,Context context) throws Throwable
    {
        HttpServletRequest request = context.getHttpServletRequest();
        var returnType=context.getRequestMethod().getMethod().getReturnType();
        var pathAndQuery=HtmlUtils.getRequestPathAndQuery(context);
        String redirect=new PathAndQuery(this.deviceSessionControllerPath+ "/initialize").addQuery("redirect", pathAndQuery).toString();
        if (returnType==RemoteResponse.class)
        {
            RemoteResponse response=new RemoteResponse();
            response.location(redirect);
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
