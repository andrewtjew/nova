package org.nova.http.server;

import org.nova.http.server.annotations.CookieStateParam;

public class PathParamValue
{
    final ParameterInfo parameterInfo;
    final Object value;
    
    public PathParamValue(ParameterInfo parameterInfo,Object value)
    {
        this.parameterInfo=parameterInfo;
        this.value=value;
    }
    public Object getValue()
    {
    	return this.value;    			
    }
}