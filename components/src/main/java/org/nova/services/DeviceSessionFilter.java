package org.nova.services;

import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nova.concurrent.Lock;
import org.nova.html.ext.Redirect;
import org.nova.http.server.Context;
import org.nova.http.server.Filter;
import org.nova.http.server.RequestHandler;
import org.nova.http.server.Response;
import org.nova.tracing.Trace;

public abstract class DeviceSessionFilter<ROLE extends Enum,SESSION extends DeviceSession<ROLE>> extends Filter
{
    final private SessionManager<SESSION> sessionManager;
    private SESSION debugSession;
    
    public DeviceSessionFilter(SessionManager<SESSION> sessionManager)
    {
        this.sessionManager=sessionManager;
    }

    public void setDebugSession(SESSION session)
    {
        this.debugSession=session;
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
        else if (session.isAccessDenied(handler))
        {
          this.sessionManager.removeSession(parent, session.getToken());
          return new Response<Redirect>(new Redirect("/"));
        }

        Lock<String> lock=null;
        {
            if (method.getAnnotation(AllowNoLock.class)==null)
            {
                lock=sessionManager.waitForLock(parent,session.getToken());
                if (lock==null)
                {
                    return handleNoLock(parent, context);
                }
            }
        }
        session.beginSessionProcessing(lock);
        try
        {
            session.setContext(context);
            context.setState(session);
            Response<?> response=context.next(parent);
            if (session.isPage())
            {
                HttpServletRequest request=context.getHttpServletRequest();
                String pathAndQuery=request.getRequestURI();
                String query=request.getQueryString();
                if (query!=null)
                {
                    pathAndQuery=pathAndQuery+"?"+query;
                }
                logPage(parent,session,pathAndQuery);
            }
            return response;
        }
        catch (Throwable t)
        {
            parent.close(t);
        }
        finally
        {
            session.endSessionProcessing();
            HttpServletResponse response=context.getHttpServletResponse();
            response.setHeader("Cache-Control","no-store, no-cache, must-revalidate, max-age=0");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }
        return handleException(parent, context, parent.getThrowable());
	}
	
    abstract protected Response<?> handleNoLock(Trace parent,Context context) throws Throwable;
    abstract protected Response<?> handleException(Trace parent,Context context,Throwable t) throws Throwable;
	abstract protected Response<?> requestDeviceSession(Trace parent,Context context) throws Throwable;
    abstract protected String getToken(Trace parent,Context context);
	abstract protected void logPage(Trace parent,SESSION session,String pathAndQuery) throws Throwable;
}
