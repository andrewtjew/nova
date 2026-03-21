package org.nova.service.deviceSession;

import org.nova.http.server.Response;

public record AbnormalResult<CONTENT>(String seeOther,Integer statusCode,Response<CONTENT> response)
{
    public AbnormalResult(String redirect)
    {
        this(redirect,null,null);
    }
    public AbnormalResult(int statusCode)
    {
        this(null,statusCode,null);
    }
    public AbnormalResult(Response<CONTENT> response)
    {
        this(null,null,response);
    }
    public AbnormalResult(int statusCode,Response<CONTENT> response)
    {
        this(null,statusCode,response);
    }
    public AbnormalResult(CONTENT content)
    {
        this(null,null,new Response<CONTENT>(content));
    }
    public AbnormalResult()
    {
        this(null,null,null);
    }
}