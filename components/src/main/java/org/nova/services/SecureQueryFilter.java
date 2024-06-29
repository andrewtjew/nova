package org.nova.services;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.nova.core.NameString;
import org.nova.http.server.Context;
import org.nova.http.server.DecodingHttpServletRequest;
import org.nova.http.server.Filter;
import org.nova.http.server.Response;
import org.nova.http.server.SecureHttpServletRequest;
import org.nova.json.ObjectMapper;
import org.nova.security.QuerySecurity;
import org.nova.security.QuerySecurity.NameParameter;
import org.nova.testing.Debugging;
import org.nova.tracing.Trace;

public class SecureQueryFilter extends Filter
{
    final private SessionManager<? extends QuerySecurity> sessionManager;
    final private String cookieName;

    static class QuerySecurityRequest extends SecureHttpServletRequest
    {
        public QuerySecurityRequest(HttpServletRequest request,QuerySecurity querySecurity,String secureParameters) throws Throwable
        {
            super(request);
            NameParameter[] parameters=querySecurity.decodeParameters(secureParameters);
            this.setParameter(querySecurity.getSecurityQueryKey(), secureParameters);
            for (NameParameter parameter:parameters)
            {
                this.setParameter(parameter.name, parameter.value);
            }
        }
    }
    
    final static boolean DEBUG=true;
    
    public SecureQueryFilter(SessionManager<? extends QuerySecurity> sessionManager,String cookieName)
    {
        this.sessionManager = sessionManager;
        this.cookieName=cookieName;
    }

    @Override
    public Response<?> executeNext(Trace parent, Context context) throws Throwable
    {
        try
        {
            Cookie[] cookies = context.getHttpServletRequest().getCookies();
            if (cookies != null)
            {
                for (Cookie cookie : cookies)
                {
                    if (this.cookieName.equals(cookie.getName()))
                    {
                        String value = cookie.getValue();
                        value = URLDecoder.decode(value, StandardCharsets.UTF_8);
                        UserState userState = ObjectMapper.readObject(value, UserState.class);
                        QuerySecurity querySecurity = this.sessionManager.getSessionByToken(userState.token);
                        if (querySecurity!=null)
                        {
                            context.setState(userState);
                            String secureParameters=context.getHttpServletRequest().getParameter(querySecurity.getSecurityQueryKey());
                            if (secureParameters!=null)
                            {
                                context.setHttpServletRequest(new QuerySecurityRequest(context.getHttpServletRequest(),querySecurity,secureParameters));
                            }
                            else if (Debugging.ENABLE&&DEBUG)
                            {
//                                String method=context.getHttpServletRequest().getMethod();
//                                if (method=="GET")
                                {
                                    RequiredRoles requiredRoles=context.getRequestHandler().getMethod().getDeclaredAnnotation(RequiredRoles.class);
                                    if (requiredRoles!=null)
                                    {
                                        if (requiredRoles.value().length>0)
                                        {
                                            if (secureParameters==null)
                                            {
                                                System.err.println("Not using SessionPathAndQuery:"+context.getRequestHandler().getMethod().getName());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return context.next(parent);
        } 
        catch (Throwable t)
        {
            parent.close(t);
            context.seeOther("/");
            return null;
        }
    }

}
