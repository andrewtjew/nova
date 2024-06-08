package org.nova.html.bootstrap.remote;

import org.nova.security.QuerySecurity;

public class RemoteStatePathAndQuery extends org.nova.http.client.PathAndQuery
{
  public RemoteStatePathAndQuery(RemoteStateItem content,String path) throws Throwable
  {
      super(path);
      addQuery(content.getRemoteStateBinding().getKey(),content.id());
  }

}
