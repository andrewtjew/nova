package org.samples;

import org.nova.html.elements.Element;
import org.nova.html.tags.h1;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.StateParam;
import org.nova.services.WebSessionController;
import org.nova.tracing.Trace;

public class SessionController extends WebSessionController<Service>
{
	public SessionController(Service service) 
	{
		super(service);
	}

	@GET
	@Path("/hello/user")
	public Element hello(Trace parent,@StateParam UserSession session)
	{
		return new h1().addInner("Hello, "+session.getUser());
	}
		
}
