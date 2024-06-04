package org.nova.html.remote;

import org.nova.http.server.RemoteStateBinding;
import org.nova.http.server.annotations.ContentWriters;

@ContentWriters(RemoteResponseWriter.class)
public class RemoteStateForm extends RemoteForm
{
    public RemoteStateForm(String id,RemoteStateBinding binding, String action) throws Throwable
    {
        super(id);
        binding.setState(this);
        if (action!=null)
        {
            action(action);
        }
    }
    public RemoteStateForm(RemoteStateBinding binding,String action) throws Throwable
    {
        this(null,binding, action);
    }

    public RemoteStateForm(RemoteStateBinding binding) throws Throwable
    {
        this(binding, null);
    }


//    public RemoteResponse location(String pathAndQuery) throws Throwable
//    {
//        RemoteResponse response=new RemoteResponse();
//        response.location(pathAndQuery);
//        return response;
//    }
    
    public RemoteResponse render(RemoteResponse response)
    {
        if (response!=null)
        {
            response.outerHtml(this);
        }
        return response;
    }    

}