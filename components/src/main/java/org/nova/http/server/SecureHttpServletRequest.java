package org.nova.http.server;

import java.util.Enumeration;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public abstract class SecureHttpServletRequest extends HttpServletRequestWrapper
{

    private HashMap<String, String[]> parameters = new HashMap<String, String[]>();
    public SecureHttpServletRequest(HttpServletRequest request) throws Throwable
    {
        super(request);
        this.parameters=new HashMap<String, String[]>();
        Enumeration<String> names=request.getParameterNames();
        while (names.hasMoreElements())
        {
            String name=names.nextElement();
            this.parameters.put(name, request.getParameterValues(name));
        }
    }
    public String[] getParameterValues(String name)
    {
        return parameters.get(name);
    }
    public String getParameter(String name)
    {
        String[] values=parameters.get(name);
        if (values==null)
        {
            return null;
        }
        if (values.length==0)
        {
            return null;
        }
        return values[0];
    }
    public Enumeration<String> getParameterNames() 
    {
        return java.util.Collections.enumeration(this.parameters.keySet());
    }    
    public void setParameter(String name, String value)
    {
        parameters.put(name, new String[] {value});
    }
    
}