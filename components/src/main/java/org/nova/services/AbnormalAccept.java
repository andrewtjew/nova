package org.nova.services;

public record AbnormalAccept(String seeOther,Integer statusCode)
{
    public AbnormalAccept(String redirect)
    {
        this(redirect,null);
    }
    public AbnormalAccept(int statusCode)
    {
        this(null,statusCode);
    }
    public AbnormalAccept()
    {
        this(null,null);
    }
}