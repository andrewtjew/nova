package org.nova.http.server;

import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.html.ext.Redirect;
import org.nova.services.Session;
import org.nova.services.SessionFilter;
import org.nova.tracing.Trace;

public class AccessHandler extends ServletHandler
{
    
    final private byte[] offlineRedirect;
    private boolean online;
    final private String offlineLocation;
    final private SessionFilter sessionFilter;
    public AccessHandler(String offlineLocation,SessionFilter sessionFilter) throws Throwable 
    {
    	Redirect redirect=new Redirect(offlineLocation);
    	this.offlineRedirect=redirect.toString().getBytes(StandardCharsets.UTF_8);
    	this.offlineLocation=offlineLocation;
    	this.online=true;
    	this.sessionFilter=sessionFilter;
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
            if (this.sessionFilter!=null)
            {
                Session session=this.sessionFilter.getSession(request);
                if (session!=null)
                {
                    return false;
                }
            }
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
