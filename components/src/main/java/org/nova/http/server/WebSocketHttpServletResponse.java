package org.nova.http.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.nova.utils.Utils;

class WebSocketHttpServletResponse implements HttpServletResponse
{
    
    final static String CRLF="\r\n";
    
    final HashMap<String,String> headers;
    private String charset;
    private String contentType;
    private int statusCode;
    private Long contentLength;
    private int bufferSize;
    private WebSocketHttpServletOutputStream outputStream;

    WebSocketHttpServletResponse()
    {
        this.headers=new HashMap<>();
        this.statusCode=200;
        this.bufferSize=8192;
        this.charset=null;
        this.contentType=null;
        this.contentLength=null;
    }
    
    WebSocketHttpServletOutputStream getWebSocketHttpServletOutputStream()
    {
        return this.outputStream;
    }
    String getResponseText()
    {
        if (this.outputStream!=null)
        {
            int length=this.outputStream.getByteArrayOutputStream().size();
            this.setIntHeader("Content-Length", length);
        }
        StringBuilder sb=new StringBuilder();
        sb.append("HTTP/1.1 ").append(this.statusCode).append(' ').append(org.apache.commons.httpclient.HttpStatus.getStatusText(200)).append(CRLF);
        for (Entry<String, String> entry:this.headers.entrySet())
        {
            sb.append(entry.getKey()).append(':').append(entry.getValue()).append(CRLF);
        }
        sb.append(CRLF);
        return sb.toString();
    }
    byte[] getContent()
    {
        if (this.outputStream==null)
        {
            return null;
        }
        return this.outputStream.getByteArrayOutputStream().toByteArray();
    }
    
    @Override
    public String getCharacterEncoding()
    {
        return this.charset;
    }
    @Override
    public String getContentType()
    {
        return this.contentType;
    }
    @Override
    public ServletOutputStream getOutputStream() throws IOException
    {
        if (this.outputStream==null)
        {
            this.outputStream=new WebSocketHttpServletOutputStream(this.bufferSize);
        }
        return this.outputStream;
    }
    @Override
    public PrintWriter getWriter() throws IOException
    {
        return new PrintWriter(getOutputStream());
    }
    @Override
    public void setCharacterEncoding(String charset)
    {
        this.charset=charset;
        setContentType();
    }
    private void setContentType()
    {
        StringBuilder sb=new StringBuilder();
        if (this.contentType!=null)
        {
            sb.append(this.contentType);
            if (this.charset!=null)
            {
                sb.append("; ").append("charset=").append(this.charset);
            }
            this.headers.put("Content-Type",sb.toString());
        }
                
    }
    @Override
    public void setContentLength(int len)
    {
        // TODO Auto-generated method stub
    }
    @Override
    public void setContentLengthLong(long len)
    {
        // TODO Auto-generated method stub
    }
    @Override
    public void setContentType(String type)
    {
        this.contentType=type;
        setContentType();
        
    }
    @Override
    public void setBufferSize(int size)
    {
        this.bufferSize=size;
    }
    @Override
    public int getBufferSize()
    {
        return this.bufferSize;
    }
    @Override
    public void flushBuffer() throws IOException
    {
        if (this.outputStream!=null)
        {
            this.outputStream.flush();
        }
    }
    @Override
    public void resetBuffer()
    {
        if (this.outputStream!=null)
        {
            this.outputStream.reset();
        }
    }
    @Override
    public boolean isCommitted()
    {
        return this.outputStream!=null;
    }
    @Override
    public void reset()
    {
        if (this.outputStream!=null)
        {
            throw new IllegalStateException();
        }
        this.statusCode=200;
        this.headers.clear();
    }
    @Override
    public void setLocale(Locale loc)
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
    public void addCookie(Cookie cookie)
    {
        // TODO Auto-generated method stub
        
    }
    @Override
    public boolean containsHeader(String name)
    {
        return this.headers.containsKey(name);
    }
    @Override
    public String encodeURL(String url)
    {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String encodeRedirectURL(String url)
    {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String encodeUrl(String url)
    {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String encodeRedirectUrl(String url)
    {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void sendError(int sc, String msg) throws IOException
    {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void sendError(int sc) throws IOException
    {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void sendRedirect(String location) throws IOException
    {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void setDateHeader(String name, long date)
    {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void addDateHeader(String name, long date)
    {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void setHeader(String name, String value)
    {
        this.headers.put(name, value);
    }
    @Override
    public void addHeader(String name, String value)
    {
        String existing=this.headers.remove(name);
        if (existing==null)
        {
            this.headers.put(name, value);
        }
        else
        {
            this.headers.put(name, existing+"; "+value);
        }
        // TODO Auto-generated method stub
        
    }
    @Override
    public void setIntHeader(String name, int value)
    {
        setHeader(name,Integer.toString(value));
    }
    @Override
    public void addIntHeader(String name, int value)
    {
        addHeader(name,Integer.toString(value));
    }
    @Override
    public void setStatus(int sc)
    {
        this.statusCode=sc;
    }
    @Override
    public void setStatus(int sc, String sm)
    {
        this.statusCode=sc;
    }
    @Override
    public int getStatus()
    {
        return this.statusCode;
    }
    @Override
    public String getHeader(String name)
    {
        return this.headers.get(name);
    }
    @Override
    public Collection<String> getHeaders(String name)
    {
        String value=this.headers.get(name);
        if (value==null)
        {
            return null;
        }
        String[] values=Utils.split(value,';');
        ArrayList<String> list=new ArrayList<>();
        for (String v:values)
        {
            list.add(v.trim());
        }
        return list;
    }
    @Override
    public Collection<String> getHeaderNames()
    {
        return this.headers.keySet();
    }
    
    
}