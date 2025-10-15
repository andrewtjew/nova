package org.nova.http.server;

import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public abstract class DecodingHttpServletRequest extends HttpServletRequestWrapper
{

    private HashMap<String, String> parameters = new HashMap<String, String>();

    public DecodingHttpServletRequest(HttpServletRequest request)
    {
        super(request);
    }

    public String getParameter(String name)
    {
        if (parameters.get(name) != null)
        {
            return parameters.get(name);
        }
        return super.getRequest().getParameter(name);
    }
    public String[] getParameterValues(String name)
    {
        if (parameters.get(name) != null)
        {
            return new String[] {parameters.get(name)};
        }
        return super.getRequest().getParameterValues(name);
    }
    void setParameter(String name, String value)
    {
        parameters.put(name, value);
    }
    abstract public String decodeParameter(String name) throws Throwable;
}