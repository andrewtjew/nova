package org.nova.service.deviceSession;

import org.nova.http.server.Response;

public record AbnormalResult(String seeOther,Integer statusCode,Response<?> response)
{
    public AbnormalResult(String redirect)
    {
        this(redirect,null,null);
    }
    public AbnormalResult(int statusCode)
    {
        this(null,statusCode,null);
    }
    public AbnormalResult(Response<?> response)
    {
        this(null,null,response);
    }
    public AbnormalResult(int statusCode,Response<?> response)
    {
        this(null,statusCode,response);
    }
    public AbnormalResult(Object content)
    {
        this(null,null,new Response<>(content));
    }
    public AbnormalResult()
    {
        this(null,null,null);
    }
}