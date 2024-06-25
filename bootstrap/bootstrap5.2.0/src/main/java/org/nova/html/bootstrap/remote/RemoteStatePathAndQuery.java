package org.nova.html.bootstrap.remote;

import org.nova.html.remote.RemoteStateForm;
import org.nova.security.QuerySecurity;

public class RemoteStatePathAndQuery extends org.nova.http.client.PathAndQuery
{
  public RemoteStatePathAndQuery(RemoteStateContent content,String path) throws Throwable
  {
      super(path);
      addQuery(content.getRemoteStateBinding().getKey(),content.id());
  }
  public RemoteStatePathAndQuery(RemoteStateForm form,String path) throws Throwable
  {
      super(path);
      addQuery(form.getRemoteStateBinding().getKey(),form.id());
  }

}
