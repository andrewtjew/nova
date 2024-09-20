package org.nova.http.server;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.tracing.Trace;

public class MoveHandler extends ServletHandler
{
    
    final private String moveToLocation;
    public MoveHandler(String moveToLocation) 
    {
    	this.moveToLocation=moveToLocation;
    }
    @Override
    public boolean handle(Trace parent, HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        response.setHeader("Location", this.moveToLocation);
        response.setStatus(HttpStatus.MOVED_PERMANENTLY_301);
        response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
    	return true;
    }

}
