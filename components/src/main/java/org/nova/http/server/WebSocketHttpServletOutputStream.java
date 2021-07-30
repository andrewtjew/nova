package org.nova.http.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.ReadListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.eclipse.jetty.websocket.api.Session;

public class WebSocketHttpServletOutputStream extends ServletOutputStream
{
    final private ByteArrayOutputStream outputStream;
    
    WebSocketHttpServletOutputStream(int bufferSize)
    {
        this.outputStream=new ByteArrayOutputStream(bufferSize);
    }
    @Override
    public boolean isReady()
    {
        return true;
    }
    @Override
    public void setWriteListener(WriteListener writeListener)
    {
        try
        {
            writeListener.onWritePossible();
        }
        catch (IOException e)
        {
        }
    }
    @Override
    public void write(int arg0) throws IOException
    {
        this.outputStream.write(arg0);
    }
    
    void reset()
    {
        this.outputStream.reset();
    }
    
    ByteArrayOutputStream getByteArrayOutputStream()
    {
        return this.outputStream;
    }
}