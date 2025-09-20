package org.sample;

import java.time.ZoneId;
import java.util.ArrayList;
import org.nova.core.NameObject;
import org.nova.services.DeviceSession;
import org.nova.tracing.Trace;

public class UserSession extends DeviceSession<Role>
{
    public Long userId;
    final private ZoneId zoneId;

    public UserSession(Service service,long deviceSessionId,String token,ZoneId zoneId) throws Throwable
    {
        super(deviceSessionId,token,Role.class);
        this.zoneId=zoneId;
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

    public void login(Trace parent,long userId,Role role)
    {
        this.userId=userId;
        this.addRole(role);
    }
    public Long getUserId()
    {
        return this.userId;
    }
}
