package org.nova.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.nova.json.ObjectMapper;

public interface QuerySecurity
{
    static public class NameParameter
    {
        public String name;
        public String value;
    }
    public NameParameter[] decodeParameters(String query) throws Throwable;
    public String encodeParameters(Object object) throws Throwable;
    public String getSecurityQueryKey();
    
}
//public class QuerySecurity
//{
//    static public class NameParameter
//    {
//        public String name;
//        public String value;
//    }
//    
//    final protected SecretKey secretKey;
//    public QuerySecurity(SecretKey secretKey)
//    {
//        this.secretKey=secretKey;
//    }
//    public NameParameter[] decodeParameters(String query) throws Throwable
//    {
//        int index=query.indexOf('=');
//        String code=query.substring(0,index+1);
//        String text=query.substring(index+1);
//        byte[] hmac=SecurityUtils.computeHashHMACSHA256(this.secretKey, text.getBytes());
//        String computed=Base64.getUrlEncoder().encodeToString(hmac);
//        if (code.equals(computed)==false)
//        {
//            throw new Exception();
//        }
//        return ObjectMapper.readObject(text, NameParameter[].class);
//        
//    }
//    //We don't need to encrypt as the client is aware of the parameters, we don't want the client to be able to generate static parameters, so we just apply server side hmac
//    public String encodeParameters(Object object) throws Throwable
//    {
//        if (object==null)
//        {
//            return null;
//        }
//        String parameters=ObjectMapper.writeObjectToString(object);
//        byte[] objectBytes=parameters.getBytes(StandardCharsets.UTF_8);
//        byte[] hmac=SecurityUtils.computeHashHMACSHA256(this.secretKey, objectBytes);
//        String code=Base64.getUrlEncoder().encodeToString(hmac);
//        return "?_="+code+parameters;
//    }
//    
//}
