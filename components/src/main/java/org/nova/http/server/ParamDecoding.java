package org.nova.http.server;

public interface ParamDecoding
{
    public String decodeQueryParam(String cypherText) throws Throwable;
    public String decodePathParam(String cypherText)  throws Throwable;
}
