package org.nova.html.remote;

import org.nova.html.elements.FormElement;
import org.nova.html.ext.HtmlUtils;
import org.nova.http.client.PathAndQuery;
import org.nova.http.client.SecurePathAndQuery;
import org.nova.json.ObjectMap;
import org.nova.json.ObjectMapper;
import org.nova.security.PathAndQuerySecurity;

public class Remote 
{
    public static String js_getStatic(String url) throws Throwable
    {
        return HtmlUtils.js_call("nova.remote.getStatic",url);             
    }
    public static String js_getStatic(PathAndQuery pathAndQuery) throws Throwable
    {
        return js_getStatic(pathAndQuery.toString());             
    }
    public static String js_getRemote(String id,String url) throws Throwable
    {
        return HtmlUtils.js_call("nova.remote.getRemote",id,url);             
    }
    public static String js_getRemote(String id,PathAndQuery pathAndQuery) throws Throwable
    {
        return js_getRemote(id,pathAndQuery.toString());             
    }
    public static String js_postStatic(String url) throws Throwable
    {
        return HtmlUtils.js_call("nova.remote.postStatic",url,null);             
    }
    public static String js_postStatic(PathAndQuery pathAndQuery) throws Throwable
    {
        return js_postStatic(pathAndQuery.toString());             
    }
    public static String js_postStatic(PathAndQuerySecurity security,String url) throws Throwable
    {
        return js_postStatic(new SecurePathAndQuery(security,url));
    }
    public static String js_postStatic(String url,ObjectMap objectMap) throws Throwable
    {
        String data=ObjectMapper.writeObjectToString(objectMap);
        return HtmlUtils.js_call("nova.remote.postStatic",url,data);             
    }
    public static String js_postStatic(PathAndQuery pathAndQuery,ObjectMap objectMap) throws Throwable
    {
        return js_postStatic(pathAndQuery.toString(),objectMap);
    }
    public static String js_postFormUrlEncoded(FormElement<?> form,String action) throws Throwable
    {
        return HtmlUtils.js_call("nova.remote.postFormUrlEncoded",form.id(),action);             
    }
    public static String js_postFormUrlEncoded(FormElement<?> form) throws Throwable
    {
        return HtmlUtils.js_call("nova.remote.postFormUrlEncoded",form.id(),form.action());             
    }
    public static String js_postFormUrlEncoded(String formId,String action) throws Throwable
    {
        return HtmlUtils.js_call("nova.remote.postFormUrlEncoded",formId,action);             
    }
}
