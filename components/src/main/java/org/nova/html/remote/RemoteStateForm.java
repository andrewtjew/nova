package org.nova.html.remote;

import org.nova.http.server.StateHandling;
import org.nova.http.server.annotations.ContentWriters;

@ContentWriters(RemoteResponseWriter.class)
public class RemoteStateForm extends RemoteForm
{
    public RemoteStateForm(StateHandling stateHandling, String action) throws Throwable
    {
        stateHandling.setHandlerElement(this);
        if (action!=null)
        {
            action(action);
        }
    }

    public RemoteStateForm(StateHandling stateHandling) throws Throwable
    {
        this(stateHandling, null);
    }


    public RemoteResponse location(String pathAndQuery) throws Throwable
    {
        RemoteResponse response=new RemoteResponse();
        response.location(pathAndQuery);
        return response;
    }
    
    public RemoteResponse respond(RemoteResponse response)
    {
        response.outerHtml(this);
        return response;
    }    

}