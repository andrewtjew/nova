package org.nova.http.server;

import org.nova.http.server.RequestHandlerMap.Map;

public class PathNode
{
	final Map map;
	RequestHandler requestHandler;

	PathNode()
	{
		map = new Map();
	}
	RequestHandler getRequestHandler()
	{
	    return this.requestHandler;
	}
}