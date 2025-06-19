package org.nova.html.remote;

import org.nova.http.server.Context;

/* A page may contain per session remote element. A remote element bound to a server side state object.
 * The ssso is created by the application and it's lifetime is determined by the application. 
 * To prevent ssso leaks, the application layer must provide sound ssso lifetime management, for example using the session filter. 
 * When a remote element calls the server, the request must include the id of the ssso.
 * The implementation dictates how the pass the id to the server for example using a query parameter.
 * getStateKey is then the key of the query parameter and the value is the id. '
 * The implementation must ensure that this key does not clash with other query parameter keys used by the application layer.
 * This interface is used by FilterChain and implemented by the application session object. 
 * If a request handler has a @StateParam parameter that is not of a session object type, then FilterChain asks the session object for the server side object and injects it as the @StateParam object.
 * 
 */
public interface RemoteStateBinding
{
    //The remote element doing the calling for example using RemoteForm.js_post(). The handler calls this method to get the remote element.
    public <T> T getRemoteCaller(Context context) throws Throwable;

    //Stores the ssso.
    public void setState(Object id,Object state) throws Throwable;
    
    //The state key is used 
    public String getStateKey();
}
