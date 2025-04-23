package org.nova.services;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.nova.concurrent.Lock;
import org.nova.html.ext.Page;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.ext.Redirect;
import org.nova.html.remote.Remote;
import org.nova.html.tags.script;
import org.nova.http.server.Context;
import org.nova.http.server.Filter;
import org.nova.http.server.RequestHandler;
import org.nova.http.server.Response;
import org.nova.json.ObjectMapper;
import org.nova.services.RoleSession.AccessResult;
import org.nova.testing.Debugging;
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

    abstract public Response<?> bindSession(Trace parent,Context context,SESSION session) throws Throwable;
//    {
//        session.setContext(parent,context);
//        context.setState(session);
//        return null;
//    }
    
    private COOKIESTATE getCookieState(HttpServletRequest request)
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
        RequestHandler handler=context.getRequestHandler();
        Method method=handler.getMethod();
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
            session.beginSessionProcessing(lock);
        }
        
        boolean keepStateAlive=false; 
        try
        {
            if (session.verifyQuery(context)==false)
            {
                return handleInvalidQuery(parent, context);
            }
            AccessResult result=session.isAccessDenied(handler);
            if (result.denied)
            {
              if (TypeUtils.isNullOrEmpty(result.redirect)==false)
              {
                  session.setContinuation(context);
                  return new Response<Redirect>(new Redirect(result.redirect));
              }
              else
              {
                  this.sessionManager.removeSession(parent, session.getToken());
                  return new Response<Redirect>(new Redirect("/"));
              }
            }

            Response<?> stateResponse=bindSession(parent,context,session);
            if (stateResponse!=null)
            {
                return stateResponse; 
            }
            Response<?> response=context.next(parent);
            if (response!=null)
            {
                Object content=response.getContent();
                if (content instanceof Page)
                {
                    Page page=(Page)content;
                    keepStateAlive=true;
                    if (page.isContinuationDisallowed()==false)
                    {
                        String action=session.useContinuation();
                        if (action!=null)
                        {
                            page.body().returnAddInner(new script()).addInner(new LiteralHtml(action));
                        }
                    }
                    logPage(parent,session,context,page);
                }
            }
            return response;
        }
        catch (Throwable t)
        {
            context.seeOther("/");
            parent.close(t);
            return handleException(parent, context, parent.getThrowable());
        }
        finally
        {
            session.updateStates(keepStateAlive);
            session.endSessionProcessing();
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
	abstract protected void logPage(Trace parent,SESSION session,Context context,Page page) throws Throwable;
	
	
	
}
