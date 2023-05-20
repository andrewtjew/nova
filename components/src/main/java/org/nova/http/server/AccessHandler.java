package org.nova.http.server;

import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.html.elements.StringComposer;
import org.nova.html.ext.Redirect;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;

public class AccessHandler extends ServletHandler
{
    
    final private byte[] offlineRedirect;
    private boolean online;
    final private String offlineLocation;
    public AccessHandler(String offlineLocation) throws Throwable 
    {
    	Redirect redirect=new Redirect(offlineLocation);
    	this.offlineRedirect=redirect.toString().getBytes(StandardCharsets.UTF_8);
    	this.offlineLocation=offlineLocation;
    	this.online=true;
    }
    synchronized public void setOnline(boolean online)
    {
        this.online=online;
    }
    synchronized public boolean isOnline()
    {
        return this.online;
    }
    
    @Override
    public boolean handle(Trace parent, HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        if (this.online==false)
        {
            String URI=request.getRequestURI();
            if (this.offlineLocation.equals(URI)==false)
            {
                response.setContentLength(this.offlineRedirect.length);
                response.setContentType("text/html");
                response.setStatus(HttpStatus.OK_200);
                response.getOutputStream().write(this.offlineRedirect);
                return true;
            }
        }
        return false;
    }
}
