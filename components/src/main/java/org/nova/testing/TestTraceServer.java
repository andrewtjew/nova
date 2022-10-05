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
package org.nova.testing;

import java.util.LinkedList;

import javax.ws.rs.POST;

import org.nova.http.server.JettyServerFactory;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.HtmlContentWriter;
import org.nova.http.server.HttpServer;
import org.nova.http.server.HttpServerConfiguration;
import org.nova.http.server.HttpTransport;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.http.server.Transformers;
import org.nova.http.server.annotations.ContentParam;
import org.nova.http.server.annotations.PUT;
import org.nova.http.server.annotations.Path;
import org.nova.logging.LogUtils;
import org.nova.tracing.TraceManager;

public class TestTraceServer
{
	final private HttpServer server;
	final private int maximumMessages;
	
	public TestTraceServer(int maximumMessages,int threads,int port) throws Throwable
	{
		this.maximumMessages=maximumMessages;
        this.server=new HttpServer(new TraceManager(), LogUtils.createConsoleLogger(),false,new HttpServerConfiguration());
        HttpTransport httpTransport=new HttpTransport(this.server,JettyServerFactory.createServer(threads,port));

		Transformers transformers=new Transformers();
        transformers.add(new GzipContentDecoder());
        transformers.add(new JSONContentReader());
        transformers.add(new JSONContentWriter());
        transformers.add(new HtmlContentWriter());
		this.server.setTransformers(transformers);
		this.server.registerHandlers(this);
		httpTransport.start();
	}

	public TestTraceServer() throws Throwable
	{
		this(10000,10,9111);
	}

	
	@POST
	@Path("/log")
	public void log(@ContentParam String message)
	{
	}
	
}
