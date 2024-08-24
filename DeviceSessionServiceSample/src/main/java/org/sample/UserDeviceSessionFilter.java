package org.sample;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nova.concurrent.Lock;
import org.nova.html.bootstrap.LinkButton;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.elements.Element;
import org.nova.html.ext.Page;
import org.nova.html.ext.Redirect;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.Context;
import org.nova.http.server.DecodingHttpServletRequest;
import org.nova.http.server.Filter;
import org.nova.http.server.RequestHandler;
import org.nova.http.server.Response;
import org.nova.http.server.ValueQ;
import org.nova.json.ObjectMapper;
import org.nova.localization.LanguageCode;
import org.nova.services.DeviceSessionFilter;
import org.nova.services.SessionManager;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Insert;
import org.nova.sqldb.Row;
import org.nova.sqldb.Select;
import org.nova.sqldb.SqlUtils;
import org.nova.sqldb.Transaction;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;


public class UserDeviceSessionFilter extends DeviceSessionFilter<Role, UserSession,CookieState>
{
    public UserDeviceSessionFilter(Service service)
    {
        super(service.getSessionManager(),"X-Cookie",CookieState.class);
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
            return new Response<Redirect>(new Redirect(new PathAndQuery(DeviceController.PATH + "/initialize").addQuery("redirect", redirect).toString()));
        }
        else
        {
            context.seeOther("/");
            return null;
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
        context.seeOther("/");
        return null;
    }

    @Override
    protected void logPage(Trace parent, UserSession session, Context context, Page page) throws Throwable
    {
        // TODO Auto-generated method stub
        
    }

}
