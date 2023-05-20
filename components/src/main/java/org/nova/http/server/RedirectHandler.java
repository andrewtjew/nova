package org.nova.http.server;

import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.html.elements.StringComposer;
import org.nova.html.ext.Redirect;
import org.nova.tracing.Trace;

public class RedirectHandler extends ServletHandler
{
    
    final private byte[] content;
    public RedirectHandler(String location) throws Throwable 
    {
    	Redirect redirect=new Redirect(location);
    	StringComposer composer=new StringComposer();
    	redirect.compose(composer);
    	String content=composer.getStringBuilder().toString();
    	this.content=content.getBytes(StandardCharsets.UTF_8);
    	redirect.toString();
    }
    @Override
    public boolean handle(Trace parent, HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        response.setContentLength(this.content.length);
        response.setContentType("text/html");
        response.setStatus(HttpStatus.OK_200);
        
        response.getOutputStream().write(this.content);
    	return true;
    }

}
