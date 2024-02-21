package org.nova.balancing;

import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.configuration.Configuration;
import org.nova.frameworks.ServerApplication;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.elements.StringComposer;
import org.nova.html.ext.BasicPage;
import org.nova.html.ext.Text;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.html.remoting.HtmlRemotingWriter;
import org.nova.html.tags.h1;
import org.nova.html.tags.h2;
import org.nova.html.tags.h3;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.http.server.JSONPatchContentReader;
import org.nova.http.server.ServletHandler;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.services.WebController;
import org.nova.services.WebSessionController;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;

@ContentDecoders(GzipContentDecoder.class)
@ContentEncoders(GzipContentEncoder.class)
@ContentReaders({JSONContentReader.class,JSONPatchContentReader.class})
@ContentWriters({JSONContentWriter.class,HtmlElementWriter.class})
public class Handler extends ServletHandler
{
	final private Service service;
	final private String tooBusy;
	
	
	public Handler(Service service) throws Throwable 
	{
		this.service=service;
		BasicPage page=new BasicPage();
		page.returnAddInner(new h2()).addInner("Server too busy. Check back later.");
		StringComposer composer=new StringComposer();
		page.compose(composer);
		this.tooBusy=composer.getStringBuilder().toString();
		
//		this.service.update("http://localhost:10011", 0, Runtime.getRuntime().availableProcessors());
	}

	@Override
	public boolean handle(Trace parent, HttpServletRequest request, HttpServletResponse response) throws Throwable 
	{
		String endPoint=this.service.getEndPoint();
		if (endPoint==null)
		{
			response.getWriter().write(this.tooBusy);
			response.setStatus(HttpStatus.SERVICE_UNAVAILABLE_503);
			return true;
		}
        response.setStatus(HttpStatus.TEMPORARY_REDIRECT_307);
        String location=endPoint+request.getRequestURI();
        response.setHeader("Location",location);
		return true;
	}
	
	@GET
	@Path("/ping")
	public Element main(Trace parent)
	{
		return new BasicPage().addInner(System.currentTimeMillis());
	}		
	
	@POST
	@Path("/update")
	public void update(Trace parent,
			@QueryParam("endPoint") String endPoint,
			@QueryParam("load") double load,
			@QueryParam("cores") double cores
			)
	{
		this.service.update(endPoint, load, cores);
	}	
	
	
}
