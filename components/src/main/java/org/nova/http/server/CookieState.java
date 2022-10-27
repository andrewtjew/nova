package org.nova.http.server;

import org.nova.http.server.annotations.CookieStateParam;

public class CookieState
{
    final CookieStateParam cookieStateParam;
    final ParameterInfo parameterInfo;
    final Object parameter;
    
    public CookieState(CookieStateParam cookieStateParam,ParameterInfo parameterInfo,Object parameter)
    {
        this.cookieStateParam=cookieStateParam;
        this.parameterInfo=parameterInfo;
        this.parameter=parameter;
    }
    public Object getParameter()
    {
    	return this.parameter;    			
    }
}