package org.nova.http.server;

import org.nova.html.elements.TagElement;
import org.nova.security.QuerySecurity;

public interface RemoteStateBinding
{
    public TagElement<?> getState(Context context) throws Throwable;
    public void setState(TagElement<?> element) throws Throwable;
    public String getKey();
    public QuerySecurity getQuerySecurity();
}
