package org.nova.html.remote;

import org.nova.http.server.StateHandling;
import org.nova.http.server.annotations.ContentWriters;

@ContentWriters(RemoteResponseWriter.class)
public class RemoteStateForm extends RemoteForm
{
    private RemoteResponse response;
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


    public RemoteResponse beginResponse()
    {
        this.response=new RemoteResponse();
        return response;
    }

    public RemoteResponse location(String pathAndQuery) throws Throwable
    {
        this.response.location(pathAndQuery);
        return this.response;
    }
    
    public RemoteResponse endResponse()
    {
        this.response.outerHtml(this);
        return this.response;
    }    

}