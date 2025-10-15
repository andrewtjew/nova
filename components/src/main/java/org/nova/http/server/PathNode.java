package org.nova.http.server;

import org.nova.http.server.RequestMethodMap.Map;

public class PathNode
{
	final Map map;
	RequestMethod requestMethod;

	PathNode()
	{
		map = new Map();
	}
	RequestMethod getRequestMethod()
	{
	    return this.requestMethod;
	}
}