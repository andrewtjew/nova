package org.samples;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import org.nova.core.NameObject;
import org.nova.services.WebAccessSession;
import org.nova.tracing.Trace;


public class UserSession extends WebAccessSession<Service>
{
    public UserSession(Service service,String token,String user)
    {
        super(token, null, user,OffsetDateTime.now().getOffset(),null);
    }
    public UserSession(Service service,String token,String user,ZoneOffset zoneOffset,String language)
    {
        super(token, null, user,zoneOffset,language);
    }

    @Override
    public void onClose(Trace trace) throws Throwable
    {
    }

    @Override
    public NameObject[] getDisplayItems()
    {
        ArrayList<NameObject> list=new ArrayList<>();
        // Add
        return list.toArray(new NameObject[list.size()]);
    }

    @Override
    public boolean isInGroup(String group)
    {
        // TODO Auto-generated method stub
        return false;
    }
}
