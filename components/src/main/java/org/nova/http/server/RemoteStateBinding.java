package org.nova.http.server;

import org.nova.html.elements.TagElement;
import org.nova.security.QuerySecurity;

public interface RemoteStateBinding
{
    public Object getState(Context context) throws Throwable;
    public void setState(String key,Object state) throws Throwable;
    public String getStateKey();
    
    
//    public QuerySecurity getQuerySecurity();
}
