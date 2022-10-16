package org.nova.html.remote;

import org.nova.html.ext.HtmlUtils;
import org.nova.json.ObjectMap;
import org.nova.json.ObjectMapper;

public class Remote 
{
    public static String js_getStatic(String action) throws Throwable
    {
        return HtmlUtils.js_call("nova.remote.getStatic",action);             
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
}
