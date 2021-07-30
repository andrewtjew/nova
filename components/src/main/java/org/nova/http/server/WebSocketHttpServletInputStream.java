package org.nova.http.server;

import java.io.BufferedReader;
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
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.eclipse.jetty.websocket.api.Session;

public class WebSocketHttpServletInputStream extends ServletInputStream
{
    int index;
    final private byte[] bytes;
    final private int offset;
    final private int length;

    WebSocketHttpServletInputStream(byte[] bytes,int offset,int length)
    {
        this.bytes=bytes;
        this.offset=offset;
        this.length=length;
        this.index=0;
    }
    @Override
    public boolean isFinished()
    {
        return this.index>=length;
    }

    @Override
    public boolean isReady()
    {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener)
    {
        try
        {
            readListener.onDataAvailable();
            readListener.onAllDataRead();
        }
        catch (IOException e)
        {
        }
    }

    @Override
    public int read() throws IOException
    {
        if (this.index>=this.length)
        {
            return -1;
        }
        return this.bytes[this.offset+(this.index++)];
    }
    
}