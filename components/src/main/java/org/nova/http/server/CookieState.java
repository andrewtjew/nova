package org.nova.http.server;

import org.nova.http.server.annotations.CookieParam;

class CookieState
{
    final CookieParam cookieParam;
    final ParameterInfo parameterInfo;
    final Object parameter;
    
    CookieState(CookieParam cookieParam,ParameterInfo parameterInfo,Object parameter)
    {
        this.cookieParam=cookieParam;
        this.parameterInfo=parameterInfo;
        this.parameter=parameter;
    }
}