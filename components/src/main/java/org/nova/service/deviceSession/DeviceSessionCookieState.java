package org.nova.service.deviceSession;

public class DeviceSessionCookieState
{
    private String token;
    
    public DeviceSessionCookieState(String token)
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
