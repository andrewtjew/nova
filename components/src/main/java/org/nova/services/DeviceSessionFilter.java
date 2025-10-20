package org.nova.services;

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
import org.nova.html.ext.DeviceSessionPage;
import org.nova.http.server.Context;
import org.nova.http.server.Filter;
import org.nova.http.server.RequestMethod;
import org.nova.http.server.Response;
import org.nova.json.ObjectMapper;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;

public abstract class DeviceSessionFilter<ROLE extends Enum<?>,SESSION extends DeviceSession<ROLE>,COOKIESTATE extends DeviceCookieState> extends Filter
{
    
    final private SessionManager<SESSION> sessionManager;
    private SESSION debugSession;
    final private Class<COOKIESTATE> coookieStateType;
    final private String cookieStateName;
    
    public DeviceSessionFilter(SessionManager<SESSION> sessionManager,String cookieStateName,Class<COOKIESTATE> cookieStateType)
    {
        this.sessionManager=sessionManager;
        this.coookieStateType=cookieStateType;
        this.cookieStateName=cookieStateName;
    }
    public void setDebugSession(SESSION session)
    {
        this.debugSession=session;
    }
    public String getCookieStateName()
    {
        return this.cookieStateName;
    }
    public Class<COOKIESTATE> getCookieStateType()
    {
        return this.coookieStateType;
    }
    public SessionManager<SESSION> getSessionManager()
    {
        return this.sessionManager;
    }
    
    public COOKIESTATE getCookieState(HttpServletRequest request)
    {
        try
        {
            Cookie[] cookies = request.getCookies();
            if (cookies != null)
            {
                for (Cookie cookie : cookies)
                {
                    if (this.cookieStateName.equals(cookie.getName()))
                    {
                        String value = cookie.getValue();
                        value = URLDecoder.decode(value, StandardCharsets.UTF_8);
                        return ObjectMapper.readObject(value, this.coookieStateType);
                    }
                }
            }
        } 
        catch (Throwable t)
        {
        }
        return null;
    }
    public void setCookieState(HttpServletResponse response,COOKIESTATE state) throws Throwable
    {
        String json=ObjectMapper.writeObjectToString(state);
        String value=URLEncoder.encode(json,StandardCharsets.UTF_8);
        Cookie cookie=new Cookie(getCookieStateName(), value);
        cookie.setPath("/");
        response.addCookie(cookie);    
     }    
    
    public Cookie encodeCookieState(COOKIESTATE state) throws Throwable
    {
        String json=ObjectMapper.writeObjectToString(state);
        String value=URLEncoder.encode(json,StandardCharsets.UTF_8);
        return new Cookie(getCookieStateName(), value);
    }    
    
    final static String LOG_CATEGORY_DEBUG=DeviceSessionFilter.class.getSimpleName();
    final static boolean DEBUG=true;
    final static boolean DEBUG_ACCESS=true;
    final static boolean DEBUG_EXCEPTION_PRINT_STACK_TRACE=false;

    protected String getToken(Trace parent, Context context)
    {
          COOKIESTATE cookieState=context.getState(); //If SessionParametesFilter is in the handler stack, get userState from SessionParametesFilter.  
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
    
	@Override
	public Response<?> executeNext(Trace parent, Context context) throws Throwable 
	{
		String token=getToken(parent,context);
		SESSION session=this.sessionManager.getSessionByToken(token);
        RequestMethod requestMethod=context.getRequestMethod();
        Method method=requestMethod.getMethod();
        if (session==null)
        {
            if (method.getAnnotation(AllowNoSession.class)!=null)
            {
                if (session==null)
                {
                    return context.next(parent);
                }
            }
            if (this.debugSession==null)
            {
                if (method.getAnnotation(AllowNoSession.class)==null)
                {
                    return requestDeviceSession(parent,context);
                }
            }
            else
            {
                session=this.debugSession;
            }
        }

        Lock<String> lock=null;
        if (method.getAnnotation(AllowNoLock.class)==null)
        {
            lock=sessionManager.waitForLock(parent,session.getToken());
            if (lock==null)
            {
                return handleNoLock(parent, context);
            }
        }
        
        try
        {
            AbnormalAccept abnormalAccept=session.acceptRequest(parent, context);
            if (abnormalAccept!=null)
            {
                if (Debug.ENABLE && DEBUG && DEBUG_ACCESS)
                {
                    Debugging.log(LOG_CATEGORY_DEBUG, "Access denied: key="+requestMethod.getKey()+", method="+Debugging.toString(requestMethod.getMethod()),LogLevel.ERROR);
                }
                if (abnormalAccept.statusCode()!=null)
                {
                    context.getHttpServletResponse().setStatus(abnormalAccept.statusCode());
                }
                if (TypeUtils.isNullOrEmpty(abnormalAccept.seeOther())==false)
                {
                    context.seeOther(abnormalAccept.seeOther());
                    return null;
                }
                return handleInvalidQuery(parent, context);
            }
            context.setState(session);

            Response<?> response=context.next(parent);
            if (response!=null)
            {
                Object content=response.getContent();
                if (content instanceof DeviceSessionPage)
                {
                    @SuppressWarnings("unchecked")
                    DeviceSessionPage<SESSION> page=(DeviceSessionPage<SESSION>)content;
                    page.end(parent, context, session);
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
            lock.close();
            HttpServletResponse response=context.getHttpServletResponse();
            response.setHeader("Cache-Control","no-store, no-cache, must-revalidate, max-age=0");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }

	}
	
    abstract protected Response<?> handleInvalidQuery(Trace parent,Context context) throws Throwable;
    abstract protected Response<?> handleNoLock(Trace parent,Context context) throws Throwable;
    abstract protected Response<?> handleException(Trace parent,Context context,Throwable t) throws Throwable;
	abstract protected Response<?> requestDeviceSession(Trace parent,Context context) throws Throwable;
}
