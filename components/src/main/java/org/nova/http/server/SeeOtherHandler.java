package org.nova.http.server;

import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.html.elements.StringComposer;
import org.nova.html.ext.Redirect;
import org.nova.tracing.Trace;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SeeOtherHandler extends ServletHandler
{
    
    final private String location;
    public SeeOtherHandler(String location) throws Throwable 
    {
        this.location=location;
    }
    @Override
    public boolean handle(Trace parent, HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        response.setStatus(HttpStatus.SEE_OTHER_303);
        response.setHeader("Location",location);
        return true;
    }

}
