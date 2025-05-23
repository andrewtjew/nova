package org.sample;
import jakarta.servlet.http.HttpServletRequest;

import org.nova.html.elements.Element;
import org.nova.html.ext.Redirect;
import org.nova.html.ext.DeviceSessionPage;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.Context;
import org.nova.http.server.Response;
import org.nova.services.DeviceSessionFilter;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;


public class UserDeviceSessionFilter extends DeviceSessionFilter<Role, UserSession,CookieState>
{
    public UserDeviceSessionFilter(Service service)
    {
        super(service.getSessionManager(),DeviceController.COOKIE_NAME,CookieState.class);
    }

    @Override
    protected Response<?> requestDeviceSession(Trace parent, Context context) throws Throwable
    {
        HttpServletRequest request = context.getHttpServletRequest();
        String URI = request.getRequestURI();
        if (URI.startsWith(DeviceController.PATH))
        {
            return context.next(parent);
        }
        String method = request.getMethod();
        if ("GET".equals(method))
        {
            String queryString = request.getQueryString();
            String redirect = TypeUtils.isNullOrSpace(queryString) ? URI : URI + "?" + queryString;
            return Response.seeOther(new PathAndQuery(DeviceController.PATH + "/initialize").addQuery("redirect", redirect).toString());
        }
        else
        {
            return Response.seeOther("/");
        }
    }

//    @Override
//    protected String getToken(Trace parent, Context context)
//    {
//          CookieState userState=context.getState(); //If SessionParametesFilter is in the handler stack, get userState from SessionParametesFilter.  
//          if (userState==null)
//          {
//              userState=getUserStateFromCookie(context.getHttpServletRequest());
//          }
//          if ((userState==null)||(userState.token==null))
//          {
//              return null;
//          }
//          return userState.token;
//    }

    @Override
    protected Response<?> handleNoLock(Trace parent, Context context) throws Throwable
    {
        return null;
    }

    @Override
    protected Response<?> handleException(Trace parent, Context context, Throwable t) throws Throwable
    {
        return Response.seeOther("/");
    }

    @Override
    protected Response<?> handleInvalidQuery(Trace parent, Context context) throws Throwable
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response<?> bindSession(Trace parent, Context context, UserSession session) throws Throwable
    {
        context.setState(session);
        return null;
    }

}
