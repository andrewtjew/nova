package org.nova.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.nova.http.server.Context;
import org.nova.json.ObjectMapper;

public interface QuerySecurity
{
    public String getSecurityQueryKey();
    public void verifyQuery(Context context) throws Throwable;
    public String signQuery(String query) throws Throwable;
}
