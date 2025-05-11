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
import java.util.HashMap;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.server.JettyWebSocketServlet;
import org.eclipse.jetty.websocket.server.JettyWebSocketServletFactory;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;
import org.nova.html.tags.body;
import org.nova.html.tags.h1;
import org.nova.html.tags.html;
import org.nova.json.ObjectMapper;
import org.nova.tracing.Trace;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class WebSocketTransport 
{
    static class Listener implements WebSocketListener
    {
        private Session session;
        
        Listener(WebSocketTransport webSocketTransport)
        {
        }
        
        @Override
        public void onWebSocketClose(int arg0, String arg1)
        {
        }

        @Override
        public void onWebSocketConnect(Session session)
        {
            this.session=session;
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
    

    public class MyJettyWebSocketServlet extends JettyWebSocketServlet
    {
        @Override
        protected void configure(JettyWebSocketServletFactory factory)
        {
            // At most 1 MiB text messages.
            factory.setMaxTextMessageSize(1048576);

            // Add the WebSocket endpoint.
            factory.addMapping("/ws/echo", (upgradeRequest, upgradeResponse) ->
            {
                // Possibly inspect the upgrade request and modify the upgrade response.

                // Create the new WebSocket endpoint.
                return new Listener(null);
            });
        }
    }	
    
    static class TestHandler extends AbstractHandler
    {

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
        {
            if ("/hello".equals(target))
            {
                html html=new html();
                body body=html.returnAddInner(new body());
                body.returnAddInner(new h1()).addInner("Hello:"+System.currentTimeMillis());
                
            }
        }
        
    }
    
	public WebSocketTransport(String path,HttpServer httpServer,Server[] servers) throws Exception
	{
	 // Create a Server with a ServerConnector listening on port 8080.
	    Server server = new Server(18080);

	    // Create a ServletContextHandler with the given context path.
	    ServletContextHandler handler = new ServletContextHandler(server, "/");
	    server.setHandler(handler);

	    JettyWebSocketServletContainerInitializer.configure(handler, (servletContext, container) ->
	    {
	        // Configure the ServerContainer.
	        container.setMaxTextMessageSize(128 * 1024);

	        // Use JettyWebSocketCreator to have more control on the WebSocket endpoint creation.
	        container.addMapping("/ws/echo", (upgradeRequest, upgradeResponse) ->
	        {
	            // Create the new WebSocket endpoint.
	            return new Listener(null);
	        });
	    });

	    // Starting the Server will start the ServletContextHandler.
	    server.start();	}
	

	
}
