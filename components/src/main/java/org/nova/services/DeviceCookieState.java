package org.nova.services;

public class DeviceCookieState
{
    private String token;
    
    public DeviceCookieState(String token)
    {
        this.token=token;
    }
    
    public String getToken()
    {
        return this.token;
    }
    public void setToken(String token)
    {
        this.token=token;
    }
}
