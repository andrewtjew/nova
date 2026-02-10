package org.sample;
import jakarta.servlet.http.HttpServletRequest;

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
            context.seeOther(new PathAndQuery(DeviceController.PATH + "/initialize").addQuery("redirect", redirect).toString());
            return null;
        }
        else
        {
            context.seeOther("/");
            return null;
        }
    }

    @Override
    protected Response<?> handleNoLock(Trace parent, Context context) throws Throwable
    {
        UserPage page = new UserPage(null, null);
        page.body().addInner("No Lock");
        return new Response<UserPage>(page);
    }

    @Override
    protected Response<UserPage> handleException(Trace parent, Context context, Throwable t) throws Throwable
    {
        UserPage page = new UserPage(null, null);
        page.body().addInner("exception");
        return new Response<UserPage>(page);
    }

    @Override
    protected Response<?> handleInvalidQuery(Trace parent, Context context) throws Throwable
    {
        UserPage page = new UserPage(null, null);
        page.body().addInner("Invalid Query");
        return new Response<UserPage>(page);
    }

}
