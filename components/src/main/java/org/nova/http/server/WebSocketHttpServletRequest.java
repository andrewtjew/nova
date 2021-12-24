package org.nova.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.eclipse.jetty.websocket.api.Session;
import org.nova.json.ObjectMap;
import org.nova.json.ObjectMapper;
import org.nova.json.ValueString;
import org.nova.parsing.scan.Scanner;
import org.nova.parsing.scan.TextSource;

class WebSocketHttpServletRequest implements HttpServletRequest
{
    static class HeaderNameEnumeration implements Enumeration<String>
    {
        final private Iterator<String> iterator;
        HeaderNameEnumeration(HashMap<String,String> headers)
        {
            this.iterator=headers.keySet().iterator();
        }
        @Override
        public boolean hasMoreElements()
        {
            return this.iterator.hasNext();
        }

        @Override
        public String nextElement()
        {
            return this.iterator.next();
        }
        
    }
    static class ValueEnumeration implements Enumeration<String>
    {
        final private String[] parts;
        private int index;
        ValueEnumeration(String values)
        {
            if (values!=null)
            {
                this.parts=org.nova.utils.Utils.split(values,';');
            }
            else
            {
                this.parts=null;
            }
        }
        @Override
        public boolean hasMoreElements()
        {
            if (this.parts==null)
            {
                return false;
            }
            else
            {
                return this.index<this.parts.length;
            }
        }

        @Override
        public String nextElement()
        {
            if ((this.parts==null)||(this.index>=this.parts.length))
            {
                throw new NoSuchElementException();
            }
            return this.parts[this.index++].trim();
        }
        
    }
    
    
    
    final static String CRLF="\r\n";
    
    final private Session session;
    final HashMap<String,String> headers;
    final int contentLength;
    final String protocol;
    final String path;
    final String method;
    private WebSocketHttpServletInputStream inputStream;

    static public class WebSocketHttpRequest
    {
        public String method;
        public String url;
        public ValueString data;
        public ObjectMap headers;
        
    }
    
    WebSocketHttpServletRequest(Session session,String text) throws Throwable
    {
        this.session=session;
        WebSocketHttpRequest request=ObjectMapper.readObject(text, WebSocketHttpRequest.class);
        this.method=request.method;
        this.path=request.url;
        this.protocol="NOVA-WS-HTTP/1.0";
        this.headers=new HashMap<>();
        String contentLength=this.headers.get("Content-Length");
        
        
        this.headers.put("Content-Type", "application/json");
        this.headers.put("Accept", "*/*");
        if (request.headers!=null)
        {
            for (Entry<String, Object> entry:request.headers.entrySet())
            {
                this.headers.put(entry.getKey(), entry.getValue().toString());
            }
        }
        if (request.data!=null)
        {
            String data=request.data.get();
            byte[] bytes=data.getBytes();
            this.inputStream=new WebSocketHttpServletInputStream(bytes,0,bytes.length);
            if (contentLength!=null)
            {
                this.contentLength=Integer.parseInt(contentLength);
            }
            else
            {
                this.contentLength=bytes.length;
            }
        }
        else
        {
            this.contentLength=0;
        }
    }
//    void setContent(byte[] bytes,int offset,int length)
//    {
//        this.inputStream=new WebSocketHttpServletInputStream(bytes,offset,length);
//    }
    
    @Override
    public Object getAttribute(String name)
    {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames()
    {
        return null;
    }

    @Override
    public String getCharacterEncoding()
    {
        return this.headers.get("Accept-Charset");
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException
    {
        this.headers.put("Accept-Charset", env);
    }

    @Override
    public int getContentLength()
    {
        return this.contentLength;
    }

    @Override
    public long getContentLengthLong()
    {
        return this.contentLength;
    }

    @Override
    public String getContentType()
    {
        return this.headers.get("Content-Type");
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        return this.inputStream;
    }

    @Override
    public String getParameter(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames()
    {
        return new ValueEnumeration(null); //incomplete.
    }

    @Override
    public String[] getParameterValues(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String[]> getParameterMap()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProtocol()
    {
        return this.protocol;
    }

    @Override
    public String getScheme()
    {
        return "http";//!!incomplete. get this from the server.
    }

    @Override
    public String getServerName()
    {
        return this.session.getLocalAddress().getHostName();
    }

    @Override
    public int getServerPort()
    {
        return this.session.getLocalAddress().getPort();
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRemoteAddr()
    {
        return this.session.getRemoteAddress().getHostString();
    }

    @Override
    public String getRemoteHost()
    {
        return this.session.getRemoteAddress().getHostName();
    }

    @Override
    public void setAttribute(String name, Object o)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeAttribute(String name)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Locale getLocale()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isSecure()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRealPath(String path)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRemotePort()
    {
        return this.session.getRemoteAddress().getPort();
    }

    @Override
    public String getLocalName()
    {
        return this.session.getLocalAddress().getHostName();
    }

    @Override
    public String getLocalAddr()
    {
        return this.session.getLocalAddress().getHostString();
    }

    @Override
    public int getLocalPort()
    {
        return this.session.getLocalAddress().getPort();
    }

    @Override
    public ServletContext getServletContext()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AsyncContext startAsync(javax.servlet.ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAsyncStarted()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isAsyncSupported()
    {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DispatcherType getDispatcherType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAuthType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Cookie[] getCookies()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getDateHeader(String name)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getHeader(String name)
    {
        return this.headers.get(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name)
    {
        return new ValueEnumeration(this.headers.get(name));
    }

    @Override
    public Enumeration<String> getHeaderNames()
    {
        return new HeaderNameEnumeration(this.headers);
    }

    @Override
    public int getIntHeader(String name)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getMethod()
    {
        return this.method;
    }

    @Override
    public String getPathInfo()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPathTranslated()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getContextPath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getQueryString()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRemoteUser()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isUserInRole(String role)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Principal getUserPrincipal()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRequestedSessionId()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRequestURI()
    {
        return this.path;
    }

    @Override
    public StringBuffer getRequestURL()
    {
        StringBuffer sb=new StringBuffer();
        sb.append(this.path);
        sb.append(" http://");
        sb.append(this.getServerName());
        sb.append(":");
        sb.append(this.getLocalPort());
        sb.append(this.path);
        return sb;
    }

    @Override
    public String getServletPath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HttpSession getSession(boolean create)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HttpSession getSession()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String changeSessionId()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void logout() throws ServletException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}