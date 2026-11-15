package org.nova.security;

import org.nova.http.server.Response;

public record AbnormalResponse(String seeOther,Integer statusCode,Response<?> response)
{
    public AbnormalResponse(String redirect)
    {
        this(redirect,null,null);
    }
    public AbnormalResponse(int statusCode)
    {
        this(null,statusCode,null);
    }
    public AbnormalResponse(Response<?> response)
    {
        this(null,null,response);
    }
    public AbnormalResponse(int statusCode,Response<?> response)
    {
        this(null,statusCode,response);
    }
    public AbnormalResponse(Object content)
    {
        this(null,null,new Response<>(content));
    }
    public AbnormalResponse()
    {
        this(null,null,null);
    }
}