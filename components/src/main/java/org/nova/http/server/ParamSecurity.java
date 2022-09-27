package org.nova.http.server;

public interface ParamSecurity
{
    public String decodeQueryParam(String cypherText) throws Throwable;
    public String decodePathParam(String cypherText)  throws Throwable;
    public String encodeQueryParam(Object object) throws Throwable;
    public String encodePathParam(Object object) throws Throwable;
    
}
