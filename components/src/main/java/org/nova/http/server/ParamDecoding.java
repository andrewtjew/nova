package org.nova.http.server;

import org.nova.utils.TypeUtils;

public interface ParamDecoding
{
    public String decodeQueryParam(String cypherText) throws Throwable;
    public String decodePathParam(String cypherText)  throws Throwable;
    
}
