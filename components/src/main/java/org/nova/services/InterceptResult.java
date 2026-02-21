package org.nova.services;

import org.nova.html.elements.Element;
import org.nova.http.server.Response;

public record InterceptResult<CONTENT>(String seeOther,Integer statusCode,Response<CONTENT> response)
{
    public InterceptResult(String redirect)
    {
        this(redirect,null,null);
    }
    public InterceptResult(int statusCode)
    {
        this(null,statusCode,null);
    }
    public InterceptResult(Response<CONTENT> response)
    {
        this(null,null,response);
    }
    public InterceptResult(int statusCode,Response<CONTENT> response)
    {
        this(null,statusCode,response);
    }
    public InterceptResult()
    {
        this(null,null,null);
    }
}