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
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
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
	    
	    try
	    {
    		for (Server server:this.servers)
    		{
    	        AbstractHandler handler=new AbstractHandler()
    	        {
    	            @Override
    	            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    	            {
    	                handleRequest(target, baseRequest, request, response);
    	            }
    	        };
//    	        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 1000000);
//    	        server.setAttribute("org.eclipse.jetty.server.Request.maxFormKeys", 10000);
    			server.setHandler(handler);
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
 	
	public void handleRequest(String string, Request request, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
			throws IOException, ServletException
	{
		try
		{
			this.httpServer.handle(servletRequest, servletResponse);
		}
		catch (Throwable t)
		{
		    this.httpServer.getLogger().log(t);
		}
		finally
		{
            request.setHandled(true);
		}
	}

    public void stop() throws Throwable
    {
        for (Server server:this.servers)
        {
            server.stop();
        }
    }
}
