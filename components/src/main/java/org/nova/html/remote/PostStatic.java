package org.nova.html.remote;

import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.JsObject;
import org.nova.json.ObjectMap;
import org.nova.json.ObjectMapper;

@Deprecated()
//Use RemoteUtils
public class PostStatic 
{
	final private ObjectMap parameters;
	public PostStatic()
	{
		this.parameters=new ObjectMap();
	}
	public PostStatic addParameter(String name,Object value)
	{
		this.parameters.put(name, value);
		return this;
	}
	public String js_post(String action) throws Throwable
	{
		String data=ObjectMapper.writeObjectToString(this.parameters);
		return HtmlUtils.js_call("nova.remote.postStatic",action,data);
				
	}
}
