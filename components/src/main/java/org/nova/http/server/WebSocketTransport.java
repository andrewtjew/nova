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

import java.util.HashMap;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.nova.json.ObjectMapper;
import org.nova.tracing.Trace;

public class WebSocketTransport 
{
    static class Listener implements WebSocketListener
    {
        private Session session;
        final private WebSocketTransport webSocketTransport;
        
        Listener(WebSocketTransport webSocketTransport)
        {
            this.webSocketTransport=webSocketTransport;
        }
        
        @Override
        public void onWebSocketClose(int arg0, String arg1)
        {
            this.webSocketTransport.removeListener(this);
        }

        @Override
        public void onWebSocketConnect(Session session)
        {
            this.session=session;
            this.webSocketTransport.addListener(this);
        }

        @Override
        public void onWebSocketError(Throwable throwable)
        {
        }

        @Override
        public void onWebSocketBinary(byte[] bytes, int offset, int length)
        {
        }

        @Override
        public void onWebSocketText(String text)
        {
            try
            {
                System.out.println("rec:"+text);
                this.webSocketTransport.handle(this,text);
            }
            catch (Throwable e)
            {
            }
        }
        
        public Session getSession()
        {
            return this.session;
        }
    }
    
    static public class Creator implements WebSocketCreator
    {
        final private WebSocketTransport webSocketTransport;
        
        Creator(WebSocketTransport webSocketTransport)
        {
            this.webSocketTransport=webSocketTransport;
        }

        @Override
        public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp)
        {
            return new Listener(this.webSocketTransport);
        }
    }   
    
    final private String path;
	final private HashMap<Integer,Listener> listeners;
	final private HttpServer httpServer;
	final private Creator creator;

	
	public WebSocketTransport(String path,HttpServer httpServer,Server[] servers) throws Exception
	{
	    this.path=path;
	    this.listeners=new HashMap<>();
	    this.httpServer=httpServer;
	    this.creator=new Creator(this);

	    WebSocketHandler handler = new WebSocketHandler()
        {
            @Override
            public void configure(WebSocketServletFactory factory)
            {
                factory.setCreator(creator);
            }
    
        };
        for (Server server:servers)
        {
            server.setHandler(handler);
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
	
    static public class NovaWsHttpResponse
    {
        //The names map to javascript 
        public int statusCode;
        public String data;
    }

    public void handle(Listener listener,String text) throws Throwable
    {
        
        try (Trace trace=new Trace(this.httpServer.getTraceManager(),"NOVA-WS-HTTP")) 
        {
            try
            {
                WebSocketHttpServletResponse response=new WebSocketHttpServletResponse();
                response.setStatus(200);
                Session session=listener.getSession();
                try
                {
                    WebSocketHttpServletRequest request=new WebSocketHttpServletRequest(session, text);
                    this.httpServer.handle(request, response);
        //            byte[] contentBytes=response.getContent();
        //            if (contentBytes!=null)
        //            {
        //                ByteBuffer buffer=ByteBuffer.allocate(contentBytes.length);
        //                buffer.put(contentBytes);
        //                buffer.flip();
        //                session.getRemote().sendBytes(buffer);
        //            }
                    
                }
                catch (Throwable t)
                {
                    trace.close(t);
                    response.setStatus(500);
                }
                NovaWsHttpResponse r=new NovaWsHttpResponse();
                r.data=response.getResponseText();
                r.statusCode=response.getStatus();
                session.getRemote().sendString(ObjectMapper.writeObjectToString(r));
            }
            catch (Throwable t)
            {
                trace.close(t);
            }
        }
    }

}
