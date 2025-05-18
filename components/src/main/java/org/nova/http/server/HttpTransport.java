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
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;
import org.nova.http.server.WebSocketTransport.TestWebSocket;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

public class HttpTransport 
{
    final private HttpServer httpServer;
    final private Server[] servers;
    final private int [] ports;
    
	public HttpTransport(HttpServer httpServer,Server[] servers) throws Exception
	{
        this.ports=new int[servers.length];
        for (int i=0;i<servers.length;i++)
        {
            this.ports[i]=((ServerConnector)((servers[i].getConnectors())[0])).getPort();
        }
        this.servers = servers;
	    this.httpServer=httpServer;
	}

	public HttpTransport(HttpServer httpServer,Server server) throws Exception
	{
		this(httpServer,  new Server[]{server});
	}
	
	public HttpServer getHttpServer()
	{
	    return this.httpServer;
	}

	public void start() throws Exception
	{
	    Map<String,WebSocketInitializer<?>> webSocketInitializers=this.httpServer.getWebSocketInitializers();
	    try
	    {
    		for (Server server:this.servers)
    		{
    	        AbstractHandler handler=new AbstractHandler()
    	        {
    	            @Override
    	            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    	            {
    	                try
    	                {
    	                    if (httpServer.handle(target,request, response))
    	                    {
    	                        String connection=response.getHeader("Connection");
    	                        if (TypeUtils.containsIgnoreCase(connection, "keep-alive"))
    	                        {
    	                            response.setHeader("Connection", "keep-alive");
    	                        }
    	                        baseRequest.setHandled(true);
    	                    }
    	                    else
    	                    {
    	                        baseRequest.setHandled(false);
    	                    }
    	                }
    	                catch (Throwable t)
    	                {
    	                    httpServer.getLogger().log(t);
    	                    baseRequest.setHandled(true); //Exception implies handler activity.
    	                }
    	            }
    	        };
    	        if (webSocketInitializers.size()==0)
    	        {
        	        server.setHandler(handler);
    	        }
    	        else
    	        {
                    HandlerCollection collection=new HandlerCollection(); 
                    ServletContextHandler servletHandler = new ServletContextHandler(server, "/");
                    collection.addHandler(handler);
                    collection.addHandler(servletHandler);
                    
                    JettyWebSocketServletContainerInitializer.configure(servletHandler, (servletContext, container) ->
                    {
                        // Configure the ServerContainer.
                        container.setMaxTextMessageSize(128 * 1024);

                        for (var entry:webSocketInitializers.entrySet())
                        {
                            container.addMapping(entry.getKey(), (upgradeRequest, upgradeResponse) ->
                            {
                                WebSocketInitializer<?> initializer=(WebSocketInitializer<?>)entry.getValue();
                                return new WebSocketResponder(this.httpServer.getTraceManager(), this.httpServer.getLogger(), initializer);
                            });
                        }
                    });
                    
                    server.setHandler(collection);
    	        }
    	        
    			server.start();
    		}
	    }
	    catch (Throwable t)
	    {
	        throw new Exception("Unable to start using ports: "+Utils.combine(this.ports, ", "),t);
	    }
	}
	
	public int[] getPorts()
	{
       return this.ports;
    }

    public void stop() throws Throwable
    {
        for (Server server:this.servers)
        {
            server.stop();
        }
    }
}
