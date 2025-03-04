package org.samples;

import org.nova.html.elements.Element;
import org.nova.html.tags.h1;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Path;
import org.nova.services.WebController;
import org.nova.tracing.Trace;

public class OpenController extends WebController<Service>
{
	public OpenController(Service service) 
	{
		super(service);
	}

	@GET
	@Path("/hello")
//	@AllowGroups()
	public Element hello(Trace parent)
	{
		return new h1().addInner("Hello world");
	}
	
}
