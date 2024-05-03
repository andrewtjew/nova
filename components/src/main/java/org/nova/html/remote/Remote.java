package org.nova.html.remote;

import org.nova.html.elements.FormElement;
import org.nova.html.ext.HtmlUtils;
import org.nova.json.ObjectMap;
import org.nova.json.ObjectMapper;

public class Remote 
{
    public static String js_getStatic(String action) throws Throwable
    {
        return HtmlUtils.js_call("nova.remote.getStatic",action);             
    }
    public static String js_getRemote(String href,String id) throws Throwable
    {
        return HtmlUtils.js_call("nova.remote.getRemote",href,id);             
    }
    public static String js_postStatic(String action) throws Throwable
    {
        return HtmlUtils.js_call("nova.remote.postStatic",action,null);             
    }
    public static String js_postStatic(String action,ObjectMap objectMap) throws Throwable
    {
        String data=ObjectMapper.writeObjectToString(objectMap);
        return HtmlUtils.js_call("nova.remote.postStatic",action,data);             
    }
    public static String js_postFormUrlEncoded(FormElement<?> form,String action) throws Throwable
    {
        return HtmlUtils.js_call("nova.remote.postFormUrlEncoded",form.id(),action);             
    }
}
