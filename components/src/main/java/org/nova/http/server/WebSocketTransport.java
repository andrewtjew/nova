/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.nova.annotations.Description;
import org.nova.collections.RingBuffer;
import org.nova.http.Header;
import org.nova.logging.Item;
import org.nova.logging.Logger;
import org.nova.metrics.RateMeter;
import org.nova.operations.OperatorVariable;
import org.nova.test.Testing;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;
import org.nova.utils.Utils;

import com.google.common.base.Strings;

public class WebSocketTransport 
{
	final private String path;
	final private HashMap<Integer,Listener> listeners;
	final private HttpServer httpServer;
	Server server;

	static WebSocketTransport SERVER;
	
	
	public WebSocketTransport(String path,HttpServer httpServer,Server server) throws Exception
	{
        SERVER=this;
	    this.path=path;
	    this.listeners=new HashMap<>();
	    this.httpServer=httpServer;
	    this.server=server;

	    ServletContextHandler handler=new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");
        server.setHandler(handler);

        ServletHolder holder=new ServletHolder(new Servlet());
        handler.addServlet(holder, this.path);
        server.start();
	}
	
	static class Listener implements WebSocketListener
	{
	    private Session session;
	    private WebSocketHttpServletRequest request=null;
        @Override
        public void onWebSocketClose(int arg0, String arg1)
        {
            SERVER.removeListener(this);
        }

        public WebSocketHttpServletRequest getWebSocketHttpServletRequest()
        {
            return this.request;
        }
        @Override
        public void onWebSocketConnect(Session session)
        {
            this.session=session;
            SERVER.addListener(this);
        }

        @Override
        public void onWebSocketError(Throwable throwable)
        {
        }

        @Override
        public void onWebSocketBinary(byte[] bytes, int offset, int length)
        {
            if (length==this.request.getContentLength())
            {
                this.request.setContent(bytes, offset, length);
                SERVER.handle(this);
            }
        }

        @Override
        public void onWebSocketText(String text)
        {
            try
            {
                this.request=new WebSocketHttpServletRequest(this.session, text);
                if (this.request.getContentLength()==0)
                {
                    SERVER.handle(this);
                }
            }
            catch (Throwable e)
            {
            }
        }
	    
        Session getSession()
        {
            return this.session;
        }
	}
	
	@SuppressWarnings("serial")
    static class Servlet extends WebSocketServlet
	{

        @Override
        public void configure(WebSocketServletFactory factory)
        {
            factory.register(Listener.class);
        }
	    
	}

	
	void addListener(Listener listener)
	{
	    Session session=listener.getSession();
	    int port=session.getLocalAddress().getPort();
	    synchronized(this.listeners)
	    {
	        this.listeners.put(port, listener);
	    }
	}
    void removeListener(Listener listener)
    {
        Session session=listener.getSession();
        int port=session.getLocalAddress().getPort();
        synchronized(this.listeners)
        {
            this.listeners.remove(port);
        }
    }
	
    public void handle(Listener listener)
    {
        try
        {
            WebSocketHttpServletRequest request=listener.getWebSocketHttpServletRequest();
            WebSocketHttpServletResponse response=new WebSocketHttpServletResponse();
            this.httpServer.handle(request, response);
            Session session=listener.getSession();
            session.getRemote().sendString(response.getResponseText());
            byte[] contentBytes=response.getContent();
            if (contentBytes!=null)
            {
                ByteBuffer buffer=ByteBuffer.allocate(contentBytes.length);
                buffer.put(contentBytes);
                buffer.flip();
                session.getRemote().sendBytes(buffer);
            }
            
        }
        catch (Throwable t)
        {
            this.httpServer.getLogger().log(t);
            t.printStackTrace();
        }
    }

}
